import copy


def func(num):
    print(*num)

    for j in range(n):
        if j + 1 > num[-1]:
            new_res = copy.deepcopy(num)
            new_res.append(j + 1)
            func(new_res)


n = int(input())
print()
for i in range(n):
    func([i+1])