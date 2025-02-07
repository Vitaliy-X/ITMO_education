import time
import numpy as np
import pandas as pd
from matplotlib import pyplot as plt
from sklearn.linear_model import SGDClassifier
from sklearn.model_selection import train_test_split, learning_curve, ShuffleSplit
from sklearn.metrics import accuracy_score, make_scorer
from sklearn.svm import SVC

from scripts.GradientDescentClassifier import GradientDescentClassifier
from scripts.RidgeRegression import RidgeRegression
from scripts.SVM import SVM


def train_test_split_impl(X, y, random_state=42):
    X = X.values
    y = y.values
    X_train, X_test, y_train, y_test = train_test_split(
        X,
        y,
        stratify=y,
        test_size=0.2,
        random_state=random_state,
    )
    y_train = np.where(y_train == 0, -1, y_train)
    y_test = np.where(y_test == 0, -1, y_test)
    return X_train, X_test, y_train, y_test


def evaluate_model(model, X_train, y_train, X_test, y_test):
    model.fit(X_train, y_train)
    y_pred_test = model.predict(X_test)
    return accuracy_score(y_test, np.where(y_pred_test >= 0, 1, -1))


def grid_search(params, model_class, X_train, y_train, X_test, y_test):
    best_score = 0
    best_params = None

    for param_combination in params:
        model = model_class(**param_combination)
        score = evaluate_model(model, X_train, y_train, X_test, y_test)

        # print(f"Params: {param_combination}, Score: {score}")

        if score > best_score:
            best_score = score
            best_params = param_combination

    return best_params, best_score


def epochs_tuning(X_train, y_train):
    gd_epochs_range = range(10, 110, 2)
    svm_epochs_range = range(10, 110, 2)

    best_gd_epochs = 0
    best_svm_epochs = 0
    min_time_difference = float('inf')

    for gd_epochs in gd_epochs_range:
        for svm_epochs in svm_epochs_range:
            gd_classifier = GradientDescentClassifier(epochs=gd_epochs)
            svm_classifier = SVM(epochs=svm_epochs)

            start_time = time.time()
            gd_classifier.fit(X_train, y_train)
            gd_time = time.time() - start_time

            start_time = time.time()
            svm_classifier.fit(X_train, y_train)
            svm_time = time.time() - start_time

            time_difference = abs(gd_time - svm_time)
            if time_difference < min_time_difference:
                min_time_difference = time_difference
                best_gd_epochs = gd_epochs
                best_svm_epochs = svm_epochs

            print(f"GD Epochs: {gd_epochs}, SVM Epochs: {svm_epochs}, Time Difference: {time_difference:.6f}")

    print(
        f"Best GD Epochs: {best_gd_epochs}, Best SVM Epochs: {best_svm_epochs}, Min Time Difference: {min_time_difference:.6f}")
    return best_gd_epochs, best_svm_epochs


def plot_learning_curve(svm_scores, gd_scores, title='Learning Curve'):
    plt.figure(figsize=(10, 6))
    plt.plot(svm_scores, label='SVM Training Loss')
    plt.plot(gd_scores, label='Gradient Descent Training Loss')
    plt.xlabel('Epochs')
    plt.ylabel('Loss')
    plt.title(title)
    plt.legend()
    plt.grid(True)
    plt.show()


def plot_learning_curve_custom(train_sizes, train_scores, test_scores, baseline_score, title='Learning Curve'):
    plt.figure(figsize=(10, 6))
    plt.plot(train_sizes, train_scores, 'o-', color="r", label="Training score")
    plt.plot(train_sizes, test_scores, 'o-', color="g", label="Test score")
    plt.axhline(y=baseline_score, color='b', linestyle='--', label='Linear Regression Test Score')
    plt.xlabel("Training examples")
    plt.ylabel("Score")
    plt.title(title)
    plt.legend(loc="best")
    plt.grid(True)
    plt.show()


def evaluate_model_custom(model, X_train, y_train, X_test, y_test):
    model.fit(X_train, y_train)
    y_pred_train = np.sign(model.predict(X_train))
    y_pred_test = np.sign(model.predict(X_test))
    train_accuracy = accuracy_score(y_train, y_pred_train)
    test_accuracy = accuracy_score(y_test, y_pred_test)
    return train_accuracy, test_accuracy


def evaluate_and_record(model_class, model_params, X_train_subset, y_train_subset, X_test, y_test, train_scores,
                        test_scores):
    model = model_class(**model_params)
    train_acc, test_acc = evaluate_model_custom(model, X_train_subset, y_train_subset, X_test, y_test)
    train_scores.append(train_acc)
    test_scores.append(test_acc)


def main():
    data = pd.read_csv('./data/cars_data.csv')
    data['evaluation'] = data['evaluation'].apply(lambda x: 1 if x == 'отличная цена' or x == 'хорошая цена' else 0)

    X = data.drop('evaluation', axis=1)
    y = data['evaluation']

    X_train, X_test, y_train, y_test = train_test_split_impl(X, y)

    # best_gd_epochs, best_svm_epochs = epochs_tuning(X_train, y_train)

    gd_params = [
        {'learning_rate': lr, 'alpha': alpha, 'beta': beta}
        for lr in [0.0001, 0.001, 0.01, 0.1]
        for alpha in [0.01, 0.1, 0.2, 0.5]
        for beta in [0.1, 0.5, 1.0]
    ]

    svm_params = [
        {'C': C, 'gamma': gamma, 'kernel': kernel}
        for C in [0.001, 0.1, 1.0, 10.0]
        for gamma in [0.1, 1.0, 10.0]
        for kernel in ['linear', 'poly', 'rbf']
    ]

    ridge_params = [
        {'alpha': alpha}
        for alpha in [0.01, 0.1, 1.0, 10.0, 100.0]
    ]

    best_gd_params, best_gd_score = grid_search(gd_params, GradientDescentClassifier, X_train, y_train, X_test, y_test)
    print(f"Best GD Params: {best_gd_params}, Best GD Score: {best_gd_score}")

    best_svm_params, best_svm_score = grid_search(svm_params, SVM, X_train, y_train, X_test, y_test)
    print(f"Best SVM Params: {best_svm_params}, Best SVM Score: {best_svm_score}")

    best_ridge_params, best_ridge_score = grid_search(ridge_params, RidgeRegression, X_train, y_train, X_test, y_test)
    print(f"Best Ridge Params: {best_ridge_params}, Best Ridge Score: {best_ridge_score}")

    # ===============

    # # Custom SVM
    # custom_model = SVM(epochs=100, C=0.001, gamma=0.1, kernel='linear')
    # # custom_model = GradientDescentClassifier(epochs=40, learning_rate=0.01)
    # custom_model.fit(X_train, y_train)
    # custom_predictions = np.sign(custom_model.predict(X_test))
    # custom_accuracy = accuracy_score(y_test, custom_predictions)
    # print("Custom SVM Predictions:", custom_predictions)
    # print("Custom SVM Accuracy:", custom_accuracy)
    #
    # # Sklearn SVM
    # sklearn_model = SVC(C=1.0, kernel='linear', gamma=0.1)
    # # sklearn_model = SGDClassifier(max_iter=40, learning_rate='constant', eta0=0.01)
    # sklearn_model.fit(X_train, y_train)
    # sklearn_predictions = sklearn_model.predict(X_test)
    # sklearn_accuracy = accuracy_score(y_test, sklearn_predictions)
    # print("Sklearn SVM Predictions:", sklearn_predictions)
    # print("Sklearn SVM Accuracy:", sklearn_accuracy)

    # ===============

    # svm = SVM(epochs=100, C=0.01, gamma=0.01, kernel='poly')
    # svm.fit(X_train, y_train, eval_loss=True)
    #
    # gd = GradientDescentClassifier(epochs=100, learning_rate=0.01)
    # gd.fit(X_train, y_train, eval_loss=True)
    #
    # plot_learning_curve(svm.train_scores, gd.train_scores, title='Learning Curve for SVM and Gradient Descent')

    # ===============

    # ridge_model = RidgeRegression(alpha=1.0)
    # ridge_model.fit(X_train, y_train)
    # ridge_predictions = np.sign(ridge_model.predict(X_test))
    # ridge_accuracy = accuracy_score(y_test, ridge_predictions)
    #
    # train_sizes = np.linspace(0.1, 0.9, 5)
    # svm_train_scores = []
    # svm_test_scores = []
    # gd_train_scores = []
    # gd_test_scores = []
    #
    # for train_size in train_sizes:
    #     train_size_float = float(train_size)
    #     X_train_subset, _, y_train_subset, _ = train_test_split(X_train, y_train, train_size=train_size_float,
    #                                                             random_state=42)
    #
    #     evaluate_and_record(SVM, {'epochs': 200, 'C': 0.01, 'gamma': 0.01, 'kernel': 'linear'},
    #                         X_train_subset, y_train_subset, X_test, y_test, svm_train_scores, svm_test_scores)
    #
    #     evaluate_and_record(GradientDescentClassifier, {'epochs': 200, 'learning_rate': 0.01},
    #                         X_train_subset, y_train_subset, X_test, y_test, gd_train_scores, gd_test_scores)
    #
    # plot_learning_curve_custom(train_sizes, svm_train_scores, svm_test_scores, ridge_accuracy,
    #                            title='Learning Curve (Custom SVM)')
    # plot_learning_curve_custom(train_sizes, gd_train_scores, gd_test_scores, ridge_accuracy,
    #                            title='Learning Curve (Custom Gradient Descent)')


if __name__ == "__main__":
    main()
