// Licensed under the Apache License, Version 2.0 (the "License");
package net.sourceforge.eclipsejetty.starter.console.command;

import java.util.Collection;

import net.sourceforge.eclipsejetty.starter.console.AbstractCommand;
import net.sourceforge.eclipsejetty.starter.console.Command;
import net.sourceforge.eclipsejetty.starter.console.Context;
import net.sourceforge.eclipsejetty.starter.console.util.CommandUtils;
import net.sourceforge.eclipsejetty.starter.console.util.WordWrap;
import net.sourceforge.eclipsejetty.starter.util.Utils;

public class HelpCommand extends AbstractCommand
{

    public HelpCommand()
    {
        super("help", "h", "?");
    }

    /**
     * {@inheritDoc}
     */
    public String getFormat()
    {
        return "[command {parameters}]";
    }

    /**
     * {@inheritDoc}
     */
    public String getDescription()
    {
        return "Shows a list of commands and provides help for each command.";
    }

    @Override
    protected String getHelpDescription()
    {
        return "You can call this help, with or without a command.\n"
            + "If called without a command, it shows a list of all possible commands.\n"
            + "If called with a command, it shows a description of the specified command.";

    }

    /**
     * {@inheritDoc}
     */
    public int getOrdinal()
    {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public int execute(Context context) throws Exception
    {
        String command = context.consumeStringParameter();

        if (command != null)
        {
            showDetailHelp(context, command);
        }
        else
        {
            showHelp(context);
        }

        return 0;
    }

    private void showHelp(Context context)
    {
        Collection<Command> commands = context.console.getCommands();

        int maxNameLength = 0;

        for (Command command : commands)
        {
            maxNameLength = Math.max(maxNameLength, CommandUtils.getNameDescriptor(command, true).length());
        }

        String prefix = Utils.repeat(" ", maxNameLength + 3);

        for (Command command : commands)
        {
            if (command.getOrdinal() < 0)
            {
                continue;
            }

            showHelp(context, command, prefix);
        }

        context.out.println();
        context.out.println(new WordWrap().perform("Using > will pipe the output of any command to a file. "
            + "Arguments may contain ${..} placeholds, to access environment and system properties.",
            context.lineLength));
    }

    private void showHelp(Context context, Command command, String prefix)
    {
        context.out.print(String.format("%-" + prefix.length() + "s", CommandUtils.getNameDescriptor(command, true)));

        context.out.println(Utils.prefixLine(
            new WordWrap().perform(command.getDescription(), context.lineLength - prefix.length()), prefix, false));
    }

    private int showDetailHelp(Context context, String name) throws Exception
    {
        Command command = context.console.getCommand(name);

        if (command == null)
        {
            context.err.println("Unknown command: " + name);

            return -1;
        }

        return command.help(context);
    }

}
