from matplotlib import pyplot as plt
from sklearn.neighbors import KNeighborsClassifier, RadiusNeighborsClassifier
from sklearn.metrics import accuracy_score
from sklearn.model_selection import cross_val_score
import numpy as np


def uniform_kernel(distances):
    return 1/2 * (np.abs(distances) < 1)


def gaussian_kernel(distances):
    return 1 / np.sqrt(2 * np.pi) * np.exp(-(distances ** 2) / 2)


def custom_kernel(distances, a=1, b=1):
    return (1 - np.abs(distances) ** a) ** b


def triangular_kernel(distances):
    return (1 - np.abs(distances)) * (np.abs(distances) < 1)


def evaluate_knn(X_train, y_train, X_test, y_test, window_type='fixed', n_neighbors=5,
                 radius=0.0005, metric='minkowski', kernel=uniform_kernel, class_priors=None):

    y_train = np.array(y_train)

    if window_type == 'fixed':
        knn = KNeighborsClassifier(n_neighbors=n_neighbors, metric=metric)
        knn.fit(X_train, y_train)
        distances, indices = knn.kneighbors(X_test)
    elif window_type == 'radius':
        knn = RadiusNeighborsClassifier(radius=radius, metric=metric)
        knn.fit(X_train, y_train)
        distances, indices = knn.radius_neighbors(X_test)
    else:
        raise ValueError("window_type must be 'fixed' or 'radius'")

    y_pred = []
    for i in range(len(indices)):
        if len(indices[i]) > 0:
            max_distance = np.max(distances[i]) if len(distances[i]) > 0 else 1
            if max_distance > 0:
                normalized_distances = distances[i] / max_distance
            else:
                normalized_distances = np.zeros_like(distances[i])

            weights = kernel(normalized_distances)

            if class_priors is not None:
                prior_weights = np.array([class_priors[c] for c in y_train[indices[i]]])
                weights *= prior_weights

            y_pred.append(np.bincount(y_train[indices[i]], weights=weights).argmax())
        else:
            y_pred.append(np.bincount(y_train).argmax())

    y_pred = np.array(y_pred)
    accuracy = accuracy_score(y_test, y_pred)

    return accuracy, distances


def plot_accuracy(X_train, y_train, X_test, y_test, max_k=20):
    train_accuracies = []
    test_accuracies = []

    for k in range(1, max_k + 1):
        knn = KNeighborsClassifier(n_neighbors=k, metric='minkowski')
        knn.fit(X_train, y_train)

        train_scores = cross_val_score(knn, X_train, y_train, cv=2, scoring='accuracy')
        train_accuracies.append(train_scores.mean())

        y_pred = knn.predict(X_test)
        test_accuracy = accuracy_score(y_test, y_pred)
        test_accuracies.append(test_accuracy)

    plt.figure(figsize=(12, 6))
    plt.plot(range(1, max_k + 1), train_accuracies, label='Train Accuracy', color='b')
    plt.plot(range(1, max_k + 1), test_accuracies, label='Test Accuracy', color='g')
    plt.xlabel('Number of Neighbors (k)')
    plt.ylabel('Accuracy')
    plt.title('Accuracy vs. Number of Neighbors')
    plt.legend()
    plt.grid(True)
    plt.show()
