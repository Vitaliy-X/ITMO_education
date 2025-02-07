import math


def count_frequencies(n, data):
    cnt_x = {}
    cnt_y_given_x = {}

    for x, y in data:
        if x not in cnt_x:
            cnt_x[x] = 0
        cnt_x[x] += 1
        if x not in cnt_y_given_x:
            cnt_y_given_x[x] = {}
        if y not in cnt_y_given_x[x]:
            cnt_y_given_x[x][y] = 0
        cnt_y_given_x[x][y] += 1

    return cnt_x, cnt_y_given_x


def calculate_entropy(n, cnt_x, cnt_y_given_x):
    h_y_given_x = 0.0

    for x in cnt_x:
        p_x = cnt_x[x] / n
        h_y_given_x_x = 0.0
        for y in cnt_y_given_x[x]:
            p_y_given_x = cnt_y_given_x[x][y] / cnt_x[x]
            h_y_given_x_x -= p_y_given_x * math.log(p_y_given_x)
        h_y_given_x += p_x * h_y_given_x_x

    return h_y_given_x


if __name__ == "__main__":
    kx, ky = map(int, input().split())
    n = int(input())
    data = [tuple(map(int, input().split())) for _ in range(n)]
    cnt_x, cnt_y_given_x = count_frequencies(n, data)
    result = calculate_entropy(n, cnt_x, cnt_y_given_x)
    print(result)
