from math import sqrt, acos


def calculate_angle(vx1, vy1, vx2, vy2):
    dot_prod = vx1 * vx2 + vy1 * vy2
    magnitude_v1 = sqrt(vx1 ** 2 + vy1 ** 2)
    magnitude_v2 = sqrt(vx2 ** 2 + vy2 ** 2)
    cos_theta = dot_prod / (magnitude_v1 * magnitude_v2)
    angle_radians = acos(cos_theta)
    return angle_radians


vec1_x, vec1_y, vec2_x, vec2_y = map(int, input().split(" "))

angle_between_vectors = calculate_angle(vec1_x, vec1_y, vec2_x, vec2_y)

print(angle_between_vectors)
