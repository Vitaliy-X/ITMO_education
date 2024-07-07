package expression;

public class Count extends UnaryAbstract implements BuilderExpression {
    public Count(BuilderExpression x) {
        super(x, "count");
    }
    @Override
    public int evaluateMini(int x) {
        return Integer.bitCount(x);
    }
}
