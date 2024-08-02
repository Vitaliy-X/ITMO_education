MODULE = 1000000007


def read_input(input_file):
    with open(input_file, 'r') as file:
        num_rules, start_symbol = file.readline().split()
        num_rules = int(num_rules)
        rules_2, rules_1 = {chr(i): [] for i in range(ord('A'), ord('Z') + 1)}, {chr(i): [] for i in
                                                                                 range(ord('A'), ord('Z') + 1)}

        for _ in range(num_rules):
            a, b, c = file.readline().split()
            if len(c) == 2:
                rules_2[a].append(c)
            else:
                rules_1[a].append(c)

        word = file.readline().strip()

    return start_symbol, rules_2, rules_1, word


def initialize_dp_matrix(word, rules_1):
    dp = {a: [[0 for _ in range(len(word))] for _ in range(len(word))] for a in rules_1}
    for s, char in enumerate(word):
        for a, rule_list in rules_1.items():
            for rule in rule_list:
                if rule == char:
                    dp[a][s][s] = 1
    return dp


def compute_dp_matrix(dp, word, rules_2):
    for l in range(1, len(word)):
        for s in range(len(word) - l):
            for a, rule_list in rules_2.items():
                partial_sum = 0
                for rule in rule_list:
                    rb, rc = ord(rule[0]) - 65, ord(rule[1]) - 65
                    for k in range(s, s + l):
                        partial_sum = (partial_sum + (
                                dp[chr(rb + 65)][s][k] * dp[chr(rc + 65)][k + 1][s + l]) % MODULE) % MODULE
                dp[a][s][s + l] = partial_sum


def write_output(output_file, result):
    with open(output_file, 'w') as out:
        out.write(str(result))


start_symbol, rules_2, rules_1, word = read_input('nfc.in')
dp = initialize_dp_matrix(word, rules_1)
compute_dp_matrix(dp, word, rules_2)
result = dp[start_symbol][0][len(word) - 1]
write_output('nfc.out', result)
