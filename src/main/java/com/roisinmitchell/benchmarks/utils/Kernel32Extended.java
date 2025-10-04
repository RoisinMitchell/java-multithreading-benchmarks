package com.roisinmitchell.benchmarks.utils;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT.HANDLE;

/**
 * JNA (Java Native Access) interface that extends the standard {@link Kernel32}
 * mapping to include additional Windows API functions used for thread and CPU
 * affinity control.
 * <p>
 * This interface allows Java code to call specific native methods from the
 * Windows {@code kernel32.dll} library, such as
 * {@code SetThreadAffinityMask} and {@code GetCurrentProcessorNumber}.
 * These functions enable direct manipulation of thread-core mapping, providing
 * finer control during benchmarking and multithreading experiments.
 * </p>
 *
 * <p>
 * This interface is Windows-specific and should only be used when the runtime
 * environment supports JNA and the native {@code kernel32} library.
 * </p>
 *
 * <p><b>Technical reference:</b></p>
 * <ul>
 *   <li>{@code SetThreadAffinityMask} – restricts a thread to specific CPU cores</li>
 *   <li>{@code GetCurrentProcessorNumber} – returns the current core executing the thread</li>
 * </ul>
 *
 * <p><b>Example usage:</b></p>
 * <pre>{@code
 * HANDLE thread = Kernel32Extended.INSTANCE.GetCurrentThread();
 * BaseTSD.ULONG_PTR mask = new BaseTSD.ULONG_PTR(1L << 2); // pin to core #2
 * Kernel32Extended.INSTANCE.SetThreadAffinityMask(thread, mask);
 *
 * int currentCore = Kernel32Extended.INSTANCE.GetCurrentProcessorNumber();
 * System.out.println("Running on core: " + currentCore);
 * }</pre>
 *
 * @author Roisin Mitchell
 * @version 1.0
 * @since JDK 21
 */
public interface Kernel32Extended extends Kernel32 {

    /**
     * Singleton instance used to access the native {@code kernel32.dll} functions.
     * <p>
     * The JNA {@link Native#load(String, Class)} method dynamically binds this
     * interface to the system’s kernel32 library at runtime.
     * </p>
     */
    Kernel32Extended INSTANCE = Native.load("kernel32", Kernel32Extended.class);

    /**
     * Sets the processor affinity mask for a thread.
     * <p>
     * This determines which CPU cores the specified thread is allowed to run on.
     * Threads are identified using a {@link HANDLE}, and the affinity mask defines
     * one or more cores (bits set to 1).
     * </p>
     *
     * @param hThread the handle to the thread whose affinity is to be set
     * @param dwThreadAffinityMask a bitmask representing allowed cores (e.g., 1 << coreId)
     * @return the previous affinity mask, or zero if the call failed
     */
    BaseTSD.ULONG_PTR SetThreadAffinityMask(HANDLE hThread, BaseTSD.ULONG_PTR dwThreadAffinityMask);

    /**
     * Retrieves the number of the logical processor the calling thread is currently running on.
     *
     * @return the zero-based logical processor number
     */
    int GetCurrentProcessorNumber();
}
