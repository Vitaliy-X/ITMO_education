package expression;

import java.util.Objects;

public class Const implements BuilderExpression {
    private final int cons;

    public Const(int cons) {
        this.cons = cons;
    }

    @Override
    public String toString() {
        return String.valueOf(cons);
    }
    @Override
    public int evaluate(int value) {
        return cons;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (getClass() == o.getClass()) {
            Const obj = (Const) o;
            return cons == obj.cons;
        }
        return false;
    }
    @Override
    public int hashCode() {
        return Objects.hashCode(cons);
    }
    @Override
    public int evaluate(int x, int y, int z) {
        return cons;
    }
}