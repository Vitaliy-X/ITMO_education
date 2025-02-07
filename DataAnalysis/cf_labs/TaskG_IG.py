class SegmentTree:
    def __init__(self, size):
        self.size = size
        self.tree = (2 * size + 1) * [0]

    def update(self, index, delta):
        index += self.size
        self.tree[index] += delta
        while index > 1:
            index //= 2
            self.tree[index] = self.tree[2 * index] + self.tree[2 * index + 1]

    def query(self, start, end):
        total = 0
        start += self.size
        end += self.size
        while start < end:
            if start % 2 == 1:
                total += self.tree[start]
                start += 1
            if end % 2 == 1:
                end -= 1
                total += self.tree[end]
            start //= 2
            end //= 2
        return total


def calculate_gini_index(n, k, cls):
    left_tree = SegmentTree(k + 1)
    right_tree = SegmentTree(k + 1)

    tot_cnt = [0] * (k + 1)
    for c in cls:
        tot_cnt[c] += 1
        right_tree.update(c, 1)

    l_size = 0
    r_size = n

    gini_vals = []

    for i in range(n - 1):
        c = cls[i]
        l_size += 1
        r_size -= 1

        left_tree.update(c, 1)
        right_tree.update(c, -1)

        gini_l = 1.0 - sum((left_tree.query(j, j + 1) / l_size) ** 2 for j in range(1, k + 1))
        gini_r = 1.0 - sum((right_tree.query(j, j + 1) / r_size) ** 2 for j in range(1, k + 1))

        gini_split = (l_size / n) * gini_l + (r_size / n) * gini_r
        gini_vals.append(gini_split)

    return gini_vals


if __name__ == "__main__":
    n, k = map(int, input().split())
    cls = list(map(int, input().split()))
    gini_vals = calculate_gini_index(n, k, cls)
    for gini in gini_vals:
        print(gini)
