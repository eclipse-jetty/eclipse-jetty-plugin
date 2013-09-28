// Licensed under the Apache License, Version 2.0 (the "License");
package net.sourceforge.eclipsejetty.starter.console.util;

import net.sourceforge.eclipsejetty.starter.console.Command;

public class CommandUtils
{

    public static String getNameDescriptor(Command command, boolean withFormat)
    {
        StringBuilder result = new StringBuilder();

        for (String name : command.getNames())
        {
            if (result.length() > 0)
            {
                result.append(", ");
            }

            result.append(name);
        }

        if ((withFormat) && (command.getFormat().length() > 0))
        {
            result.append(" <arg>");
        }

        return result.toString();
    }

    public static String getFormatDescriptor(Command command)
    {
        return command.getNames()[0] + " " + command.getFormat();
    }

}
