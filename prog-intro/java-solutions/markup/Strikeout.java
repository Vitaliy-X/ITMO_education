package markup;

import java.util.List;

public class Strikeout extends Markable implements Marker{
    private final List<? extends Marker> list;

    public Strikeout(List<? extends Marker> list) {
        this.list = list;
    }

    public void toMark(StringBuilder str) {
        toMark(str, this.list, "~");
    }

    public void toTeg(StringBuilder str) {
        toTeg(str, this.list, "<s>", "</s>");
    }
}