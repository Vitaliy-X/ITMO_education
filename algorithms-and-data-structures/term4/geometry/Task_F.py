import math


def compute_distance(point1, point2):
    return math.sqrt((point1[0] - point2[0]) ** 2 + (point1[1] - point2[1]) ** 2)


def andrew_scan(points):
    points = sorted(set(points))

    if len(points) <= 1:
        return points

    def cross(o, a, b):
        return (a[0] - o[0]) * (b[1] - o[1]) - (a[1] - o[1]) * (b[0] - o[0])

    lower = []
    for p in points:
        while len(lower) >= 2 and cross(lower[-2], lower[-1], p) <= 0:
            lower.pop()
        lower.append(p)

    upper = []
    for p in reversed(points):
        while len(upper) >= 2 and cross(upper[-2], upper[-1], p) <= 0:
            upper.pop()
        upper.append(p)

    return lower[:-1] + upper[:-1]


num_points = int(input())
points_list = [tuple(map(int, input().split())) for _ in range(num_points)]

convex_hull_points = andrew_scan(points_list)

perimeter_length = 0
for i in range(len(convex_hull_points)):
    perimeter_length += compute_distance(convex_hull_points[i], convex_hull_points[(i + 1) % len(convex_hull_points)])
perimeter_result = perimeter_length

print(perimeter_result)
