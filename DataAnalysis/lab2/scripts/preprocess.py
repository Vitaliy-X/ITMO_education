import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import LabelEncoder


def load_and_preprocess_data(file_path):
    data = get_normalized_data(file_path)

    X = data.drop('evaluation', axis=1)
    y = data['evaluation']
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

    return X_train, X_test, y_train, y_test


def make_correlation(file_path):
    data = get_normalized_data(file_path)

    plt.figure(figsize=(10, 8))
    sns.heatmap(data.corr(), annot=True, fmt=".2f", cmap='coolwarm')
    plt.title('Корреляционная матрица')
    plt.show()


def get_normalized_data(file_path):
    data = pd.read_csv(file_path)
    label_encoder = LabelEncoder()
    data['evaluation'] = label_encoder.fit_transform(data['evaluation'])
    return data
