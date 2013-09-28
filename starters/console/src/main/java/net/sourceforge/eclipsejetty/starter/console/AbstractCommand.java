// Licensed under the Apache License, Version 2.0 (the "License");
package net.sourceforge.eclipsejetty.starter.console;

import net.sourceforge.eclipsejetty.starter.console.util.CommandUtils;
import net.sourceforge.eclipsejetty.starter.console.util.WordWrap;

public abstract class AbstractCommand implements Command
{

    private final String[] names;

    public AbstractCommand(String... names)
    {
        super();

        this.names = names;
    }

    /**
     * {@inheritDoc}
     */
    public String[] getNames()
    {
        return names;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {
        return true;
    }

    public int help(Context context) throws Exception
    {
        context.out.println(new WordWrap().perform("Usage:    " + CommandUtils.getFormatDescriptor(this),
            context.lineLength));

        if (getNames().length > 1)
        {
            context.out.println("Synonyms: " + CommandUtils.getNameDescriptor(this, false));
        }

        context.out.println();

        context.out.println(new WordWrap().perform(getHelpDescription(), context.lineLength));
        context.out.println();

        return 0;
    }

    protected abstract String getHelpDescription();

}
