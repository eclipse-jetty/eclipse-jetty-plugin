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
package net.sourceforge.eclipsejetty.jetty;

import java.util.Collection;
import java.util.LinkedHashSet;

import net.sourceforge.eclipsejetty.util.DOMBuilder;

public abstract class AbstractServerConfiguration extends AbstractConfiguration
{

    private final Collection<String> defaultClasspath;

    private Integer port;
    private Integer sslPort;

    private String defaultWar;
    private String defaultContextPath;

    public AbstractServerConfiguration()
    {
        super();

        defaultClasspath = new LinkedHashSet<String>();
    }

    public Integer getPort()
    {
        return port;
    }

    public void setPort(Integer port)
    {
        this.port = port;
    }

    public Integer getSslPort()
    {
        return sslPort;
    }

    public void setSslPort(Integer sslPort)
    {
        this.sslPort = sslPort;
    }

    public String getDefaultWar()
    {
        return defaultWar;
    }

    public void setDefaultWar(String defaultWar)
    {
        this.defaultWar = defaultWar;
    }

    public String getDefaultContextPath()
    {
        return defaultContextPath;
    }

    public void setDefaultContextPath(String defaultContextPath)
    {
        this.defaultContextPath = defaultContextPath;
    }

    public Collection<String> getDefaultClasspath()
    {
        return defaultClasspath;
    }

    public void addDefaultClasspath(String... classpaths)
    {
        for (String classpath : classpaths)
        {
            defaultClasspath.add(classpath);
        }
    }

    @Override
    protected String getIdToConfigure()
    {
        return "Server";
    }

    @Override
    protected void buildContent(DOMBuilder builder)
    {
        buildConnector(builder);
        buildSSLConnector(builder);

        buildHandler(builder);
    }

    protected void buildConnector(DOMBuilder builder)
    {
        if (port != null)
        {
            builder.begin("Call").attribute("name", "addConnector");
            builder.begin("Arg");

            builder.begin("New").attribute("class", getConnectorClass());
            buildConnectorSetters(builder);
            builder.end();

            builder.end();
            builder.end();
        }
    }

    protected abstract String getConnectorClass();

    protected void buildConnectorSetters(DOMBuilder builder)
    {
        builder.begin("Set").attribute("name", "port").text(getPort()).end();
    }

    protected void buildSSLConnector(DOMBuilder builder)
    {
        if (sslPort != null)
        {
            builder.begin("Call").attribute("name", "addConnector");
            builder.begin("Arg");

            builder.begin("New").attribute("class", getSSLConnectorClass());
            buildSSLConnectorSetters(builder);
            builder.end();

            builder.end();
            builder.end();
        }
    }

    protected abstract String getSSLConnectorClass();

    protected void buildSSLConnectorSetters(DOMBuilder builder)
    {
        builder.begin("Set").attribute("name", "port").text(getSslPort()).end();
    }

    protected void buildHandler(DOMBuilder builder)
    {
        buildDefaultHandler(builder);
    }

    protected void buildDefaultHandler(DOMBuilder builder)
    {
        builder.begin("Set").attribute("name", "handler");

        builder.begin("New").attribute("class", getDefaultHandlerClass());
        builder.begin("Arg").attribute("type", "String").text(getDefaultWar()).end();
        builder.begin("Arg").attribute("type", "String").text(getDefaultContextPath()).end();
        buildDefaultHandlerSetters(builder);
        builder.end();

        builder.end();
    }

    protected abstract String getDefaultHandlerClass();

    protected void buildDefaultHandlerSetters(DOMBuilder builder)
    {
        builder.begin("Set").attribute("name", "extraClasspath").text(link(defaultClasspath)).end();
    }

}
