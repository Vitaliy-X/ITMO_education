package info.kgeorgiy.ja.gavriliuk.crawler;

import info.kgeorgiy.java.advanced.crawler.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.*;

public class WebCrawler implements NewCrawler {
    private static final String USAGE = "WebCrawler url [depth [downloads [extractors [perHost]]]]";
    private final Downloader downloader;
    private final int perHost;
    private final ExecutorService extractors, downloaders;
    private final ConcurrentMap<String, HostManager> hosts = new ConcurrentHashMap<>();

    public WebCrawler(Downloader downloader, int downloaders, int extractors, int perHost) {
        this.downloader = downloader;
        this.perHost = perHost;
        this.downloaders = Executors.newFixedThreadPool(downloaders);
        this.extractors = Executors.newFixedThreadPool(extractors);
    }

    @Override
    public Result download(String url, int depth, Set<String> excludes) {
        return new WebCrawlerEngine().crawl(url, depth, excludes);
    }

    public static void main(String[] args) {
        if (checkArgs(args)) {
            return;
        }

        try {
            int depth = parseArgument(args, 1, 1);
            int downloaders = parseArgument(args, 2, 16);
            int extractors = parseArgument(args, 3, 16);
            int perHost = parseArgument(args, 4, 10);

            try (WebCrawler crawler = new WebCrawler(new CachingDownloader(1), downloaders, extractors, perHost)) {
                Result result = crawler.download(args[0], depth, new HashSet<>());
                System.out.println("Downloaded: " + result.getDownloaded());
                System.out.println("Errors: " + result.getErrors());
            }
        } catch (IOException e) {
            System.err.println("Error initializing downloader: " + e.getMessage());
        }
    }

    private static int parseArgument(String[] args, int pos, int otherwise) {
        if (args.length <= pos) {
            return otherwise;
        }
        return Integer.parseInt(args[pos]);
    }

    private static boolean checkArgs(String[] args) {
        if (args.length < 1 || Arrays.stream(args).anyMatch(Objects::isNull)) {
            System.err.println("Invalid arguments provided.");
            System.err.println(USAGE);
            return true;
        }
        return false;
    }

    @Override
    public void close() {
        extractors.shutdownNow();
        downloaders.shutdownNow();
    }

    private class HostManager {
        private final Queue<Runnable> taskQueue = new LinkedList<>();
        private int activeTasks = 0;
        private final Object lock = new Object();

        public void schedule(Runnable task) {
            Runnable wrappedTask = () -> {
                try {
                    task.run();
                } finally {
                    taskCompleted();
                }
            };

            synchronized (lock) {
                if (activeTasks < perHost) {
                    submitTask(wrappedTask);
                } else {
                    taskQueue.offer(wrappedTask);
                }
            }
        }

        private void submitTask(Runnable task) {
            synchronized (lock) {
                downloaders.execute(task);
                activeTasks++;
            }
        }

        private void taskCompleted() {
            synchronized (lock) {
                Runnable nextTask = taskQueue.poll();
                if (nextTask != null) {
                    downloaders.execute(nextTask);
                } else {
                    activeTasks--;
                }
            }
        }
    }

    private class WebCrawlerEngine {
        private final Phaser synchronizationBarrier = new Phaser(1);
        private final Set<String> visitedUrls = ConcurrentHashMap.newKeySet();

        private void processDownload(ConcurrentLinkedQueue<TraversalStage> queue,
                                     Set<String> downloadedPages,
                                     ConcurrentMap<String, IOException> downloadErrors,
                                     TraversalStage currentStage,
                                     int maxDepth, Set<String> excludes) throws MalformedURLException {

            if (excludes.stream().anyMatch(currentStage.url::contains)) {
                return;
            }

            final HostManager hostControl = hosts.computeIfAbsent(
                    URLUtils.getHost(currentStage.url), (str) -> new HostManager());

            synchronizationBarrier.register();

            hostControl.schedule(() -> {
                try {
                    final Document pageContent = downloader.download(currentStage.url);
                    downloadedPages.add(currentStage.url);
                    if (currentStage.depth < maxDepth) {
                        processExtraction(queue, pageContent, currentStage.depth + 1, excludes);
                    }
                } catch (IOException e) {
                    downloadErrors.put(currentStage.url, e);
                } finally {
                    synchronizationBarrier.arriveAndDeregister();
                }
            });
        }

        private void processExtraction(ConcurrentLinkedQueue<TraversalStage> queue, Document pageContent, int currentDepth, Set<String> excludes) {
            synchronizationBarrier.register();

            extractors.submit(() -> {
                try {
                    pageContent.extractLinks().stream()
                            .filter(link -> excludes.stream().noneMatch(link::contains))
                            .filter(visitedUrls::add)
                            .forEach(link -> queue.add(new TraversalStage(link, currentDepth)));
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                } finally {
                    synchronizationBarrier.arriveAndDeregister();
                }
            });
        }

        public Result crawl(String initialUrl, int depth, Set<String> excludes) {
            ConcurrentLinkedQueue<TraversalStage> traversalQueue =
                    new ConcurrentLinkedQueue<>(List.of(new TraversalStage(initialUrl, 1)));

            int lastProcessedDepth = 0;
            ConcurrentMap<String, IOException> errors = new ConcurrentHashMap<>();
            Set<String> downloaded = ConcurrentHashMap.newKeySet();

            visitedUrls.add(initialUrl);
            while (!traversalQueue.isEmpty()) {
                TraversalStage currentStage = traversalQueue.poll();
                if (currentStage.depth > lastProcessedDepth) {
                    lastProcessedDepth++;
                    synchronizationBarrier.arriveAndAwaitAdvance();
                }

                if (excludes.stream().noneMatch(currentStage.url::contains)) {
                    try {
                        processDownload(traversalQueue, downloaded, errors, currentStage, depth, excludes);
                    } catch (MalformedURLException e) {
                        errors.put(currentStage.url, e);
                    }
                }

                if (traversalQueue.isEmpty()) {
                    synchronizationBarrier.arriveAndAwaitAdvance();
                }
            }

            return new Result(new ArrayList<>(downloaded), errors);
        }

        private record TraversalStage(String url, int depth) {}
    }
}