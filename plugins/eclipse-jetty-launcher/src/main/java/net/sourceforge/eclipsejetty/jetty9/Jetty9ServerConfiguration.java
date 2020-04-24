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
package net.sourceforge.eclipsejetty.jetty9;

import java.util.Collection;

import net.sourceforge.eclipsejetty.jetty.JettyConfigBuilder;
import net.sourceforge.eclipsejetty.jetty.JettyVersionType;
import net.sourceforge.eclipsejetty.jetty8.Jetty8ServerConfiguration;

public class Jetty9ServerConfiguration extends Jetty8ServerConfiguration
{

    public Jetty9ServerConfiguration()
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
        return JettyVersionType.JETTY_9;
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.jetty7.Jetty7ServerConfiguration#buildThreadPool(net.sourceforge.eclipsejetty.jetty.JettyConfigBuilder)
     */
    @Override
    protected void buildThreadPool(JettyConfigBuilder builder)
    {
        builder.comment("Thread Pool");

        builder.beginGet("ThreadPool");
        {
            builder.set("minThreads", 1);

            Integer connectionLimit = getThreadPoolLimit();

            if (connectionLimit != null)
            {
                builder.set("maxThreads", connectionLimit);
            }

            builder.set("idleTimeout", 60000);
            builder.set("detailedDump", false);
        }
        builder.end();
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.jetty7.Jetty7ServerConfiguration#buildHttpConfig(net.sourceforge.eclipsejetty.jetty.JettyConfigBuilder)
     */
    @Override
    protected void buildHttpConfig(JettyConfigBuilder builder)
    {
        if (getPort() == null)
        {
            return;
        }

        builder.comment("HTTP Config");

        builder.beginNew("httpConfig", "org.eclipse.jetty.server.HttpConfiguration");
        {
            builder.set("secureScheme", "https");

            if (getSslPort() != null)
            {
                builder.set("securePort", getSslPort());
            }

            builder.set("outputBufferSize", 32768);
            builder.set("requestHeaderSize", 8192);
            builder.set("responseHeaderSize", 8192);
            builder.set("sendServerVersion", true);
            builder.set("sendDateHeader", false);
            builder.set("headerCacheSize", 512);
        }
        builder.end();
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.jetty7.Jetty7ServerConfiguration#buildHttpConnector(net.sourceforge.eclipsejetty.jetty.JettyConfigBuilder)
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
                builder.beginNew("org.eclipse.jetty.server.ServerConnector");
                {
                    builder.argRef("server", "Server");
                    builder.beginArg("factories");
                    {
                        builder.beginArray("org.eclipse.jetty.server.ConnectionFactory");
                        {
                            builder.beginItem();
                            {
                                builder.beginNew("org.eclipse.jetty.server.HttpConnectionFactory");
                                {
                                    builder.argRef("config", "httpConfig");
                                }
                                builder.end();
                            }
                            builder.end();
                        }
                        builder.end();
                    }
                    builder.end();

                    builder.set("port", getPort());
                    builder.set("idleTimeout", 30000);
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
     * @see net.sourceforge.eclipsejetty.jetty7.Jetty7ServerConfiguration#buildHttpsConfig(net.sourceforge.eclipsejetty.jetty.JettyConfigBuilder)
     */
    @Override
    protected void buildHttpsConfig(JettyConfigBuilder builder)
    {
        if (getSslPort() == null)
        {
            return;
        }

        builder.comment("HTTPs Config");

        builder.beginNew("sslContextFactory", "org.eclipse.jetty.util.ssl.SslContextFactory");
        {
            builder.set("KeyStorePath", getKeyStorePath());
            builder.set("KeyStorePassword", getKeyStorePassword());
            builder.set("KeyManagerPassword", getKeyManagerPassword());
            builder.set("TrustStorePath", getKeyStorePath());
            builder.set("TrustStorePassword", getKeyStorePassword());
            builder.set("EndpointIdentificationAlgorithm", "");
            builder.setArray("ExcludeCipherSuites", //
                "SSL_RSA_WITH_DES_CBC_SHA", //
                "SSL_DHE_RSA_WITH_DES_CBC_SHA", //
                "SSL_DHE_DSS_WITH_DES_CBC_SHA", //
                "SSL_RSA_EXPORT_WITH_RC4_40_MD5", //
                "SSL_RSA_EXPORT_WITH_DES40_CBC_SHA", //
                "SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA", //
                "SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA");
        }
        builder.end();

        builder.beginNew("sslHttpConfig", "org.eclipse.jetty.server.HttpConfiguration");
        {
            builder.argRef("httpConfig");
            builder.beginCall("addCustomizer");
            {
                builder.beginArg();
                {
                    builder.beginNew("org.eclipse.jetty.server.SecureRequestCustomizer").end();
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
     * @see net.sourceforge.eclipsejetty.jetty7.Jetty7ServerConfiguration#buildHttpsConnector(net.sourceforge.eclipsejetty.jetty.JettyConfigBuilder)
     */
    @Override
    protected void buildHttpsConnector(JettyConfigBuilder builder)
    {
        if (getSslPort() == null)
        {
            return;
        }

        builder.comment("HTTPs Connector");

        builder.beginCall("sslConnector", "addConnector");
        {
            builder.beginArg();
            {
                builder.beginNew("org.eclipse.jetty.server.ServerConnector");
                {
                    builder.argRef("server", "Server");
                    builder.beginArg("factories");
                    {
                        builder.beginArray("org.eclipse.jetty.server.ConnectionFactory");
                        {
                            builder.beginItem();
                            {
                                builder.beginNew("org.eclipse.jetty.server.SslConnectionFactory");
                                {
                                    builder.arg("next", "http/1.1");
                                    builder.argRef("sslContextFactory", "sslContextFactory");
                                }
                                builder.end();
                            }
                            builder.end();

                            builder.beginItem();
                            {
                                builder.beginNew("org.eclipse.jetty.server.HttpConnectionFactory");
                                {
                                    builder.argRef("config", "sslHttpConfig");
                                }
                                builder.end();
                            }
                            builder.end();
                        }
                        builder.end();
                    }
                    builder.end();

                    builder.set("port", getSslPort());
                    builder.set("idleTimeout", 30000);
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
     * @see net.sourceforge.eclipsejetty.jetty7.Jetty7ServerConfiguration#collectDefaultHandlerConfigurations(java.util.Collection)
     */
    @Override
    protected void collectDefaultHandlerConfigurations(Collection<String> configurations)
    {
        // intentionally left blank
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.jetty7.Jetty7ServerConfiguration#buildAnnotations(net.sourceforge.eclipsejetty.jetty.JettyConfigBuilder)
     */
    @Override
    protected void buildAnnotations(JettyConfigBuilder builder)
    {
        if (!isAnnotationsEnabled())
        {
            return;
        }

        builder.comment("Annotations");

        builder.beginCall(null, "org.eclipse.jetty.webapp.Configuration$ClassList", "setServerDefault");
        {
            builder.argRef("Server");
            builder.beginCall("addBefore");
            {
                builder.arg("beforeClass", "org.eclipse.jetty.webapp.JettyWebXmlConfiguration");
                builder.argArray("org.eclipse.jetty.annotations.AnnotationConfiguration");
            }
            builder.end();
        }
        builder.end();
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.jetty7.Jetty7ServerConfiguration#buildJNDI(net.sourceforge.eclipsejetty.jetty.JettyConfigBuilder)
     */
    @Override
    protected void buildJNDI(JettyConfigBuilder builder)
    {
        if (!isJndiEnabled())
        {
            return;
        }

        builder.comment("JNDI");

        builder.beginCall(null, "org.eclipse.jetty.webapp.Configuration$ClassList", "setServerDefault");
        {
            builder.argRef("Server");
            builder.beginCall("addAfter");
            {
                builder.arg("afterClass", "org.eclipse.jetty.webapp.FragmentConfiguration");
                builder.argArray(//
                    "org.eclipse.jetty.plus.webapp.EnvConfiguration", //
                    "org.eclipse.jetty.plus.webapp.PlusConfiguration" //
                );
            }
            builder.end();
        }
        builder.end();
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.jetty7.Jetty7ServerConfiguration#buildJMX(net.sourceforge.eclipsejetty.jetty.JettyConfigBuilder)
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

        builder.beginCall("addBean");
        {
            builder.beginArg();
            {
                builder.beginNew("MBeanContainer", "org.eclipse.jetty.jmx.MBeanContainer");
                {
                    builder.argRef("MBeanServer");
                }
                builder.end();
            }
            builder.end();
        }
        builder.end();

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

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.jetty7.Jetty7ServerConfiguration#buildExtraOptions(net.sourceforge.eclipsejetty.jetty.JettyConfigBuilder)
     */
    @Override
    protected void buildExtraOptions(JettyConfigBuilder builder)
    {
        builder.comment("Extra Options");

        builder.set("stopAtShutdown", true);
        builder.set("stopTimeout", 1000);
        builder.set("dumpAfterStart", false);
        builder.set("dumpBeforeStop", false);
    }

}
