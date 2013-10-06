package net.sourceforge.eclipsejetty.starter.console;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import net.sourceforge.eclipsejetty.starter.console.util.Arguments;

public class Process
{

    private final ConsoleAdapter consoleAdapter;
    public final Process parent;
    public final Arguments args;
    public final InputStream in;
    public final PrintStream out;
    public final PrintStream err;

    private final int result = 0;

    public Process(ConsoleAdapter consoleAdapter, Process parent, Arguments args, InputStream in, PrintStream out,
        PrintStream err)
    {
        super();

        this.consoleAdapter = consoleAdapter;
        this.parent = parent;
        this.args = args;
        this.in = in;
        this.out = out;
        this.err = err;
    }

    public Process redirect(InputStream in, PrintStream out, PrintStream err)
    {
        return new Process(consoleAdapter, parent, args, in, out, err);
    }

    public int execute()
    {
        try
        {
            int index = args.lastIndexOf(">", ">>", "&");

            if (index > 0)
            {
                String arg = args.consumeString(index);

                return new Process(consoleAdapter, this, args.consume(index).add(0, arg), in, out, err).execute();
            }

            return execute(this);
        }
        finally
        {
            if (err != System.err)
            {
                err.close();
            }

            if (out != System.out)
            {
                out.close();
            }

            if (in != System.in)
            {
                try
                {
                    in.close();
                }
                catch (IOException e)
                {
                    // ignore
                }
            }
        }
    }

    public int getResult()
    {
        return result;
    }

    private static int execute(Process process)
    {
        if (process.args.isEmpty())
        {
            return 0;
        }

        int result = 0;

        String name = process.args.consumeString();

        try
        {
            result = execute(process, name);
        }
        catch (ArgumentException e)
        {
            process.err.println(e);
        }
        catch (Exception e)
        {
            process.err.println("An exception occured:");

            e.printStackTrace(process.err);

            result = -1;
        }

        return result;

    }

    private static int execute(Process process, String processName) throws Exception
    {
        Command command = process.consoleAdapter.getCommand(processName.toLowerCase());

        if (command == null)
        {
            process.err.println("Unknown command: " + processName);
            process.err.println("Type \"help\" to get help.");

            return -1;
        }

        return command.execute(processName, process);
    }

}
