package com.roisinmitchell.benchmarks.tasks;

import com.roisinmitchell.benchmarks.BenchmarkTask;
import com.roisinmitchell.benchmarks.utils.CpuAffinityWindows;

public class MatrixMultiplicationTask extends BenchmarkTask {
    private final int size;
    private final int parallelism;
    private final int[] coreList;

    private final double[][] firstMatrix;
    private final double[][] secondMatrix;

    public MatrixMultiplicationTask(int size, int parallelism, int[] coreList) {
        this.size = size;
        this.parallelism = parallelism;
        this.coreList = coreList;

        System.out.println("Generating matrices (" + size + "x" + size + ")...");
        this.firstMatrix = randomMatrix(size);
        this.secondMatrix = randomMatrix(size);
    }

    public MatrixMultiplicationTask(int size, int parallelism) {
        this(size, parallelism, null);
    }

    @Override
    public Long call() {
        double[][] result = multiplyManualParallel(firstMatrix, secondMatrix);
        return (long) result[0][0]; // prevent JIT optimization
    }

    @Override
    public String getName() {
        return "Matrix Multiplication (" + size + "x" + size + ") [Manual " + parallelism + " threads]";
    }

    private double[][] multiplyManualParallel(double[][] a, double[][] b) {
        int rows = a.length;
        int cols = b[0].length;
        int common = b.length;
        double[][] result = new double[rows][cols];

        Thread[] threads = new Thread[parallelism];
        int rowsPerThread = (int) Math.ceil(rows / (double) parallelism);

        for (int t = 0; t < parallelism; t++) {
            final int threadId = t;
            final int startRow = t * rowsPerThread;
            final int endRow = Math.min(rows, startRow + rowsPerThread);

            threads[t] = new Thread(() -> {
                if (coreList != null && coreList.length > 0) {
                    int core = coreList[threadId % coreList.length];
                    try {
                        CpuAffinityWindows.setCurrentThreadAffinity(core);
                    } catch (Exception e) {
                        System.out.println("Affinity failed for thread " + threadId + ": " + e.getMessage());
                    }
                }

                for (int i = startRow; i < endRow; i++) {
                    double[] row = a[i];
                    double[] resRow = result[i];
                    for (int j = 0; j < cols; j++) {
                        double sum = 0;
                        for (int k = 0; k < common; k++) {
                            sum += row[k] * b[k][j];
                        }
                        resRow[j] = sum;
                    }
                }
            }, "MatMul-" + t);
            threads[t].start();
        }

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return result;
    }

    private double[][] randomMatrix(int size) {
        double[][] matrix = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = Math.random();
            }
        }
        return matrix;
    }
}
