// Licensed under the Apache License, Version 2.0 (the "License");
package net.sourceforge.eclipsejetty.starter.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Abstract base class for the Jetty launcher
 * 
 * @author Manfred Hantschel
 */
public abstract class AbstractJettyLauncherMain
{

    public static final String CONFIGURATION_KEY = "jetty.launcher.configuration";
    public static final String HIDE_LAUNCH_INFO_KEY = "jetty.launcher.hideLaunchInfo";

    protected void launch(String[] args) throws Exception
    {
        long millis = System.currentTimeMillis();
        boolean showInfo = System.getProperty(HIDE_LAUNCH_INFO_KEY) == null;

        if (showInfo)
        {
            BufferedPrintWriter writer = new BufferedPrintWriter();

            try
            {
                printLogo(writer);
                writer.println();
            }
            finally
            {
                System.out.println(writer);
            }
        }

        String configurationFileDef = System.getProperty(CONFIGURATION_KEY);

        if (configurationFileDef == null)
        {
            throw new IOException("-D" + CONFIGURATION_KEY + " missing");
        }

        File[] configurationFiles = getConfigurationFiles(configurationFileDef);

        start(configurationFiles, showInfo);

        if (showInfo)
        {
            BufferedPrintWriter writer = new BufferedPrintWriter();

            try
            {
                printStartupTime(writer, millis);
            }
            finally
            {
                System.out.println(writer);
            }
        }
    }

    protected abstract void start(File[] configurationFiles, boolean showInfo) throws Exception;

    protected abstract void printLogo(PrintWriter writer);

    protected void configure(PrintWriter writer, File[] configurationFiles, Object server)
        throws Exception
    {
        for (int i = 0; i < configurationFiles.length; i += 1)
        {
            File configurationFile = configurationFiles[i];

            if (writer != null)
            {
                writer.println(String.format("%18s%s", (i == 0) ? "Configuration: " : "",
                    configurationFile.getAbsolutePath()));
            }

            Class<?> type = determineClass(configurationFile);
            FileInputStream in = new FileInputStream(configurationFile);

            try
            {
                configure(writer, in, type, server);
            }
            finally
            {
                in.close();
            }
        }
    }

    protected abstract void configure(PrintWriter writer, FileInputStream in, Class<?> type, Object server)
        throws Exception;

    private static File[] getConfigurationFiles(String definitionList) throws IOException
    {
        String[] definitions = definitionList.split(File.pathSeparator);
        List<File> files = new ArrayList<File>();

        for (String definition : definitions)
        {
            if (definition.trim().length() <= 0)
            {
                continue;
            }

            File file = new File(definition.trim());

            files.add(file);

            if (!file.canRead())
            {
                throw new IOException("Cannot read configuration file: " + file);
            }
        }

        return files.toArray(new File[files.size()]);
    }

    public static Class<?> determineClass(File file) throws IOException
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;

        try
        {
            builder = factory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e)
        {
            throw new IOException("Failed to configure parser");
        }

        Document document;

        try
        {
            document = builder.parse(new InputSource(file.getAbsolutePath()));
        }
        catch (SAXException e)
        {
            throw new IOException("Failed to parse " + file);
        }
        catch (IOException e)
        {
            throw new IOException("Failed to read " + file);
        }

        NodeList nodeList = document.getElementsByTagName("Configure");

        if (nodeList.getLength() < 1)
        {
            throw new IOException("Failed to find <Configure> element in " + file);
        }

        Node node = nodeList.item(0);
        NamedNodeMap attributes = node.getAttributes();

        if (attributes == null)
        {
            throw new IOException("Failed to class argument in <Configure> element in " + file);
        }

        Node item = attributes.getNamedItem("class");

        if (item == null)
        {
            throw new IOException("Failed to class argument in <Configure> element in " + file);
        }

        String value = item.getNodeValue().trim();

        try
        {
            return Class.forName(value);
        }
        catch (ClassNotFoundException e)
        {
            throw new IOException("Unknown class " + value + " in <Configure> element in " + file);
        }
    }

    public void printStartupTime(BufferedPrintWriter writer, long millis)
    {
        Runtime runtime = Runtime.getRuntime();

        System.gc();

        double seconds = ((double) System.currentTimeMillis() - millis) / 1000d;
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();

        String duration = "Jetty startup finished in " + formatSeconds(seconds) + ".";
        String memory =
            "Used memory: " + formatBytes(totalMemory - freeMemory) + " of " + formatBytes(totalMemory) + " ("
                + formatBytes(maxMemory) + " maximum)";

        String line = repeat("-", Math.max(duration.length(), memory.length()));

        writer.println(line);
        writer.println(duration);
        writer.println(memory);
        writer.println(line);
    }

    protected static String formatSeconds(double seconds)
    {
        StringBuilder result = new StringBuilder();
        int minutes = (int) (seconds / 60);

        seconds -= minutes * 60;

        if (minutes > 0)
        {
            result.append(minutes).append(" m ");
        }

        result.append(String.format("%,.3f s", seconds));

        return result.toString();
    }

    protected static String formatBytes(long bytes)
    {
        if (Long.MAX_VALUE == bytes)
        {
            return "\u221e Bytes";
        }

        String unit = "Bytes";
        double value = bytes;

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

        return String.format("%,.1f %s", value, unit);
    }

    protected static String repeat(String s, int length)
    {
        StringBuilder builder = new StringBuilder();

        while (builder.length() < length)
        {
            builder.append(s);
        }

        return builder.substring(0, length);
    }
}
