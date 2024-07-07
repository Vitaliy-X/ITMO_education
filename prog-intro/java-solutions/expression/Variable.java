package expression;

import java.util.Objects;

public class Variable implements BuilderExpression{
    private final String var;

    public Variable(String var) {
        this.var = var;
    }

    @Override
    public String toString() {
        return var;
    }

    @Override
    public int evaluate(int value) {
        return value;
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
            Variable obj = (Variable) o;
            return Objects.equals(var, obj.var);
        }
        return false;
    }
    @Override
    public int hashCode() {
        return Objects.hashCode(var);
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return switch (var) {
            case "x" -> x;
            case "y" -> y;
            case "z" -> z;
            default -> 0;
        };
    }
}