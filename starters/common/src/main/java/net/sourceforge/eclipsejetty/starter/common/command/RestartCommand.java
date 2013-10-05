package net.sourceforge.eclipsejetty.starter.common.command;

import net.sourceforge.eclipsejetty.starter.common.ServerAdapter;
import net.sourceforge.eclipsejetty.starter.console.AbstractCommand;
import net.sourceforge.eclipsejetty.starter.console.Context;

public class RestartCommand extends AbstractCommand
{

    private final ServerAdapter adapter;

    public RestartCommand(ServerAdapter adapter)
    {
        super("restart", "r");

        this.adapter = adapter;
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

    public int execute(Context context) throws Exception
    {
        context.out.println("Restarting the server...");

        if (adapter.isRunning())
        {
            adapter.stop();

            while (adapter.isRunning())
            {
                Thread.sleep(1000);
            }
        }

        adapter.start();

        return 0;
    }

    @Override
    protected String getHelpDescription()
    {
        return "Restarts the server.";
    }

}
