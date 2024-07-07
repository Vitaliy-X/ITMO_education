package expression;

public class Add extends Operations implements BuilderExpression {
    public Add(BuilderExpression oLeft, BuilderExpression oRight) {
        super(oLeft, oRight, "+");
    }
    public int evaluateMini(int value1, int value2) {
        return value1 + value2;
    }
}