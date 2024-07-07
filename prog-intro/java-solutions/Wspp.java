import java.util.ArrayList;
//import java.util.Scanner;
import java.io.*;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Scanner;

public class Wspp {
    public static void main(String[] args) {
        LinkedHashMap <String, ArrayList<Integer>> map = new LinkedHashMap<>();
        try {
            FileReader fl = new FileReader(args[0]);
            try {
                Scanner scan = new Scanner(fl);

                String word;
                int count = 0;

                while (scan.hasNextLine()) {
                    String str = scan.nextLine();
                    Scanner sc = new Scanner(str);
                    while (sc.hasNext()){
                        word = sc.next().toLowerCase();

                        char [] chars = word.toCharArray();
                        StringBuilder w = new StringBuilder();
                        for (char symbol: chars){
                            if (Character.getType(symbol) == Character.DASH_PUNCTUATION || Character.isLetter(symbol) || symbol == '\''){
                                w.append(symbol);
                            } else {
                                w.append(" ");
                            }
                        }
                        String [] wordArr = w.toString().split(" ");
                        for (String el: wordArr){
                            if (el.equals("")) continue;
                            count++;

                            if (!map.containsKey(el)) {
                                ArrayList<Integer> index = new ArrayList<>();
                                index.add(count);
                                map.put(el, index);
                            } else {
                                map.get(el).add(count);
                            }
                        }
                    }
                    sc.close();
                }
                scan.close();
            } finally {
                fl.close();
            }

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

//        --------------------------------------------------------------

        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(args[1]), "UTF-8"));

            Set <String> keys = map.keySet();
            for (String key: keys) {
                writer.write(key + " " + map.get(key).size());
                for (Integer i: map.get(key)) {
                    writer.write(" " + i);
                }
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Err: " + e);
        }
    }
}