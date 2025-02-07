import numpy as np

import knn_model
from sklearn.model_selection import StratifiedKFold


def tune_hyperparameters(X, y):
    param_grid = {
        'window_type': ['fixed', 'radius'],
        'n_neighbors': [3, 5, 7],
        'radius': [0.00001, 0.0001, 0.005, 0.01, 0.5, 1.0],
        'metric': ['cosine', 'minkowski', 'chebyshev'],
        'kernel': [knn_model.uniform_kernel, knn_model.gaussian_kernel, knn_model.custom_kernel,
                   knn_model.triangular_kernel]
    }

    best_score = 0
    best_params = None

    skf = StratifiedKFold(n_splits=5, shuffle=True, random_state=42)

    for window_type in param_grid['window_type']:
        if window_type == 'fixed':
            hyperparams = param_grid['n_neighbors']
            hyperparam_name = 'n_neighbors'
        elif window_type == 'radius':
            hyperparams = param_grid['radius']
            hyperparam_name = 'radius'
        else:
            continue

        for hyperparam in hyperparams:
            for metric in param_grid['metric']:
                for kernel in param_grid['kernel']:
                    scores = []
                    for train_index, val_index in skf.split(X, y):
                        X_train_split, X_val = X.iloc[train_index], X.iloc[val_index]
                        y_train_split, y_val = y.iloc[train_index], y.iloc[val_index]

                        score, _ = knn_model.evaluate_knn(
                            X_train_split, y_train_split, X_val, y_val,
                            window_type=window_type,
                            **{hyperparam_name: hyperparam},
                            metric=metric,
                            kernel=kernel
                        )
                        scores.append(score)

                    mean_score = np.mean(scores)

                    if mean_score > best_score:
                        best_score = mean_score
                        if window_type == 'fixed':
                            best_params = (window_type, hyperparam, None, metric, kernel)
                        else:
                            best_params = (window_type, None, hyperparam, metric, kernel)

    print(f'Best Score: {best_score}')
    print(f'Best Parameters: window_type={best_params[0]}, n_neighbors={best_params[1]},'
          f' radius={best_params[2]}, metric={best_params[3]}, kernel={best_params[4].__name__}')

    return best_params, best_score
