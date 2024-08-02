import copy


def func(var):
    global n, k
    if len(var) == k:
        print(*var)
    else:
        for i in range(1, n + 1):
            if i > int(var[-1]) and n - i >= k - len(var) - 1:
                new_res = copy.deepcopy(var)
                new_res.append(i)
                func(new_res)


string = input()
lis = string.split()
n, k = int(lis[0]), int(lis[1])
for i in range(1, n+1):
    func([i])