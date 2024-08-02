n = int(input())

base = ["0", "1"]
codes = base

for i in range(1, n):
    for j in range(len(base) - 1, -1, -1):
        codes.append(base[j])

    for j in range(len(codes) // 2):
        codes[j] = "0{}".format(codes[j])

    for j in range(len(codes) // 2, len(codes)):
        codes[j] = "1{}".format(codes[j])

    base = codes

for el in codes:
    print(el)
