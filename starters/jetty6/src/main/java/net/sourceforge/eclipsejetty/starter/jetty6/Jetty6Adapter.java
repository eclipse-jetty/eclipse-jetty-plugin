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
package net.sourceforge.eclipsejetty.starter.jetty6;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import net.sourceforge.eclipsejetty.starter.common.AbstractServerAdapter;
import net.sourceforge.eclipsejetty.starter.common.ServerAdapter;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.handler.HandlerCollection;
import org.mortbay.jetty.webapp.WebAppContext;

/**
 * Implemation of the {@link ServerAdapter} for Jetty 6
 * 
 * @author Manfred Hantschel
 */
public class Jetty6Adapter extends AbstractServerAdapter
{

    private final Server server;

    public Jetty6Adapter(Server server)
    {
        super();

        this.server = server;
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.common.ServerAdapter#getServer()
     */
    public Object getServer()
    {
        return server;
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.common.ServerAdapter#start()
     */
    public void start() throws Exception
    {
        server.start();
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.common.ServerAdapter#stop()
     */
    public void stop() throws Exception
    {
        server.stop();
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.common.ServerAdapter#isRunning()
     */
    public boolean isRunning()
    {
        return server.isRunning();
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.common.ServerAdapter#getPorts()
     */
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

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.common.ServerAdapter#getSecurePorts()
     */
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

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.common.ServerAdapter#getContextPaths()
     */
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

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.common.AbstractServerAdapter#getVersionDescription()
     */
    @Override
    protected String getVersionDescription()
    {
        return Server.getVersion();
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.common.AbstractServerAdapter#getClassPathDescription()
     */
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
