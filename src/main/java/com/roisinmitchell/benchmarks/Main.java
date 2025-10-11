package com.roisinmitchell.benchmarks;

import com.roisinmitchell.benchmarks.tasks.MatrixMultiplicationTask;

public class Main {
    public static void main(String[] args) {
        int matrixSize = 1000; // Increase for longer runs (e.g. 1000 or 2000)
        int warmupRuns = 3;
        int measuredRuns = 4;

        // Internal algorithm parallelism levels
        int[] parallelismLevels = {1, 4, 8, 16, 32, 64, 128};

        // Hardware core configurations (these control where threads are pinned)
        int[][] coreConfigs = {
                {0},
                {0, 1},
                {0, 1, 2},
                {0, 1, 2, 3},
                {0, 1, 2, 3, 4}
        };

        for (int[] cores : coreConfigs) {
            System.out.println("\n==============================");
            System.out.print("Running on cores: ");
            for (int c : cores) System.out.print(c + " ");
            System.out.println("\n==============================");

            // Pin the outer runner thread(s)
            BenchmarkRunner runner = new BenchmarkRunner(cores);

            for (int parallelism : parallelismLevels) {
                // ✅ Pass cores into the task so inner threads are pinned properly
                BenchmarkTask task = new MatrixMultiplicationTask(matrixSize, parallelism, cores);

                // Run with one outer thread — internal threads handle parallelism
                runner.runAll(task, new int[]{1}, warmupRuns, measuredRuns);
            }
        }

        System.out.println("\n--------------------------------------------------");
        System.out.println("Experiment complete.");
    }
}
