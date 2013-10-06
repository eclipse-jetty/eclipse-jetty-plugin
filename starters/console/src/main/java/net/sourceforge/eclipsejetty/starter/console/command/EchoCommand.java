// Licensed under the Apache License, Version 2.0 (the "License");
package net.sourceforge.eclipsejetty.starter.console.command;

import net.sourceforge.eclipsejetty.starter.console.AbstractCommand;
import net.sourceforge.eclipsejetty.starter.console.ConsoleAdapter;
import net.sourceforge.eclipsejetty.starter.console.Process;

public class EchoCommand extends AbstractCommand
{

    public EchoCommand(ConsoleAdapter consoleAdapter)
    {
        super(consoleAdapter, "echo", "e");
    }

    public String getFormat()
    {
        return "{<TEXT>}";
    }

    public String getDescription()
    {
        return "Prints the text.";
    }

    public int getOrdinal()
    {
        return 550;
    }

    public int execute(String processName, Process process) throws Exception
    {
        StringBuilder builder = new StringBuilder();
        String argument;

        while ((argument = process.args.consumeString()) != null)
        {
            if (builder.length() > 0)
            {
                builder.append(" ");
            }

            builder.append(argument);
        }

        process.out.println(builder.toString());

        return 0;
    }

    @Override
    protected String getHelpDescription()
    {
        return "Prints the text. May contain ${..} placeholders.";
    }

}
