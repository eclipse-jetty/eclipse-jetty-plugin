// Licensed under the Apache License, Version 2.0 (the "License");
package net.sourceforge.eclipsejetty.starter.common.command;

import net.sourceforge.eclipsejetty.starter.common.DumpableServerAdapter;
import net.sourceforge.eclipsejetty.starter.console.AbstractCommand;
import net.sourceforge.eclipsejetty.starter.console.ConsoleAdapter;
import net.sourceforge.eclipsejetty.starter.console.Process;

public class DumpCommand extends AbstractCommand
{

    private final DumpableServerAdapter serverAdapter;

    public DumpCommand(ConsoleAdapter consoleAdapter, DumpableServerAdapter serverAdapter)
    {
        super(consoleAdapter, "dump", "d");

        this.serverAdapter = serverAdapter;
    }

    public String getFormat()
    {
        return "";
    }

    public String getDescription()
    {
        return "Dump the state of Jetty.";
    }

    @Override
    protected String getHelpDescription()
    {
        return "The dump feature in Jetty provides a good snapshot of the status of the threadpool, select sets, classloaders, and so forth. "
            + "You can use \"dump > file.txt\" to write the dump to a file.";
    }

    public int getOrdinal()
    {
        return 9000;
    }

    public int execute(String processName, Process process) throws Exception
    {
        process.out.println(serverAdapter.dump());

        return 0;
    }

}
