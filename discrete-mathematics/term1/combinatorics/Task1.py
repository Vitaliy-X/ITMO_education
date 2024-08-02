def func(begin):
    global n
    if len(begin) == n:
        print(begin)
    else:
        func("{0}0".format(begin))
        func("{0}1".format(begin))


n = int(input())
func('')