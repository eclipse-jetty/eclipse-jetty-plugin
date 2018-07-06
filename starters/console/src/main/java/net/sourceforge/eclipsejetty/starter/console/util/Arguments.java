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
package net.sourceforge.eclipsejetty.starter.console.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.eclipsejetty.starter.console.ArgumentException;
import net.sourceforge.eclipsejetty.starter.util.Utils;

/**
 * Accessor for arguments. Placeholders in the arguments will automatically be resolved. The common concept is, that
 * when consuming an argument, it will be removed.
 * 
 * @author Manfred Hantschel
 */
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

    /**
     * Adds an argument at the end of the list of arguments
     * 
     * @param argument the argument
     * @return the {@link Arguments} itself
     */
    public Arguments add(String argument)
    {
        return add(args.size(), argument);
    }

    /**
     * Adds an argument at the specified index
     * 
     * @param index the index
     * @param argument the argument
     * @return the {@link Arguments} itself
     */
    public Arguments add(int index, String argument)
    {
        args.add(index, argument);

        return this;
    }

    /**
     * Returns true if no argument is available (anymore).
     * 
     * @return true if empty
     */
    public boolean isEmpty()
    {
        return args.isEmpty();
    }

    /**
     * Returns the number of (remaining) arguments.
     * 
     * @return the number of (remaining) arguments
     */
    public int size()
    {
        return args.size();
    }

    /**
     * Returns the first index of one of the specified arguments.
     * 
     * @param keys the arguments
     * @return the first index, -1 if none was found
     */
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

    /**
     * Returns the last index of one of the specified arguments.
     * 
     * @param keys the arguments
     * @return the last index, -1 if none was found
     */
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

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Iterable#iterator()
     */
    public Iterator<String> iterator()
    {
        return args.iterator();
    }

    /**
     * Returns a new {@link Arguments} object with all arguments, starting at the specified index. The arguments will be
     * removed from the original list of arguments.
     * 
     * @param startIndex the start index
     * @return a new {@link Arguments} object
     */
    public Arguments consume(int startIndex)
    {
        List<String> result = new ArrayList<String>();

        while (startIndex < args.size())
        {
            result.add(args.remove(startIndex));
        }

        return new Arguments(result);
    }

    /**
     * Removes and returns the first arguments as string.
     * 
     * @return the first argument as string, null if there is no argument
     */
    public String consumeString()
    {
        return consumeString(0);
    }

    /**
     * Removes and returns the argument at the specified index as string.
     * 
     * @param index the index
     * @return the argument at the specified index as string, null if the index is out of bounds.
     */
    public String consumeString(int index)
    {
        if (isEmpty())
        {
            return null;
        }

        if (index >= size())
        {
            return null;
        }

        return Utils.resolvePlaceholders(args.remove(index));
    }

    /**
     * Searches for the specified argument. If found, removes it and returns and removes the next argument.
     * 
     * @param key the argument
     * @return the value part (next argument), null if key was not found
     */
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
            throw new ArgumentException(String.format("Invalid argument: %s. Value is missing.", key));
        }

        return Utils.resolvePlaceholders(args.remove(indexOf));
    }

    /**
     * Removes and returns the first arguments as long.
     * 
     * @return the first argument as long, null if there is no argument
     */
    public Long consumeLong()
    {
        return consumeLong(0);
    }

    /**
     * Removes and returns the argument at the specified index as long.
     * 
     * @param index the index
     * @return the argument at the specified index as long, null if the index is out of bounds.
     */
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
            throw new ArgumentException(String.format("Invalid number: %s", value));
        }
    }

    /**
     * Searches for the specified argument. If found, removes it and returns and removes the next argument.
     * 
     * @param key the argument
     * @return the value part (next argument), null if key was not found
     */
    public Long consumeLong(String key)
    {
        String value = consumeString(key);

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
            throw new ArgumentException(String.format("Invalid number: %s", value));
        }
    }

    /**
     * Removes and returns the first arguments as file.
     * 
     * @return the first argument as file, null if there is no argument
     */
    public File consumeFile()
    {
        return consumeFile(0);
    }

    /**
     * Removes and returns the argument at the specified index as long.
     * 
     * @param index the index
     * @return the argument at the specified index as long, null if the index is out of bounds.
     */
    public File consumeFile(int index)
    {
        String file = consumeString(index);

        return (file != null) ? new File(file) : null;
    }

    /**
     * Searches for the specified argument. If found, removes it and returns and removes the next argument.
     * 
     * @param key the argument
     * @return the value part (next argument), null if key was not found
     */
    public File consumeFile(String key)
    {
        String file = consumeString(key);

        return (file != null) ? new File(file) : null;
    }

    /**
     * Consumes the specified argument.
     * 
     * @param flag the argument
     * @return true if the argument was found, false otherwise.
     */
    public boolean consumeFlag(String flag)
    {
        return args.remove(flag);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
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
