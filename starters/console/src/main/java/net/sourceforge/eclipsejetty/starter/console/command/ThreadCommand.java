// Licensed under the Apache License, Version 2.0 (the "License");
package net.sourceforge.eclipsejetty.starter.console.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.sourceforge.eclipsejetty.starter.console.AbstractCommand;
import net.sourceforge.eclipsejetty.starter.console.Context;
import net.sourceforge.eclipsejetty.starter.console.ParameterException;
import net.sourceforge.eclipsejetty.starter.console.util.WildcardUtils;
import net.sourceforge.eclipsejetty.starter.util.Utils;

public class ThreadCommand extends AbstractCommand
{

    public ThreadCommand()
    {
        super("thread", "t");
    }

    public String getFormat()
    {
        return "{<ID>}";
    }

    public String getDescription()
    {
        return "Thread information.";
    }

    @Override
    protected String getHelpDescription()
    {
        return "Displays information gathered form threads. If the command is invoked without an <ID>, "
            + "a summary of all threads will be displayed. If the command is invoked with an <ID> (as thread id), "
            + "detail information of the thread will be displayed. You can use * as <ID> to display detail information "
            + "of all threads.";
    }

    public int getOrdinal()
    {
        return 520;
    }

    public int execute(Context context) throws Exception
    {
        if (!context.hasParameters())
        {
            return list(context);
        }

        String id;

        while ((id = context.consumeStringParameter()) != null)
        {
            show(context, id);
        }
        
        return 0;
    }

    private int list(Context context)
    {
        List<Thread> threads = getThreads();

        int idLength = 0;
        int nameLength = 0;
        int classLength = 0;

        for (Thread thread : threads)
        {
            idLength = Math.max(idLength, String.valueOf(thread.getId()).length());
            nameLength = Math.max(nameLength, thread.getName().length());
            classLength = Math.max(classLength, thread.getClass().getName().length());
        }

        context.out.printf(" %" + idLength + "s | %-" + nameLength + "s | %-" + classLength + "s \n", "ID", "Name",
            "Class");
        context.out.println("-" + Utils.repeat("-", idLength) + "-+-" + Utils.repeat("-", nameLength) + "-+-"
            + Utils.repeat("-", classLength) + "-");

        for (Thread thread : threads)
        {
            context.out.printf(" %" + idLength + "d | %-" + nameLength + "s | %-" + classLength + "s \n",
                thread.getId(), thread.getName(), thread.getClass().getName());
        }

        context.out.println();
        context.out.println("Thread count: " + threads.size());

        return 0;
    }

    private int show(Context context, String id)
    {
        List<Thread> threads = getThreads();
        boolean hit = false;

        for (Thread thread : threads)
        {
            if (WildcardUtils.match(String.valueOf(thread.getId()), id))
            {
                show(context, thread);

                hit = true;
            }
        }

        if (!hit)
        {
            throw new ParameterException("Invalid thread ID: " + id);
        }

        return 0;
    }

    private void show(Context context, Thread thread)
    {
        String title = thread.getId() + ".) " + thread.getName() + " (" + thread.getClass() + ")";

        context.out.println(title);
        context.out.println(Utils.repeat("-", title.length()));

        StackTraceElement[] stackTraceElements = thread.getStackTrace();

        for (StackTraceElement element : stackTraceElements)
        {
            context.out.println(element);
        }

        context.out.println();
    }

    private List<Thread> getThreads()
    {
        List<Thread> threads = new ArrayList<Thread>(Thread.getAllStackTraces().keySet());

        Collections.sort(threads, new Comparator<Thread>()
        {

            public int compare(Thread o1, Thread o2)
            {
                return (int) (o1.getId() - o2.getId());
            }

        });

        return threads;
    }

}
