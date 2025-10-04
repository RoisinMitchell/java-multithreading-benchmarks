package com.roisinmitchell.benchmarks;

import com.roisinmitchell.benchmarks.utils.CpuAffinityWindows;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * The {@code BenchmarkRunner} class provides a framework for executing and timing
 * benchmark tasks across multiple thread configurations.
 * <p>
 * It supports warm-up runs (to allow JIT compilation to stabilise),
 * averages multiple measured runs for accuracy, and optionally applies
 * CPU affinity on Windows systems to control which cores threads run on.
 * </p>
 *
 * <p>This class is designed for benchmarking CPU-bound workloads that implement
 * the {@link BenchmarkTask} interface. It can be reused to test different
 * workloads or hardware configurations.</p>
 *
 * <p><b>Example usage:</b></p>
 * <pre>{@code
 * BenchmarkTask task = new MatrixMultiplicationTask(400);
 * int[] threads = {1, 2, 4, 8, 12, 24};
 * BenchmarkRunner runner = new BenchmarkRunner();
 * runner.runAll(task, threads, 1, 3);
 * }</pre>
 *
 * @author Roisin Mitchell
 * @version 1.0
 * @since JDK 21
 */
public class BenchmarkRunner {

    /**
     * Runs the provided benchmark task using multiple thread counts and prints
     * average execution times for each configuration.
     *
     * @param task          the benchmark task to execute
     * @param threadCounts  an array of thread counts to test (e.g., {1, 2, 4, 8})
     * @param warmupRuns    the number of warm-up iterations to perform (excluded from timing)
     * @param measuredRuns  the number of timed runs to average for each thread configuration
     */
    public void runAll(BenchmarkTask task, int[] threadCounts, int warmupRuns, int measuredRuns) {
        System.out.println("=== Running " + task.getName() + " ===");

        for (int threads : threadCounts) {
            try {
                long avgTime = runBenchmark(task, threads, warmupRuns, measuredRuns);
                System.out.printf("Threads: %2d | Avg Time: %6d ms%n", threads, avgTime);
            } catch (Exception e) {
                System.err.println("Error running benchmark with " + threads + " threads: " + e.getMessage());
            }
        }
    }

    /**
     * Runs the specified benchmark multiple times (including warm-up and measured runs)
     * and returns the average execution time.
     * <p>
     * Warm-up runs help eliminate the effects of JIT compilation and caching on performance.
     * </p>
     *
     * @param task          the benchmark task to execute
     * @param threads       the number of threads to use in each run
     * @param warmupRuns    how many warm-up runs to perform (ignored in timing)
     * @param measuredRuns  how many runs to average for timing
     * @return the average execution time in milliseconds
     * @throws Exception if an error occurs during task execution or thread management
     */
    public long runBenchmark(BenchmarkTask task, int threads, int warmupRuns, int measuredRuns) throws Exception {
        // Warm-up runs (for JIT optimisation)
        for (int i = 0; i < warmupRuns; i++) {
            runOnce(task, threads, false);
        }

        // Measured runs
        List<Long> times = new ArrayList<>();
        for (int i = 0; i < measuredRuns; i++) {
            times.add(runOnce(task, threads, true));
        }

        // Compute average
        return (long) times.stream().mapToLong(Long::longValue).average().orElse(0);
    }

    /**
     * Executes one benchmark run using the specified number of threads.
     * <p>
     * Each thread executes the provided {@link BenchmarkTask}. If the platform supports it,
     * threads may be pinned to specific CPU cores using {@link CpuAffinityWindows}.
     * </p>
     *
     * @param task     the task to run concurrently on multiple threads
     * @param threads  the number of threads to use in this run
     * @param print    whether to print timing results for this individual run
     * @return the total execution time in milliseconds for this run
     * @throws Exception if any thread execution fails
     */
    private long runOnce(BenchmarkTask task, int threads, boolean print) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        List<Future<Long>> results = new ArrayList<>();

        long start = System.nanoTime();

        for (int i = 0; i < threads; i++) {
            final int threadId = i;
            results.add(pool.submit(() -> {
                int coreId = threadId % Runtime.getRuntime().availableProcessors();
                try {
                    CpuAffinityWindows.setCurrentThreadAffinity(coreId);
                } catch (UnsatisfiedLinkError | NoClassDefFoundError e) {
                    if (threadId == 0) {
                        System.out.println("⚠️  CPU affinity not supported on this platform.");
                    }
                } catch (Exception e) {
                    if (threadId == 0) {
                        System.out.println("⚠️  Failed to set thread affinity: " + e.getMessage());
                    }
                }

                return task.call();
            }));
        }

        for (Future<Long> f : results) f.get();
        pool.shutdown();

        long end = System.nanoTime();
        long duration = (end - start) / 1_000_000;

        if (print) {
            System.out.printf("  Run completed with %2d threads in %6d ms%n", threads, duration);
        }

        return duration;
    }
}
