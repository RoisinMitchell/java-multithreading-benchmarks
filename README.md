# Java Multithreading Benchmarks

A lightweight and extensible Java framework designed to explore how threads and CPU cores interact on real hardware.  
This project focuses on understanding multithreading performance — how it scales across multiple cores, and where it begins to plateau or decline when oversubscribed.

It was built to provide a hands-on, code-based environment for studying **Java concurrency**, **JVM optimisation**, and **hardware-level scheduling** in a controlled and reproducible way.

---

## Overview

Modern CPUs can run many threads in parallel, but real-world performance depends on how the **JVM**, **operating system scheduler**, and **CPU hardware** cooperate.

This framework was built to:

- Benchmark multithreaded workloads such as matrix multiplication under controlled conditions  
- Experiment with CPU affinity, allowing threads to be pinned to specific physical cores (on Windows)  
- Observe performance scaling, oversubscription effects, and scheduling behaviour  
- Collect reproducible performance data across multiple runs using consistent warm-up and timing logic  

It provides a modular structure where you can plug in new workloads (for example, sorting, numerical computation, or data processing) and run them under different core and thread configurations.

---

## Architecture

The framework is structured around four main components:

| Component | Description |
|------------|--------------|
| **BenchmarkTask** | Defines the workload to be executed (for example, matrix multiplication). |
| **BenchmarkRunner** | Handles task execution, thread creation, timing, and optional CPU affinity control. |
| **BenchmarkExperiment** | Coordinates multiple benchmark runs across core and thread configurations. |
| **BenchmarkLogger** | Logs results into timestamped CSV files for later analysis or visualisation. |

These components together allow repeatable, fine-grained benchmarking while minimising JVM and OS interference.

---

## Tech Stack

| Category | Details |
|-----------|----------|
| **Language** | Java 21 |
| **Build Tool** | Maven |
| **Operating System** | Windows 11 |
| **Hardware (Test System)** | AMD Ryzen 9 5900X (12 cores / 24 threads), 32 GB DDR5 |
| **Dependencies** | [`jna`](https://github.com/java-native-access/jna) and `jna-platform` — used for calling Windows APIs to set CPU affinity |

---

## Running the Benchmark

### 1. Clone the repository

```bash
git clone https://github.com/<your-username>/java-multithreading-benchmarks.git
cd java-multithreading-benchmarks
```

### 2. Build and run

```bash
mvn clean compile exec:java -Dexec.mainClass="com.roisinmitchell.benchmarks.Main"
```

### 3. Output and logs

When executed, the framework:

- Runs a benchmark task (for example, matrix multiplication)  
- Varies thread counts and core configurations  
- Measures average execution time over several warm-up and measured runs  
- Writes all results to a CSV log file for later visualisation  

---

## Example Output

```
==============================
Running on cores: 0 
==============================

>>> Testing core set [0] with parallelism=1
Generating matrices (1400x1400)...
  Run completed with  1 threads in   6619 ms
  Run completed with  1 threads in   6525 ms
  Run completed with  1 threads in   6635 ms

>>> Testing core set [0] with parallelism=2
Generating matrices (1400x1400)...
  Run completed with  1 threads in   2911 ms
  Run completed with  1 threads in   2921 ms
  Run completed with  1 threads in   2866 ms

>>> Testing core set [0] with parallelism=4
Generating matrices (1400x1400)...
  Run completed with  1 threads in   3022 ms
  Run completed with  1 threads in   7905 ms
  Run completed with  1 threads in   7879 ms
```

Example CSV log:

```
matrix_size,core_count,cores_used,parallelism,avg_time_ms
1200,4,"[0,1,2,3]",4,957
1200,4,"[0,1,2,3]",8,1894
1200,12,"[0..11]",16,475
```

Each row represents a benchmark configuration — combining **matrix size**, **core count**, and **internal thread parallelism** — with the resulting average runtime in milliseconds.

---

## What You Can Learn

This framework helps illustrate the real limits of multithreading in Java:

- How performance scales as thread count increases  
- When oversubscription begins to degrade performance  
- How CPU pinning stabilises results by preventing OS thread migration  
- Why JVM warm-up (JIT optimisation) is essential for accurate benchmarking  

---

## Author

**Roisin Mitchell**  
Developed as part of a personal exploration into Java concurrency, performance tuning, and real hardware scheduling behaviour.  
It demonstrates how software-level parallelism interacts with the physical limits of CPU architecture.

---

## Future Improvements

- Add Linux/macOS CPU affinity support (via JNI or system calls)  
- Extend workloads to include memory-bound and I/O-bound tasks  
- Integrate JVM profiling for deeper performance analysis  
