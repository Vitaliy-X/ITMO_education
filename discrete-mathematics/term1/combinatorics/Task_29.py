s = input()
left, right = s.split("=")
arr = [int(i) for i in right.split("+")]
l = len(arr)

if l == 1:
    print("No solution")

else:
    if arr[l - 1] - 1 >= arr[l - 2] + 1:
        arr[l - 1] -= 1
        arr[l - 2] += 1

        while arr[l - 2] + arr[l - 2] <= arr[l - 1]:
            arr.append(arr[l - 1] - arr[l - 2])
            arr[l - 1] = arr[l - 2]
            l += 1

    else:
        arr[l - 2] += arr[l - 1]
        l -= 1

    print(left, end="=")
    print(*arr[:l], sep="+")