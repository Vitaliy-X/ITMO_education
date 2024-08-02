from math import sqrt


def distance_point_to_segment(x0, y0, x1, y1, x2, y2):
    dx = x2 - x1
    dy = y2 - y1

    if dx == 0 and dy == 0:
        return sqrt((x0 - x1) ** 2 + (y0 - y1) ** 2)

    t = ((x0 - x1) * dx + (y0 - y1) * dy) / (dx * dx + dy * dy)

    t = max(0, min(1, t))

    proj_x = x1 + t * dx
    proj_y = y1 + t * dy

    return sqrt((x0 - proj_x) ** 2 + (y0 - proj_y) ** 2)


x0, y0, x1, y1, x2, y2 = map(int, input().split())
print(f"{distance_point_to_segment(x0, y0, x1, y1, x2, y2):.20f}")