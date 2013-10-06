package net.sourceforge.eclipsejetty.starter.console.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.eclipsejetty.starter.console.ArgumentException;
import net.sourceforge.eclipsejetty.starter.util.Utils;

public class Arguments implements Iterable<String>
{

    private final List<String> args;

    public Arguments(String... args)
    {
        this(new ArrayList<String>(Arrays.asList(args)));
    }

    public Arguments(List<String> args)
    {
        super();

        this.args = args;
    }

    public Arguments add(String argument)
    {
        return add(args.size(), argument);
    }

    public Arguments add(int index, String argument)
    {
        args.add(index, argument);

        return this;
    }

    public boolean isEmpty()
    {
        return args.isEmpty();
    }

    public int size()
    {
        return args.size();
    }

    public int indexOf(String... keys)
    {
        int index = Integer.MAX_VALUE;

        for (String key : keys)
        {
            int currentIndex = args.indexOf(key);

            if ((currentIndex >= 0) && (currentIndex < index))
            {
                index = currentIndex;
            }
        }

        return (index < Integer.MAX_VALUE) ? index : -1;
    }

    public int lastIndexOf(String... keys)
    {
        int index = -1;

        for (String key : keys)
        {
            int currentIndex = args.lastIndexOf(key);

            if (currentIndex > index)
            {
                index = currentIndex;
            }
        }

        return index;
    }

    public Iterator<String> iterator()
    {
        return args.iterator();
    }

    public Arguments consume(int index)
    {
        List<String> result = new ArrayList<String>();

        while (index < args.size())
        {
            result.add(args.remove(index));
        }

        return new Arguments(result);
    }

    public String consumeString()
    {
        return consumeString(0);
    }

    public String consumeString(int index)
    {
        if (isEmpty())
        {
            return null;
        }

        return Utils.resolvePlaceholders(args.remove(index));
    }

    public String consumeString(String key)
    {
        int indexOf = args.indexOf(key);

        if (indexOf < 0)
        {
            return null;
        }

        args.remove(indexOf);

        if (indexOf >= args.size())
        {
            throw new ArgumentException("Invalid argument: " + key + ". Value is missing.");
        }

        return Utils.resolvePlaceholders(args.remove(indexOf));
    }

    public List<String> consumeStrings(String key)
    {
        int indexOf = args.indexOf(key);

        if (indexOf < 0)
        {
            return null;
        }

        args.remove(indexOf);

        List<String> results = new ArrayList<String>();

        while (indexOf < args.size())
        {
            results.add(Utils.resolvePlaceholders(args.remove(indexOf)));
        }

        return results;
    }

    public Long consumeLong()
    {
        return consumeLong(0);
    }

    public Long consumeLong(int index)
    {
        String value = consumeString(index);

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
            throw new ArgumentException("Invalid number: " + value);
        }
    }

    public File consumeFile()
    {
        return consumeFile(0);
    }

    public File consumeFile(int index)
    {
        String file = consumeString(index);

        return (file != null) ? new File(file) : null;
    }

    public File consumeFile(String key)
    {
        String file = consumeString(key);

        return (file != null) ? new File(file) : null;
    }

    public List<File> consumeFiles(String key)
    {
        List<String> values = consumeStrings(key);

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

    public boolean consumeFlag(String flag)
    {
        return args.remove(flag);
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();

        for (String arg : args)
        {
            if (builder.length() > 0)
            {
                builder.append(" ");
            }

            builder.append(arg);
        }

        return builder.toString();
    }

}
