package net.sourceforge.eclipsejetty.starter.console.command;

import net.sourceforge.eclipsejetty.starter.console.AbstractCommand;
import net.sourceforge.eclipsejetty.starter.console.ConsoleAdapter;
import net.sourceforge.eclipsejetty.starter.console.Process;
import net.sourceforge.eclipsejetty.starter.util.Utils;

public class AmpersandCommand extends AbstractCommand
{

    public AmpersandCommand(ConsoleAdapter consoleAdapter)
    {
        super(consoleAdapter, "&");
    }

    public String getFormat()
    {
        return Utils.BLANK;
    }

    public String getDescription()
    {
        return "Executes a process asynchoniously.";
    }

    public int getOrdinal()
    {
        return -1;
    }

    public int execute(String processName, final Process process) throws Exception
    {
        Thread thread = new Thread(new Runnable()
        {

            public void run()
            {
                int result = -1;

                try
                {
                    result = process.parent.execute();
                }
                finally
                {
                    process.out.printf("Finished [%s]: %s\n", Thread.currentThread().getId(), result);
                }
            }

        }, "Eclipse Jetty Console Process");

        thread.start();

        process.out.printf("Started [%s]\n", thread.getId());

        return 0;
    }

    @Override
    protected String getHelpDescription()
    {
        return "Executes a process asynchoniously.";
    }

}
