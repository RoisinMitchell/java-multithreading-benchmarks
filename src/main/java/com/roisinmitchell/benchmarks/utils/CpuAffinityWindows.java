package com.roisinmitchell.benchmarks.utils;

import com.sun.jna.platform.win32.*;
import com.sun.jna.platform.win32.WinNT.HANDLE;

/**
 * Provides methods to pin Java threads to specific CPU cores on Windows.
 * Uses the Windows API via JNA.
 */
public class CpuAffinityWindows {

    /**
     * Pins the current thread to the given CPU core.
     *
     * @param core zero-based index of the CPU core
     */
    public static void setCurrentThreadAffinity(int core) {
        HANDLE thread = Kernel32.INSTANCE.GetCurrentThread();
        BaseTSD.ULONG_PTR mask = new BaseTSD.ULONG_PTR(1L << core);

        BaseTSD.ULONG_PTR result =
                Kernel32Extended.INSTANCE.SetThreadAffinityMask(thread, mask);

        if (result.longValue() == 0) {
            throw new RuntimeException(
                    "Failed to set thread affinity: " + Kernel32.INSTANCE.GetLastError()
            );
        }
    }

    /**
     * Returns the current logical processor running this thread.
     *
     * @return zero-based processor number
     */
    public static int getCurrentProcessorNumber() {
        return Kernel32Extended.INSTANCE.GetCurrentProcessorNumber();
    }
}
