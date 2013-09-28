// Licensed under the Apache License, Version 2.0 (the "License");
package net.sourceforge.eclipsejetty.starter.console.command;

import net.sourceforge.eclipsejetty.starter.console.AbstractCommand;
import net.sourceforge.eclipsejetty.starter.console.Context;
import net.sourceforge.eclipsejetty.starter.console.ParameterException;
import net.sourceforge.eclipsejetty.starter.console.util.MemoryUtils;
import net.sourceforge.eclipsejetty.starter.util.Utils;


public class MemoryCommand extends AbstractCommand
{

    public MemoryCommand()
    {
        super("memory", "m");
    }

    public String getFormat()
    {
        return "[gc]";
    }

    public String getDescription()
    {
        return "Memory utilities.";
    }

    protected String getHelpDescription()
    {
        return "Prints memory information to the console. If invoked with the gc command, it "
            + "performs a garbage collection.";
    }

    public int getOrdinal()
    {
        return 500;
    }

    public int execute(Context context)
    {
        String command = context.consumeStringParameter();
        
        long freeMemory = MemoryUtils.printMemoryUsage(context.out);

        if (command == null) {
            return 0;
        }
        
        if ("gc".equalsIgnoreCase(command)) {
            return gc(context, freeMemory);
        }

        throw new ParameterException("Invalid command: " + command);
    }

    private int gc(Context context, long freeMemory)
    {
        context.out.println();
        context.out.print("Performing GC...");

        long millis = System.nanoTime();
        
        System.gc();
        
        context.out.printf(" [%s]\n", Utils.formatSeconds((double)(System.nanoTime() - millis) / 1000000000d));
        context.out.println();

        long newFreeMemory = MemoryUtils.printMemoryUsage(context.out);

        context.out.println();
        context.out.printf("Saved Memory:      %13s\n", Utils.formatBytes(newFreeMemory - freeMemory));

        return 0;
    }

}
