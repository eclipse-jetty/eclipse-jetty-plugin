// Licensed under the Apache License, Version 2.0 (the "License");
package net.sourceforge.eclipsejetty.starter.console.command;

import net.sourceforge.eclipsejetty.starter.console.AbstractCommand;
import net.sourceforge.eclipsejetty.starter.console.Context;

public class EchoCommand extends AbstractCommand
{

    public EchoCommand()
    {
        super("echo");
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

    public int execute(Context context) throws Exception
    {
        StringBuilder builder = new StringBuilder();
        String parameter;
        
        while ((parameter = context.consumeStringParameter()) != null) {
            if (builder.length() > 0) {
                builder.append(" ");
            }
            
            builder.append(parameter);
        }
        
        context.out.println(builder.toString());
        
        return 0;
    }

    @Override
    protected String getHelpDescription()
    {
        return "Prints the text. May contain ${..} placeholders.";
    }
    
}
