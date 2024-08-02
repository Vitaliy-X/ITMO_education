import math


def dis(el, n):
    a1 = []
    a2 = []
    a3 = []
    for ignore in range(n):
        a1.append(el % 3)
        a2.append((el + 1) % 3)
        a3.append((el + 2) % 3)
        el = el // 3
    print(*a1, sep="")
    print(*a2, sep="")
    print(*a3, sep="")


n = int(input())
a = []
for el in range(int(math.pow(3, n - 1))):
    dis(el, n)