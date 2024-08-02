s = input()
num = len(s) - 1
keys = list(s)

while num >= 0:
    if s[num] == '1':
        break
    keys[num] = 1
    num -= 1

def func():
    global num, keys
    if num < 0:
        print('-')
    else:
        keys[num] = 0
        print(*keys, sep='')
func()

num = len(s) - 1
t = list(s)

while num >= 0:
    if s[num] == '0':
        break
    t[num] = 0
    num -= 1

def fun():
    global num, t
    if num < 0:
        print('-')
    else:
        t[num] = 1
        print(*t, sep='')
fun()