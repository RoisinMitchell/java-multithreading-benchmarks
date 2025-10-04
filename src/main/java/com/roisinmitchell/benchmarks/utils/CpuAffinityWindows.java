package com.roisinmitchell.benchmarks.utils;

import com.sun.jna.platform.win32.*;
import com.sun.jna.platform.win32.WinNT.HANDLE;

public class CpuAffinityWindows {

    public static void setCurrentThreadAffinity(int core) {
        HANDLE thread = Kernel32.INSTANCE.GetCurrentThread();
        BaseTSD.ULONG_PTR mask = new BaseTSD.ULONG_PTR(1L << core);

        BaseTSD.ULONG_PTR result = Kernel32Extended.INSTANCE.SetThreadAffinityMask(thread, mask);

        if (result.longValue() == 0) {
            throw new RuntimeException("Failed to set thread affinity: " + Kernel32.INSTANCE.GetLastError());
        }
    }

    public static int getCurrentProcessorNumber() {
        return Kernel32Extended.INSTANCE.GetCurrentProcessorNumber();
    }
}
