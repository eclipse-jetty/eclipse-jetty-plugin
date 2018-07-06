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
package net.sourceforge.eclipsejetty.starter.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Common utils for Jetty starters.
 * 
 * @author Manfred Hantschel
 */
public class Utils
{

    public static final String EMPTY = "";

    public static interface PlaceholderResolver
    {
        String resolve(String key);
    }

    /**
     * The prefix for entities (${)
     */
    public static final String ENTITY_PREFIX = "${";

    /**
     * The postfix for entities (})
     */
    public static final String ENTITY_POSTFIX = "}";

    /**
     * Returns the default value if the value is null
     * 
     * @param value the value
     * @param defaultValue the default value
     * @return the value, of the default value if value is null
     */
    public static <TYPE> TYPE ensure(TYPE value, TYPE defaultValue)
    {
        return (value != null) ? value : defaultValue;
    }

    /**
     * Repeats the string until the maximum length is reached
     * 
     * @param s the string
     * @param maxLength the maximum length
     * @return a string with the maxLength
     */
    public static String repeat(String s, int maxLength)
    {
        StringBuilder result = new StringBuilder();

        while (result.length() < maxLength)
        {
            result.append(s);
        }

        return result.substring(0, maxLength);
    }

    /**
     * Prefixes each line with the specified prefix.
     * 
     * @param value the text
     * @param prefix the prefix
     * @param includeFirstLine true, to add the prefix at the beginning of the text
     * @return the text
     */
    public static String prefixLine(final String value, final String prefix, final boolean includeFirstLine)
    {
        if (value == null)
        {
            return Utils.EMPTY;
        }

        String replacement = "\n" + prefix;
        String result = value.replace("\n", replacement);

        return (includeFirstLine) ? prefix + result : result;
    }

    /**
     * Formats seconds in human readable form.
     * 
     * @param seconds the seconds
     * @return the formatted seconds
     */
    public static String formatSeconds(double seconds)
    {
        StringBuilder result = new StringBuilder();
        int minutes = (int) (seconds / 60);
        int digits = 1;

        if (seconds < 0.01)
        {
            digits = 6;
        }
        else if (seconds < 1)
        {
            digits = 3;
        }

        seconds -= minutes * 60;

        if (minutes > 0)
        {
            result.append(minutes).append(" m ");
        }

        result.append(String.format("%,." + digits + "f s", seconds));

        return result.toString();
    }

    /**
     * Formats bytes in human readable form.
     * 
     * @param bytes the number of bytes
     * @return the formatted bytes
     */
    public static String formatBytes(long bytes)
    {
        if (Long.MAX_VALUE == bytes)
        {
            return "\u221e B ";
        }

        String unit = "B ";
        double value = Math.abs(bytes);

        if (value > 1024)
        {
            value /= 1024;
            unit = "KB";
        }

        if (value > 1024)
        {
            value /= 1024;
            unit = "MB";
        }

        if (value > 1024)
        {
            value /= 1024;
            unit = "GB";
        }

        if (value > 1024)
        {
            value /= 1024;
            unit = "TB";
        }

        if (value > 1024)
        {
            value /= 1024;
            unit = "PB";
        }

        if (value > 1024)
        {
            value /= 1024;
            unit = "EB"; // the Enterprise might still use it. 
        }

        String result = String.format("%,.1f %s", value, unit);

        if (bytes < 0)
        {
            result = "-" + result;
        }

        return result;
    }

    /**
     * Writes the content to a file
     * 
     * @param file the file
     * @param content the content
     * @throws IOException on occasion
     */
    public static void write(File file, String content) throws IOException
    {
        FileWriter writer = new FileWriter(file);

        try
        {
            writer.write(content);
        }
        finally
        {
            writer.close();
        }
    }

    /**
     * Replaces all placeholders like ${...} by using System.properties and environment variables
     * 
     * @param value the value
     * @return the value with resolved placehodlers
     */
    public static String resolvePlaceholders(String value)
    {
        return resolvePlaceholders(value, new PlaceholderResolver()
        {

            public String resolve(String key)
            {
                String value = System.getProperty(key);

                if (value == null)
                {
                    value = System.getenv(key);
                }

                if (value == null)
                {
                    value = ENTITY_PREFIX + key + ENTITY_POSTFIX;
                }

                return value;
            }
        });
    }

    /**
     * Replaces all placeholders like ${...} by using the specified resolver
     * 
     * @param value the value
     * @param resolver the resolver
     * @return the value with resolved placehodlers
     */
    public static String resolvePlaceholders(String value, PlaceholderResolver resolver)
    {
        if (value == null)
        {
            return null;
        }

        int beginIndex = value.indexOf(ENTITY_PREFIX);

        if (beginIndex < 0)
        {
            return value;
        }

        int endIndex = value.indexOf(ENTITY_POSTFIX, beginIndex);

        if (endIndex < 0)
        {
            return value;
        }

        StringBuilder result = new StringBuilder();
        int currentIndex = 0;

        while (currentIndex < value.length())
        {
            if ((beginIndex - currentIndex) > 0)
            {
                result.append(value.substring(currentIndex, beginIndex));
            }

            String key = value.substring(beginIndex + ENTITY_PREFIX.length(), endIndex);
            String resolvedValue = resolver.resolve(key);

            if (resolvedValue == null)
            {
                result.append(ENTITY_PREFIX + key + ENTITY_POSTFIX);
            }
            else
            {
                result.append(resolvedValue);
            }

            currentIndex = endIndex + 1;

            if (currentIndex < value.length())
            {
                beginIndex = value.indexOf(ENTITY_PREFIX, currentIndex);

                if (beginIndex < 0)
                {
                    if (currentIndex < value.length())
                    {
                        result.append(value.substring(currentIndex));
                    }

                    break;
                }

                endIndex = value.indexOf(ENTITY_POSTFIX, beginIndex);

                if (endIndex < 0)
                {
                    if (currentIndex < value.length())
                    {
                        result.append(value.substring(currentIndex));
                    }

                    break;
                }
            }
        }

        return result.toString();
    }

}
