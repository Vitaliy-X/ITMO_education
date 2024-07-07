package info.kgeorgiy.ja.gavriliuk.iterative;

import info.kgeorgiy.java.advanced.iterative.NewScalarIP;
import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.*;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Implementation of parallel computing
 */
public class IterativeParallelism implements NewScalarIP {
    private final ParallelMapper mapper;

    /**
     * To calculate a function without {@link ParallelMapper}
     */
    public IterativeParallelism() {
        this.mapper = null;
    }

    /**
     * To calculate a function with {@link ParallelMapper}
     *
     * @param mapper mapper for calculate functions
     */
    public IterativeParallelism(ParallelMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T maximum(int threads, List<? extends T> data, Comparator<? super T> comparator, int step) throws InterruptedException {
        if (data.isEmpty()) {
            throw new NoSuchElementException("Data is empty");
        }
        return computeConcurrently(threads, data, stream -> stream.max(comparator).orElseThrow(), stream -> stream.max(comparator).orElseThrow(), step);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T minimum(int threads, List<? extends T> data, Comparator<? super T> comparator, int step) throws InterruptedException {
        return maximum(threads, data, comparator.reversed(), step);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> boolean all(int threads, List<? extends T> data, Predicate<? super T> condition, int step) throws InterruptedException {
        return computeConcurrently(threads, data, stream -> stream.allMatch(condition), stream -> stream.allMatch(Boolean::booleanValue), step);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> boolean any(int threads, List<? extends T> data, Predicate<? super T> condition, int step) throws InterruptedException {
        return !all(threads, data, condition.negate(), step);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> int count(int threads, List<? extends T> data, Predicate<? super T> condition, int step) throws InterruptedException {
        return computeConcurrently(threads, data, stream -> (int) stream.filter(condition).count(), stream -> stream.reduce(0, Integer::sum), step);
    }

    private static <T> List<Stream<T>> partitionData(int threads, List<T> data) {
        int maxThreads = Math.min(threads, data.size());
        List<Stream<T>> partitions = new ArrayList<>(maxThreads);
        int blockSize = data.size() / maxThreads;
        int remainder = data.size() % maxThreads;

        int start = 0;
        for (int i = 0; i < maxThreads; i++) {
            int end = start + blockSize + (i < remainder ? 1 : 0);
            partitions.add(data.subList(start, end).stream());
            start = end;
        }

        return partitions;
    }

    private <T, R> R computeConcurrently(int threads, List<T> data,
                                         Function<Stream<T>, R> taskProcessor,
                                         Function<Stream<R>, R> resultProcessor,
                                         int step) throws InterruptedException {
        if (threads <= 0) {
            throw new IllegalArgumentException("Number of threads must be greater then 0, actual: " + threads);
        }

        final int dataSize = data.size();
        data = Stream.iterate(0, index -> index < dataSize, index -> index + step)
                .map(data::get)
                .collect(Collectors.toList());

        List<Stream<T>> partitions = partitionData(threads, data);

        if (mapper == null && partitions.size() == 1) {
            return taskProcessor.apply(partitions.getFirst());
        }

        List<R> results = mapper != null ? mapper.map(taskProcessor, partitions) : manuallyMap(taskProcessor, partitions);
        return resultProcessor.apply(results.stream());
    }

    private static <T, R> List<R> manuallyMap(
            Function<Stream<T>, R> taskProcessor,
            List<Stream<T>> partitions
    ) throws InterruptedException {
        List<R> results = new ArrayList<>(Collections.nCopies(partitions.size(), null));
        List<Thread> threads = new ArrayList<>(partitions.size());

        // :NOTE: Переписать на стримы
        for (int i = 0; i < partitions.size(); i++) {
            final int index = i;
            Thread thread = new Thread(() -> {
                R result = taskProcessor.apply(partitions.get(index));
                synchronized (results) {
                    results.set(index, result);
                }
            });
            threads.add(thread);
            thread.start();
        }

        // :NOTE: если какой-то из тредов кинет ошибку, остальные не будут заджоинены
        for (Thread thread : threads) {
            thread.join();
        }

        return results;
    }
}