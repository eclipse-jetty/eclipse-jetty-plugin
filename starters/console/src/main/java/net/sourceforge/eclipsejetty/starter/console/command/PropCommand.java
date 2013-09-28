// Licensed under the Apache License, Version 2.0 (the "License");
package net.sourceforge.eclipsejetty.starter.console.command;

import java.util.Map.Entry;
import java.util.Properties;

import net.sourceforge.eclipsejetty.starter.console.AbstractCommand;
import net.sourceforge.eclipsejetty.starter.console.Context;
import net.sourceforge.eclipsejetty.starter.console.util.WildcardUtils;

public class PropCommand extends AbstractCommand
{

    public PropCommand(String... names)
    {
        super("prop", "p");
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

    public int execute(Context context) throws Exception
    {
        String key = context.consumeStringParameter();

        if (key == null)
        {
            key = "*";
        }

        String value = context.consumeStringParameter();

        if (value == null)
        {
            Properties properties = System.getProperties();

            for (Entry<Object, Object> entry : properties.entrySet())
            {
                if (WildcardUtils.match(String.valueOf(entry.getKey()).toLowerCase(), key.toLowerCase()))
                {
                    context.out.printf("%s = %s\n", entry.getKey(), entry.getValue());
                }
            }
        }
        else
        {
            System.setProperty(key, value);

            context.out.println("Property set.");
        }

        return 0;
    }

    @Override
    protected String getHelpDescription()
    {
        return "Called without parameters, it will display all system properties. "
            + "If one parameter is specified, it will display the specified property (the "
            + "key may then contain wildcards). If two parameters are specified, it will "
            + "try to set the specified property to the specified value.";
    }

}
