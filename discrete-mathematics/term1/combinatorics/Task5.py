lis = input().split()
n = int(lis[0])
k = int(lis[1])


def telem(p):
    if p == 1:
        return [str(j) for j in range(k)]
    select = telem(p - 1)
    der = select[::-1]
    res = []
    i = 0
    while i < k:
        if i % 2 == 1:
            for j in der:
                res.append(str(i) + str(j))
        else:
            for j in select:
                res.append(str(i) + str(j))
        i += 1
    return res


res = telem(n)
for el in res:
    print(*el[::-1], sep='')