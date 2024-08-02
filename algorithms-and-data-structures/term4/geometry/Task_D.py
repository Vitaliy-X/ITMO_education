def point_in_polygon(px, py, polygon):
    global x_intersect
    num_vertices = len(polygon)
    is_inside = False

    x_old, y_old = polygon[0]
    for i in range(1, num_vertices + 1):
        x_new, y_new = polygon[i % num_vertices]
        if py > min(y_old, y_new):
            if py <= max(y_old, y_new):
                if px <= max(x_old, x_new):
                    if y_old != y_new:
                        x_intersect = (py - y_old) * (x_new - x_old) / (y_new - y_old) + x_old
                    if x_old == x_new or px <= x_intersect:
                        is_inside = not is_inside
        x_old, y_old = x_new, y_new

    return is_inside


def point_on_polygon_edge(px, py, polygon):
    num_vertices = len(polygon)
    for i in range(num_vertices):
        x_start, y_start = polygon[i]
        x_end, y_end = polygon[(i + 1) % num_vertices]
        if (min(x_start, x_end) <= px <= max(x_start, x_end) and
                min(y_start, y_end) <= py <= max(y_start, y_end) and
                (x_end - x_start) * (py - y_start) == (y_end - y_start) * (px - x_start)):
            return True
    return False


num_vertices, point_x, point_y = map(int, input().split())
polygon_vertices = [tuple(map(int, input().split())) for _ in range(num_vertices)]

if point_in_polygon(point_x, point_y, polygon_vertices) or point_on_polygon_edge(point_x, point_y, polygon_vertices):
    print("YES")
else:
    print("NO")
