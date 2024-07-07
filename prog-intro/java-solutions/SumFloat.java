public class SumFloat {
    public static void main(String[] args) {
        float sum = 0;

        for (String elem: args) {
            String str = elem;
            str = str.replaceAll("[^-0-9.eE]", " ");
            String [] str1 = str.split(" ");

            for (String i: str1) {
                if (i.equals("")) continue;
                sum += Float.parseFloat(i);
            }
        }
        System.out.println(sum);
    }
}