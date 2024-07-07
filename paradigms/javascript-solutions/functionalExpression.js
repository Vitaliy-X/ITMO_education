"use strict";

const cnst = (value) => () => value;
const higherOrder = operation => (...args) => (...values) => operation(...args.map(op => op(...values)));

const argFun = (f) => higherOrder((...args) => args.indexOf(f(...args)));
// const argMin3 = argFun(Math.min);
// const argMax3 = argFun(Math.max);
// const argMin5 = argFun(Math.min);
// const argMax5 = argFun(Math.max);
const [argMin3, argMax3, argMin5, argMax5] = Array(4).fill().map((_, i) => argFun(Math[i % 2 ? 'max' : 'min']));

const add = higherOrder((lhs, rhs) => lhs + rhs);
const subtract = higherOrder((lhs, rhs) => lhs - rhs);
const multiply = higherOrder((lhs, rhs) => lhs * rhs);
const divide = higherOrder((lhs, rhs) => lhs / rhs);

const negate = higherOrder(element => -element);
const sin = higherOrder(element => Math.sin(element));
const cos = higherOrder(element => Math.cos(element));

const one = cnst(1);
const two = cnst(2);

const variable = name => (x, y, z) => {
    switch (name) {
        case "x":
            return x;
        case "y":
            return y;
        case "z":
            return z;
    }
}

function isDigit(el) {
    // (/^-?\d+$/.test(el));
    return Boolean(el >= '0' && el <= '9');
}

function isVariable(el) {
    // (/^[xyz]$/.test(el))
    return Boolean(el === "x" || el === "y" || el === "z")
}

let operations = {
    '+': [add, 2],
    '-': [subtract, 2],
    '*': [multiply, 2],
    '/': [divide, 2],
    'negate': [negate, 1],
    'sin': [sin, 1],
    'cos': [cos, 1],
    'argMin3': [argMin3, 3],
    'argMax3': [argMax3, 3],
    'argMin5': [argMin5, 5],
    'argMax5': [argMax5, 5],
};

let constants = {
    'one': one,
    'two': two,
};

function parse(expr) {
    const stack = []
    for (let element of expr.split(' ')) {
        if (element in operations) {
            // :NOTE: без for
            const args = stack.slice(-operations[element][1]).map(_ => stack.pop()).reverse();
            // :NOTE: без apply
            stack.push(operations[element][0](...args));
        }  else if (isVariable(element)) {
            stack.push(variable(element));
        } else if (element in constants) {
            stack.push(constants[element]);
        } else if ((isDigit(element[0]) || (element[0] === '-' && element.length !== 1)) && typeof element === 'string') {
            stack.push(cnst(parseInt(element)));
        }
    }
    return stack.pop();
}


// let expression =
//     add(
//         subtract(
//             multiply(
//                 variable("x"),
//                 variable("x")),
//             multiply(
//                 cnst(2),
//                 variable("x"))
//         ),
//         cnst(1)
//     );
// console.log("expression = (x^2 - 2x + 1):")
// for (let x = 0; x < 10; x++) {
//     console.log("expression(", + x + " ) = " + expression(x, 0, 0))
// }