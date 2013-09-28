// Licensed under the Apache License, Version 2.0 (the "License");
package net.sourceforge.eclipsejetty.starter.common.command;

import net.sourceforge.eclipsejetty.starter.common.ServerAdapter;
import net.sourceforge.eclipsejetty.starter.console.AbstractCommand;
import net.sourceforge.eclipsejetty.starter.console.Context;

public class InfoCommand extends AbstractCommand
{

    private final ServerAdapter adapter;

    public InfoCommand(ServerAdapter adapter)
    {
        super("info", "i");

        this.adapter = adapter;
    }

    public String getFormat()
    {
        return "";
    }

    public String getDescription()
    {
        return "Show the launcher info.";
    }

    protected String getHelpDescription()
    {
        return "Show the launcher info.  You can use \"info > file.txt\" to write the info to a file.";
    }

    public int getOrdinal()
    {
        return 9010;
    }

    public int execute(Context context) throws Exception
    {
        adapter.info(context.out);

        return 0;
    }

}
