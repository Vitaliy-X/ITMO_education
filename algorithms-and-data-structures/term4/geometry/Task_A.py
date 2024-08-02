def line_equation(x1, y1, x2, y2):
    A = y2 - y1
    B = x1 - x2
    C = x2 * y1 - x1 * y2
    return A, B, C


x1, y1, x2, y2 = map(int, input().split(' '))
A, B, C = line_equation(x1, y1, x2, y2)
print(-A, -B, -C)
