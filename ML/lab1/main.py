import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
from sklearn.model_selection import train_test_split
from sklearn.metrics import f1_score
from sklearn.preprocessing import LabelEncoder

# === Утилиты ===
def load_and_preprocess_data(file_path):
    data = pd.read_csv(file_path)
    label_encoder = LabelEncoder()
    data['evaluation'] = label_encoder.fit_transform(data['evaluation'])
    X = data.drop(columns=['evaluation'])
    y = data['evaluation']
    return train_test_split(X.values, y.values, test_size=0.2, random_state=42)

def softmax(z):
    exp_z = np.exp(z - np.max(z, axis=1, keepdims=True))
    return exp_z / np.sum(exp_z, axis=1, keepdims=True)

def soft_argmax_cross_entropy(logits, y_true):
    logits = np.asarray(logits)
    y_true = np.asarray(y_true)
    exp_logits = np.exp(logits - np.max(logits, axis=1, keepdims=True))
    softmax_probs = exp_logits / np.sum(exp_logits, axis=1, keepdims=True)
    num_samples = logits.shape[0]
    correct_log_probs = -np.log(softmax_probs[range(num_samples), y_true])
    loss = np.sum(correct_log_probs) / num_samples
    grad = softmax_probs
    grad[range(num_samples), y_true] -= 1
    grad /= num_samples
    return loss, grad

def activation_function(Z, func='identity'):
    if func == 'identity':
        return Z
    elif func == 'relu':
        return np.maximum(0, Z)
    elif func == 'tanh':
        return np.tanh(Z)
    else:
        raise ValueError("Unsupported activation function")

class AdamOptimizer:
    def __init__(self, parameters, learning_rate=0.01, beta1=0.9, beta2=0.999, epsilon=1e-8):
        self.parameters = parameters
        self.learning_rate = learning_rate
        self.beta1 = beta1
        self.beta2 = beta2
        self.epsilon = epsilon
        self.m = [np.zeros_like(p) for p in parameters]
        self.v = [np.zeros_like(p) for p in parameters]
        self.t = 0

    def update(self, grads):
        self.t += 1
        for i, (param, grad) in enumerate(zip(self.parameters, grads)):
            self.m[i] = self.beta1 * self.m[i] + (1 - self.beta1) * grad
            self.v[i] = self.beta2 * self.v[i] + (1 - self.beta2) * (grad ** 2)
            m_hat = self.m[i] / (1 - self.beta1 ** self.t)
            v_hat = self.v[i] / (1 - self.beta2 ** self.t)
            param -= self.learning_rate * m_hat / (np.sqrt(v_hat) + self.epsilon)

# === MLP модели ===
class MultiSimpleMLP:
    def __init__(self, input_dim, output_dim, hidden_layers, activation='tanh', epochs=100, learning_rate=0.01):
        self.activation = activation
        self.epochs = epochs
        self.learning_rate = learning_rate
        layer_dims = [input_dim] + hidden_layers + [output_dim]
        self.weights = []
        self.biases = []
        for i in range(len(layer_dims) - 1):
            self.weights.append(np.random.randn(layer_dims[i], layer_dims[i+1]) * np.sqrt(2. / layer_dims[i]))
            self.biases.append(np.zeros((1, layer_dims[i+1])))
        self.optimizer = AdamOptimizer(self.weights + self.biases, learning_rate=self.learning_rate)
        self.losses = []

    def forward(self, X):
        self.z_values = []
        self.a_values = [X]
        A = X
        for i in range(len(self.weights) - 1):
            Z = np.dot(A, self.weights[i]) + self.biases[i]
            A = activation_function(Z, self.activation)
            self.z_values.append(Z)
            self.a_values.append(A)
        Z = np.dot(A, self.weights[-1]) + self.biases[-1]
        self.z_values.append(Z)
        A = softmax(Z)
        self.a_values.append(A)
        return A

    def backward(self, y_true, output):
        loss, delta = soft_argmax_cross_entropy(output, y_true)
        grads_W, grads_B = [], []
        for i in reversed(range(len(self.weights))):
            A_prev = self.a_values[i]
            dW = np.dot(A_prev.T, delta)
            dB = np.sum(delta, axis=0, keepdims=True)
            grads_W.insert(0, dW)
            grads_B.insert(0, dB)
            if i != 0:
                Z_prev = self.z_values[i - 1]
                dZ = np.dot(delta, self.weights[i].T)
                if self.activation == 'relu':
                    dZ *= (Z_prev > 0)
                elif self.activation == 'tanh':
                    dZ *= 1 - np.tanh(Z_prev) ** 2
                delta = dZ
        self.optimizer.update(grads_W + grads_B)
        return loss

    def train(self, X, y):
        for _ in range(self.epochs):
            output = self.forward(X)
            loss = self.backward(y, output)
            self.losses.append(loss)

    def predict(self, X):
        return np.argmax(self.forward(X), axis=1)

    def loss_history(self):
        return self.losses

class RBF_Layer:
    def __init__(self, input_dim, output_dim, gamma=0.1):
        self.centers = np.random.randn(output_dim, input_dim)
        self.biases = np.zeros((1, output_dim))
        self.gamma = gamma

    def forward(self, X):
        self.X = X
        diff = X[:, np.newaxis, :] - self.centers[np.newaxis, :, :]
        self.dists = np.sum(diff ** 2, axis=2)
        return np.exp(-self.gamma * self.dists) + self.biases

    def backward(self, grad_output):
        grad_input = np.zeros_like(self.X)
        grad_centers = np.zeros_like(self.centers)
        grad_biases = np.sum(grad_output, axis=0, keepdims=True)
        exp_term = np.exp(-self.gamma * self.dists)
        for i in range(self.X.shape[0]):
            for j in range(self.centers.shape[0]):
                grad = grad_output[i, j] * exp_term[i, j] * 2 * self.gamma * (self.X[i] - self.centers[j])
                grad_input[i] += grad
                grad_centers[j] -= grad
        return grad_input, grad_centers, grad_biases

class MultiRBFMLP:
    def __init__(self, input_dim, output_dim, hidden_layers, gamma=0.1, epochs=100, learning_rate=0.01):
        self.epochs = epochs
        self.learning_rate = learning_rate
        self.gamma = gamma
        self.rbf_layers = [RBF_Layer(input_dim if i == 0 else hidden_layers[i-1], hidden_layers[i], gamma)
                           for i in range(len(hidden_layers))]
        self.weights = np.random.randn(hidden_layers[-1], output_dim) * 0.1
        self.biases = np.zeros((1, output_dim))
        self.losses = []

    def forward(self, X):
        self.rbf_outputs = []
        A = X
        for layer in self.rbf_layers:
            A = layer.forward(A)
            self.rbf_outputs.append(A)
        self.last_output = A
        self.logits = np.dot(A, self.weights) + self.biases
        return softmax(self.logits)

    def train(self, X, y):
        for _ in range(self.epochs):
            output = self.forward(X)
            loss, dZ = soft_argmax_cross_entropy(output, y)
            self.losses.append(loss)
            dW = np.dot(self.last_output.T, dZ)
            dB = np.sum(dZ, axis=0, keepdims=True)
            dH = np.dot(dZ, self.weights.T)
            grad_input, _, _ = self.rbf_layers[-1].backward(dH)
            self.weights -= self.learning_rate * dW
            self.biases -= self.learning_rate * dB

    def predict(self, X):
        return np.argmax(self.forward(X), axis=1)

    def loss_history(self):
        return self.losses

class MultiMixedMLP:
    def __init__(self, input_dim, output_dim, simple_layers, rbf_layers, activation='tanh', gamma=0.1, epochs=100, lr=0.01):
        self.simple = MultiSimpleMLP(input_dim, simple_layers[-1], simple_layers[:-1], activation, epochs, lr)
        self.rbf = MultiRBFMLP(simple_layers[-1], output_dim, rbf_layers, gamma, epochs, lr)
        self.epochs = epochs
        self.losses = []

    def train(self, X, y):
        for _ in range(self.epochs):
            hidden = self.simple.forward(X)
            out = self.rbf.forward(hidden)
            loss, _ = soft_argmax_cross_entropy(out, y)
            self.losses.append(loss)
            self.rbf.train(hidden, y)

    def predict(self, X):
        hidden = self.simple.forward(X)
        return self.rbf.predict(hidden)

    def loss_history(self):
        return self.losses

# === Оценка и графики ===
def evaluate_model(model, X_test, y_test):
    y_pred = model.predict(X_test)
    return f1_score(y_test, y_pred, average='weighted')

def plot_loss(model, title='Loss'):
    plt.figure()
    plt.plot(model.loss_history())
    plt.title(title)
    plt.xlabel('Epochs')
    plt.ylabel('Loss')
    plt.grid(True)
    plt.show()

def f1_vs_depth(X_train, X_test, y_train, y_test, ModelClass, label):
    depths = [1, 2, 3, 4]
    f1_scores = []
    for d in depths:
        hidden = [64] * d
        model = ModelClass(X_train.shape[1], len(np.unique(y_train)), hidden, epochs=100)
        model.train(X_train, y_train)
        f1 = evaluate_model(model, X_test, y_test)
        f1_scores.append(f1)
    plt.plot(depths, f1_scores, marker='o', label=label)

# === main() ===
def main():
    X_train, X_test, y_train, y_test = load_and_preprocess_data('./data/cars_data.csv')

    # MultiSimpleMLP
    simple = MultiSimpleMLP(X_train.shape[1], len(np.unique(y_train)), [64, 32], epochs=100)
    simple.train(X_train, y_train)
    plot_loss(simple, "MultiSimpleMLP Loss")

    # MultiRBFMLP
    rbf = MultiRBFMLP(X_train.shape[1], len(np.unique(y_train)), [64, 32], epochs=100)
    rbf.train(X_train, y_train)
    plot_loss(rbf, "MultiRBFMLP Loss")

    # MultiMixedMLP
    mixed = MultiMixedMLP(X_train.shape[1], len(np.unique(y_train)), [64, 32], [32], epochs=100)
    mixed.train(X_train, y_train)
    plot_loss(mixed, "MultiMixedMLP Loss")

    # F1-график
    f1_vs_depth(X_train, X_test, y_train, y_test, MultiSimpleMLP, 'MultiSimpleMLP')
    f1_vs_depth(X_train, X_test, y_train, y_test, MultiRBFMLP, 'MultiRBFMLP')
    plt.title('F1 Score vs Hidden Layers')
    plt.xlabel('Hidden Layers')
    plt.ylabel('F1 Score')
    plt.legend()
    plt.grid(True)
    plt.show()

if __name__ == "__main__":
    main()
