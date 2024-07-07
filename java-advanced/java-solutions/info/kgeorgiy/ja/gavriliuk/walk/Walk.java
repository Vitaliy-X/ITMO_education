package info.kgeorgiy.ja.gavriliuk.walk;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

public class Walk {
    public static void main(String[] args) {
        if (args == null || args.length != 2 || args[0] == null || args[1] == null) {
            System.err.println("Run program with two arguments, example: java Walk <input file> <output file>");
            return;
        }

        Path outputPath;
        try {
            outputPath = Path.of(args[1]);
        } catch (InvalidPathException e) {
            System.err.println("Invalid path: " + e.getInput());
            return;
        }

        try {
            if (outputPath.getParent() != null) {
                Files.createDirectories(outputPath.getParent());
            }
        } catch (IOException e) {
            System.err.println("Could not create directories for the output file: " + e.getMessage());
            return;
        } catch (SecurityException e) {
            System.err.println("No permission to create directories for the output file: " + e.getMessage());
            return;
        }

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(args[0], StandardCharsets.UTF_8));
             FileOutputStream fileOutputStream = new FileOutputStream(args[1])) {

            while (bufferedReader.ready()) {
                String fileName = bufferedReader.readLine();
                String hash = jenkinsHash(fileName);
                fileOutputStream.write(hash.getBytes(StandardCharsets.UTF_8));
                fileOutputStream.write(" ".getBytes(StandardCharsets.UTF_8));
                fileOutputStream.write(fileName.getBytes(StandardCharsets.UTF_8));
                fileOutputStream.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found or no access: " + e.getMessage());
        } catch (SecurityException e) {
            System.err.println("No permission to read file: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("An IO error occurred: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    public static String jenkinsHash(String filePath) {
        int hash = 0;
        try (FileInputStream fis = new FileInputStream(filePath);
             BufferedInputStream bis = new BufferedInputStream(fis, 256)) {
            byte[] buffer = new byte[256];
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                for (int i = 0; i < bytesRead; i++) {
                    int b = buffer[i] & 0xFF;
                    hash += b;
                    hash += (hash << 10);
                    hash ^= (hash >>> 6);
                }
            }
        } catch (InvalidPathException e) {
            System.err.println("Invalid file path: " + filePath);
        } catch (SecurityException e) {
            System.err.println("No permission to read file: " + filePath);
            return "00000000";
        } catch (IOException e) {
            return "00000000";
        }

        hash += (hash << 3);
        hash ^= (hash >>> 11);
        hash += (hash << 15);

        return String.format("%08x", hash);
    }
}