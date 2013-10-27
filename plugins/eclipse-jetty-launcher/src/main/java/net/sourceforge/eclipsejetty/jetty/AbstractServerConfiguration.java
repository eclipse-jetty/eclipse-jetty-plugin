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

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import net.sourceforge.eclipsejetty.util.DOMBuilder;

/**
 * Abstract builder for a Jetty configuration
 * 
 * @author Manfred Hantschel
 */
public abstract class AbstractServerConfiguration extends AbstractConfiguration
{

    private final Collection<String> defaultClasspath;

    private boolean jndi = false;
    private boolean jmx = false;
    private Integer port;
    private Integer sslPort;
    private Integer gracefulShutdown;
    private Integer threadPoolLimit;
    private Integer acceptorLimit;

    private String keyStorePath;
    private String keyStorePassword;
    private String keyManagerPassword;

    private File defaultWar;
    private String defaultContextPath;
    private File customWebDefaultsFile;

    public AbstractServerConfiguration()
    {
        super();

        defaultClasspath = new LinkedHashSet<String>();
    }

    /**
     * Returns true if JNDI support is enabled.
     * 
     * @return true if JNDI support is enabled
     */
    public boolean isJndi()
    {
        return jndi;
    }

    /**
     * Toggles the JNDI support
     * 
     * @param jndi true to enable JNDI
     */
    public void setJndi(boolean jndi)
    {
        this.jndi = jndi;
    }

    /**
     * Returns true if JMX support is enabled.
     * 
     * @return true if JMX support is enabled
     */
    public boolean isJmx()
    {
        return jmx;
    }

    /**
     * Toggles the JMX support
     * 
     * @param jmx true to enable JMX
     */
    public void setJmx(boolean jmx)
    {
        this.jmx = jmx;
    }

    /**
     * Returns the (HTTP) port
     * 
     * @return the port
     */
    public Integer getPort()
    {
        return port;
    }

    /**
     * Sets the (HTTP) port, null to disable
     * 
     * @param port the port
     */
    public void setPort(Integer port)
    {
        this.port = port;
    }

    /**
     * Returns the HTTPs port
     * 
     * @return the HTTPs port
     */
    public Integer getSslPort()
    {
        return sslPort;
    }

    /**
     * Sets the HTTPs port, null to disable. The keystore stuff must also be set: {@link #setKeyStorePath(String)},
     * {@link #setKeyStorePassword(String)}, {@link #setKeyManagerPassword(String)}.
     * 
     * @param sslPort the HTTPs port
     */
    public void setSslPort(Integer sslPort)
    {
        this.sslPort = sslPort;
    }

    /**
     * Return the graceful shutdown timeout (in milliseconds).
     * 
     * @return the graceful shutdown timeout (in milliseconds)
     */
    public Integer getGracefulShutdown()
    {
        return gracefulShutdown;
    }

    /**
     * Sets the graceful shutdown timeout (in milliseconds)
     * 
     * @param gracefulShutdown the graceful shutdown timeout (in milliseconds)
     */
    public void setGracefulShutdown(Integer gracefulShutdown)
    {
        this.gracefulShutdown = gracefulShutdown;
    }

    /**
     * Returns the thread pool limit
     * 
     * @return the thread pool limit
     */
    public Integer getThreadPoolLimit()
    {
        return threadPoolLimit;
    }

    /**
     * Sets the thread pool limit, null to disable
     * 
     * @param threadPoolLimit the thread pool limit
     */
    public void setThreadPoolLimit(Integer threadPoolLimit)
    {
        this.threadPoolLimit = threadPoolLimit;
    }

    /**
     * Returns the acceptor limit
     * 
     * @return the acceptor limit
     */
    public Integer getAcceptorLimit()
    {
        return acceptorLimit;
    }

    /**
     * Sets the acceptor limit, null to disable
     * 
     * @param acceptorLimit the acceptor limit
     */
    public void setAcceptorLimit(Integer acceptorLimit)
    {
        this.acceptorLimit = acceptorLimit;
    }

    /**
     * Returns the key store path
     * 
     * @return the key store path
     */
    public String getKeyStorePath()
    {
        return keyStorePath;
    }

    /**
     * Sets the key store path
     * 
     * @param keyStorePath the key store path
     */
    public void setKeyStorePath(String keyStorePath)
    {
        this.keyStorePath = keyStorePath;
    }

    /**
     * Returns the key store password
     * 
     * @return the key store password
     */
    public String getKeyStorePassword()
    {
        return keyStorePassword;
    }

    /**
     * Sets the key store password
     * 
     * @param keyStorePassword the key store password
     */
    public void setKeyStorePassword(String keyStorePassword)
    {
        this.keyStorePassword = keyStorePassword;
    }

    /**
     * Returns the key manager password
     * 
     * @return the key manager password
     */
    public String getKeyManagerPassword()
    {
        return keyManagerPassword;
    }

    /**
     * Set the key manager password
     * 
     * @param keyManagerPassword the key manager password
     */
    public void setKeyManagerPassword(String keyManagerPassword)
    {
        this.keyManagerPassword = keyManagerPassword;
    }

    /**
     * Returns the path to the webapp directory
     * 
     * @return the path to the webapp directory
     */
    public File getDefaultWar()
    {
        return defaultWar;
    }

    /**
     * Sets the path to the webapp directory
     * 
     * @param defaultWar the path to the webapp directory
     */
    public void setDefaultWar(File defaultWar)
    {
        this.defaultWar = defaultWar;
    }

    /**
     * Returns the context path
     * 
     * @return the context path
     */
    public String getDefaultContextPath()
    {
        return defaultContextPath;
    }

    /**
     * Sets the context path
     * 
     * @param defaultContextPath the context path
     */
    public void setDefaultContextPath(String defaultContextPath)
    {
        this.defaultContextPath = defaultContextPath;
    }

    /**
     * Returns the path to the custom web defaults file
     * 
     * @return the path to the custom web defaults file
     */
    public File getCustomWebDefaultsFile()
    {
        return customWebDefaultsFile;
    }

    /**
     * Sets the path to the custom web defaults file
     * 
     * @param customWebDefaultsFile the path to the custom web defaults file
     */
    public void setCustomWebDefaultsFile(File customWebDefaultsFile)
    {
        this.customWebDefaultsFile = customWebDefaultsFile;
    }

    /**
     * Returns the classpath of the web application
     * 
     * @return the classpath of the web application
     */
    public Collection<String> getDefaultClasspath()
    {
        return defaultClasspath;
    }

    /**
     * Sets the classpath of the web application
     * 
     * @param classpaths the classpath of the web application
     */
    public void addDefaultClasspath(String... classpaths)
    {
        for (String classpath : classpaths)
        {
            defaultClasspath.add(classpath);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.jetty.AbstractConfiguration#getIdToConfigure()
     */
    @Override
    protected String getIdToConfigure()
    {
        return "Server";
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.jetty.AbstractConfiguration#buildContent(net.sourceforge.eclipsejetty.util.DOMBuilder)
     */
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

    /**
     * Builds the thread pool part.
     * 
     * @param builder the builder
     */
    protected abstract void buildThreadPool(DOMBuilder builder);

    /**
     * Builds the HTTP config part.
     * 
     * @param builder the builder
     */
    protected abstract void buildHttpConfig(DOMBuilder builder);

    /**
     * Builds the HTTPS config part.
     * 
     * @param builder the builder
     */
    protected abstract void buildHttpsConfig(DOMBuilder builder);

    /**
     * Builds the JNDI part, if enabled.
     * 
     * @param builder the builder
     */
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

    /**
     * Returns the list of JNDI items as required by the defined Jetty.
     * 
     * @return the list of JNDI items as required by the defined Jetty
     */
    protected abstract List<String> getJNDIItems();

    /**
     * Builds the JMX part, if enabled.
     * 
     * @param builder the builder
     */
    protected abstract void buildJMX(DOMBuilder builder);

    /**
     * Builds the HTTP connector part.
     * 
     * @param builder the builder
     */
    protected abstract void buildHttpConnector(DOMBuilder builder);

    /**
     * Builds the HTTPs connector part, if enabled
     * 
     * @param builder the builder
     */
    protected abstract void buildHttpsConnector(DOMBuilder builder);

    /**
     * Builds the handler part.
     * 
     * @param builder the builder
     */
    protected void buildHandler(DOMBuilder builder)
    {
        buildDefaultHandler(builder);
    }

    /**
     * Builds the default handler.
     * 
     * @param builder the builder
     */
    protected void buildDefaultHandler(DOMBuilder builder)
    {
        builder.begin("Set").attribute("name", "handler");
        {
            builder.begin("New").attribute("class", getDefaultHandlerClass());
            {
                builder.element("Arg", "type", "String", getDefaultWar());
                builder.element("Arg", "type", "String", getDefaultContextPath());

                if (getCustomWebDefaultsFile() != null)
                {
                    builder.element("Set", "name", "defaultsDescriptor", getCustomWebDefaultsFile().getAbsolutePath());
                }

                buildJNDI(builder);
                buildDefaultHandlerSetters(builder);
            }
            builder.end();
        }
        builder.end();
    }

    /**
     * Returns the default handler class for the Jetty version.
     * 
     * @return the default handler class
     */
    protected abstract String getDefaultHandlerClass();

    /**
     * Builds additional handler setters
     * 
     * @param builder the builder
     */
    protected void buildDefaultHandlerSetters(DOMBuilder builder)
    {
        builder.element("Set", "name", "extraClasspath", link(defaultClasspath));
    }

    /**
     * Builds extra options.
     * 
     * @param builder the builder
     */
    protected abstract void buildExtraOptions(DOMBuilder builder);

}
