import math


def func(var):
    global a, n, i, tested
    num = 0
    if var in tested:
        return 0
    if i + 1 == n:
        return 1
    j = 1
    while j < n + 1:
        if j in tested:
            continue
        if j < var:
            num += math.factorial(n - i - 1)
        elif j == var:
            tested.append(int(a[i]))
            i += 1
            return func(int(a[i])) + num
        else:
            return 0
        j += 1


n = int(input())
a = list(input().split())
tested = []
i = 0
answer = func(int(a[0]))
print(answer - 1)