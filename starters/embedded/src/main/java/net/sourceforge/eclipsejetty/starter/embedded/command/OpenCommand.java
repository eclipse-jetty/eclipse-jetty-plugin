// Licensed under the Apache License, Version 2.0 (the "License");
package net.sourceforge.eclipsejetty.starter.embedded.command;

import java.awt.Desktop;
import java.net.URI;
import java.util.Iterator;

import net.sourceforge.eclipsejetty.starter.common.ServerAdapter;
import net.sourceforge.eclipsejetty.starter.console.AbstractCommand;
import net.sourceforge.eclipsejetty.starter.console.ConsoleAdapter;
import net.sourceforge.eclipsejetty.starter.console.Process;
import net.sourceforge.eclipsejetty.starter.util.Utils;

public class OpenCommand extends AbstractCommand
{

    private final ServerAdapter adapter;

    public OpenCommand(ConsoleAdapter consoleAdapter, ServerAdapter adapter)
    {
        super(consoleAdapter, "open", "o");

        this.adapter = adapter;
    }

    @Override
    public boolean isEnabled()
    {
        try
        {
            Class.forName("java.awt.Desktop");
        }
        catch (ClassNotFoundException e)
        {
            return false;
        }

        if (Desktop.isDesktopSupported())
        {
            Desktop desktop = Desktop.getDesktop();

            return desktop.isSupported(Desktop.Action.BROWSE);
        }

        return false;
    }

    @Override
    public String getFormat()
    {
        return Utils.BLANK;
    }

    @Override
    public String getDescription()
    {
        return "Opens a browser.";
    }

    @Override
    protected String getHelpDescription()
    {
        return "Opens a browser.";
    }

    @Override
    public int getOrdinal()
    {
        return 3000;
    }

    @Override
    public int execute(String processName, Process process) throws Exception
    {
        Iterator<Integer> portIterator = adapter.getPorts().iterator();

        if (!portIterator.hasNext())
        {
            process.err.println("No connector provided.");
            return -1;
        }

        String url = "http://localhost:" + portIterator.next();

        Iterator<String> pathIterator = adapter.getContextPaths().iterator();

        if (!pathIterator.hasNext())
        {
            url += "/";
        }
        else
        {
            url += pathIterator.next();
        }

        process.out.println(String.format("Opening %s...", url));

        Desktop desktop = Desktop.getDesktop();

        desktop.browse(new URI(url));

        return 0;
    }

}
