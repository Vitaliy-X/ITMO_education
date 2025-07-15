import numpy as np


def read_matrix(rows):
    matrix = []
    for _ in range(rows):
        matrix.append(list(map(int, input().split())))
    return np.array(matrix)


def build_system(inp, out):  # with convolutions
    n, m = inp.shape[0], out.shape[0]
    k = n - m + 1
    s_matrix = []
    for i in range(k * k):
        kernel_basis = np.zeros((k, k))
        kernel_basis.flat[i] = 1
        temp = np.zeros((m, m))
        for dx in range(m):
            for dy in range(m):
                temp[dx, dy] = np.sum(inp[dx:dx + k, dy:dy + k] * kernel_basis)
        s_matrix.append(temp.flatten())
    return np.array(s_matrix).T


def main():
    n, m = map(int, input().split())
    mat_A = read_matrix(n)
    mat_B = read_matrix(m)
    k = n - m + 1

    cf = build_system(mat_A, mat_B)
    target = mat_B.flatten()

    res, *_ = np.linalg.lstsq(cf, target, rcond=None)
    kernel = res.reshape((k, k))

    for row in kernel:
        for i in row:
            print(f"{i}", end=" ")
        print()


if __name__ == "__main__":
    main()
