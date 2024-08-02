import math


def calculate_distance(x1, y1, x2, y2, x3, y3):
    if x1 == x2:
        if min(y1, y2) <= y3 <= max(y1, y2):
            return abs(x3 - x1)
        else:
            return min(math.dist((x3, y3), (x1, y1)), math.dist((x3, y3), (x1, y2)))
    elif y1 == y2:
        if min(x1, x2) <= x3 <= max(x1, x2):
            return abs(y3 - y1)
        else:
            return min(math.dist((x3, y3), (x1, y1)), math.dist((x3, y3), (x2, y1)))
    else:
        slope = (y2 - y1) / (x2 - x1)
        intercept = y1 - slope * x1
        x_proj = (slope * (y3 - intercept) + x3) / (1 + slope ** 2)
        y_proj = slope * x_proj + intercept
        if min(x1, x2) <= x_proj <= max(x1, x2) and min(y1, y2) <= y_proj <= max(y1, y2):
            return math.dist((x3, y3), (x_proj, y_proj))
        else:
            return min(math.dist((x3, y3), (x1, y1)), math.dist((x3, y3), (x2, y2)))


def calculate_determinants(x1, y1, x2, y2, x3, y3, x4, y4):
    det = (x2 - x1) * (y3 - y4) - (y2 - y1) * (x3 - x4)
    det1 = (x2 - x1) * (y3 - y1) - (y2 - y1) * (x3 - x1)
    det2 = (y3 - y4) * (x3 - x1) - (x3 - x4) * (y3 - y1)
    return det, det1, det2


def check_intersection(det, det1, det2):
    if det != 0:
        t_param = det1 / det
        s_param = det2 / det
        return 0 <= t_param <= 1 and 0 <= s_param <= 1
    return False


def find_min_distance(x1, y1, x2, y2, x3, y3, x4, y4):
    if check_intersection(*calculate_determinants(x1, y1, x2, y2, x3, y3, x4, y4)):
        return 0.0
    distances = [
        calculate_distance(x1, y1, x2, y2, x3, y3),
        calculate_distance(x1, y1, x2, y2, x4, y4),
        calculate_distance(x3, y3, x4, y4, x1, y1),
        calculate_distance(x3, y3, x4, y4, x2, y2)
    ]
    return min(distances)


def read_input():
    input_data = list(map(int, input().split()))
    if len(input_data) == 8:
        return input_data
    else:
        x1, y1, x2, y2 = input_data[:4]
        x3, y3, x4, y4 = map(int, input().split())
        return x1, y1, x2, y2, x3, y3, x4, y4


x1, y1, x2, y2, x3, y3, x4, y4 = read_input()
min_distance = find_min_distance(x1, y1, x2, y2, x3, y3, x4, y4)
print(f"{min_distance:.9f}")
