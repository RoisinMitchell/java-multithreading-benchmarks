package com.roisinmitchell.benchmarks;

import java.util.concurrent.Callable;

/**
 * An abstract base class representing a unit of work that can be executed
 * and measured by the benchmarking framework.
 * <p>
 * Subclasses of {@code BenchmarkTask} define specific workloads
 * (for example, matrix multiplication, sorting, or CPU-bound loops)
 * and implement the {@link #call()} method to perform the actual computation.
 * </p>
 *
 * <p>
 * The {@link #call()} method returns a numeric result — usually a checksum or
 * derived value — to prevent the JVM from optimising away the computation
 * during benchmarking. The {@link #getName()} method provides a human-readable
 * name for the task, used in output and reports.
 * </p>
 *
 * <p><b>Example subclass:</b></p>
 * <pre>{@code
 * public class MatrixMultiplicationTask extends BenchmarkTask {
 *     @Override
 *     public Long call() {
 *         // Perform computation here
 *         return 123L;
 *     }
 *
 *     @Override
 *     public String getName() {
 *         return "Matrix Multiplication (400x400)";
 *     }
 * }
 * }</pre>
 *
 * @author Roisin Mitchell
 * @version 1.0
 * @since JDK 21
 */
public abstract class BenchmarkTask implements Callable<Long> {

    /**
     * Executes the benchmarked computation.
     * <p>
     * Implementations should perform a repeatable workload whose performance
     * can be measured, and return a small computed value (such as a checksum
     * or one element of a result) to prevent dead-code elimination.
     * </p>
     *
     * @return a computed value derived from the benchmark’s work
     * @throws Exception if an error occurs during computation
     */
    @Override
    public abstract Long call() throws Exception;

    /**
     * Returns a descriptive name for this benchmark task.
     * <p>
     * This name is displayed in console output and reports, and should
     * identify the task type and any relevant parameters (e.g., "Matrix Multiplication (400x400)").
     * </p>
     *
     * @return the human-readable benchmark name
     */
    public abstract String getName();
}
