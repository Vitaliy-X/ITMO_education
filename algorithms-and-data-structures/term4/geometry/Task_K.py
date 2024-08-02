from math import atan2, pi


def compute_polar_angle(coord_x, coord_y):
    angle = atan2(coord_y, coord_x)
    if angle < 0:
        angle += 2 * pi
    return angle


point_x, point_y = map(int, input().split(' '))

polar_angle_result = compute_polar_angle(point_x, point_y)

print(polar_angle_result)
