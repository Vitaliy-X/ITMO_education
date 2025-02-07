import numpy as np
from sklearn.metrics import accuracy_score


class GradientDescentClassifier:
    def __init__(
            self,
            alpha=0.2,
            beta=0.5,
            epochs=250,
            learning_rate=0.01,
            loss_func_name='logistic',
    ):
        self._alpha = alpha
        self._beta = beta
        self._epochs = epochs
        self._learning_rate = learning_rate
        self._loss_func_name = loss_func_name
        self._weights = None
        self._bias = None
        self.train_scores = []

    def fit(self, X, y, eval_loss=False):
        def hinge_func(_pred):
            return np.maximum(0, 1 - _pred * y)

        def hinge_func_gradient(_pred):
            margin = y * _pred
            return np.where(margin < 1, -y, 0)

        def logistic_func(_pred):
            return np.log(1 + np.exp(-(y * _pred)))

        def logistic_func_gradient(_pred):
            return -(y / (1 + np.exp(y * _pred)))

        def perceptron_loss(_pred):
            return np.maximum(0, -_pred)

        def perceptron_loss_gradient(_pred):
            return np.where(_pred < 0, -y, 0)

        def regularization_gradient():
            return self._alpha * np.sign(self._weights) + self._beta * self._weights

        def loss_gradient(pred):
            loss = loss_func_gradient(pred)
            reg_grad = regularization_gradient()

            dw = (1 / n_samples) * np.dot(X.T, loss) + reg_grad
            db = (1 / n_samples) * np.sum(loss)
            return dw, db

        loss_funcs = {
            'hinge': (hinge_func, hinge_func_gradient),
            'logistic': (logistic_func, logistic_func_gradient),
            'perceptron': (perceptron_loss, perceptron_loss_gradient),
        }

        n_samples, n_features = X.shape
        self._weights = np.zeros(n_features)
        self._bias = 0
        loss_func, loss_func_gradient = loss_funcs[self._loss_func_name]

        for epoch in range(self._epochs):
            y_pred = np.dot(X, self._weights) + self._bias
            diff_w, diff_b = loss_gradient(y_pred)
            self._weights -= self._learning_rate * diff_w
            self._bias -= self._learning_rate * diff_b

            if eval_loss:
                current_loss = np.sum(loss_func(y_pred)) / n_samples
                self.train_scores.append(current_loss)

    def predict(self, X):
        return np.dot(X, self._weights) + self._bias

    @staticmethod
    def score(y_test, y_pred):
        y_pred_binary = np.where(y_pred >= 0, 1, -1)
        return accuracy_score(y_test, y_pred_binary)
