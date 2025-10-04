package com.roisinmitchell.benchmarks.tasks;

import com.roisinmitchell.benchmarks.BenchmarkTask;

/**
 * A CPU-bound benchmark task that performs dense matrix multiplication
 * on randomly generated square matrices of configurable size.
 * <p>
 * This workload is designed to stress the CPU’s floating-point units
 * and demonstrate the effects of multithreading, core utilization,
 * and oversubscription on real hardware.
 * </p>
 *
 * <p>
 * The result of the computation is reduced to a single value
 * ({@code c[0][0]}) to prevent the JVM from optimizing away
 * the matrix multiplication as dead code.
 * </p>
 *
 * <p><b>Benchmark characteristics:</b></p>
 * <ul>
 *   <li>CPU-bound workload (no I/O)</li>
 *   <li>Scales with matrix size and thread count</li>
 *   <li>Demonstrates cache pressure and CPU core contention</li>
 * </ul>
 *
 * <p><b>Example:</b></p>
 * <pre>{@code
 * BenchmarkTask task = new MatrixMultiplicationTask(400);
 * runner.runAll(task, new int[]{1, 2, 4, 8, 12}, 1, 3);
 * }</pre>
 *
 * @author Roisin Mitchell
 * @version 1.0
 * @since JDK 21
 */
public class MatrixMultiplicationTask extends BenchmarkTask {

    /** The size of the square matrices (e.g., 400 = 400x400). */
    private final int size;

    /**
     * Constructs a new matrix multiplication benchmark with the given size.
     *
     * @param size the dimension of the matrices to multiply
     */
    public MatrixMultiplicationTask(int size) {
        this.size = size;
    }

    /**
     * Executes the matrix multiplication benchmark.
     * <p>
     * Generates two random matrices, multiplies them,
     * and returns a derived value to prevent JIT elimination.
     * </p>
     *
     * @return a numeric result from the output matrix to ensure computation validity
     */
    @Override
    public Long call() {
        double[][] a = randomMatrix(size);
        double[][] b = randomMatrix(size);
        double[][] c = multiply(a, b);

        // Return a value so that the JIT compiler cannot optimize away unused work
        return (long) c[0][0];
    }

    /**
     * Returns a descriptive name for this benchmark, including matrix size.
     *
     * @return the name of the benchmark task (e.g. "Matrix Multiplication (400x400)")
     */
    @Override
    public String getName() {
        return "Matrix Multiplication (" + size + "x" + size + ")";
    }

    /**
     * Multiplies two dense square matrices using the classic O(n³) algorithm.
     * <p>
     * This method intentionally uses a simple, naive algorithm to maximize
     * CPU workload and make scaling effects visible during benchmarking.
     * </p>
     *
     * @param firstMatrix  the left-hand matrix (A)
     * @param secondMatrix the right-hand matrix (B)
     * @return the resulting matrix (A × B)
     */
    private double[][] multiply(double[][] firstMatrix, double[][] secondMatrix) {
        int rows = firstMatrix.length;
        int cols = secondMatrix[0].length;
        int common = secondMatrix.length;

        double[][] resultMatrix = new double[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double sum = 0;
                for (int k = 0; k < common; k++) {
                    sum += firstMatrix[i][k] * secondMatrix[k][j];
                }
                resultMatrix[i][j] = sum;
            }
        }

        return resultMatrix;
    }

    /**
     * Generates a random square matrix filled with double-precision values.
     *
     * @param size the matrix dimension
     * @return a randomly populated square matrix
     */
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
