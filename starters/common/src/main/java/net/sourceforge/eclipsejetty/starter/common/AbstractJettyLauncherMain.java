// Licensed under the Apache License, Version 2.0 (the "License");
package net.sourceforge.eclipsejetty.starter.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.sourceforge.eclipsejetty.starter.console.Console;
import net.sourceforge.eclipsejetty.starter.util.Utils;
import net.sourceforge.eclipsejetty.starter.util.service.GlobalServiceResolver;

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
    public static final String DISABLE_CONSOLE_KEY = "jetty.launcher.disableConsole";

    protected void launch(String[] args) throws Exception
    {
        long millis = System.currentTimeMillis();
        boolean showInfo = System.getProperty(HIDE_LAUNCH_INFO_KEY) == null;
        boolean consoleEnabled = System.getProperty(DISABLE_CONSOLE_KEY) == null;

        if (showInfo)
        {
            printLogo(System.out);
        }

        String configurationFileDef = System.getProperty(CONFIGURATION_KEY);

        if (configurationFileDef == null)
        {
            throw new IOException("-D" + CONFIGURATION_KEY + " missing");
        }

        File[] configurationFiles = getConfigurationFiles(configurationFileDef);
        ServerAdapter adapter = createAdapter(configurationFiles, showInfo);

        configure(System.out, adapter, configurationFiles, showInfo);

        if (showInfo)
        {
            adapter.info(System.out);
        }

        initConsole(consoleEnabled, adapter);

        adapter.start();

        if (showInfo)
        {
            printStartupTime(System.out, millis, consoleEnabled);
        }
    }

    private void initConsole(boolean consoleEnabled, ServerAdapter adapter)
    {
        if (consoleEnabled)
        {
            try
            {
                Class.forName("net.sourceforge.eclipsejetty.starter.console.Console");

                GlobalServiceResolver serviceResolver = GlobalServiceResolver.INSTANCE;
                
                serviceResolver.register(adapter);

                Console console = Console.INSTANCE;
                
                console.initialize(serviceResolver);
                console.start();
            }
            catch (ClassNotFoundException e)
            {
                // ignore
            }
        }
    }

    protected abstract ServerAdapter createAdapter(File[] configurationFiles, boolean showInfo) throws Exception;

    protected abstract void printLogo(PrintStream out);

    protected void configure(PrintStream out, ServerAdapter adapter, File[] configurationFiles, boolean showInfo)
        throws Exception
    {
        for (int i = 0; i < configurationFiles.length; i += 1)
        {
            File configurationFile = configurationFiles[i];

            if (showInfo)
            {
                out.println(String.format("%18s%s", (i == 0) ? "Configuration: " : "",
                    configurationFile.getAbsolutePath()));
            }

            Class<?> type = determineClass(configurationFile);
            FileInputStream in = new FileInputStream(configurationFile);

            try
            {
                configure(in, type, adapter);
            }
            finally
            {
                in.close();
            }
        }
    }

    protected abstract void configure(FileInputStream in, Class<?> type, ServerAdapter adapter) throws Exception;

    protected static File[] getConfigurationFiles(String definitionList) throws IOException
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

    protected static Class<?> determineClass(File file) throws IOException
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

    protected void printStartupTime(PrintStream out, long millis, boolean consoleEnabled)
    {
        Runtime runtime = Runtime.getRuntime();

        System.gc();

        double seconds = ((double) System.currentTimeMillis() - millis) / 1000d;
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();

        String duration = "Jetty startup finished in " + Utils.formatSeconds(seconds) + ".";
        String console = (consoleEnabled) ? "Console available: type \"help\"." : "";
        String memory =
            "Used memory: " + Utils.formatBytes(totalMemory - freeMemory) + " of " + Utils.formatBytes(totalMemory)
                + " (" + Utils.formatBytes(maxMemory) + " maximum)";

        String line = Utils.repeat("-", Math.max(duration.length(), Math.max(memory.length(), console.length())));

        out.println(line);
        out.println(duration);
        out.println(memory);

        if (console.length() > 0)
        {
            out.println(console);
        }

        out.println(line);
    }

}
