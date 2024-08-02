import sys

string = input()
lis = string.split()
n, k = int(lis[0]), int(lis[1])

arr = list(input().split())

arr.append(n + 1)
flag = True

for i in range(k-1, -1, -1):
    if int(arr[i + 1]) - int(arr[i]) >= 2:
        flag = False
        arr[i] = int(arr[i]) + 1
        for j in range(i+1, k):
            arr[j] = int(arr[j - 1]) + 1
        arr.pop()
        break
if flag:
    print(-1)
    sys.exit(0)
print(*arr)