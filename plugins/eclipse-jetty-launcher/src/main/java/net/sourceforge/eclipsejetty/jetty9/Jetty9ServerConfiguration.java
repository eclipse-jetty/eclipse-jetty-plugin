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

import java.util.Arrays;
import java.util.List;

import net.sourceforge.eclipsejetty.jetty8.Jetty8ServerConfiguration;
import net.sourceforge.eclipsejetty.util.DOMBuilder;

public class Jetty9ServerConfiguration extends Jetty8ServerConfiguration
{

    public Jetty9ServerConfiguration()
    {
        super();
    }

    @Override
    protected void buildThreadPool(DOMBuilder builder)
    {
        builder.begin("Get").attribute("name", "ThreadPool");
        {
            builder.begin("Set").attribute("name", "minThreads").attribute("type", "int");
            {
                builder.begin("Property").attribute("name", "threads.min").attribute("default", 2).end();
            }
            builder.end();

            Integer connectionLimit = getConnectionLimit();

            if (connectionLimit != null)
            {
                builder.begin("Set").attribute("name", "maxThreads").attribute("type", "int");
                {
                    builder.begin("Property").attribute("name", "threads.max").attribute("default", connectionLimit)
                        .end();
                }
                builder.end();
            }

            builder.begin("Set").attribute("name", "idleTimeout").attribute("type", "int");
            {
                builder.begin("Property").attribute("name", "threads.timeout").attribute("default", 60000).end();
            }
            builder.end();

            builder.element("Set", "name", "detailedDump", false);
        }
        builder.end();
    }

    @Override
    protected void buildHttpConfig(DOMBuilder builder)
    {
        builder.begin("New").attribute("id", "httpConfig")
            .attribute("class", "org.eclipse.jetty.server.HttpConfiguration");
        {
            builder.element("Set", "name", "secureScheme", "https");
            builder.element("Set", "name", "securePort", 8443);
            builder.element("Set", "name", "outputBufferSize", 32768);
            builder.element("Set", "name", "requestHeaderSize", 8192);
            builder.element("Set", "name", "responseHeaderSize", 8192);
            builder.element("Set", "name", "sendServerVersion", true);
            builder.element("Set", "name", "sendDateHeader", false);
            builder.element("Set", "name", "headerCacheSize", 512);
        }
        builder.end();
    }

    @Override
    protected void buildHttpsConfig(DOMBuilder builder)
    {
        if (getSslPort() != null)
        {
            builder.begin("New").attribute("id", "sslContextFactory")
                .attribute("class", "org.eclipse.jetty.util.ssl.SslContextFactory");
            {
                builder.element("Set", "name", "KeyStorePath", getKeyStorePath());
                builder.element("Set", "name", "KeyStorePassword", getKeyStorePassword());
                builder.element("Set", "name", "KeyManagerPassword", getKeyManagerPassword());
                builder.element("Set", "name", "TrustStorePath", getKeyStorePath());
                builder.element("Set", "name", "TrustStorePassword", getKeyStorePassword());
                builder.element("Set", "name", "EndpointIdentificationAlgorithm");
                builder.begin("Set").attribute("name", "ExcludeCipherSuites");
                {
                    builder.begin("Array").attribute("type", "String");
                    {
                        builder.element("Item", "SSL_RSA_WITH_DES_CBC_SHA");
                        builder.element("Item", "SSL_DHE_RSA_WITH_DES_CBC_SHA");
                        builder.element("Item", "SSL_DHE_DSS_WITH_DES_CBC_SHA");
                        builder.element("Item", "SSL_RSA_EXPORT_WITH_RC4_40_MD5");
                        builder.element("Item", "SSL_RSA_EXPORT_WITH_DES40_CBC_SHA");
                        builder.element("Item", "SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA");
                        builder.element("Item", "SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA");
                    }
                    builder.end();
                }
                builder.end();
            }
            builder.end();

            builder.begin("New").attribute("id", "sslHttpConfig")
                .attribute("class", "org.eclipse.jetty.server.HttpConfiguration");
            {
                builder.begin("Arg");
                {
                    builder.element("Ref", "refid", "httpConfig");
                }
                builder.end();

                builder.begin("Call").attribute("name", "addCustomizer");
                {
                    builder.begin("Arg");
                    {
                        builder.element("New", "class", "org.eclipse.jetty.server.SecureRequestCustomizer");
                    }
                    builder.end();
                }
                builder.end();
            }
            builder.end();
        }
    }

    @Override
    protected void buildHttpConnector(DOMBuilder builder)
    {
        if (getPort() != null)
        {
            builder.begin("Call").attribute("name", "addConnector");
            {
                builder.begin("Arg");
                {
                    builder.begin("New").attribute("class", "org.eclipse.jetty.server.ServerConnector");
                    {
                        builder.begin("Arg").attribute("name", "server");
                        {
                            builder.element("Ref", "refid", "Server");
                        }
                        builder.end();

                        builder.begin("Arg").attribute("name", "factories");
                        {
                            builder.begin("Array").attribute("type", "org.eclipse.jetty.server.ConnectionFactory");
                            {
                                builder.begin("Item");
                                {
                                    builder.begin("New").attribute("class",
                                        "org.eclipse.jetty.server.HttpConnectionFactory");
                                    {
                                        builder.begin("Arg").attribute("name", "config");
                                        {
                                            builder.element("Ref", "refid", "httpConfig");
                                        }
                                        builder.end();
                                    }
                                    builder.end();
                                }
                                builder.end();
                            }
                            builder.end();
                        }
                        builder.end();

                        builder.begin("Set").attribute("name", "host");
                        {
                            builder.element("Property", "name", "jetty.host");
                        }
                        builder.end();

                        builder.element("Set", "name", "port", getPort());
                        builder.element("Set", "name", "idleTimeout", 30000);
                    }
                    builder.end();
                }
                builder.end();
            }
            builder.end();
        }
    }

    @Override
    protected void buildHttpsConnector(DOMBuilder builder)
    {
        if (getSslPort() != null)
        {
            builder.begin("Call").attribute("id", "sslConnector").attribute("name", "addConnector");
            {
                builder.begin("Arg");
                {
                    builder.begin("New").attribute("class", "org.eclipse.jetty.server.ServerConnector");
                    {
                        builder.begin("Arg").attribute("name", "server");
                        {
                            builder.element("Ref", "refid", "Server");
                        }
                        builder.end();

                        builder.begin("Arg").attribute("name", "factories");
                        {
                            builder.begin("Array").attribute("type", "org.eclipse.jetty.server.ConnectionFactory");
                            {
                                builder.begin("Item");
                                {
                                    builder.begin("New").attribute("class",
                                        "org.eclipse.jetty.server.SslConnectionFactory");
                                    {
                                        builder.element("Arg", "name", "next", "http/1.1");
                                        builder.begin("Arg").attribute("name", "sslContextFactory");
                                        {
                                            builder.element("Ref", "refid", "sslContextFactory");
                                        }
                                        builder.end();
                                    }
                                    builder.end();
                                }
                                builder.end();

                                builder.begin("Item");
                                {
                                    builder.begin("New").attribute("class",
                                        "org.eclipse.jetty.server.HttpConnectionFactory");
                                    {
                                        builder.begin("Arg").attribute("name", "config");
                                        {
                                            builder.element("Ref", "refid", "sslHttpConfig");
                                        }
                                        builder.end();
                                    }
                                    builder.end();
                                }
                                builder.end();
                            }
                            builder.end();
                        }
                        builder.end();

                        builder.begin("Set").attribute("name", "host");
                        {
                            builder.element("Property", "name", "jetty.host");
                        }
                        builder.end();

                        builder.begin("Set").attribute("name", "port");
                        {
                            builder.begin("Property").attribute("name", "jetty.https.port")
                                .attribute("default", getSslPort()).end();
                        }
                        builder.end();

                        builder.element("Set", "name", "idleTimeout", 30000);
                    }
                    builder.end();
                }
                builder.end();
            }
            builder.end();
        }
    }

    @Override
    protected List<String> getJNDIItems()
    {
        return Arrays.asList("org.eclipse.jetty.webapp.WebInfConfiguration",
            "org.eclipse.jetty.webapp.WebXmlConfiguration", "org.eclipse.jetty.webapp.MetaInfConfiguration",
            "org.eclipse.jetty.webapp.FragmentConfiguration", "org.eclipse.jetty.plus.webapp.EnvConfiguration",
            "org.eclipse.jetty.plus.webapp.PlusConfiguration", "org.eclipse.jetty.annotations.AnnotationConfiguration",
            "org.eclipse.jetty.webapp.JettyWebXmlConfiguration");
    }

    @Override
    protected void buildJMX(DOMBuilder builder)
    {
        if (isJmx())
        {
            builder.begin("Call").attribute("id", "MBeanServer")
                .attribute("class", "java.lang.management.ManagementFactory")
                .attribute("name", "getPlatformMBeanServer").end();

            builder.begin("Call").attribute("name", "addBean");
            {
                builder.begin("Arg");
                {
                    builder.begin("New").attribute("id", "MBeanContainer")
                        .attribute("class", "org.eclipse.jetty.jmx.MBeanContainer");
                    {
                        builder.begin("Arg");
                        {
                            builder.element("Ref", "refid", "MBeanServer");
                        }
                        builder.end();
                    }
                    builder.end();
                }
                builder.end();
            }
            builder.end();

            builder.begin("Call").attribute("name", "addBean");
            {
                builder.begin("Arg");
                {
                    builder.element("New", "class", "org.eclipse.jetty.util.log.Log");
                }
                builder.end();
            }
            builder.end();
        }
    }

    @Override
    protected void buildExtraOptions(DOMBuilder builder)
    {
        builder.element("Set", "name", "stopAtShutdown", true);
        builder.element("Set", "name", "stopTimeout", 1000);
        builder.element("Set", "name", "dumpAfterStart", false);
        builder.element("Set", "name", "dumpBeforeStop", false);
    }

}
