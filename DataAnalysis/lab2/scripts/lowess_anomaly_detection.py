import numpy as np
import pandas as pd
from sklearn.neighbors import NearestNeighbors

from scripts import knn_model


def preprocess_data(X):
    if not isinstance(X, pd.DataFrame):
        X = pd.DataFrame(X)

    X = X.apply(pd.to_numeric, errors='coerce')
    X = X.fillna(X.mean())

    return X


def lowess_anomaly_detection(X_train, y_train, kernel, metric):
    X_train = preprocess_data(X_train).to_numpy()
    y_train = np.array(y_train)

    n_neighbors = 5
    neighbors_model = NearestNeighbors(n_neighbors=n_neighbors, metric=metric)
    neighbors_model.fit(X_train)

    weights = np.ones(len(y_train))

    for idx, x in enumerate(X_train):
        x_reshaped = x.reshape(1, -1)
        neighbors_idx = neighbors_model.kneighbors(x_reshaped, return_distance=False)[0]

        count_correct = 0
        count_total = len(neighbors_idx)

        for neighbor_idx in neighbors_idx:
            weight = weights[neighbor_idx]
            if y_train[neighbor_idx] == y_train[idx]:
                count_correct += weight
            count_total += weight

        p = count_correct / count_total
        weights[idx] = kernel(1 - p)

    return weights


def validate_before_and_after_weights(X_train, y_train, X_test, y_test, best_params):
    window_type, n_neighbors, radius, metric, kernel = best_params
    kernel = knn_model.custom_kernel

    X_train = preprocess_data(X_train)
    X_test = preprocess_data(X_test)

    accuracy_before, _ = knn_model.evaluate_knn(
        X_train, y_train, X_test, y_test,
        window_type=window_type,
        n_neighbors=n_neighbors,
        radius=radius,
        metric=metric,
        kernel=kernel,
        class_priors=None
    )

    weights = lowess_anomaly_detection(X_train, y_train, kernel, metric)
    print(weights)

    accuracy_after, _ = knn_model.evaluate_knn(
        X_train, y_train, X_test, y_test,
        window_type=window_type,
        n_neighbors=n_neighbors,
        radius=radius,
        metric=metric,
        kernel=kernel,
        class_priors=weights
    )

    print(f"Точность до взвешивания: {accuracy_before}")
    print(f"Точность после взвешивания: {accuracy_after}")

    return accuracy_before, accuracy_after
