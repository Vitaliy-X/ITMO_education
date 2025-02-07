import org.jetbrains.annotations.NotNull;

/**
 * В теле класса решения разрешено использовать только финальные переменные типа RegularInt.
 * Нельзя volatile, нельзя другие типы, нельзя блокировки, нельзя лазить в глобальные переменные.
 *
 * @author Гаврилюк Виталий
 */
public class Solution implements MonotonicClock {
    private final RegularInt c1 = new RegularInt(0);
    private final RegularInt c2 = new RegularInt(0);
    private final RegularInt c3 = new RegularInt(0);
    private final RegularInt cc1 = new RegularInt(0);
    private final RegularInt cc2 = new RegularInt(0);

    @Override
    public void write(@NotNull Time time) {
        // write right-to-left
        cc1.setValue(time.getD1());
        cc2.setValue(time.getD2());
        c3.setValue(time.getD3());
        c2.setValue(time.getD2());
        c1.setValue(time.getD1());
    }

    @NotNull
    @Override
    public Time read() {
        int primaryD1 = c1.getValue();
        int primaryD2 = c2.getValue();
        int primaryD3 = c3.getValue();

        int secondaryD1 = cc1.getValue();
        int secondaryD2 = cc2.getValue();

        int resultD1 = secondaryD1;
        int resultD2 = (primaryD1 == secondaryD1) ? secondaryD2 : 0;
        int resultD3 = (primaryD1 == secondaryD1 && primaryD2 == secondaryD2) ? primaryD3 : 0;

        return new Time(resultD1, resultD2, resultD3);
    }
}
