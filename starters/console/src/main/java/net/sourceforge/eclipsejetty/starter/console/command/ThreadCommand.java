// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package net.sourceforge.eclipsejetty.starter.console.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.sourceforge.eclipsejetty.starter.console.AbstractCommand;
import net.sourceforge.eclipsejetty.starter.console.ArgumentException;
import net.sourceforge.eclipsejetty.starter.console.ConsoleAdapter;
import net.sourceforge.eclipsejetty.starter.console.Process;
import net.sourceforge.eclipsejetty.starter.console.util.WildcardUtils;
import net.sourceforge.eclipsejetty.starter.util.Utils;

/**
 * Prints thread information
 * 
 * @author Manfred Hantschel
 */
public class ThreadCommand extends AbstractCommand
{

    public ThreadCommand(ConsoleAdapter consoleAdapter)
    {
        super(consoleAdapter, "thread", "t");
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.console.Command#getFormat()
     */
    public String getFormat()
    {
        return "{<ID>}";
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.console.Command#getDescription()
     */
    public String getDescription()
    {
        return "Thread information.";
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.console.AbstractCommand#getHelpDescription()
     */
    @Override
    protected String getHelpDescription()
    {
        return "Displays information gathered form threads. If the command is invoked without an <ID>, "
            + "a summary of all threads will be displayed. If the command is invoked with an <ID> (as thread id), "
            + "detail information of the thread will be displayed. You can use * as <ID> to display detail information "
            + "of all threads.";
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.console.Command#getOrdinal()
     */
    public int getOrdinal()
    {
        return 520;
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.console.Command#execute(java.lang.String,
     *      net.sourceforge.eclipsejetty.starter.console.Process)
     */
    public int execute(String commandName, Process process) throws Exception
    {
        if (process.args.isEmpty())
        {
            return list(process);
        }

        String id;

        while ((id = process.args.consumeString()) != null)
        {
            show(process, id);
        }

        return 0;
    }

    private int list(Process process)
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

        process.out.printf(" %" + idLength + "s | %-" + nameLength + "s | %-" + classLength + "s \n", "ID", "Name",
            "Class");
        process.out.println("-" + Utils.repeat("-", idLength) + "-+-" + Utils.repeat("-", nameLength) + "-+-"
            + Utils.repeat("-", classLength) + "-");

        for (Thread thread : threads)
        {
            process.out.printf(" %" + idLength + "d | %-" + nameLength + "s | %-" + classLength + "s \n",
                thread.getId(), thread.getName(), thread.getClass().getName());
        }

        process.out.println();
        process.out.printf("Thread count: %s\n", threads.size());

        return 0;
    }

    private int show(Process process, String id)
    {
        List<Thread> threads = getThreads();
        boolean hit = false;

        for (Thread thread : threads)
        {
            if (WildcardUtils.match(String.valueOf(thread.getId()), id))
            {
                show(process, thread);

                hit = true;
            }
        }

        if (!hit)
        {
            throw new ArgumentException(String.format("Invalid thread ID: %s", id));
        }

        return 0;
    }

    private void show(Process process, Thread thread)
    {
        String title = thread.getId() + ".) " + thread.getName() + " (" + thread.getClass() + ")";

        process.out.println(title);
        process.out.println(Utils.repeat("-", title.length()));

        StackTraceElement[] stackTraceElements = thread.getStackTrace();

        for (StackTraceElement element : stackTraceElements)
        {
            process.out.println(element);
        }

        process.out.println();
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
