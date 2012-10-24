// Licensed under the Apache License, Version 2.0 (the "License");
package net.sourceforge.eclipsejetty.starter.jetty8;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import net.sourceforge.eclipsejetty.starter.common.AbstractJettyLauncherMain;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.xml.XmlConfiguration;
import org.xml.sax.SAXException;

/**
 * Main for Jetty 8
 * 
 * @author Christian K&ouml;berl
 * @author Manfred Hantschel
 */
public class Jetty8LauncherMain extends AbstractJettyLauncherMain
{

    public static void main(String[] args) throws Exception
    {
        new Jetty8LauncherMain().launch(args);
    }

    /* (non-Javadoc)
     * @see net.sourceforge.eclipsejetty.jetty5.Jetty5LauncherMain#printLogo()
     */
    @Override
    protected void printLogo()
    {
        System.out.println("   ____    ___                   __    __  __         ___");
        System.out.println("  / __/___/ (_)__  ___ ___   __ / /__ / /_/ /___ __  ( _ )");
        System.out.println(" / _// __/ / / _ \\(_-</ -_) / // / -_) __/ __/ // / / _  |");
        System.out.println("/___/\\__/_/_/ .__/___/\\__/  \\___/\\__/\\__/\\__/\\_, /  \\___/");
        System.out.println("           /_/                              /___/");
    }

    @Override
    protected void start(File[] configurationFiles, File[] webAppConfigurationFiles, boolean showInfo) throws Exception
    {
        Server server = new Server();

        configure("Server-Config", configurationFiles, server, showInfo);

        for (Handler handler : server.getHandlers())
        {
            if (handler instanceof WebAppContext)
            {
                configure("WebApp-Config", webAppConfigurationFiles, handler, showInfo);
            }
        }

        if (showInfo)
        {
            printInfo(server);
        }

        server.start();
    }

    private void configure(String name, File[] configurationFiles, Object object, boolean showInfo)
        throws FileNotFoundException, SAXException, IOException, Exception
    {
        for (int i = 0; i < configurationFiles.length; i += 1)
        {
            File configurationFile = configurationFiles[i];
            XmlConfiguration configuration;

            if (showInfo)
            {
                System.out.println(String.format("%18s%s", (i == 0) ? name + ": " : "",
                    configurationFile.getAbsolutePath()));
            }

            FileInputStream in = new FileInputStream(configurationFile);

            try
            {
                configuration = new XmlConfiguration(in);
            }
            finally
            {
                in.close();
            }

            configuration.configure(object);
        }
    }

    private void printInfo(Server server)
    {
        System.out.println("         Version: " + Server.getVersion());
        System.out.println("         Context: " + getContextPaths(server));
        System.out.println("            Port: " + getPorts(server));
        System.out.println("       Classpath: " + getClassPaths(server).replaceAll("\\n", "\n                  "));
        System.out.println();
    }

    protected String getPorts(Server server)
    {
        StringBuilder builder = new StringBuilder();
        Connector[] connectors = server.getConnectors();

        if (connectors != null)
        {
            for (Connector connector : connectors)
            {
                if (builder.length() > 0)
                {
                    builder.append(", ");
                }

                builder.append(connector.getPort());
            }
        }

        return builder.toString();
    }

    protected String getContextPaths(Server server)
    {
        return getContextPaths(new StringBuilder(), server.getHandler()).toString();
    }

    protected StringBuilder getContextPaths(StringBuilder builder, Handler... handlers)
    {
        if (handlers != null)
        {
            for (Handler handler : handlers)
            {
                if (handler instanceof HandlerCollection)
                {
                    getContextPaths(builder, ((HandlerCollection) handler).getHandlers());
                }
                else if (handler instanceof ContextHandler)
                {
                    if (builder.length() > 0)
                    {
                        builder.append(", ");
                    }

                    builder.append(((ContextHandler) handler).getContextPath());
                }
            }
        }

        return builder;
    }

    protected String getClassPaths(Server server)
    {
        StringBuilder builder = new StringBuilder();
        List<String> classPathEntries =
            new ArrayList<String>(getClassPaths(new LinkedHashSet<String>(), server.getHandler()));

        Collections.sort(classPathEntries);

        for (String entry : classPathEntries)
        {
            if (builder.length() > 0)
            {
                builder.append("\n");
            }

            builder.append(entry);
        }

        return builder.toString();
    }

    protected Collection<String> getClassPaths(Collection<String> classPath, Handler... handlers)
    {
        if (handlers != null)
        {
            for (Handler handler : handlers)
            {
                if (handler instanceof HandlerCollection)
                {
                    getClassPaths(classPath, ((HandlerCollection) handler).getHandlers());
                }
                else if (handler instanceof WebAppContext)
                {
                    String extraClasspath = ((WebAppContext) handler).getExtraClasspath();

                    if (extraClasspath != null)
                    {
                        // Collections.addAll(classPath, extraClasspath.split(File.pathSeparator)); // it seems, Jetty was built for Windows
                        Collections.addAll(classPath, extraClasspath.split(";"));
                    }
                }
            }
        }

        return classPath;
    }

}
