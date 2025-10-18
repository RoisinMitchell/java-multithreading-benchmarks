package com.roisinmitchell.benchmarks.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Writes benchmark results to a timestamped CSV file.
 */
public class BenchmarkLogger implements AutoCloseable {
    private final PrintWriter writer;
    private final String filename;

    /**
     * Creates a new CSV log file with a timestamped name.
     */
    public BenchmarkLogger(String baseName) throws IOException {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        this.filename = baseName + "_" + timestamp + ".csv";
        this.writer = new PrintWriter(new FileWriter(filename));

        writer.println("matrix_size,core_count,cores_used,parallelism,avg_time_ms");
        writer.flush();
    }

    /**
     * Appends one benchmark result to the CSV file.
     */
    public void logResult(int matrixSize, int coreCount, String coresUsed,
                          int parallelism, long avgTimeMs) {
        writer.printf("%d,%d,\"%s\",%d,%d%n",
                matrixSize, coreCount, coresUsed, parallelism, avgTimeMs);
        writer.flush();
    }

    /** Returns the current log file name. */
    public String getFilename() {
        return filename;
    }

    /** Closes the log file. */
    @Override
    public void close() {
        writer.close();
    }
}
