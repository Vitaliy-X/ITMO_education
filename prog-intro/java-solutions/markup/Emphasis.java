package markup;

import java.util.List;

public class Emphasis extends Markable implements Marker {
    private final List<? extends Marker> list;

    public Emphasis (List<? extends Marker> list) {
        this.list = list;
    }

    public void toMark(StringBuilder str) {
        toMark(str, this.list, "*");
    }

    public void toTeg(StringBuilder str) {
        toTeg(str, this.list, "<em>", "</em>");
    }
}
