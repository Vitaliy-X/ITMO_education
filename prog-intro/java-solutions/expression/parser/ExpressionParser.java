package expression.parser;

import expression.*;

public class ExpressionParser extends BaseParser implements TripleParser {
    private boolean flagUnaryMinus;
    private int checkerBracketO;
    private int checkerBracketC;

    @Override
    public BuilderExpression parse(String expression){
        this.source = new StringSource(expression);
        take();
        flagUnaryMinus = true;
        checkerBracketO = 0;
        checkerBracketC = 0;
        return parseExpression(null);
    }

    private BuilderExpression parseExpression(BuilderExpression parsed){
        skipWhitespace();
        while (!eof()) {
            if (checkerBracketO < checkerBracketC + 1) {
                take(')');
            } else if (ch == ')') {
                return parsed;
            }
            parsed = parseHigh(parsed);
        }
        return parsed;
    }

    private BuilderExpression parseHigh(BuilderExpression parsed) {
        if (take('(')) {
            checkerBracketO++;
            parsed = parseExpression(parsed);
            take(')');
            checkerBracketC++;
            return parsed;

        } else if (take('c')) {
            return parseCount(parsed);

        } else if (isDigit()) {
            return parseConst(false);

        } else if (flagUnaryMinus && take('-')) {
            return isDigit() ? parseConst(true) : new UnaryMinus(parseHigh(null));

        } else if (ch == 'x' || ch == 'y' || ch == 'z') {
            return parseVariable();

        } else {
            return parseMid(parsed);
        }
    }

    private BuilderExpression parseMid(BuilderExpression parsed, String op) {
        BuilderExpression newParsed = getExpression();
        newParsed = make(parsed, newParsed, op);
        if (ch == '-' || ch == '+') {
            return newParsed;
        }
        return parseMid(newParsed);
    }

    private BuilderExpression parseMid(BuilderExpression parsed) {
        if (take('*')) {
            return parseMid(parsed, "*");
        } else if (take('/')) {
            return parseMid(parsed, "/");
        }
        return parseLow(parsed);
    }

    private BuilderExpression parseLow(BuilderExpression parsed){
        if (take('+')) {
            flagUnaryMinus = true;
            return make(parsed, parseExpression(null), "+");
        } else if (take('-')) {
            BuilderExpression newParsed = getExpression();
            return (ch != '-' && ch != '+') ? make(parsed, parseHigh(newParsed), "-") : make(parsed, newParsed, "-");
        }
        return parseExpression(parsed);
    }

    private BuilderExpression getExpression() {
        flagUnaryMinus = true;
        BuilderExpression newParsed = parseHigh(null);
        skipWhitespace();
        return newParsed;
    }

    private BuilderExpression parseCount(BuilderExpression parsed) {
        take('o');
        take('u');
        take('n');
        take('t');
        BuilderExpression newParsed = getExpression();
        return make(parsed, newParsed, "c");
    }

    private BuilderExpression make(BuilderExpression first, BuilderExpression second, String type){
        switch (type) {
            case "+": {
                return new Add(first, second);
            }
            case "-": {
                return new Subtract(first, second);
            }
            case "*": {
                return new Multiply(first, second);
            }
            case "/": {
                return new Divide(first, second);
            }
            case "c": {
                return new Count(second);
            }
            default: {
                return null;
            }
        }
    }

    private BuilderExpression parseVariable() {
        flagUnaryMinus = false;
        switch (ch) {
            case 'x': {
                take();
                return new Variable("x");
            }
            case 'y': {
                take();
                return new Variable("y");
            }
            case 'z': {
                take();
                return new Variable("z");
            }
            default: {
                return null;
            }
        }
    }

    private BuilderExpression parseConst(boolean type) {
        flagUnaryMinus = false;
        StringBuilder str = new StringBuilder();
        if (type) {
            str.append('-');
        }
        while (isDigit()) {
            str.append(ch);
            take();
        }
        return new Const(Integer.parseInt(str.toString()));
    }
}