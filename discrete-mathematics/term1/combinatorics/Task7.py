def func(num):
    global n
    if len(num) != n:
        for i in range(1, n+1):
            if '{}'.format(i) not in num:
                func(num + '{}'.format(i))
    else:
        print(*num)


n = int(input())
for i in range(1, n+1):
    func('{}'.format(i))