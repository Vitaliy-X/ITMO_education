def read_input_file():
    with open('cf.in', 'r') as file:
        rule_count, start_symbol = file.readline().split()
        rule_count = int(rule_count)
        rules = []
        epsilon_rules = set()
        alphabet_count = 26

        def get_symbol_rhs(symbol):
            for rule in rules:
                if len(rule[1]) == 1 and rule[1] == symbol:
                    return rule[0]
            return None

        for _ in range(rule_count):
            line = file.readline().split()
            lhs, rhs = line[0], ''
            if len(line) == 3:
                rhs = line[2]
            if len(rhs) == 0:
                rules.append((lhs, ''))
                epsilon_rules.add(lhs)
            elif rhs.isupper():
                rules.append((lhs, list(rhs)))
            elif rhs.islower() and len(rhs) == 1:
                rules.append((lhs, rhs))
            else:
                rhs_symbols = [symbol if not symbol.islower() else get_symbol_rhs(symbol) or (rules.append((chr(alphabet_count + ord('A')), symbol)), chr(alphabet_count + ord('A')))[1] for symbol in rhs]
                alphabet_count += len([symbol for symbol in rhs if symbol.islower()])
                rules.append((lhs, rhs_symbols))

        word = file.readline().strip()
        return rules, start_symbol, epsilon_rules, alphabet_count, word


def expand_rules(rules, counter):
    while True:
        stop_expanding = True
        for i in range(len(rules)):
            if len(rules[i][1]) >= 3:
                rules.append((chr(counter + ord('A')), rules[i][1][1:]))
                rules[i] = (rules[i][0], [rules[i][1][0], chr(counter + ord('A'))])
                counter += 1
                stop_expanding = False
                break
        if stop_expanding:
            break
    return rules, counter


def find_epsilons(rules, epsilons):
    while True:
        initial_epsilons = len(epsilons)
        for rule in rules:
            if isinstance(rule[1], list):
                for symbol in rule[1]:
                    if symbol in epsilons:
                        epsilons.add(rule[0])
        if len(epsilons) == initial_epsilons:
            break
    return epsilons


def remove_unit_productions(rules, epsilons):
    while True:
        stop_removing = True
        for i in range(len(rules)):
            if len(rules[i][1]) == 2:
                for j in range(2):
                    if rules[i][1][j] in epsilons:
                        new_rhs = list(rules[i][1])
                        new_rhs.pop(j)
                        new_rule = (rules[i][0], new_rhs)
                        if new_rule not in rules:
                            rules.append(new_rule)
                            stop_removing = False
                            break
        if stop_removing:
            break
    return rules


def generate_new_rules(rules, removed_indices, temp_values):
    new_rules = []
    for idx in range(len(rules)):
        if (idx not in removed_indices) and isinstance(rules[idx][1], list) and len(rules[idx][1]) == 1:
            new_rhs = process(rules[idx][1][0], rules, removed_indices, temp_values)
            removed_indices.append(idx)
            for value in new_rhs:
                new_rules.append([rules[idx][0], value])

    removed_indices.sort()
    removed_indices.reverse()

    for index in removed_indices:
        rules.pop(index)

    for rule in new_rules:
        if rule not in rules:
            rules.append(rule)

    filtered_rules = rules.copy()
    for rule in filtered_rules:
        if len(rule[1]) == 0:
            rules.remove(rule)
    return rules


def process(non_terminal, rules, removed_indices, temp_values):
    if temp_values[ord(non_terminal) - ord('A')]:
        return []
    new_rhs = []
    temp_values[ord(non_terminal) - ord('A')] = 1
    for idx in range(len(rules)):
        if idx not in removed_indices:
            if rules[idx][0] == non_terminal:
                if not isinstance(rules[idx][1], str) and len(rules[idx][1]) != 2:
                    new_rhs.extend(process(rules[idx][1][0], rules, removed_indices, temp_values))
                else:
                    new_rhs.append(rules[idx][1])
    temp_values[ord(non_terminal) - ord('A')] = 0
    return new_rhs


def create_binary_and_terminal_rules(rules, total_rules):
    binary_rules = [[] for _ in range(total_rules)]
    terminal_rules = [[] for _ in range(total_rules)]
    for rule in rules:
        if not isinstance(rule[1], str):
            binary_rules[ord(rule[0]) - ord('A')].append(rule[1])
        else:
            if rule[1] not in terminal_rules[ord(rule[0]) - ord('A')]:
                terminal_rules[ord(rule[0]) - ord('A')].append(rule[1])
    return binary_rules, terminal_rules


def compute_probabilities(w, binary_rules, terminal_rules, total_rules):
    probs = [[[0 for _ in range(len(w))] for _ in range(len(w))] for _ in range(total_rules)]
    for seq in range(len(w)):
        for idx in range(total_rules):
            for term_rule in terminal_rules[idx]:
                if term_rule == w[seq]:
                    probs[idx][seq][seq] = 1

    for length in range(1, len(w)):
        for start in range(len(w) - length):
            for i in range(total_rules):
                result_flag = 0
                for bin_rule in binary_rules[i]:
                    left, right = bin_rule
                    for k in range(start, start + length):
                        left_idx = ord(left) - ord('A')
                        right_idx = ord(right) - ord('A')
                        if probs[left_idx][start][k] * probs[right_idx][k + 1][start + length] > 0:
                            result_flag = 1
                probs[i][start][start + length] = int(probs[i][start][start + length] or result_flag)
    return probs


def save_result_to_file(result):
    with open('cf.out', 'w') as out:
        if not result:
            out.write('no\n')
        else:
            out.write('yes\n')


def convert_parsed_rules(rules):
    parsed_rules = []
    for rule in rules:
        left = rule[0]
        right = rule[1]
        if isinstance(right, list):
            right = "".join(right)
        parsed_rules.append((left, right))
    return parsed_rules


rules, start, eps, counter, w = read_input_file()
rules, counter = expand_rules(rules, counter)
eps = find_epsilons(rules, eps)
rules = remove_unit_productions(rules, eps)
deleted_rules = []
visited = [0 for _ in range(counter)]
rules = generate_new_rules(rules, deleted_rules, visited)
binary_rules, terminal_rules = create_binary_and_terminal_rules(rules, counter)
probs = compute_probabilities(w, binary_rules, terminal_rules, counter)
start_idx = ord(start) - ord('A')
result = probs[start_idx][0][len(w) - 1]
save_result_to_file(result)
