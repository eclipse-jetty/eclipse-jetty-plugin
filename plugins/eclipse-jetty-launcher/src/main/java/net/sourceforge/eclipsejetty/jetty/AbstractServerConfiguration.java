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
import java.util.List;

import net.sourceforge.eclipsejetty.util.DOMBuilder;

public abstract class AbstractServerConfiguration extends AbstractConfiguration
{

    private final Collection<String> defaultClasspath;

    private boolean jndi = false;
    private boolean jmx = false;
    private Integer port;
    private Integer sslPort;

    private String keyStorePath;
    private String keyStorePassword;
    private String keyManagerPassword;

    private String defaultWar;
    private String defaultContextPath;

    public AbstractServerConfiguration()
    {
        super();

        defaultClasspath = new LinkedHashSet<String>();
    }

    public boolean isJndi()
    {
        return jndi;
    }

    public void setJndi(boolean jndi)
    {
        this.jndi = jndi;
    }

    public boolean isJmx()
    {
        return jmx;
    }

    public void setJmx(boolean jmx)
    {
        this.jmx = jmx;
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

    public String getKeyStorePath()
    {
        return keyStorePath;
    }

    public void setKeyStorePath(String keyStorePath)
    {
        this.keyStorePath = keyStorePath;
    }

    public String getKeyStorePassword()
    {
        return keyStorePassword;
    }

    public void setKeyStorePassword(String keyStorePassword)
    {
        this.keyStorePassword = keyStorePassword;
    }

    public String getKeyManagerPassword()
    {
        return keyManagerPassword;
    }

    public void setKeyManagerPassword(String keyManagerPassword)
    {
        this.keyManagerPassword = keyManagerPassword;
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
        buildThreadPool(builder);
        buildHttpConfig(builder);
        buildHttpConnector(builder);
        buildHttpsConfig(builder);
        buildHttpsConnector(builder);
        buildHandler(builder);
        buildJMX(builder);
        buildExtraOptions(builder);

    }

    protected abstract void buildThreadPool(DOMBuilder builder);

    protected abstract void buildHttpConfig(DOMBuilder builder);

    protected abstract void buildHttpsConfig(DOMBuilder builder);

    protected void buildJNDI(DOMBuilder builder)
    {
        if (isJndi())
        {
            builder.begin("Array").attribute("id", "plusConfig").attribute("type", "String");
            {
                for (String item : getJNDIItems())
                {
                    builder.element("Item", item);
                }
            }
            builder.end();

            builder.begin("Set").attribute("name", "configurationClasses");
            {
                builder.element("Ref", "id", "plusConfig");
            }
            builder.end();
        }
    }

    protected abstract List<String> getJNDIItems();

    protected abstract void buildJMX(DOMBuilder builder);

    protected abstract void buildHttpConnector(DOMBuilder builder);

    protected abstract void buildHttpsConnector(DOMBuilder builder);

    protected void buildHandler(DOMBuilder builder)
    {
        buildDefaultHandler(builder);
    }

    protected void buildDefaultHandler(DOMBuilder builder)
    {
        builder.begin("Set").attribute("name", "handler");
        {
            builder.begin("New").attribute("class", getDefaultHandlerClass());
            {
                builder.element("Arg", "type", "String", getDefaultWar());
                builder.element("Arg", "type", "String", getDefaultContextPath());
                buildJNDI(builder);
                buildDefaultHandlerSetters(builder);
            }
            builder.end();
        }
        builder.end();
    }

    protected abstract String getDefaultHandlerClass();

    protected void buildDefaultHandlerSetters(DOMBuilder builder)
    {
        builder.element("Set", "name", "extraClasspath", link(defaultClasspath));
    }

    protected abstract void buildExtraOptions(DOMBuilder builder);

}
