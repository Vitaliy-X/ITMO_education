def evaluate_ranks(values):
    sorted_values = sorted(values)
    ranks = {}
    for r, v in enumerate(sorted_values, start=1):
        ranks[v] = r
    return ranks


def spearman():
    N = int(input())
    x1 = []
    x2 = []

    for _ in range(N):
        y1, y2 = map(int, input().split())
        x1.append(y1)
        x2.append(y2)

    rank1 = evaluate_ranks(x1)
    rank2 = evaluate_ranks(x2)

    diff = 0
    for i in range(N):
        d = rank1[x1[i]] - rank2[x2[i]]
        diff += d * d

    spearman_coef = 1 - (6 * diff) / (N * (N * N - 1))

    return spearman_coef


print(spearman())
