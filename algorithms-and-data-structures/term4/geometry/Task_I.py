def check_convexity(points):
    def compute_orientation(a, b, c):
        return (b[0] - a[0]) * (c[1] - b[1]) - (b[1] - a[1]) * (c[0] - b[0])

    num_points = len(points)
    if num_points < 3:
        return False

    initial_sign = None
    for i in range(num_points):
        a, b, c = points[i], points[(i + 1) % num_points], points[(i + 2) % num_points]
        orient = compute_orientation(a, b, c)
        if orient != 0:
            if initial_sign is None:
                initial_sign = orient
            elif orient * initial_sign < 0:
                return False

    return True


n = int(input())
vertices = [tuple(map(int, input().split())) for _ in range(n)]

print("YES") if check_convexity(vertices) else print("NO")
