package markup;

import java.util.List;

public class Strong extends Markable implements Marker{
    private final List<? extends Marker> list;

    public Strong(List<? extends Marker> list) {
        this.list = list;
    }

    public void toMark(StringBuilder str) {
        toMark(str, this.list, "__");
    }

    public void toTeg(StringBuilder str) {
        toTeg(str, this.list, "<strong>", "</strong>");
    }
}
