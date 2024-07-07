public class Sum {
    public static void main(String[] args) {
        int sum = 0;

        for (String elem: args) {
            String str = elem;
            str = str.replaceAll("[^-0-9]", " ");
            String [] str1 = str.split(" ");

            for (String i: str1) {
                if (i.equals("")) continue;
                sum += Integer.parseInt(i);
            }
        }
        System.out.println(sum);
    }
}