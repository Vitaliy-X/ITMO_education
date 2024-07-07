package expression;

import java.util.Objects;

public abstract class Operations {
    public final BuilderExpression leftEx;
    public final BuilderExpression rightEx;
    public final String operation;

    public Operations(BuilderExpression leftEx, BuilderExpression rightEx, String op) {
        this.leftEx = leftEx;
        this.rightEx = rightEx;
        this.operation = op;
    }
    public abstract int evaluateMini(int value1, int value2);

    public BuilderExpression getLeftEx() {
        return leftEx;
    }
    public BuilderExpression getRightEx() {
        return rightEx;
    }

    public String toString() {
        return '(' + getLeftEx().toString() + ' ' + operation + ' ' + getRightEx().toString() + ')';
    }

    public int evaluate(int x) {
        return evaluateMini(leftEx.evaluate(x), rightEx.evaluate(x));
    }

    public int evaluate(int x, int y, int z) {
        return evaluateMini(getLeftEx().evaluate(x, y, z), getRightEx().evaluate(x, y, z));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (getClass() == o.getClass()) {
            Operations obj = (Operations) o;
            return Objects.equals(leftEx, obj.leftEx) &&
                    Objects.equals(rightEx, obj.rightEx) &&
                    Objects.equals(operation, obj.operation);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return ((Objects.hashCode(leftEx) * 29 + Objects.hashCode(rightEx)) * 31 + Objects.hashCode(operation)) * 37;
    }
}