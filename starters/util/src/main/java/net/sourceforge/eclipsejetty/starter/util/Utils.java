// Licensed under the Apache License, Version 2.0 (the "License");
package net.sourceforge.eclipsejetty.starter.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Utils
{

    public static final String BLANK = "";
    
    public static interface PlaceholderResolver {
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

    public static <TYPE> TYPE ensure(TYPE value, TYPE defaultValue)
    {
        return (value != null) ? value : defaultValue;
    }

    public static String repeat(String s, int maxLength)
    {
        StringBuilder result = new StringBuilder();

        while (result.length() < maxLength)
        {
            result.append(s);
        }

        return result.substring(0, maxLength);
    }

    public static String prefixLine(final String value, final String prefix, final boolean includeFirstLine)
    {
        if (value == null)
        {
            return Utils.BLANK;
        }

        String replacement = "\n" + prefix;
        String result = value.replace("\n", replacement);

        return (includeFirstLine) ? prefix + result : result;
    }

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
    public static String resolvePlaceholders(String value) {
        return resolvePlaceholders(value, new PlaceholderResolver() {

            public String resolve(String key) {
                String value = System.getProperty(key);

                if (value == null) {
                    value = System.getenv(key);
                }

                if (value == null) {
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
    public static String resolvePlaceholders(String value, PlaceholderResolver resolver) {
        if (value == null) {
            return null;
        }

        int beginIndex = value.indexOf(ENTITY_PREFIX);

        if (beginIndex < 0) {
            return value;
        }

        int endIndex = value.indexOf(ENTITY_POSTFIX, beginIndex);

        if (endIndex < 0) {
            return value;
        }

        StringBuilder result = new StringBuilder();
        int currentIndex = 0;

        while (currentIndex < value.length()) {
            if ((beginIndex - currentIndex) > 0) {
                result.append(value.substring(currentIndex, beginIndex));
            }

            String key = value.substring(beginIndex + ENTITY_PREFIX.length(), endIndex);
            String resolvedValue = resolver.resolve(key);

            if (resolvedValue == null) {
                result.append(ENTITY_PREFIX + key + ENTITY_POSTFIX);
            }
            else {
                result.append(resolvedValue);
            }

            currentIndex = endIndex + 1;

            if (currentIndex < value.length()) {
                beginIndex = value.indexOf(ENTITY_PREFIX, currentIndex);

                if (beginIndex < 0) {
                    if (currentIndex < value.length()) {
                        result.append(value.substring(currentIndex));
                    }

                    break;
                }

                endIndex = value.indexOf(ENTITY_POSTFIX, beginIndex);

                if (endIndex < 0) {
                    if (currentIndex < value.length()) {
                        result.append(value.substring(currentIndex));
                    }

                    break;
                }
            }
        }

        return result.toString();
    }

}
