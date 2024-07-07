package expression;

public class UnaryMinus extends UnaryAbstract {
    public UnaryMinus(BuilderExpression x) {
        super(x, "-");
    }
    @Override
    public int evaluateMini(int x) {
        return -1 * x;
    }
}
