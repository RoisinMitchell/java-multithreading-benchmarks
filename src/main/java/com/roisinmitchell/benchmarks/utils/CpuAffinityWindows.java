package com.roisinmitchell.benchmarks.utils;

import com.sun.jna.platform.win32.*;
import com.sun.jna.platform.win32.WinNT.HANDLE;

/**
 * Windows-specific utility class for managing CPU core affinity using the
 * native Win32 API via JNA (Java Native Access).
 * <p>
 * Thread affinity determines which physical CPU core a given thread is allowed
 * to execute on. This is useful for low-level performance benchmarking,
 * allowing more controlled experiments on how threads are distributed across
 * cores and hardware threads.
 * </p>
 *
 * <p>
 * This class uses the {@code Kernel32.SetThreadAffinityMask} and
 * {@code Kernel32.GetCurrentProcessorNumber} functions from the Windows API.
 * It will only work correctly on Windows systems with JNA installed.
 * On other operating systems, calls to these methods will fail and should be
 * safely ignored or caught by the caller.
 * </p>
 *
 * <p><b>Usage example:</b></p>
 * <pre>{@code
 * int coreId = 3;
 * try {
 *     CpuAffinityWindows.setCurrentThreadAffinity(coreId);
 *     System.out.println("Pinned to core: " + CpuAffinityWindows.getCurrentProcessorNumber());
 * } catch (Exception e) {
 *     System.err.println("Affinity not supported: " + e.getMessage());
 * }
 * }</pre>
 *
 * @author Roisin Mitchell
 * @version 1.0
 * @since JDK 21
 */
public class CpuAffinityWindows {

    /**
     * Sets the CPU core affinity for the currently running thread.
     * <p>
     * Restricts the thread to execute only on the specified core, identified
     * by its zero-based index. This method uses the native
     * {@code SetThreadAffinityMask} function from the Windows API.
     * </p>
     *
     * @param core the zero-based CPU core index to pin this thread to
     * @throws RuntimeException if the affinity mask could not be applied
     */
    public static void setCurrentThreadAffinity(int core) {
        HANDLE thread = Kernel32.INSTANCE.GetCurrentThread();
        BaseTSD.ULONG_PTR mask = new BaseTSD.ULONG_PTR(1L << core);

        BaseTSD.ULONG_PTR result = Kernel32Extended.INSTANCE.SetThreadAffinityMask(thread, mask);

        if (result.longValue() == 0) {
            throw new RuntimeException(
                    "Failed to set thread affinity: " + Kernel32.INSTANCE.GetLastError()
            );
        }
    }

    /**
     * Returns the logical processor number that the current thread is
     * currently executing on.
     * <p>
     * This can be used to verify whether the thread affinity call succeeded.
     * </p>
     *
     * @return the zero-based processor number for the current thread
     */
    public static int getCurrentProcessorNumber() {
        return Kernel32Extended.INSTANCE.GetCurrentProcessorNumber();
    }
}
