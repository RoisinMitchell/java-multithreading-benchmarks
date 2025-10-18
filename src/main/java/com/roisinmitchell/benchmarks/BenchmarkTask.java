package com.roisinmitchell.benchmarks;

import java.util.concurrent.Callable;

/**
 * Base class for benchmark tasks.
 * Each subclass defines the work to be measured.
 */
public abstract class BenchmarkTask implements Callable<Long> {

    /**
     * Runs the task’s computation.
     *
     * @return a computed value (used to prevent optimisation)
     */
    @Override
    public abstract Long call() throws Exception;

    /** Returns a short, descriptive name for the task. */
    public abstract String getName();
}
