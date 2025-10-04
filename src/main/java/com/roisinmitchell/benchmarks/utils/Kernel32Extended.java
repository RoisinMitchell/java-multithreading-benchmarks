package com.roisinmitchell.benchmarks.utils;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.platform.win32.Kernel32;

public interface Kernel32Extended extends Kernel32 {
    Kernel32Extended INSTANCE = Native.load("kernel32", Kernel32Extended.class);
    BaseTSD.ULONG_PTR SetThreadAffinityMask(HANDLE hThread, BaseTSD.ULONG_PTR dwThreadAffinityMask);

    int GetCurrentProcessorNumber();
}
