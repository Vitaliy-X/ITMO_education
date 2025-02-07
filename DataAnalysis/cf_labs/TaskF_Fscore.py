def calculate_f_measures():
    k = int(input())
    cm = [list(map(int, input().split())) for _ in range(k)]
    tp, fp, fn, total = calculate_confusion(cm, k)
    micro_f, macro_f, f = calculate_f_scores(fn, tp, fp, total, k)

    return micro_f, macro_f, f


def calculate_confusion(cm, k):
    tp, fp, fn = [0] * k, [0] * k, [0] * k
    total = 0

    for y in range(k):
        for x in range(k):
            c = cm[y][x]
            total += c
            if y == x:
                tp[y] = c
            else:
                fn[y] += c
                fp[x] += c

    return tp, fp, fn, total


def calculate_f_scores(fn, tp, fp, total, k):
    micro_tp = get_micro(fn, tp, tp, total, k)
    micro_fp = get_micro(fn, tp, fp, total, k)
    micro_fn = get_micro(fn, tp, fn, total, k)
    macro_precision = get_macro(fn, tp, tp, fp, total, k)
    macro_recall = get_macro(fn, tp, tp, fn, total, k)
    f = get_F(fn, tp, fp, total, k)
    micro_precision = get_precision_or_recall(micro_tp, micro_fp)
    micro_recall = get_precision_or_recall(micro_tp, micro_fn)
    micro_f = get_measure(micro_precision, micro_recall)
    macro_f = get_measure(macro_precision, macro_recall)

    return micro_f, macro_f, f


def get_precision_or_recall(left, right):
    return left / (left + right) if (left + right) > 0 else 0


def get_measure(precision, recall):
    return 2 * precision * recall / (precision + recall) if (precision + recall) > 0 else 0


def get_micro(fn, tp, ms, total, k):
    m_tp = sum(ms[i] * (fn[i] + tp[i]) for i in range(k))
    return m_tp / total


def get_macro(fn, tp, ms, ns, total, k):
    macro = sum(get_precision_or_recall(ms[i], ns[i]) * (fn[i] + tp[i]) for i in range(k))
    return macro / total


def get_F(fn, tp, fp, total, k):
    f = sum(get_measure(get_precision_or_recall(tp[i], fp[i]), get_precision_or_recall(tp[i], fn[i])) * (fn[i] + tp[i])
            for i in range(k))
    return f / total


def main():
    micro_f, macro_f, f = calculate_f_measures()
    print(micro_f)
    print(macro_f)
    print(f)


if __name__ == "__main__":
    main()
