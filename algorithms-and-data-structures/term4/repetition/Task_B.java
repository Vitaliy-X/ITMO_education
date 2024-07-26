//package repetition;

import java.util.Scanner;

public class Task_B {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        scanner.nextInt();
        scanner.nextLine();
        String speech = scanner.nextLine();

        String modifiedSpeech = processSpeech(speech);
        System.out.println(modifiedSpeech);
    }

    private static String processSpeech(String speech) {
        StringBuilder result = new StringBuilder();
        int index = 0;
        while (index < speech.length()) {
            if (isSpecialWordStart(speech, index)) {
                result.append("***");
                index = skipSpecialWord(speech, index);
            } else {
                result.append(speech.charAt(index));
                index++;
            }
        }
        return result.toString();
    }

    private static boolean isSpecialWordStart(String speech, int index) {
        return index <= speech.length() - 3 && speech.startsWith("ogo", index);
    }

    private static int skipSpecialWord(String speech, int index) {
        index += 3;
        while (index <= speech.length() - 2 && speech.startsWith("go", index)) {
            index += 2;
        }
        return index;
    }
}