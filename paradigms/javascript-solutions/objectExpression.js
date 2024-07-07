"use strict";

function Operation(f, operation, operands) {
    this.operands = operands;
    this.operation = operation;
    this.function = f;
}

Operation.prototype.evaluate = function (...args) {
    const evaluatedOperands = this.operands.map(operand => operand.evaluate(...args));
    return this.function(...evaluatedOperands);
};

Operation.prototype.toString = function () {
    return this.operands.map(operand => operand.toString()).join(' ') + ' ' + this.operation;
};

Operation.prototype.prefix = function () {
    return "(" + this.operation + " " + this.operands.map(operation => operation.prefix()).join(" ") + ")";
};

function Const(el) {
    this.value = el;
}
Const.prototype.evaluate = function () {
    return this.value;
};
Const.prototype.toString = function () {
    return this.value.toString();
};
Const.prototype.prefix = function () {
    return this.value.toString();
};


const variables = ['x', 'y', 'z'];
function Variable(el) {
    this.index = variables.indexOf(el);
}
Variable.prototype.evaluate = function () {
    return arguments[this.index];
};
Variable.prototype.toString = function () {
    return variables[this.index];
};
Variable.prototype.prefix = function () {
    return variables[this.index];
};


// объект operations будет заполнен автоматически при вызове функции create
const operations = {};
function create(f, operation, minNumOfArgs) {
    function Class(...operands) {
        return new Operation(f, operation, operands)
    }
    Class.prototype = Object.create(Operation.prototype);
    Class.constructor = Class;

    // добавляем созданную функцию и количество операндов в объект operations
    operations[operation] = [Class, minNumOfArgs];

    return Class;
}

const Add = create((lhs, rhs) => lhs + rhs, "+", 2);
const Multiply = create((lhs, rhs) => lhs * rhs, "*", 2);
const Divide = create((lhs, rhs) => lhs / rhs, "/", 2);
const Subtract = create((lhs, rhs) => lhs - rhs, "-", 2);
const Negate = create(el => -el, "negate", 1);
const Exp = create(Math.exp, "exp", 1);
const Ln = create(Math.log, "ln", 1);
const Sum = create(function(...args) {
    let sum = 0;
    for (let i = 0; i < args.length; i++) {
        sum += args[i];
    }
    return sum;
}, "sum", Infinity);
const Avg = create((...args) => args.reduce((acc, val) => acc + val, 0) / args.length, "avg", Infinity);


function isDigit(el) {
    // (/^-?\d+$/.test(el));
    return Boolean(el >= '0' && el <= '9');
}

function isVariable(el) {
    // (/^[xyz]$/.test(el))
    return Boolean(el === "x" || el === "y" || el === "z")
}

function parse(exp) {
    const stack = [];
    for (const element of exp.trim().split(' ')) {
        if (element in operations) {
            const args = [];
            for (let j = 0; j < operations[element][1]; j++) {
                args.push(stack.pop());
            }
            args.reverse();
            stack.push(new operations[element][0](...args));
        } else if (isVariable(element)) {
            stack.push(new Variable(element));
        } else if ((isDigit(element[0]) || (element[0] === '-' && element.length !== 1)) && typeof element === 'string') {
            stack.push(new Const(parseInt(element)));
        }
    }
    return stack.pop();
}


const creatException = function (massage) {
    const Exception = function (index) {
        this.name = massage + " on position " + index;
    };
    Exception.prototype = new Error();
    return Exception;
};

const MissingClosingBracketException = creatException('Close bracket expected');
const MissingOpeningBracketException = creatException('Open bracket expected');
const ExtraArgumentsException = creatException('End of expression expected');
const MissingOperationException = creatException('Operation symbol or function expected');
const UnknownObjectException = creatException('Undefined object');
const UnknownOperationException = creatException('Undefined operation');
const EmptyExpressionException = creatException('Expression is empty');
const NumberOperandsException = creatException('Wrong number of arguments');


function parsePrefix(string) {
    let position = 0;
    if (string.length === 0) {
        throw new EmptyExpressionException(position);
    }
    const EOF = "\0";
    const result = parse();
    skip();
    if (!test(EOF)) {
        throw new ExtraArgumentsException(position);
    }
    return result;

    function parse() {
        const element = parseNext();
        if (element === '(') {
            const res = parseOperation();
            skip();
            if (!test(")")) {
                throw new MissingClosingBracketException(position);
            }
            return res;
        } else if (element === ")") {
            throw new MissingOpeningBracketException(position);
        } else if (!isNaN(element)) {
            return new Const(+element);
        } else if (isVariable(element)) {
            return new Variable(element);
        }
        throw new UnknownObjectException(position);
    }

    function getSym() {
        return position < string.length ? string.charAt(position) : EOF;
    }

    function test(el) {
        return getSym() === el && (position++, true);
    }

    function skip() {
        while (test(' ')) {}
    }

    function previewToken() {
        const prevPosition = position;
        const res = parseNext();
        position = prevPosition;
        return res;
    }

    function parseNext() {
        skip();
        let res = string.charAt(position++);
        while ([' ', EOF, '(', ')'].indexOf(getSym()) === -1 && ['(', ')'].indexOf(res) === -1) {
            res += string.charAt(position++);
        }
        skip();
        return res;
    }

    function parseArgs() {
        const args = [];
        while (!test(EOF) && getSym() !== ")") {
            args.push(parse());
            skip();
        }
        if (previewToken() in operations) {
            throw new UnknownOperationException(position);
        }
        return args;
    }

    function parseOperation() {
        const op = parseNext();
        const [operationFunc, operationArgsNum] = operations[op] || [];
        const args = operationFunc && parseArgs();
        if (!operationFunc) {
            throw new MissingOperationException(position);
        }
        if (args.length !== operationArgsNum && operationArgsNum !== Infinity) {
            throw new NumberOperandsException(position);
        }
        return new operationFunc(...args);
    }
}