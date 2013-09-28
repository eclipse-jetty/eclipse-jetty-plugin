// Licensed under the Apache License, Version 2.0 (the "License");
package net.sourceforge.eclipsejetty.starter.console.util;

import java.io.PrintStream;

import net.sourceforge.eclipsejetty.starter.util.Utils;

public class MemoryUtils
{

    public static long printMemoryUsage(PrintStream out)
    {
        Runtime runtime = Runtime.getRuntime();

        long freeMemory = runtime.freeMemory();
        long totalMemory = runtime.totalMemory();
        long maxMemory = runtime.maxMemory();
        long usedMemory = totalMemory - freeMemory;

        out.printf("Free Memory:       %13s\n", Utils.formatBytes(freeMemory));
        out.printf("Used Memory:       %13s\n", Utils.formatBytes(usedMemory));
        out.printf("Total Memory:      %13s\n", Utils.formatBytes(totalMemory));
        out.printf("Maximum Memory:    %13s\n", Utils.formatBytes(maxMemory));
        out.printf("Number of Threads: %,8d     \n", Thread.activeCount());

        return freeMemory;
    }

}
