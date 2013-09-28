// Licensed under the Apache License, Version 2.0 (the "License");
package net.sourceforge.eclipsejetty.starter.console;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.eclipsejetty.starter.util.Utils;

public class Context
{

    public final Console console;
    public final List<String> parameters;
    private final File redirected;
    public final PrintStream out;
    public final PrintStream err;
    public final int lineLength;

    public Context(Console console, List<String> parameters)
    {
        super();

        this.console = console;
        this.parameters = parameters;

        boolean append = false;
        List<File> files = consumeFileParamters(">");

        if (files == null)
        {
            files = consumeFileParamters(">>");
            append = true;
        }

        if (files != null)
        {
            if (files.size() == 0)
            {
                throw new ParameterException("File missing after \">\".");
            }

            if (files.size() > 1)
            {
                throw new ParameterException("Cannot redirect to more than one file.");
            }

            redirected = files.get(0);

            System.out.print("Redirecting output to " + redirected.getAbsolutePath() + "... ");

            try
            {
                out = err = new PrintStream(new FileOutputStream(redirected, append));
            }
            catch (FileNotFoundException e)
            {
                throw new ParameterException("Failed writing to " + redirected);
            }
        }
        else
        {
            redirected = null;
            out = System.out;
            err = System.err;
        }

        lineLength = 80;
    }

    public void close()
    {
        if (redirected != null)
        {
            out.close();
            err.close();

            System.out.println("done.");
        }
    }

    public boolean hasParameters()
    {
        return parameters.size() > 0;
    }

    public int parameterCount()
    {
        return parameters.size();
    }

    public String consumeStringParameter()
    {
        if (!hasParameters())
        {
            return null;
        }

        return Utils.resolvePlaceholders(parameters.remove(0));
    }

    public Long consumeLongParameter()
    {
        String value = consumeStringParameter();

        if (value == null)
        {
            return null;
        }

        try
        {
            return Long.decode(value);
        }
        catch (NumberFormatException e)
        {
            throw new ParameterException("Invalid number: " + value);
        }
    }

    public boolean consumeFlag(String flag)
    {
        return parameters.remove(flag);
    }

    public String consumeStringParameter(String key)
    {
        int indexOf = parameters.indexOf(key);

        if (indexOf < 0)
        {
            return null;
        }

        parameters.remove(indexOf);

        if (indexOf >= parameters.size())
        {
            throw new ParameterException("Invalid parameter: " + key + ". Value is missing.");
        }

        return Utils.resolvePlaceholders(parameters.remove(indexOf));
    }

    public List<String> consumeStringParameters(String key)
    {
        int indexOf = parameters.indexOf(key);

        if (indexOf < 0)
        {
            return null;
        }

        parameters.remove(indexOf);

        List<String> results = new ArrayList<String>();

        while (indexOf < parameters.size())
        {
            results.add(Utils.resolvePlaceholders(parameters.remove(indexOf)));
        }

        return results;
    }

    public File consumeFileParameter(String key)
    {
        String file = consumeStringParameter(key);

        return (file != null) ? new File(file) : null;
    }

    public List<File> consumeFileParamters(String key)
    {
        List<String> values = consumeStringParameters(key);

        if (values == null)
        {
            return null;
        }

        List<File> results = new ArrayList<File>(values.size());

        for (String value : values)
        {
            results.add(new File(value));
        }

        return results;
    }

}
