import math


def func(var):
    global n, i, tested, k
    if len(var) != n:
        for i in range(1, n + 1):
            num = math.factorial(n - len(var) - 1)
            if i in tested:
                continue
            if k >= num:
                k -= num
            else:
                tested.append(i)
                var.append(i)
                return func(var)
    else:
        return var


string = input()
lis = string.split()
n, k = int(lis[0]), int(lis[1])
tested = []
answer = []
i = 0
while i < n:
    if math.factorial(n - 1) > k:
        tested.append(i + 1)
        answer = func([i + 1])
        break
    else:
        k -= math.factorial(n - 1)
    i+=1
print(*answer)