package net.sourceforge.eclipsejetty.starter.console.command;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import net.sourceforge.eclipsejetty.starter.console.AbstractCommand;
import net.sourceforge.eclipsejetty.starter.console.ArgumentException;
import net.sourceforge.eclipsejetty.starter.console.ConsoleAdapter;
import net.sourceforge.eclipsejetty.starter.console.Process;

public class PipeCommand extends AbstractCommand
{

    public PipeCommand(ConsoleAdapter consoleAdapter)
    {
        super(consoleAdapter, ">", ">>");
    }

    public String getFormat()
    {
        return "[file]";
    }

    public String getDescription()
    {
        return "Writes the input stream to the file.";
    }

    public int getOrdinal()
    {
        return -1;
    }

    public int execute(String processName, Process process) throws Exception
    {
        boolean append = ">>".equals(processName);
        File file = process.args.consumeFile();

        PrintStream out;

        if (file != null)
        {
            process.out.printf("Redirecting output to %s\n", file.getAbsolutePath());

            out = new PrintStream(new FileOutputStream(file, append));
        }
        else
        {
            out = System.out;
        }

        try
        {
            if (process.args.size() > 0)
            {
                throw new ArgumentException("Too many arguments");
            }

            if (process.parent != null)
            {
                process.parent.redirect(process.in, out, out).execute();
            }
        }
        finally
        {
            if (out != System.out)
            {
                out.close();
            }
        }
        return 0;
    }

    @Override
    protected String getHelpDescription()
    {
        return "Writes the input stream to the specified file. " //
            + "This command is used for > and >> pipe commands. " //
            + "If -a is specified, it will append the output to the specified file.";
    }

}
