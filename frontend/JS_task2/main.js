function calc(startValue) {
    if (typeof startValue !== 'number') {
        throw new Error("the argument is not a number");
    }

    function recursiveCalc(accumulatedValue) {
        function performOperation(operator, operand) {
            if (typeof operand !== 'number') {
                throw new Error("the argument is not a number");
            }

            let updatedValue = accumulatedValue;

            switch (operator) {
                case '+':
                    updatedValue += operand;
                    break;
                case '-':
                    updatedValue -= operand;
                    break;
                case '*':
                    updatedValue *= operand;
                    break;
                case '/':
                    updatedValue /= operand;
                    break;
                case '%':
                    updatedValue %= operand;
                    break;
                case '**':
                    updatedValue **= operand;
                    break;
                default:
                    throw new Error("unsupported sign");
            }

            return recursiveCalc(updatedValue);
        }

        performOperation.valueOf = function() {
            return accumulatedValue;
        };

        return performOperation;
    }

    return recursiveCalc(startValue);
}

const value = calc(1)('+', 3);
console.log(value('*', 3) + value('*', 2))