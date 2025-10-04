# Java Multithreading Benchmarks

A lightweight, extensible Java framework for exploring multithreading performance on real hardware.
It provides a modular structure for defining benchmark tasks, controlling thread-to-core affinity, and running reproducible CPU-bound experiments.

## Overview

This project was built to study how threads and cores interact on modern CPUs including how performance scales when the number of threads exceeds physical cores.

**The framework allows:**

- Defining workloads as independent benchmark tasks

- Running benchmarks across configurable thread counts

- Measuring average performance with warm-up and measured runs

- Optionally pinning threads to specific CPU cores (Windows only) using JNA

## Tech Stack

- Language: Java 21

- Build Tool: Maven

- OS: Windows 11

- Hardware (Test System): AMD Ryzen 9 5900X, 32 GB DDR5

**Dependencies:**

jna and jna-platform (for Windows CPU affinity)

## Running the Benchmark

**Clone the repository:**
git clone https://github.com/<your-username>/java-multithreading-benchmarks.git

**Build and run:**
mvn clean compile exec:java -Dexec.mainClass="com.roisinmitchell.benchmarks.Main"

## Example Output
=== Multithreading Benchmark: Matrix Multiplication ===<br>
CPU: AMD Ryzen 9 5900X | OS: Windows 11 | JDK 21<br>

Threads:  1 | Avg Time:  856 ms<br>
Threads:  2 | Avg Time:  447 ms<br>
Threads:  4 | Avg Time:  239 ms<br>
Threads:  8 | Avg Time:  130 ms<br>
Threads: 12 | Avg Time:  112 ms<br>
Threads: 24 | Avg Time:  118 ms<br>


## Author

Roisin Mitchell<br>
Built as part of a personal exploration into Java concurrency, performance tuning, and real hardware behavior.