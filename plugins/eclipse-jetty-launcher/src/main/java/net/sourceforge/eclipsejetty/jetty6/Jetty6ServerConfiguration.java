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
package net.sourceforge.eclipsejetty.jetty6;

import java.util.Collection;

import net.sourceforge.eclipsejetty.jetty.AbstractServerConfiguration;
import net.sourceforge.eclipsejetty.jetty.JettyConfigBuilder;
import net.sourceforge.eclipsejetty.jetty.JettyVersionType;

/**
 * Configuration for Jetty 6
 * 
 * @author Manfred Hantschel
 */
public class Jetty6ServerConfiguration extends AbstractServerConfiguration
{

    public Jetty6ServerConfiguration()
    {
        super();
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.jetty.AbstractConfiguration#getJettyVersionType()
     */
    @Override
    protected JettyVersionType getJettyVersionType()
    {
        return JettyVersionType.JETTY_6;
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.jetty.AbstractConfiguration#getDocType()
     */
    @Override
    protected String getDocType()
    {
        // <!DOCTYPE Configure PUBLIC "-//Mort Bay Consulting//DTD Configure//EN" "http://jetty.mortbay.org/configure.dtd">
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.jetty.AbstractServerConfiguration#buildThreadPool(net.sourceforge.eclipsejetty.jetty.JettyConfigBuilder)
     */
    @Override
    protected void buildThreadPool(JettyConfigBuilder builder)
    {
        builder.comment("Thread Pool");

        builder.beginSet("ThreadPool");
        {
            builder.beginNew("org.mortbay.thread.QueuedThreadPool");
            {
                builder.set("minThreads", 1);

                Integer connectionLimit = getThreadPoolLimit();

                if (connectionLimit != null)
                {
                    builder.set("maxThreads", connectionLimit);
                }

                builder.set("lowThreads", 1);

            }
            builder.end();
        }
        builder.end();
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.jetty.AbstractServerConfiguration#buildHttpConfig(net.sourceforge.eclipsejetty.jetty.JettyConfigBuilder)
     */
    @Override
    protected void buildHttpConfig(JettyConfigBuilder builder)
    {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.jetty.AbstractServerConfiguration#buildHttpConnector(net.sourceforge.eclipsejetty.jetty.JettyConfigBuilder)
     */
    @Override
    protected void buildHttpConnector(JettyConfigBuilder builder)
    {
        if (getPort() == null)
        {
            return;
        }

        builder.comment("HTTP Connector");

        builder.beginCall("addConnector");
        {
            builder.beginArg();
            {
                builder.beginNew("org.mortbay.jetty.nio.SelectChannelConnector");
                {
                    builder.set("port", getPort());
                    builder.set("maxIdleTime", 30000);

                    if (getAcceptorLimit() != null)
                    {
                        builder.set("Acceptors", getAcceptorLimit());
                    }

                    builder.set("statsOn", false);

                    if (getSslPort() != null)
                    {
                        builder.set("confidentialPort", getSslPort());
                    }

                    builder.set("lowResourcesConnections", 5000);
                    builder.set("lowResourcesMaxIdleTime", 5000);
                }
                builder.end();
            }
            builder.end();
        }
        builder.end();
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.jetty.AbstractServerConfiguration#buildHttpsConfig(net.sourceforge.eclipsejetty.jetty.JettyConfigBuilder)
     */
    @Override
    protected void buildHttpsConfig(JettyConfigBuilder builder)
    {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.jetty.AbstractServerConfiguration#buildHttpsConnector(net.sourceforge.eclipsejetty.jetty.JettyConfigBuilder)
     */
    @Override
    protected void buildHttpsConnector(JettyConfigBuilder builder)
    {
        if (getSslPort() == null)
        {
            return;
        }

        builder.comment("HTTPs Connector");

        builder.beginCall("addConnector");
        {
            builder.beginArg();
            {
                builder.beginNew("org.mortbay.jetty.security.SslSocketConnector");
                {
                    builder.set("Port", getSslPort());
                    builder.set("maxIdleTime", 30000);

                    if (getAcceptorLimit() != null)
                    {
                        builder.set("Acceptors", getAcceptorLimit());
                    }

                    builder.set("handshakeTimeout", 2000);
                    builder.set("keystore", getKeyStorePath());
                    builder.set("password", getKeyStorePassword());
                    builder.set("keyPassword", getKeyManagerPassword());
                    builder.set("truststore", getKeyStorePath());
                    builder.set("trustPassword", getKeyStorePassword());
                }
                builder.end();
            }
            builder.end();
        }
        builder.end();
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.jetty.AbstractServerConfiguration#getDefaultHandlerClass()
     */
    @Override
    protected String getDefaultHandlerClass()
    {
        return "org.mortbay.jetty.webapp.WebAppContext";
    }

    @Override
    protected void collectDefaultHandlerConfigurations(Collection<String> configurations)
    {
        if (isJndiEnabled())
        {
            configurations.add("org.mortbay.jetty.webapp.WebInfConfiguration");
            configurations.add("org.mortbay.jetty.plus.webapp.EnvConfiguration");
            configurations.add("org.mortbay.jetty.plus.webapp.Configuration");
            configurations.add("org.mortbay.jetty.webapp.JettyWebXmlConfiguration");
            configurations.add("org.mortbay.jetty.webapp.TagLibConfiguration");
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.jetty.AbstractServerConfiguration#buildAnnotations(net.sourceforge.eclipsejetty.jetty.JettyConfigBuilder)
     */
    @Override
    protected void buildAnnotations(JettyConfigBuilder builder)
    {
        // intentionally left blank
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.jetty.AbstractServerConfiguration#buildJNDI(net.sourceforge.eclipsejetty.jetty.JettyConfigBuilder)
     */
    @Override
    protected void buildJNDI(JettyConfigBuilder builder)
    {
        // intentionally left blank
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.jetty.AbstractServerConfiguration#buildJMX(net.sourceforge.eclipsejetty.jetty.JettyConfigBuilder)
     */
    @Override
    protected void buildJMX(JettyConfigBuilder builder)
    {
        if (!isJmxEnabled())
        {
            return;
        }

        builder.comment("JMX");

        builder.call("MBeanServer", "java.lang.management.ManagementFactory", "getPlatformMBeanServer");

        builder.beginGet("Container", "container");
        {
            builder.beginCall("addEventListener");
            {
                builder.beginArg();
                {
                    builder.beginNew("org.mortbay.management.MBeanContainer");
                    {
                        builder.argRef("MBeanServer");
                        builder.call("start");
                    }
                    builder.end();
                }
                builder.end();
            }
            builder.end();
        }
        builder.end();
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.jetty.AbstractConfiguration#getClassToConfigure()
     */
    @Override
    protected String getClassToConfigure()
    {
        return "org.mortbay.jetty.Server";
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.jetty.AbstractServerConfiguration#buildExtraOptions(net.sourceforge.eclipsejetty.jetty.JettyConfigBuilder)
     */
    @Override
    protected void buildExtraOptions(JettyConfigBuilder builder)
    {
        builder.comment("Extra Options");

        builder.set("stopAtShutdown", true);
        builder.set("sendServerVersion", true);
        builder.set("sendDateHeader", true);

        Integer gracefulShutdown = getGracefulShutdown();

        if (gracefulShutdown != null)
        {
            builder.set("gracefulShutdown", gracefulShutdown);
        }
    }
}
