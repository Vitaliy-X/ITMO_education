def belongs_to_segment(x0, y0, A0, B0, A1, B1):
    def is_point_on_line(x0, y0, A0, B0, A1, B1):
        return (x0 - A0) * (B1 - B0) == (y0 - B0) * (A1 - A0)

    def is_point_between(x0, y0, A0, B0, A1, B1):
        return min(A0, A1) <= x0 <= max(A0, A1) and min(B0, B1) <= y0 <= max(B0, B1)

    if is_point_on_line(x0, y0, A0, B0, A1, B1) and is_point_between(x0, y0, A0, B0, A1, B1):
        return "YES"
    else:
        return "NO"


x0, y0, A0, B0, A1, B1 = map(int, input().split())
print(belongs_to_segment(x0, y0, A0, B0, A1, B1))