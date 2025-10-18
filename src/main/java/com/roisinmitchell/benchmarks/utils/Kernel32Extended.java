package com.roisinmitchell.benchmarks.utils;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.platform.win32.Kernel32;

/**
 * JNA interface extending {@link Kernel32} to include
 * Windows API functions for setting and getting thread CPU affinity.
 */
public interface Kernel32Extended extends Kernel32 {

    /** Instance bound to the native kernel32.dll library. */
    Kernel32Extended INSTANCE = Native.load("kernel32", Kernel32Extended.class);

    /**
     * Sets the CPU affinity mask for a thread, limiting it to specific cores.
     *
     * @param hThread handle to the thread
     * @param dwThreadAffinityMask bitmask of allowed cores (e.g. 1L << coreId)
     * @return previous affinity mask, or zero if failed
     */
    BaseTSD.ULONG_PTR SetThreadAffinityMask(HANDLE hThread, BaseTSD.ULONG_PTR dwThreadAffinityMask);

    /**
     * Gets the current logical processor number running this thread.
     *
     * @return zero-based processor number
     */
    int GetCurrentProcessorNumber();
}
