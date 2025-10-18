package com.roisinmitchell.benchmarks;

import com.roisinmitchell.benchmarks.tasks.MatrixMultiplicationTask;
import com.roisinmitchell.benchmarks.utils.BenchmarkLogger;

import java.util.Arrays;

/**
 * Runs a series of matrix multiplication benchmarks
 * across different core configurations and thread levels.
 */
public class BenchmarkExperiment {
    private final int matrixSize;
    private final int[] parallelismLevels;
    private final int[][] coreConfigs;
    private final int warmupRuns;
    private final int measuredRuns;
    private final BenchmarkLogger logger;

    /**
     * Creates a new experiment with the given configuration.
     */
    public BenchmarkExperiment(int matrixSize, int[] parallelismLevels, int[][] coreConfigs,
                               int warmupRuns, int measuredRuns, BenchmarkLogger logger) {
        this.matrixSize = matrixSize;
        this.parallelismLevels = parallelismLevels;
        this.coreConfigs = coreConfigs;
        this.warmupRuns = warmupRuns;
        this.measuredRuns = measuredRuns;
        this.logger = logger;
    }

    /**
     * Runs all benchmark combinations and logs the results to a CSV file.
     */
    public void runAll() {
        System.out.println("Detected available processors: " + Runtime.getRuntime().availableProcessors());

        for (int[] cores : coreConfigs) {
            System.out.println("\n==============================");
            System.out.print("Running on cores: ");
            for (int c : cores) System.out.print(c + " ");
            System.out.println("\n==============================");

            BenchmarkRunner runner = new BenchmarkRunner(cores);

            for (int parallelism : parallelismLevels) {
                System.out.printf(">>> Testing core set %s with parallelism=%d%n",
                        Arrays.toString(cores), parallelism);

                BenchmarkTask task = new MatrixMultiplicationTask(matrixSize, parallelism, cores);

                try {
                    // Run the benchmark and record average runtime
                    long avgTime = runner.runBenchmark(task, 1, warmupRuns, measuredRuns);
                    logger.logResult(matrixSize, cores.length, Arrays.toString(cores), parallelism, avgTime);
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage());
                }
            }

            // Pause briefly between configurations
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {}
        }

        System.out.println("\nExperiment complete.");
        System.out.println("Results written to: " + logger.getFilename());
    }
}
