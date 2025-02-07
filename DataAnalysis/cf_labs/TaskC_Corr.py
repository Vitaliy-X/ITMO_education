import numpy as np
from scipy.stats import pearsonr


def pearson_correlation(K, a, b):
    one_hot = np.eye(K)[np.array(a) - 1]
    correlation = []
    w = []
    for k in range(K):
        if np.sum(one_hot[:, k]) > 0:
            corr, _ = pearsonr(one_hot[:, k], b)
            correlation.append(corr)
            w.append(np.sum(one_hot[:, k]))

    weighted_corr = np.average(correlation, weights=w)
    return weighted_corr


_, K = map(int, input().split())
a = list(map(int, input().split()))
b = list(map(int, input().split()))

print(pearson_correlation(K, a, b))
