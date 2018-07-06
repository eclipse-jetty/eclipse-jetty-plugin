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

/**
 * Abstract builder for a Jetty configuration
 *
 * @author Manfred Hantschel
 */
public abstract class AbstractServerConfiguration extends AbstractConfiguration
{

    private final Collection<String> defaultClasspath;

    private Integer majorVersion;
    private Integer minorVersion;
    private Integer microVersion;

    private boolean annotationsEnabled = false;
    private boolean jndiEnabled = false;
    private boolean jmxEnabled = false;
    private boolean websocketEnabled = false;

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

    public Integer getMajorVersion()
    {
        return majorVersion;
    }

    public void setMajorVersion(Integer majorVersion)
    {
        this.majorVersion = majorVersion;
    }

    public Integer getMinorVersion()
    {
        return minorVersion;
    }

    public void setMinorVersion(Integer minorVersion)
    {
        this.minorVersion = minorVersion;
    }

    public Integer getMicroVersion()
    {
        return microVersion;
    }

    public void setMicroVersion(Integer microVersion)
    {
        this.microVersion = microVersion;
    }

    /**
     * Returns true if Annotations support is enabled.
     *
     * @return true if Annotations support is enabled
     */
    public boolean isAnnotationsEnabled()
    {
        return annotationsEnabled || jndiEnabled;
    }

    /**
     * Set to true if Annotations support is enabled.
     *
     * @param annotationsEnabled true if Annotations support is enabled
     */
    public void setAnnotationsEnabled(boolean annotationsEnabled)
    {
        this.annotationsEnabled = annotationsEnabled;
    }

    /**
     * Returns true if JNDI support is enabled.
     *
     * @return true if JNDI support is enabled
     */
    public boolean isJndiEnabled()
    {
        return jndiEnabled;
    }

    /**
     * Toggles the JNDI support.
     *
     * @param jndiEnabled true to enable JNDI
     */
    public void setJndiEnabled(boolean jndiEnabled)
    {
        this.jndiEnabled = jndiEnabled;
    }

    /**
     * Returns true if JMX support is enabled.
     *
     * @return true if JMX support is enabled
     */
    public boolean isJmxEnabled()
    {
        return jmxEnabled;
    }

    /**
     * Toggles the JMX support.
     *
     * @param jmxEnabled true to enable JMX
     */
    public void setJmxEnabled(boolean jmxEnabled)
    {
        this.jmxEnabled = jmxEnabled;
    }

    public boolean isWebsocketEnabled()
    {
        return websocketEnabled;
    }

    public void setWebsocketEnabled(boolean websocketEnabled)
    {
        this.websocketEnabled = websocketEnabled; 
    }

    /**
     * Returns the (HTTP) port.
     *
     * @return the port
     */
    public Integer getPort()
    {
        return port;
    }

    /**
     * Sets the (HTTP) port, null to disable.
     *
     * @param port the port
     */
    public void setPort(Integer port)
    {
        this.port = port;
    }

    /**
     * Returns the HTTPs port.
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
     * Sets the graceful shutdown timeout (in milliseconds).
     *
     * @param gracefulShutdown the graceful shutdown timeout (in milliseconds)
     */
    public void setGracefulShutdown(Integer gracefulShutdown)
    {
        this.gracefulShutdown = gracefulShutdown;
    }

    /**
     * Returns the thread pool limit.
     *
     * @return the thread pool limit
     */
    public Integer getThreadPoolLimit()
    {
        return threadPoolLimit;
    }

    /**
     * Sets the thread pool limit, null to disable.
     *
     * @param threadPoolLimit the thread pool limit
     */
    public void setThreadPoolLimit(Integer threadPoolLimit)
    {
        this.threadPoolLimit = threadPoolLimit;
    }

    /**
     * Returns the acceptor limit.
     *
     * @return the acceptor limit
     */
    public Integer getAcceptorLimit()
    {
        return acceptorLimit;
    }

    /**
     * Sets the acceptor limit, null to disable.
     *
     * @param acceptorLimit the acceptor limit
     */
    public void setAcceptorLimit(Integer acceptorLimit)
    {
        this.acceptorLimit = acceptorLimit;
    }

    /**
     * Returns the key store path.
     *
     * @return the key store path
     */
    public String getKeyStorePath()
    {
        return keyStorePath;
    }

    /**
     * Sets the key store path.
     *
     * @param keyStorePath the key store path
     */
    public void setKeyStorePath(String keyStorePath)
    {
        this.keyStorePath = keyStorePath;
    }

    /**
     * Returns the key store password.
     *
     * @return the key store password
     */
    public String getKeyStorePassword()
    {
        return keyStorePassword;
    }

    /**
     * Sets the key store password.
     *
     * @param keyStorePassword the key store password
     */
    public void setKeyStorePassword(String keyStorePassword)
    {
        this.keyStorePassword = keyStorePassword;
    }

    /**
     * Returns the key manager password.
     *
     * @return the key manager password
     */
    public String getKeyManagerPassword()
    {
        return keyManagerPassword;
    }

    /**
     * Set the key manager password.
     *
     * @param keyManagerPassword the key manager password
     */
    public void setKeyManagerPassword(String keyManagerPassword)
    {
        this.keyManagerPassword = keyManagerPassword;
    }

    /**
     * Returns the path to the webapp directory.
     *
     * @return the path to the webapp directory
     */
    public File getDefaultWar()
    {
        return defaultWar;
    }

    /**
     * Sets the path to the webapp directory.
     *
     * @param defaultWar the path to the webapp directory
     */
    public void setDefaultWar(File defaultWar)
    {
        this.defaultWar = defaultWar;
    }

    /**
     * Returns the context path.
     *
     * @return the context path
     */
    public String getDefaultContextPath()
    {
        return defaultContextPath;
    }

    /**
     * Sets the context path.
     *
     * @param defaultContextPath the context path
     */
    public void setDefaultContextPath(String defaultContextPath)
    {
        this.defaultContextPath = defaultContextPath;
    }

    /**
     * Returns the path to the custom web defaults file.
     *
     * @return the path to the custom web defaults file
     */
    public File getCustomWebDefaultsFile()
    {
        return customWebDefaultsFile;
    }

    /**
     * Sets the path to the custom web defaults file.
     *
     * @param customWebDefaultsFile the path to the custom web defaults file
     */
    public void setCustomWebDefaultsFile(File customWebDefaultsFile)
    {
        this.customWebDefaultsFile = customWebDefaultsFile;
    }

    /**
     * Returns the classpath of the web application.
     *
     * @return the classpath of the web application
     */
    public Collection<String> getDefaultClasspath()
    {
        return defaultClasspath;
    }

    /**
     * Sets the classpath of the web application.
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
    protected void buildContent(JettyConfigBuilder builder)
    {
        buildThreadPool(builder);
        buildHttpConfig(builder);
        buildHttpConnector(builder);
        buildHttpsConfig(builder);
        buildHttpsConnector(builder);
        buildHandler(builder);
        buildAnnotations(builder);
        buildJNDI(builder);
        buildJMX(builder);
        buildExtraOptions(builder);

    }

    /**
     * Builds the thread pool part.
     *
     * @param builder the builder
     */
    protected abstract void buildThreadPool(JettyConfigBuilder builder);

    /**
     * Builds the HTTP config part.
     *
     * @param builder the builder
     */
    protected abstract void buildHttpConfig(JettyConfigBuilder builder);

    /**
     * Builds the HTTP connector part.
     *
     * @param builder the builder
     */
    protected abstract void buildHttpConnector(JettyConfigBuilder builder);

    /**
     * Builds the HTTPS config part.
     *
     * @param builder the builder
     */
    protected abstract void buildHttpsConfig(JettyConfigBuilder builder);

    /**
     * Builds the HTTPs connector part, if enabled.
     *
     * @param builder the builder
     */
    protected abstract void buildHttpsConnector(JettyConfigBuilder builder);

    /**
     * Builds the handler part.
     *
     * @param builder the builder
     */
    protected void buildHandler(JettyConfigBuilder builder)
    {
        buildDefaultHandler(builder);
    }

    /**
     * Builds the default handler.
     *
     * @param builder the builder
     */
    protected void buildDefaultHandler(JettyConfigBuilder builder)
    {
        builder.comment("Handler");

        builder.beginSet("handler");
        {
            builder.beginNew(getDefaultHandlerClass());
            {
                File defaultWar = getDefaultWar();
                
                builder.arg((defaultWar != null) ? defaultWar.getAbsolutePath() : "/");
                builder.arg(getDefaultContextPath());

                if (getCustomWebDefaultsFile() != null)
                {
                    builder.set("defaultsDescriptor", getCustomWebDefaultsFile().getAbsolutePath());
                }

                Collection<String> configurations = new LinkedHashSet<String>();

                collectDefaultHandlerConfigurations(configurations);

                if (configurations.size() > 0)
                {
                    builder.setArray("configurationClasses", configurations.toArray());
                }

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
     * Builds additional handler setters.
     *
     * @param builder the builder
     */
    protected void buildDefaultHandlerSetters(JettyConfigBuilder builder)
    {
        builder.set("extraClasspath", link(defaultClasspath));
    }

    /**
     * Collect all configurations for the handler
     *
     * @param configurations the configurations
     */
    protected abstract void collectDefaultHandlerConfigurations(Collection<String> configurations);

    /**
     * Builds the Annotations part, if needed.
     *
     * @param builder the builder
     */
    protected abstract void buildAnnotations(JettyConfigBuilder builder);

    /**
     * Builds the JNDI part, if enabled.
     *
     * @param builder the builder
     */
    protected abstract void buildJNDI(JettyConfigBuilder builder);

    /**
     * Builds the JMX part, if enabled.
     *
     * @param builder the builder
     */
    protected abstract void buildJMX(JettyConfigBuilder builder);

    /**
     * Builds extra options.
     *
     * @param builder the builder
     */
    protected abstract void buildExtraOptions(JettyConfigBuilder builder);

}
