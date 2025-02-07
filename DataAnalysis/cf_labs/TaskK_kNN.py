def binary_search(objects, query_x):
    left, right = 0, len(objects) - 1
    while left < right:
        mid = (left + right) // 2
        if objects[mid][0] < query_x:
            left = mid + 1
        else:
            right = mid
    return left


def find_k_nearest(objects, query_x, k_nearest, start):
    left_p = start - 1
    right_p = start
    nearest = []

    while len(nearest) < k_nearest:
        if left_p >= 0 and (
                right_p >= len(objects) or abs(objects[left_p][0] - query_x) <= abs(objects[right_p][0] - query_x)):
            nearest.append(objects[left_p][1])
            left_p -= 1
        elif right_p < len(objects):
            nearest.append(objects[right_p][1])
            right_p += 1
        else:
            break

    return nearest, left_p, right_p


def calculate_average(nearest, k_nearest, objects, left_p, right_p, query_x):
    if len(nearest) < k_nearest:
        return -1.0

    left_distance_equal = left_p >= 0 and abs(objects[left_p][0] - query_x) == abs(objects[right_p - 1][0] - query_x)
    right_distance_equal = right_p < len(objects) and abs(objects[right_p][0] - query_x) == abs(objects[right_p - 1][0] - query_x)

    if left_distance_equal or right_distance_equal:
        return -1.0

    return sum(nearest) / k_nearest


def k_nearest_average(objects, queries):
    objects = sorted(objects)
    averages = []

    for query_x, k_nearest in queries:
        start = binary_search(objects, query_x)
        nearest, left_p, right_p = find_k_nearest(objects, query_x, k_nearest, start)
        average = calculate_average(nearest, k_nearest, objects, left_p, right_p, query_x)
        averages.append(average)

    return averages


if __name__ == "__main__":
    n = int(input())
    objects = [tuple(map(int, input().strip().split())) for _ in range(n)]
    m = int(input())
    queries = [tuple(map(int, input().strip().split())) for _ in range(m)]

    answers = k_nearest_average(objects, queries)
    for answer in answers:
        print(answer)
