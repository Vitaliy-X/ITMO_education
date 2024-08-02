import math


def calculate_distance_to_line(px, py, a, b, c):
    numerator = abs(a * px + b * py + c)
    denominator = math.sqrt(a ** 2 + b ** 2)
    return numerator / denominator


point_x, point_y, coeff_a, coeff_b, coeff_c = map(int, input().split())

distance_result = calculate_distance_to_line(point_x, point_y, coeff_a, coeff_b, coeff_c)

print(distance_result)
