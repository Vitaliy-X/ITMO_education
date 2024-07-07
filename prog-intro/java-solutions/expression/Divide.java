package expression;

public class Divide extends Operations implements BuilderExpression {
    public Divide(BuilderExpression leftEx, BuilderExpression rightEx) {
        super(leftEx, rightEx, "/");
    }
    public int evaluateMini(int value1, int value2) {
        return value1 / value2;
    }
}