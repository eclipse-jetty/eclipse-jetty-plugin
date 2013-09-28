// Licensed under the Apache License, Version 2.0 (the "License");
package net.sourceforge.eclipsejetty.starter.common.command;

import net.sourceforge.eclipsejetty.starter.common.ServerAdapter;
import net.sourceforge.eclipsejetty.starter.console.AbstractCommand;
import net.sourceforge.eclipsejetty.starter.console.Context;

public class StopCommand extends AbstractCommand
{

    private final ServerAdapter serverAdapter;

    public StopCommand(ServerAdapter serverAdapter)
    {
        super("stop");

        this.serverAdapter = serverAdapter;
    }

    public String getFormat()
    {
        return "";
    }

    public String getDescription()
    {
        return "Stops the server gracefully.";
    }

    @Override
    protected String getHelpDescription()
    {
        return "Stops the server gracefully.";
    }

    public int getOrdinal()
    {
        return 10000;
    }

    public int execute(Context context) throws Exception
    {
        context.out.println("Stopping the server...");

        serverAdapter.stop();

        return 0;
    }

}
