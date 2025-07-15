AVAILABLE = 512


def use_dnf(n, num_vars, t_values):
    rows = []
    for idx in range(n):
        if t_values[idx] == 1:
            weights = []
            for j in range(num_vars):
                if (idx >> j) & 1 == 1:
                    weights.append(1.0)
                else:
                    weights.append(-1.0)
            bias = -(bin(idx).count("1") - 0.5)
            rows.append((weights, bias))
    if len(rows) != 0:
        print(2)
        print(f"{len(rows)} 1")
        # first layer
        for weights, bias in rows:
            for i in range(len(weights)):
                print(str(weights[i]), end="")
                if i != len(weights) - 1:
                    print(" ", end="")
            print("", bias)
        # Second layer
        for i in range(len(rows)):
            print("1.0", end="")
            if i != len(rows) - 1:
                print(" ", end="")
        print(" -0.5")
    else:
        print("1")
        print("1")
        for i in range(num_vars):
            print("1.0", end="")
            if i != num_vars - 1:
                print(" ", end="")
        print(" -0.5")


def use_knf(n, num_vars, t_values):
    rows = []
    for idx in range(n):
        if t_values[idx] == 0:
            weights = []
            for j in range(num_vars):
                if (idx >> j) & 1 == 1:
                    weights.append(-1.0)
                else:
                    weights.append(1.0)
            bias = bin(idx).count("1") - 0.5
            rows.append((weights, bias))
    print(2)
    print(f"{len(rows)} 1")
    # first layer
    for weights, bias in rows:
        for i in range(len(weights)):
            print(str(weights[i]), end="")
            if i != len(weights) - 1:
                print(" ", end="")
        print("", bias)
    # Second layer
    for i in range(len(rows)):
        print("1.0", end="")
        if i != len(rows) - 1:
            print(" ", end="")
    print(" ", end="")
    print(-len(rows) + 0.5)


def main():
    num_vars = int(input())
    n = 2 ** num_vars
    t_values = []
    for _ in range(n):
        t_values.append(int(input()))
    total = sum(t_values)

    if total > AVAILABLE:
        use_knf(n, num_vars, t_values)
    else:
        use_dnf(n, num_vars, t_values)


if __name__ == "__main__":
    main()
