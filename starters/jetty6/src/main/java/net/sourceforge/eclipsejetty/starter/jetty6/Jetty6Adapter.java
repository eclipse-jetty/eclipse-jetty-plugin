// Licensed under the Apache License, Version 2.0 (the "License");
package net.sourceforge.eclipsejetty.starter.jetty6;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import net.sourceforge.eclipsejetty.starter.common.AbstractServerAdapter;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.handler.HandlerCollection;
import org.mortbay.jetty.webapp.WebAppContext;

public class Jetty6Adapter extends AbstractServerAdapter
{

    private final Server server;

    public Jetty6Adapter(Server server)
    {
        super();

        this.server = server;
    }

    public Object getServer()
    {
        return server;
    }

    public void start() throws Exception
    {
        server.start();
    }

    public void stop() throws Exception
    {
        server.stop();
    }

    public boolean isRunning()
    {
        return server.isRunning();
    }

    public Collection<Integer> getPorts()
    {
        Collection<Integer> results = new LinkedHashSet<Integer>();
        Connector[] connectors = server.getConnectors();

        if (connectors != null)
        {
            for (Connector connector : connectors)
            {
                if (!connector.getClass().getSimpleName().toLowerCase().contains("ssl"))
                {
                    results.add(connector.getPort());
                }
            }
        }

        return results;
    }

    public Collection<Integer> getSecurePorts()
    {
        Collection<Integer> results = new LinkedHashSet<Integer>();
        Connector[] connectors = server.getConnectors();

        if (connectors != null)
        {
            for (Connector connector : connectors)
            {
                if (connector.getClass().getSimpleName().toLowerCase().contains("ssl"))
                {
                    results.add(connector.getPort());
                }
            }
        }

        return results;
    }

    public Collection<String> getContextPaths()
    {
        return getContextPaths(new LinkedHashSet<String>(), server.getHandler());
    }

    protected Collection<String> getContextPaths(LinkedHashSet<String> results, Handler... handlers)
    {
        if (handlers != null)
        {
            for (Handler handler : handlers)
            {
                if (handler instanceof HandlerCollection)
                {
                    getContextPaths(results, ((HandlerCollection) handler).getHandlers());
                }
                else if (handler instanceof ContextHandler)
                {
                    results.add(((ContextHandler) handler).getContextPath());
                }
            }
        }

        return results;
    }

    @Override
    protected String getVersionDescription()
    {
        return Server.getVersion();
    }

    @Override
    protected String getClassPathDescription()
    {
        StringBuilder builder = new StringBuilder();
        List<String> classPathEntries =
            new ArrayList<String>(getClassPathDescription(new LinkedHashSet<String>(), server.getHandler()));

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

    protected Collection<String> getClassPathDescription(Collection<String> classPath, Handler... handlers)
    {
        if (handlers != null)
        {
            for (Handler handler : handlers)
            {
                if (handler instanceof HandlerCollection)
                {
                    getClassPathDescription(classPath, ((HandlerCollection) handler).getHandlers());
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
