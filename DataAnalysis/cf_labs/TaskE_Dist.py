def read_input():
    k = int(input())
    n = int(input())
    class_objs = {}
    obj_pairs = []

    for _ in range(n):
        x, y = map(int, input().split())
        if y not in class_objs:
            class_objs[y] = []
        class_objs[y].append(x)
        obj_pairs.append((x, y))

    return k, class_objs, obj_pairs


def calculate_sums_and_counts(k, class_objs):
    sums = [0] * k
    counts = [0] * k
    for i in range(k):
        if i + 1 in class_objs:
            sums[i] = sum(class_objs[i + 1])
            counts[i] = len(class_objs[i + 1])
    return sums, counts


def calculate_intraclass_distances(k, class_objs, sums):
    intra_dist = 0
    for i in range(k):
        if i + 1 in class_objs:
            obj_list = class_objs[i + 1]
            obj_list.sort()
            intra_dist += intra_class_dist(obj_list, sums[i])
    return intra_dist


def intra_class_dist(obj_list, total_sum):
    dist = 0
    rem_sum = total_sum
    acc_sum = 0
    num_elems = len(obj_list)
    for i in range(num_elems):
        val = obj_list[i]
        rem_sum -= val
        dist += (i - num_elems + 1 + i) * val + (rem_sum - acc_sum)
        acc_sum += val
    return dist


def interclass_distances(obj_pairs, sums, counts, k):
    dist = 0
    new_sums = [0] * k
    new_counts = [0] * k
    total = sum(sums)
    acc_sum = 0
    num_pairs = len(obj_pairs)
    obj_pairs.sort()
    for i in range(num_pairs):
        x, y = obj_pairs[i]
        y -= 1
        total -= x
        sums[y] -= x
        counts[y] -= 1
        dist += (2 * i - new_counts[y] - num_pairs + 1 + counts[y]) * x + (total - sums[y]) - (acc_sum - new_sums[y])
        acc_sum += x
        new_sums[y] += x
        new_counts[y] += 1
    return dist


def main():
    k, class_objs, obj_pairs = read_input()

    sums, counts = calculate_sums_and_counts(k, class_objs)
    intra_dist = calculate_intraclass_distances(k, class_objs, sums)
    inter_dist = interclass_distances(obj_pairs, sums, counts, k)

    print(intra_dist)
    print(inter_dist)


if __name__ == "__main__":
    main()
