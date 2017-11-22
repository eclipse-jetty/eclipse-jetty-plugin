// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package net.sourceforge.eclipsejetty.starter.console.util;

import java.io.PrintStream;

import net.sourceforge.eclipsejetty.starter.util.Utils;

/**
 * Common utilities for memory consumption
 * 
 * @author Manfred Hantschel
 */
public class MemoryUtils
{

    /**
     * Prints the memory usage as used by various commands.
     * 
     * @param out the stream
     * @return the free memory
     */
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
