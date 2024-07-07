package expression;

import java.util.Objects;

public abstract class UnaryAbstract implements BuilderExpression {
    private final BuilderExpression x;
    public final String operation;

    protected UnaryAbstract(BuilderExpression x, String op) {
        this.x = x;
        this.operation = op;
    }
    protected abstract int evaluateMini(int x);

    public int evaluate(int x) {
        return evaluateMini(this.x.evaluate(x));
    }

    public int evaluate(int x, int y, int z) {
        return evaluateMini(this.x.evaluate(x, y, z));
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
            UnaryAbstract obj = (UnaryAbstract) o;
            return Objects.equals(x, obj.x) &&
                    Objects.equals(operation, obj.operation);
        }
        return false;
    }

    public String toString() {
        return operation + "(" + x.toString() + ")";
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, getClass());
    }
}
