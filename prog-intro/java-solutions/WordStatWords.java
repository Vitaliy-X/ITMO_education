import java.util.Scanner;
import java.io.*;
import java.util.TreeMap;
import java.util.Set;

public class WordStatWords {
    public static void main(String[] args) {
        try {
            FileReader fl = new FileReader(args[0]);
            try {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(args[1]), "UTF-8"));
                try {
                    Scanner scan = new Scanner(fl);
                    TreeMap <String, Integer> map = new TreeMap<>();

                    String word;
                    while (scan.hasNextLine()) {
                        String str = scan.nextLine();
                        Scanner sc = new Scanner(str);
                        while (sc.hasNext()){
                            word = sc.next().toLowerCase();

                            char [] chars = word.toCharArray();
                            String w = "";
                            for (char symbol: chars){
                                if (Character.getType(symbol) == Character.DASH_PUNCTUATION || Character.isLetter(symbol) || symbol == '\''){
                                    w += String.valueOf(symbol);
                                } else {
                                    w += " ";
                                }
                            }
                            String [] wordArr = w.split(" ");
                            for (String el: wordArr){
                                if (el.equals("")) continue;
                                if (!map.containsKey(el)) {
                                    map.put(el, 1);
                                } else {
                                    map.put(el, map.get(el) + 1);
                                }
                            }
                        }
                        sc.close();
                    }


                    Set <String> keys = map.keySet();
                    for (String key: keys) {
                        writer.write(key + " " + map.get(key));
                        writer.newLine();
                    }
                    scan.close();
                } finally {
                    writer.close();
                }
            } finally {
                fl.close();
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}