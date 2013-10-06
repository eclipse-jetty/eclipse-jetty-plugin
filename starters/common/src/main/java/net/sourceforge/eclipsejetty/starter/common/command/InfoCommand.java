// Licensed under the Apache License, Version 2.0 (the "License");
package net.sourceforge.eclipsejetty.starter.common.command;

import net.sourceforge.eclipsejetty.starter.common.ServerAdapter;
import net.sourceforge.eclipsejetty.starter.console.AbstractCommand;
import net.sourceforge.eclipsejetty.starter.console.ConsoleAdapter;
import net.sourceforge.eclipsejetty.starter.console.Process;

public class InfoCommand extends AbstractCommand
{

    private final ServerAdapter serverAdapter;

    public InfoCommand(ConsoleAdapter consoleAdapter, ServerAdapter serverAdapter)
    {
        super(consoleAdapter, "info", "i");

        this.serverAdapter = serverAdapter;
    }

    public String getFormat()
    {
        return "";
    }

    public String getDescription()
    {
        return "Show the launcher info.";
    }

    @Override
    protected String getHelpDescription()
    {
        return "Show the launcher info.  You can use \"info > file.txt\" to write the info to a file.";
    }

    public int getOrdinal()
    {
        return 9010;
    }

    public int execute(String processName, Process process) throws Exception
    {
        serverAdapter.info(process.out);

        return 0;
    }

}
