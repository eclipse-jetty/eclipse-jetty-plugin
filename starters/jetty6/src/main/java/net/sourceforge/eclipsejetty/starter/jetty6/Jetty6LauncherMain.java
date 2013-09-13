// Licensed under the Apache License, Version 2.0 (the "License");
package net.sourceforge.eclipsejetty.starter.jetty6;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import net.sourceforge.eclipsejetty.starter.common.AbstractJettyLauncherMain;
import net.sourceforge.eclipsejetty.starter.common.BufferedPrintWriter;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.handler.HandlerCollection;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.xml.XmlConfiguration;

/**
 * Main for Jetty 6
 * 
 * @author Christian K&ouml;berl
 * @author Manfred Hantschel
 */
public class Jetty6LauncherMain extends AbstractJettyLauncherMain
{

    public static void main(String[] args) throws Exception
    {
        new Jetty6LauncherMain().launch(args);
    }

    /* (non-Javadoc)
     * @see net.sourceforge.eclipsejetty.jetty5.Jetty5LauncherMain#printLogo()
     */
    @Override
    protected void printLogo(PrintWriter writer)
    {
        writer.println("   ____    ___                   __    __  __         ____");
        writer.println("  / __/___/ (_)__  ___ ___   __ / /__ / /_/ /___ __  / __/");
        writer.println(" / _// __/ / / _ \\(_-</ -_) / // / -_) __/ __/ // / / _ \\");
        writer.println("/___/\\__/_/_/ .__/___/\\__/  \\___/\\__/\\__/\\__/\\_, /  \\___/");
        writer.println("           /_/                              /___/");
    }

    @Override
    protected void start(File[] configurationFiles, boolean showInfo) throws Exception
    {
        Server server = new Server();
        BufferedPrintWriter writer = (showInfo) ? new BufferedPrintWriter() : null;

        try
        {
            configure(writer, configurationFiles, server);

            if (writer != null)
            {
                printInfo(writer, server);
            }
        }
        finally
        {
            if (writer != null)
            {
                System.out.println(writer.toString());
            }
        }

        server.start();
    }

    @Override
    protected void configure(PrintWriter writer, FileInputStream in, Class<?> type, Object server) throws Exception
    {
        XmlConfiguration configuration = new XmlConfiguration(in);

        if (type.isInstance(server))
        {
            configuration.configure(server);

            return;
        }

        boolean success = false;

        Handler[] handlers = ((Server) server).getHandlers();

        if (handlers != null)
        {
            for (Handler handler : handlers)
            {
                if (type.isInstance(handler))
                {
                    configuration.configure(handler);

                    success = true;
                }
            }
        }

        if (success)
        {
            return;
        }

        Handler handler = ((Server) server).getHandler();

        if (type.isInstance(handler))
        {
            configuration.configure(handler);

            return;
        }

        throw new IllegalArgumentException("Failed to run configuration for " + type
            + ". No matching object found in server.");
    }

    private void printInfo(PrintWriter writer, Server server)
    {
        writer.println("         Version: " + Server.getVersion());
        writer.println("         Context: " + getContextPaths(server));
        writer.println("            Port: " + getPorts(server));
        writer.println("       Classpath: " + getClassPaths(server).replaceAll("\\n", "\n                  "));
        writer.println();
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
