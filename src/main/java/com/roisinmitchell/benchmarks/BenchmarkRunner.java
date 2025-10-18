package com.roisinmitchell.benchmarks;

import com.roisinmitchell.benchmarks.utils.CpuAffinityWindows;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Runs benchmark tasks with configurable thread and core settings.
 */
public class BenchmarkRunner {

    private final Integer fixedCore;
    private final int[] coreList;

    /** Default constructor (no fixed core). */
    public BenchmarkRunner() {
        this.fixedCore = null;
        this.coreList = null;
    }

    /** Runs all threads on a single fixed core. */
    public BenchmarkRunner(int fixedCore) {
        this.fixedCore = fixedCore;
        this.coreList = null;
    }

    /** Uses a custom list of cores for affinity. */
    public BenchmarkRunner(int[] coreList) {
        this.fixedCore = null;
        this.coreList = coreList;
    }

    /** Runs a task for multiple thread counts and prints average times. */
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

    /** Runs warm-up and measured benchmark runs, returning the average time. */
    public long runBenchmark(BenchmarkTask task, int threads, int warmupRuns, int measuredRuns) throws Exception {
        for (int i = 0; i < warmupRuns; i++) {
            runOnce(task, threads, false);
        }

        List<Long> times = new ArrayList<>();
        for (int i = 0; i < measuredRuns; i++) {
            times.add(runOnce(task, threads, true));
        }

        return (long) times.stream().mapToLong(Long::longValue).average().orElse(0);
    }

    /** Runs the benchmark once using a fixed thread pool. */
    private long runOnce(BenchmarkTask task, int threads, boolean print) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        List<Future<Long>> results = new ArrayList<>();

        long start = System.nanoTime();

        for (int i = 0; i < threads; i++) {
            final int threadId = i;
            results.add(pool.submit(() -> {
                int coreId = selectCore(threadId);
                try {
                    CpuAffinityWindows.setCurrentThreadAffinity(coreId);
                } catch (Exception ignored) {}
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

    /** Chooses which CPU core a thread should run on. */
    private int selectCore(int threadId) {
        int available = Runtime.getRuntime().availableProcessors();

        if (fixedCore != null) {
            return fixedCore;
        } else if (coreList != null && coreList.length > 0) {
            return coreList[threadId % coreList.length];
        } else {
            return threadId % available;
        }
    }
}
