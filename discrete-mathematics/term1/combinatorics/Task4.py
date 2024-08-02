n = int(input())
mn = {}
el = '0' * n
mn[el] = 1
print(el)

while True:
    res = '{}1'.format(el[1:])
    if res in mn:
        res = '{}0'.format(el[1:])
        if res in mn:
            break
        else:
            el = res
            print(res)
    else:
        mn[res] = 1
        el = res
        print(res)