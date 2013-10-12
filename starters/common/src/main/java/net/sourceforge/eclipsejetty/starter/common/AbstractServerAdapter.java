// Licensed under the Apache License, Version 2.0 (the "License");
package net.sourceforge.eclipsejetty.starter.common;

import java.io.PrintStream;
import java.util.Collection;
import java.util.LinkedHashSet;

public abstract class AbstractServerAdapter implements ServerAdapter
{

    public AbstractServerAdapter()
    {
        super();
    }

    public void info(PrintStream out)
    {
        out.println(String.format("         Version: %s", getVersionDescription()));
        out.println(String.format("         Context: %s", getContextPathDescription()));
        out.println(String.format("            Port: %s", getPortDescription()));
        out.println(String.format("       Classpath: %s",
            getClassPathDescription().replaceAll("\\n", "\n                  ")));
    }

    protected abstract String getVersionDescription();

    protected String getContextPathDescription()
    {
        StringBuilder builder = new StringBuilder();
        Collection<String> contextPaths = getContextPaths();

        for (String contextPath : contextPaths)
        {
            if (builder.length() > 0)
            {
                builder.append(", ");
            }

            builder.append(contextPath);
        }

        return builder.toString();
    }

    protected String getPortDescription()
    {
        StringBuilder builder = new StringBuilder();
        Collection<Integer> ports = new LinkedHashSet<Integer>();

        ports.addAll(getPorts());
        ports.addAll(getSecurePorts());

        for (Integer port : ports)
        {
            if (builder.length() > 0)
            {
                builder.append(", ");
            }

            builder.append(port);
        }

        return builder.toString();
    }

    protected abstract String getClassPathDescription();

}
