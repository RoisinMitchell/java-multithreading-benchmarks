package com.roisinmitchell.benchmarks;

import com.roisinmitchell.benchmarks.utils.BenchmarkLogger;

public class Main {
    public static void main(String[] args) {
        int matrixSize = 1400;
        int warmupRuns = 3;
        int measuredRuns = 3;

        int[] parallelismLevels = {1, 2, 4, 8, 16, 32, 64, 128, 256};
        int[][] coreConfigs = {
                {0}
//                {0, 1},
//                {0, 1, 2},
//                {0, 1, 2, 3},
//                {0, 1, 2, 3, 4},
//                {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11}
        };

        try (BenchmarkLogger logger = new BenchmarkLogger("matrix_results")) {
            BenchmarkExperiment experiment = new BenchmarkExperiment(
                    matrixSize,
                    parallelismLevels,
                    coreConfigs,
                    warmupRuns,
                    measuredRuns,
                    logger
            );

            experiment.runAll();
        } catch (Exception e) {
            System.err.println("Benchmark failed: " + e.getMessage());
        }
    }
}
