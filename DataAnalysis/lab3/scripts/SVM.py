import numpy as np
from sklearn.metrics import accuracy_score


class SVM:
    def __init__(
            self,
            d=3,
            r=0.0,
            C=1.0,
            gamma=1.0,
            epochs=250,
            learning_rate=0.01,
            kernel='rbf',
            loss_func_name='square',
    ):
        self._d = d
        self._r = r
        self._C = C
        self._gamma = gamma
        self._epochs = epochs
        self._learning_rate = learning_rate
        self._kernel = kernel
        self._loss_func_name = loss_func_name
        self._w = None
        self._b = None
        self._X_train = None
        self._y_train = None
        self.train_scores = []

    def _kernel_func(self, v1, v2):
        v1 = np.array(v1)
        v2 = np.array(v2)
        if self._kernel == 'linear':
            return v1 @ v2.T
        if self._kernel == 'poly':
            return (self._gamma * (v1 @ v2.T) + self._r) ** self._d
        if self._kernel == 'rbf':
            x1_sq = np.sum(v1 ** 2, axis=1).reshape(-1, 1)
            x2_sq = np.sum(v2 ** 2, axis=1).reshape(1, -1)
            dist = x1_sq + x2_sq - 2 * v1 @ v2.T
            return np.exp(-self._gamma * dist)
        raise ValueError(f'Invalid kernel type: {self._kernel}')

    def fit(self, X, y, eval_loss=False):
        def restore_conditions(_deviation):
            if _deviation > 0:
                index = np.where(y > 0)[0]
                delta = deviation / len(index)
            else:
                index = np.where(y < 0)[0]
                delta = -deviation / len(index)
            self._w[index] = np.clip(self._w[index] - delta, 0, self._C)

        def calculate_bias():
            sv_indices = np.where((self._w > 1e-8) & (self._w < self._C - 1e-8))[0]
            if len(sv_indices) > 0:
                k = sv_indices[0]
                self._b = y[k] - np.sum((self._w * y) * K[:, k])
            else:
                sv_indices = np.where(self._w > 1e-8)[0]
                if len(sv_indices) > 0:
                    self._b = np.mean(y[sv_indices] - np.sum((self._w * y)[:, None] * K[:, sv_indices], axis=0))
                else:
                    self._b = 0.0

        self._X_train = X
        self._y_train = y

        n = X.shape[0]
        y = y.astype(float)

        self._w = np.zeros(n)
        K = self._kernel_func(X, X)

        for epoch in range(self._epochs):
            grad = 1 - y * (K @ (self._w * y))

            self._w = self._w + self._learning_rate * grad

            self._w = np.clip(self._w, 0, self._C)

            deviation = np.sum(self._w * y)
            if np.abs(deviation) > 1e-8:
                restore_conditions(deviation)

            if eval_loss:
                calculate_bias()
                y_pred = self.predict(X)
                current_loss = np.sum(loss_func(y, y_pred, self._loss_func_name)) / n
                self.train_scores.append(current_loss)

        calculate_bias()

    def predict(self, X):
        kernel_X = self._kernel_func(X, self._X_train)
        return (kernel_X @ (self._w * self._y_train)) + self._b

    @staticmethod
    def score(y_test, y_pred):
        y_pred_binary = np.where(y_pred >= 0, 1, -1)
        return accuracy_score(y_test, y_pred_binary)


def loss_func(y_true, y_pred, loss_func_name='square'):
    if loss_func_name == 'square':
        return 0.5 * (y_true - y_pred) ** 2
    elif loss_func_name == 'hinge':
        return np.maximum(0, 1 - y_true * y_pred)
    else:
        raise ValueError(f'Invalid loss function name: {loss_func_name}')
