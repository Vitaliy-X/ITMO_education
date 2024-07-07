package expression;

public class Subtract extends Operations implements BuilderExpression {
    public Subtract(BuilderExpression leftEx, BuilderExpression rightEx) {
        super(leftEx, rightEx, "-");
    }
    public int evaluateMini(int value1, int value2) {
        return value1 - value2;
    }
}