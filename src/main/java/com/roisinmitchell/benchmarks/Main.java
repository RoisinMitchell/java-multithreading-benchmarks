package com.roisinmitchell.benchmarks;

import com.roisinmitchell.benchmarks.tasks.MatrixMultiplicationTask;

public class Main {
    public static void main(String[] args) {
        int matrixSize = 400;
        int[] threadCounts = {1, 2, 4, 8, 12, 24};
        int warmupRuns = 1;
        int measuredRuns = 3;

        BenchmarkTask task = new MatrixMultiplicationTask(matrixSize);
        BenchmarkRunner runner = new BenchmarkRunner();

        runner.runAll(task, threadCounts, warmupRuns, measuredRuns);

        System.out.println("--------------------------------------------------");
        System.out.println("Benchmark complete.");
    }
}
