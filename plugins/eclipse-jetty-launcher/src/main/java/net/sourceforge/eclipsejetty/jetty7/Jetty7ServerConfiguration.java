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
package net.sourceforge.eclipsejetty.jetty7;

import java.util.Collection;

import net.sourceforge.eclipsejetty.jetty.AbstractServerConfiguration;
import net.sourceforge.eclipsejetty.jetty.JettyConfigBuilder;
import net.sourceforge.eclipsejetty.jetty.JettyVersionType;

public class Jetty7ServerConfiguration extends AbstractServerConfiguration
{

    public Jetty7ServerConfiguration()
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
        return JettyVersionType.JETTY_7;
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
            builder.beginNew("org.eclipse.jetty.util.thread.QueuedThreadPool");
            {
                builder.set("minThreads", 1);

                Integer connectionLimit = getThreadPoolLimit();

                if (connectionLimit != null)
                {
                    builder.set("maxThreads", connectionLimit);
                }

                builder.set("detailedDump", false);
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
                builder.beginNew("org.eclipse.jetty.server.nio.SelectChannelConnector");
                {
                    builder.set("port", getPort());
                    builder.set("maxIdleTime", 300000);

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
        if (getSslPort() == null)
        {
            return;
        }

        builder.comment("HTTPs Config");

        builder.beginNew("sslContextFactory", "org.eclipse.jetty.http.ssl.SslContextFactory");
        {
            builder.set("KeyStore", getKeyStorePath());
            builder.set("KeyStorePassword", getKeyStorePassword());
            builder.set("KeyManagerPassword", getKeyManagerPassword());
            builder.set("TrustStore", getKeyStorePath());
            builder.set("TrustStorePassword", getKeyStorePassword());
        }
        builder.end();
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
                builder.beginNew("org.eclipse.jetty.server.ssl.SslSelectChannelConnector");
                {
                    builder.argRef("sslContextFactory");
                    builder.set("Port", getSslPort());
                    builder.set("maxIdleTime", 30000);

                    if (getAcceptorLimit() != null)
                    {
                        builder.set("Acceptors", getAcceptorLimit());
                    }

                    builder.set("AcceptQueueSize", 100);
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
        return "org.eclipse.jetty.webapp.WebAppContext";
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.jetty.AbstractServerConfiguration#collectDefaultHandlerConfigurations(java.util.Collection)
     */
    @Override
    protected void collectDefaultHandlerConfigurations(Collection<String> configurations)
    {
        if (isAnnotationsEnabled())
        {
            configurations.add("org.eclipse.jetty.webapp.WebInfConfiguration");
            configurations.add("org.eclipse.jetty.webapp.WebXmlConfiguration");
            configurations.add("org.eclipse.jetty.webapp.MetaInfConfiguration");
            configurations.add("org.eclipse.jetty.webapp.FragmentConfiguration");
            configurations.add("org.eclipse.jetty.annotations.AnnotationConfiguration");
            configurations.add("org.eclipse.jetty.webapp.JettyWebXmlConfiguration");
        }

        if (isJndiEnabled())
        {
            configurations.add("org.eclipse.jetty.webapp.WebInfConfiguration");
            configurations.add("org.eclipse.jetty.webapp.WebXmlConfiguration");
            configurations.add("org.eclipse.jetty.webapp.MetaInfConfiguration");
            configurations.add("org.eclipse.jetty.webapp.FragmentConfiguration");
            configurations.add("org.eclipse.jetty.plus.webapp.EnvConfiguration");
            configurations.add("org.eclipse.jetty.plus.webapp.PlusConfiguration");
            configurations.add("org.eclipse.jetty.annotations.AnnotationConfiguration");
            configurations.add("org.eclipse.jetty.webapp.JettyWebXmlConfiguration");
            configurations.add("org.eclipse.jetty.webapp.TagLibConfiguration");
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

        builder.beginNew("MBeanContainer", "org.eclipse.jetty.jmx.MBeanContainer");
        {
            builder.argRef("MBeanServer");
            builder.call("start");
        }
        builder.end();

        builder.beginGet("Container", "container");
        {
            builder.beginCall("addEventListener");
            {
                builder.argRef("MBeanContainer");
            }
            builder.end();
        }
        builder.end();

        builder.beginCall("addBean");
        {
            builder.argRef("MBeanContainer");
            builder.arg(true);
        }
        builder.end();

        builder.beginRef("MBeanContainer");
        {
            builder.beginCall("addBean");
            {
                builder.beginArg();
                {
                    builder.beginNew("org.eclipse.jetty.util.log.Log").end();
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
        return "org.eclipse.jetty.server.Server";
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

        builder.set("dumpAfterStart", false);
        builder.set("dumpBeforeStop", false);
    }

}
