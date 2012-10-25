// Licensed under the Apache License, Version 2.0 (the "License");
package net.sourceforge.eclipsejetty.starter.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
        boolean showInfo = System.getProperty(HIDE_LAUNCH_INFO_KEY) == null;

        if (showInfo)
        {
            printLogo();
            System.out.println();
        }

        String configurationFileDef = System.getProperty(CONFIGURATION_KEY);

        if (configurationFileDef == null)
        {
            throw new IOException("-D" + CONFIGURATION_KEY + " missing");
        }

        File[] configurationFiles = getConfigurationFiles(configurationFileDef);

        start(configurationFiles, showInfo);
    }

    protected abstract void start(File[] configurationFiles, boolean showInfo) throws Exception;

    protected abstract void printLogo();

    protected void configure(File[] configurationFiles, Object server, boolean showInfo) throws Exception
    {
        for (int i = 0; i < configurationFiles.length; i += 1)
        {
            File configurationFile = configurationFiles[i];

            if (showInfo)
            {
                System.out.println(String.format("%18s%s", (i == 0) ? "Configuration: " : "",
                    configurationFile.getAbsolutePath()));
            }

            Class<?> type = determineClass(configurationFile);
            FileInputStream in = new FileInputStream(configurationFile);

            try
            {
                configure(in, type, server, showInfo);
            }
            finally
            {
                in.close();
            }
        }
    }

    protected abstract void configure(FileInputStream in, Class<?> type, Object server, boolean showInfo)
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
            throw new IOException("Failed to configure parser", e);
        }

        Document document;

        try
        {
            document = builder.parse(new InputSource(file.getAbsolutePath()));
        }
        catch (SAXException e)
        {
            throw new IOException("Failed to parse " + file, e);
        }
        catch (IOException e)
        {
            throw new IOException("Failed to read " + file, e);
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

}
