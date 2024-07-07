package expression;

public class Multiply extends Operations implements BuilderExpression {
    public Multiply(BuilderExpression leftEx, BuilderExpression rightEx) {
        super(leftEx, rightEx, "*");
    }
    public int evaluateMini(int value1, int value2) {
        return value1 * value2;
    }
}