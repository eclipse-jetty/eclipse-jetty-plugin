// Licensed under the Apache License, Version 2.0 (the "License");
package net.sourceforge.eclipsejetty.starter.console.command;

import net.sourceforge.eclipsejetty.starter.console.AbstractCommand;
import net.sourceforge.eclipsejetty.starter.console.Context;


public class ExitCommand extends AbstractCommand
{

    public ExitCommand()
    {
        super("exit");
    }

    public String getFormat()
    {
        return "";
    }

    public String getDescription()
    {
        return "Exits the VM.";
    }

    protected String getHelpDescription()
    {
        return "Exists the VM. The server may not have enougth time to shutdown gracefully.";
    }

    public int getOrdinal()
    {
        return 10010;
    }

    public int execute(Context context)
    {
        context.out.println("Bye.");
        
        System.exit(0);

        return 0;
    }

}
