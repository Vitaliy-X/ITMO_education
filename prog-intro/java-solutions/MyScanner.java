import java.io.*;

public class MyScanner {
    final Reader reader;
    char[] buffer = new char[1024];
    int countOfBuffer = 0;
    int indexOfBuffer = 0;
    String LastWord;

    MyScanner(InputStream input) {
        this.reader = new InputStreamReader(input);
        updateBuffer();
    }

    MyScanner(FileInputStream input) {
        this.reader = new InputStreamReader(input);
        updateBuffer();
    }

    MyScanner(String input) {
        this.reader = new StringReader(input);
        updateBuffer();
    }

    // Scanner for lines!!! (BEGIN)

    public String nextLine() {
        StringBuilder line = new StringBuilder();
        if (System.lineSeparator().length() == 2) {
            while (true) {
                if (buffer[indexOfBuffer] == '\n') {
                    break;
                }
                if (buffer[indexOfBuffer] != '\r') {
                    line.append(buffer[indexOfBuffer++]);
                } else {
                    indexOfBuffer++;
                }

                if (!checkBuff()) {
                    break;
                }
            }
        } else {
            while (!String.valueOf(buffer[indexOfBuffer]).equals(System.lineSeparator())) {
                line.append(buffer[indexOfBuffer]);
                indexOfBuffer++;
                if (!checkBuff()) {
                    break;
                }
            }
        }
        indexOfBuffer++;
        return line.toString();
    }

    public boolean hasNextLine() {
        return checkBuff();
    }

    private boolean checkBuff() {
        if (indexOfBuffer == countOfBuffer) {
            updateBuffer();
        }
        return countOfBuffer != -1;
    }

    private void updateBuffer() {
        try {
            this.indexOfBuffer = 0;
            this.countOfBuffer = reader.read(buffer);
        } catch (IOException e) {
            System.out.println("Err");
        }
    }

//    private char usedChar() {
//        if (checkBuff()) {
//            return buffer[indexOfBuffer];
//        } else return '\n';
//    }
//
//    private char useChar() {
//        if (!checkBuff()) {
//            return '\n';
//        }
//        char el = usedChar();
//        indexOfBuffer++;
//        return el;
//    }

    public void close() {
        try {
            reader.close();
        } catch (IOException e) {
            System.out.println("Err");
        }
    }

    // Scanner for lines!!! (END)


    // Scanner for words!!! (BEGIN)

    public String Next() {
        toBeginNextWord();
        if (!checkBuff()) return "";
        StringBuilder word = new StringBuilder();
        while (!Character.isWhitespace(buffer[indexOfBuffer]) && !String.valueOf(buffer[indexOfBuffer]).equals(System.lineSeparator())) {
            word.append(buffer[indexOfBuffer]);
            indexOfBuffer++;
            if (!checkBuff()) {
                break;
            }
        }
        return word.toString();
    }

    public boolean hasNextInt() {
        while (checkBuff()) {
            if (isDigit(Next())) {
                return true;
            }
        }
        return false;
    }

    public int nextInt() {
        return Integer.parseInt(LastWord);
    }

    private void toBeginNextWord() {
        while (checkBuff() && Character.isWhitespace(buffer[indexOfBuffer])) {
            indexOfBuffer++;
        }
    }

    private boolean isDigit(String word) {
        char[] buff = word.toCharArray();
        for (char ch : buff) {
            if (!Character.isDigit(ch) && ch != '-') {
                return false;
            }
        }
        LastWord = word;
        return true;
    }

    // Scanner for words!!! (END)
}