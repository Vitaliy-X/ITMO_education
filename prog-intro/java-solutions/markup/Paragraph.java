package markup;

import java.util.List;

public class Paragraph extends Markable {
    private final List<? extends Marker> list;

    public Paragraph(List<? extends Marker> list) {
        this.list = list;
    }

    public void toMarkdown(StringBuilder str) {
        toMark(str, this.list, "");
    }

    public void toHtml(StringBuilder str) {
        toTeg(str, this.list, "", "");
    }
}
