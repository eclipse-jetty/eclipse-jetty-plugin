package net.sourceforge.eclipsejetty.starter.common.command;

import net.sourceforge.eclipsejetty.starter.common.ServerAdapter;
import net.sourceforge.eclipsejetty.starter.console.AbstractCommand;
import net.sourceforge.eclipsejetty.starter.console.ConsoleAdapter;
import net.sourceforge.eclipsejetty.starter.console.Process;

public class RestartCommand extends AbstractCommand
{

    private final ServerAdapter serverAdapter;

    public RestartCommand(ConsoleAdapter consoleAdapter, ServerAdapter serverAdapter)
    {
        super(consoleAdapter, "restart", "r");

        this.serverAdapter = serverAdapter;
    }

    public String getFormat()
    {
        return "";
    }

    public String getDescription()
    {
        return "Restarts the server.";
    }

    public int getOrdinal()
    {
        return 9990;
    }

    public int execute(String processName, Process process) throws Exception
    {
        process.out.println("Restarting the server...");

        if (serverAdapter.isRunning())
        {
            serverAdapter.stop();

            while (serverAdapter.isRunning())
            {
                Thread.sleep(1000);
            }
        }

        serverAdapter.start();

        return 0;
    }

    @Override
    protected String getHelpDescription()
    {
        return "Restarts the server.";
    }

}
