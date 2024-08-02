def func(el, op, cl):
    global n
    if cl + op != n * 2:
        if op < n:
            func(el + '(', op + 1, cl)
        if op > cl:
            func(el + ')', op, cl + 1)
    else:
        print(el)


n = int(input())
func("(", 1, 0)