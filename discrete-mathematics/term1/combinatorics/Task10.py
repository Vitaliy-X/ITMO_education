def func(n, i, el):
    if n != 0:
        for j in range(i, n + 1):
            func(n - j, j, '{}+{}'.format(el, j))
    else:
        print(el)


n = int(input())
for i in range(1, n+1):
    func(n - i, i, '{}'.format(i))