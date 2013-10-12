// Licensed under the Apache License, Version 2.0 (the "License");
package net.sourceforge.eclipsejetty.starter.console;

import net.sourceforge.eclipsejetty.starter.console.util.CommandUtils;
import net.sourceforge.eclipsejetty.starter.console.util.WordWrap;

public abstract class AbstractCommand implements Command
{

    protected final ConsoleAdapter consoleAdapter;
    private final String[] names;

    public AbstractCommand(ConsoleAdapter consoleAdapter, String... names)
    {
        super();

        this.consoleAdapter = consoleAdapter;
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

    public int help(Process process) throws Exception
    {
        process.out.println(new WordWrap().perform(
            String.format("Usage:    %s", CommandUtils.getFormatDescriptor(this)), consoleAdapter.getLineLength()));

        if (getNames().length > 1)
        {
            process.out.printf("Synonyms: %s\n", CommandUtils.getNameDescriptor(this, false));
        }

        process.out.println();

        process.out.println(new WordWrap().perform(getHelpDescription(), consoleAdapter.getLineLength()));
        process.out.println();

        return 0;
    }

    protected abstract String getHelpDescription();

}
