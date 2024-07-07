package markup;

import java.util.List;

public abstract class Markable {
    public static void toMark(StringBuilder str, List<? extends Marker> list, String symbol) {
        str.append(symbol);
        for (Marker marker: list) {
            StringBuilder str_local = new StringBuilder();
            marker.toMark(str_local);
            str.append(str_local);
        }
        str.append(symbol);
    }

    public static void toTeg(StringBuilder str, List<? extends Marker> list, String symbol1, String symbol2) {
        str.append(symbol1);
        for (Marker marker: list) {
            StringBuilder str_local = new StringBuilder();
            marker.toTeg(str_local);
            str.append(str_local);
        }
        str.append(symbol2);
    }
}
