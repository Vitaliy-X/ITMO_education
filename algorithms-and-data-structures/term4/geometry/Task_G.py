def calculate_polygon_area(points):
    num_points = len(points)
    total_area = 0
    for i in range(num_points):
        x1, y1 = points[i]
        x2, y2 = points[(i + 1) % num_points]
        total_area += x1 * y2 - x2 * y1
    return abs(total_area) / 2


num_vertices = int(input())
polygon_points = [tuple(map(int, input().split())) for _ in range(num_vertices)]

polygon_area = calculate_polygon_area(polygon_points)

print(polygon_area)