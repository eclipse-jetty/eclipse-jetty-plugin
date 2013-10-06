// Licensed under the Apache License, Version 2.0 (the "License");
package net.sourceforge.eclipsejetty.starter.console.command;

import java.util.Map.Entry;
import java.util.Properties;

import net.sourceforge.eclipsejetty.starter.console.AbstractCommand;
import net.sourceforge.eclipsejetty.starter.console.ConsoleAdapter;
import net.sourceforge.eclipsejetty.starter.console.Process;
import net.sourceforge.eclipsejetty.starter.console.util.WildcardUtils;

public class PropCommand extends AbstractCommand
{

    public PropCommand(ConsoleAdapter consoleAdapter)
    {
        super(consoleAdapter, "prop", "p");
    }

    public String getFormat()
    {
        return "[<KEY> [<VALUE>]]";
    }

    public String getDescription()
    {
        return "Manage system properties.";
    }

    public int getOrdinal()
    {
        return 530;
    }

    public int execute(String processName, Process process) throws Exception
    {
        String key = process.args.consumeString();

        if (key == null)
        {
            key = "*";
        }

        String value = process.args.consumeString();

        if (value == null)
        {
            Properties properties = System.getProperties();

            for (Entry<Object, Object> entry : properties.entrySet())
            {
                if (WildcardUtils.match(String.valueOf(entry.getKey()).toLowerCase(), key.toLowerCase()))
                {
                    process.out.printf("%s = %s\n", entry.getKey(), entry.getValue());
                }
            }
        }
        else
        {
            System.setProperty(key, value);

            process.out.println("Property set.");
        }

        return 0;
    }

    @Override
    protected String getHelpDescription()
    {
        return "Called without arguments, it will display all system properties. "
            + "If one argument is specified, it will display the specified property (the "
            + "key may then contain wildcards). If two arguments are specified, it will "
            + "try to set the specified property to the specified value.";
    }

}
