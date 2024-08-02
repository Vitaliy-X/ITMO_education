def check_same_side(px1, py1, px2, py2, a, b, c):
    eval1 = a * px1 + b * py1 + c
    eval2 = a * px2 + b * py2 + c

    return eval1 * eval2 > 0


point1_x, point1_y, point2_x, point2_y, coeff_a, coeff_b, coeff_c = map(int, input().split())

print("YES") if check_same_side(point1_x, point1_y, point2_x, point2_y, coeff_a, coeff_b, coeff_c) else print("NO")
