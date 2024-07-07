package info.kgeorgiy.ja.gavriliuk.hello;

import info.kgeorgiy.java.advanced.hello.NewHelloServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * Implementation of the {@link NewHelloServer} interface.
 */
public class HelloUDPServer implements NewHelloServer {
    private Map<Integer, DatagramSocket> socketMap;
    private ExecutorService workerPool;
    private ExecutorService receiverPool;

    /**
     * Main function to start the {@link HelloUDPServer}.
     * <p>
     * Usage: <port> <number of threads>.
     * @param args Array of arguments: {@link Integer} port, {@link Integer} number of threads.
     */
    public static void main(String[] args) {
        if (check(args)) {
            return;
        }

        try {
            int serverPort = Integer.parseInt(Objects.requireNonNull(args[0]));
            int threadCount = Integer.parseInt(Objects.requireNonNull(args[1]));

            try (HelloUDPServer server = new HelloUDPServer()) {
                server.start(serverPort, threadCount);
            }
        } catch (NumberFormatException e) {
            System.err.println("ERROR: Port and number of threads must be integers.");
        }
    }

    private static boolean check(String[] args) {
        if (args == null || args.length != 2) {
            System.err.println("Usage: <port> <number of threads>");
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(int threads, Map<Integer, String> ports) {
        this.socketMap = new ConcurrentHashMap<>();
        this.workerPool = Executors.newFixedThreadPool(threads);
        this.receiverPool = Executors.newCachedThreadPool();

        for (Map.Entry<Integer, String> entry : ports.entrySet()) {
            int port = entry.getKey();
            try {
                DatagramSocket socket = new DatagramSocket(port);
                socketMap.put(port, socket);
                receiverPool.submit(() -> handleRequests(socket, entry.getValue()));
            } catch (SocketException e) {
                System.err.println("ERROR: Unable to create socket on port " + port);
            }
        }
    }

    private void handleRequests(DatagramSocket socket, String responseTemplate) {
        while (!socket.isClosed() && !Thread.currentThread().isInterrupted()) {
            try {
                byte[] buffer = new byte[socket.getReceiveBufferSize()];
                DatagramPacket requestPacket = new DatagramPacket(buffer, buffer.length);

                socket.receive(requestPacket);
                workerPool.submit(() -> {
                    String requestMessage = new String(requestPacket.getData(), requestPacket.getOffset(), requestPacket.getLength(), StandardCharsets.UTF_8);
                    String responseMessage = responseTemplate.replace("$", requestMessage);

                    byte[] responseData = responseMessage.getBytes(StandardCharsets.UTF_8);
                    DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length, requestPacket.getSocketAddress());

                    try {
                        socket.send(responsePacket);
                    } catch (IOException e) {
                        System.err.println("ERROR: I/O exception while sending response: " + e.getMessage());
                    }
                });
            } catch (IOException e) {
                if (!socket.isClosed()) {
                    System.err.println("ERROR: I/O exception while receiving request: " + e.getMessage());
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        socketMap.values().forEach(DatagramSocket::close);
        receiverPool.shutdownNow();
        workerPool.shutdownNow();

        try {
            workerPool.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
        }
    }


}