def func(start):
    global ans, n
    if len(start) != n:
        func(start + '0')
        if len(start) == 0:
            func('1')
        elif start[-1] != '1':
            func(start + '1')
    else:
        ans.append(start)


n = int(input())
ans = []
func("")

print(len(ans))
for i in ans:
    print(i)