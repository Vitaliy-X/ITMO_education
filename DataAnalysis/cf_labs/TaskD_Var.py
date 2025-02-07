def calculate_variance():
    K = int(input())
    N = int(input())

    category_counts = {}
    sum_y = {}
    sum_y_squared = {}

    for _ in range(N):
        x, y = map(int, input().split())

        category_counts[x] = category_counts.get(x, 0) + 1
        sum_y[x] = sum_y.get(x, 0) + y
        sum_y_squared[x] = sum_y_squared.get(x, 0) + y * y

    overall_variance = 0

    for category in category_counts:
        count = category_counts[category]
        probability = count / N

        mean = sum_y[category] / count
        variance = (sum_y_squared[category] / count) - (mean ** 2)
        overall_variance += probability * variance

    return overall_variance


print(calculate_variance())
