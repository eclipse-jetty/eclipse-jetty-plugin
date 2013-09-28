// Licensed under the Apache License, Version 2.0 (the "License");
package net.sourceforge.eclipsejetty.starter.console;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import net.sourceforge.eclipsejetty.starter.console.util.Scanner;
import net.sourceforge.eclipsejetty.starter.console.util.Tokenizer;
import net.sourceforge.eclipsejetty.starter.util.service.GlobalServiceResolver;
import net.sourceforge.eclipsejetty.starter.util.service.ServiceResolver;
import net.sourceforge.eclipsejetty.starter.util.service.ServiceUtils;

public class Console implements Runnable
{

    public static final Console INSTANCE = new Console();

    public static void main(String[] args) throws InterruptedException
    {
        Console.INSTANCE.initialize(GlobalServiceResolver.INSTANCE);
        Console.INSTANCE.start();

        new Semaphore(0).acquire();
    }

    private final Map<String, Command> commands = new LinkedHashMap<String, Command>();

    private Tokenizer tokenizer;

    private Console()
    {
        super();
    }

    public void initialize(ServiceResolver resolver)
    {
        Collection<Object> commands;

        try
        {
            commands = ServiceUtils.instantiateContributions(getClass(), resolver);
        }
        catch (IOException e)
        {
            throw new ExceptionInInitializerError(e);
        }

        for (Object command : commands)
        {
            register((Command) command);
        }
    }

    public void register(Command command)
    {
        if (!command.isEnabled())
        {
            return;
        }

        for (String commandName : command.getNames())
        {
            commands.put(commandName.toLowerCase(), command);
        }
    }

    public Command getCommand(String name)
    {
        return commands.get(name.toLowerCase());
    }

    public Collection<Command> getCommands()
    {
        List<Command> result = new ArrayList<Command>(new HashSet<Command>(commands.values()));

        Collections.sort(result, new Comparator<Command>()
        {

            public int compare(Command o1, Command o2)
            {
                return o1.getOrdinal() - o2.getOrdinal();
            }

        });

        return result;
    }

    public void start()
    {
        if (tokenizer != null)
        {
            return;
        }

        Thread thread = new Thread(this, "Eclipse Jetty Console");

        thread.setDaemon(true);
        thread.start();
    }

    public void stop()
    {
        if (tokenizer == null)
        {
            return;
        }

        try
        {
            tokenizer.close();
        }
        catch (IOException e)
        {
            // ignore
        }
        finally
        {
            tokenizer = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void run()
    {
        try
        {
            tokenizer = new Tokenizer(new Scanner(new InputStreamReader(System.in)));

            try
            {
                List<String> tokens;

                while ((tokens = tokenizer.read()) != null)
                {
                    execute(tokens);
                }
            }
            finally
            {
                tokenizer.close();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace(System.err);
        }
        finally
        {
            tokenizer = null;
        }
    }

    private int execute(List<String> parameters)
    {
        if (parameters == null)
        {
            return 0;
        }

        if (parameters.size() == 0)
        {
            return 0;
        }

        int result = 0;

        String name = parameters.remove(0);

        try
        {
            Context context = new Context(this, parameters);

            try
            {
                result = execute(context, name);
            }
            catch (ParameterException e)
            {
                throw e;
            }
            catch (Exception e)
            {
                System.err.println("An exception occured:");

                e.printStackTrace(context.err);

                result = -1;
            }
            finally
            {
                context.close();
            }
        }
        catch (ParameterException e)
        {
            System.err.println(e.getMessage());
        }

        return result;
    }

    private int execute(Context context, String name) throws Exception
    {
        Command command = commands.get(name.toLowerCase());

        if (command == null)
        {
            context.err.println("Unknown command: " + name);
            context.err.println("Type \"help\" to get help.");

            return -1;
        }

        return command.execute(context);
    }

}
