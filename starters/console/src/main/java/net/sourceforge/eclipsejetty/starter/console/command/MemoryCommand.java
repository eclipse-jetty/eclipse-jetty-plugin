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
package net.sourceforge.eclipsejetty.starter.console.command;

import net.sourceforge.eclipsejetty.starter.console.AbstractCommand;
import net.sourceforge.eclipsejetty.starter.console.ArgumentException;
import net.sourceforge.eclipsejetty.starter.console.ConsoleAdapter;
import net.sourceforge.eclipsejetty.starter.console.Process;
import net.sourceforge.eclipsejetty.starter.console.util.MemoryUtils;
import net.sourceforge.eclipsejetty.starter.util.Utils;

/**
 * Prints memory usage.
 * 
 * @author Manfred Hantschel
 */
public class MemoryCommand extends AbstractCommand
{

    public MemoryCommand(ConsoleAdapter consoleAdapter)
    {
        super(consoleAdapter, "memory", "m");
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.console.Command#getFormat()
     */
    public String getFormat()
    {
        return "[gc]";
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.console.Command#getDescription()
     */
    public String getDescription()
    {
        return "Memory utilities.";
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.console.AbstractCommand#getHelpDescription()
     */
    @Override
    protected String getHelpDescription()
    {
        return "Prints memory information to the console. If invoked with the gc command, it "
            + "performs a garbage collection.";
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.console.Command#getOrdinal()
     */
    public int getOrdinal()
    {
        return 500;
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.console.Command#execute(java.lang.String,
     *      net.sourceforge.eclipsejetty.starter.console.Process)
     */
    public int execute(String commandName, Process process)
    {
        String command = process.args.consumeString();

        long freeMemory = MemoryUtils.printMemoryUsage(process.out);

        if (command == null)
        {
            return 0;
        }

        if ("gc".equalsIgnoreCase(command))
        {
            return gc(process, freeMemory);
        }

        throw new ArgumentException(String.format("Invalid command: %s", command));
    }

    private int gc(Process process, long freeMemory)
    {
        process.out.println();
        process.out.print("Performing GC...");

        long millis = System.nanoTime();

        System.gc();

        process.out.printf(" [%s]\n", Utils.formatSeconds((System.nanoTime() - millis) / 1000000000d));
        process.out.println();

        long newFreeMemory = MemoryUtils.printMemoryUsage(process.out);

        process.out.println();
        process.out.printf("Saved Memory:      %13s\n", Utils.formatBytes(newFreeMemory - freeMemory));

        return 0;
    }

}
