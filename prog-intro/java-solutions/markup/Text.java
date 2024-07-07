package markup;

public class Text extends Markable implements Marker{
    private final String str;

    public Text(String str) {
        this.str = str;
    }

    @Override
    public void toMark(StringBuilder str) {
        str.append(this.str);
    }

    @Override
    public void toTeg(StringBuilder str) {
        str.append(this.str);
    }
}
