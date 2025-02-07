import numpy as np
from sklearn.metrics import accuracy_score


class RidgeRegression:
    def __init__(self, alpha=1.0):
        self.alpha = alpha
        self.coef_ = None
        self.intercept_ = None

    def fit(self, X, y):
        X = np.hstack([np.ones((X.shape[0], 1)), X])
        # (X^T X + alpha * I)^(-1) X^T y
        I = np.eye(X.shape[1])
        I[0, 0] = 0
        self.coef_ = np.linalg.inv(X.T @ X + self.alpha * I) @ X.T @ y

    def predict(self, X):
        X = np.hstack([np.ones((X.shape[0], 1)), X])
        return X @ self.coef_

    @staticmethod
    def score(y_test, y_pred):
        y_pred_binary = np.where(y_pred >= 0, 1, -1)
        return accuracy_score(y_test, y_pred_binary)
