// Licensed under the Apache License, Version 2.0 (the "License");
package net.sourceforge.eclipsejetty.starter.console.command;

import net.sourceforge.eclipsejetty.starter.console.AbstractCommand;
import net.sourceforge.eclipsejetty.starter.console.ConsoleAdapter;
import net.sourceforge.eclipsejetty.starter.console.Process;
import net.sourceforge.eclipsejetty.starter.util.Utils;

public class ExitCommand extends AbstractCommand
{

    public ExitCommand(ConsoleAdapter consoleAdapter)
    {
        super(consoleAdapter, "exit", "x");
    }

    public String getFormat()
    {
        return Utils.BLANK;
    }

    public String getDescription()
    {
        return "Exits the VM.";
    }

    @Override
    protected String getHelpDescription()
    {
        return "Exists the VM. The server may not have enougth time to shutdown gracefully.";
    }

    public int getOrdinal()
    {
        return 10010;
    }

    public int execute(String processName, Process process)
    {
        process.out.println("Bye.");

        System.exit(0);

        return 0;
    }

}
