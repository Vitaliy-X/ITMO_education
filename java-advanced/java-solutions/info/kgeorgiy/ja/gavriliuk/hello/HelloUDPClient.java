package info.kgeorgiy.ja.gavriliuk.hello;

import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of the {@link HelloClient} interface.
 */
public class HelloUDPClient implements HelloClient {

    /**
     * Main function to start the {@link HelloUDPClient}.
     * <p>
     * Usage: <host> <prefix> <port> <number of threads> <number of requests>.
     * @param args Array of arguments: {@link String} host, {@link String} prefix, {@link Integer} port, {@link Integer} number of threads, {@link Integer} number of requests.
     */
    public static void main(String[] args) {
        if (check(args)) {
            return;
        }

        try {
            String serverHost = args[0];
            int serverPort = Integer.parseInt(args[1]);
            String requestPrefix = args[2];
            int threadCount = Integer.parseInt(args[3]);
            int requestCount = Integer.parseInt(args[4]);

            new HelloUDPClient().run(serverHost, serverPort, requestPrefix, threadCount, requestCount);
        } catch (NumberFormatException e) {
            System.err.println("ERROR: Port, number of threads, and number of requests must be integers.");
        }
    }

    private static boolean check(String[] args) {
        if (args == null || args.length != 5) {
            System.err.println("Usage: <host> <prefix> <port> <number of threads> <number of requests>");
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(String host, int port, String prefix, int threads, int requests) {
        InetSocketAddress serverAddress = new InetSocketAddress(host, port);
        ExecutorService threadPool = Executors.newFixedThreadPool(threads);

        for (int i = 0; i < threads; i++) {
            final int threadId = i + 1;
            threadPool.submit(() -> {
                try (DatagramSocket udpSocket = new DatagramSocket()) {
                    udpSocket.setSoTimeout(500);
                    for (int j = 0; j < requests; j++) {
                        String requestMessage = String.format("%s%d_%d", prefix, threadId, j + 1);
                        byte[] requestData = requestMessage.getBytes(StandardCharsets.UTF_8);
                        DatagramPacket requestPacket = new DatagramPacket(requestData, requestData.length, serverAddress);

                        while (!Thread.currentThread().isInterrupted()) {
                            try {
                                udpSocket.send(requestPacket);
                                byte[] responseBuffer = new byte[udpSocket.getReceiveBufferSize()];
                                DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
                                udpSocket.receive(responsePacket);

                                String responseMessage = new String(responsePacket.getData(), responsePacket.getOffset(), responsePacket.getLength(), StandardCharsets.UTF_8);
                                if (responseMessage.contains(requestMessage)) {
                                    System.out.println(responseMessage);
                                    break;
                                }
                            } catch (SocketTimeoutException e) {
                                System.err.println("Timeout: " + e.getMessage());
                            } catch (IOException e) {
                                System.err.println("I/O error: " + e.getMessage());
                            }
                        }
                    }
                } catch (SocketException e) {
                    System.err.println("Socket error: " + e.getMessage());
                }
            });
        }

        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination((long) threads * requests, TimeUnit.MINUTES)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
        }
    }
}