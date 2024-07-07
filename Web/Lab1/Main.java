import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        for (int i = 1; i < 101; i++) {

            List<String> command = new ArrayList<>();
            command.add("curl");
            command.add("http://1d3p.wp.codeforces.com/new");
            command.add("-H");
            command.add("Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
            command.add("-H");
            command.add("Accept-Language: ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7");
            command.add("-H");
            command.add("Cache-Control: no-cache");
            command.add("-H");
            command.add("Connection: keep-alive");
            command.add("-H");
            command.add("Content-Type: application/x-www-form-urlencoded");
            command.add("-H");
            command.add("Cookie: JSESSIONID=4B281C273ABE2048A6DAFEEA274551B5");
            command.add("-H");
            command.add("Origin: http://1d3p.wp.codeforces.com");
            command.add("-H");
            command.add("Pragma: no-cache");
            command.add("-H");
            command.add("Referer: http://1d3p.wp.codeforces.com/");
            command.add("-H");
            command.add("Upgrade-Insecure-Requests: 1");
            command.add("-H");
            command.add("User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36");
            command.add("--data-raw");
            command.add(String.format("_af=34be50b38beccce4&proof=%d&amount=%d&submit=Submit", i*i, i));
            command.add("--compressed");
            command.add("--insecure");

            ProcessBuilder processBuilder = new ProcessBuilder(command);

            try {
                Process process = processBuilder.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }

                int exitCode = process.waitFor();
                System.out.println("\nExited with error code : " + exitCode);

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
