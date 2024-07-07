package info.kgeorgiy.ja.gavriliuk.iterative;

import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Implementation of a ParallelMapper, which facilitates parallel execution of taskQueue
 * across multiple worker threads.
 */
public class ParallelMapperImpl implements ParallelMapper {
    private final Queue<Runnable> taskQueue;
    private final List<Thread> workerThreads;

    /**
     * Constructs a ParallelMapper implementation with a specified number of worker threads.
     *
     * @param threads count workerThreads
     */
    public ParallelMapperImpl(int threads) {
        if (threads <= 0) {
            throw new IllegalArgumentException("Number of threads must be greater then 0, actual: " + threads);
        }

        this.taskQueue = new LinkedList<>();

        Runnable taskExecutor = () -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Runnable nextTask;
                    synchronized (taskQueue) {
                        while (taskQueue.isEmpty()) {
                            taskQueue.wait();
                        }
                        nextTask = taskQueue.remove();
                    }
                    nextTask.run();
                }
            } catch (InterruptedException e) {
                // Restore the interrupted status
                Thread.currentThread().interrupt();
            }
        };

        this.workerThreads = Stream.iterate(0, i -> i + 1)
                .limit(threads)
                .map(i -> new Thread(taskExecutor))
                .peek(Thread::start)
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T, R> List<R> map(Function<? super T, ? extends R> function, List<? extends T> arguments) throws InterruptedException {
        ResultCollector<T, R> collector = new ResultCollector<>(function, arguments);
        Stream.iterate(0, n -> n + 1)
                .limit(arguments.size())
                .forEach(collector::submitTask);
        return collector.collectResults();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        workerThreads.forEach(Thread::interrupt);

        workerThreads.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    private class ResultCollector<T, R> {
        private final Function<? super T, ? extends R> function;
        private final List<? extends T> inputs;
        private final List<R> results;
        private int remainingTasks;
        private RuntimeException taskException;

        public ResultCollector(Function<? super T, ? extends R> function, List<? extends T> inputs) {
            this.function = function;
            this.inputs = inputs;
            this.remainingTasks = inputs.size();
            this.results = new ArrayList<>(Collections.nCopies(inputs.size(), null));
        }

        public synchronized void submitTask(int index) {
            Runnable task = () -> {
                try {
                    R result = function.apply(inputs.get(index));
                    synchronized (this) {
                        results.set(index, result);
                        if (--remainingTasks == 0) {
                            notifyAll();
                        }
                    }
                } catch (RuntimeException e) {
                    synchronized (this) {
                        if (taskException == null) {
                            taskException = e;
                        } else {
                            taskException.addSuppressed(e);
                        }
                    }
                }
            };

            synchronized (taskQueue) {
                taskQueue.add(task);
                taskQueue.notify();
            }
        }

        public synchronized List<R> collectResults() throws InterruptedException {
            while (remainingTasks > 0) {
                wait();
            }
            if (taskException != null) {
                throw taskException;
            }
            return results;
        }
    }
}