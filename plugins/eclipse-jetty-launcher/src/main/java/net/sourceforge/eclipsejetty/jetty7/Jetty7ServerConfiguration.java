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

import java.util.Arrays;
import java.util.List;

import net.sourceforge.eclipsejetty.jetty.AbstractServerConfiguration;
import net.sourceforge.eclipsejetty.util.DOMBuilder;

public class Jetty7ServerConfiguration extends AbstractServerConfiguration
{

    public Jetty7ServerConfiguration()
    {
        super();
    }

    @Override
    protected String getDocType()
    {
        // <!DOCTYPE Configure PUBLIC "-//Mort Bay Consulting//DTD Configure//EN" "http://jetty.mortbay.org/configure.dtd">
        return null;
    }

    @Override
    protected void buildThreadPool(DOMBuilder builder)
    {
        builder.begin("Set").attribute("name", "ThreadPool");
        {
            builder.begin("New").attribute("class", "org.eclipse.jetty.util.thread.QueuedThreadPool");
            {
                builder.element("Set", "name", "minThreads", 1);
                Integer connectionLimit = getConnectionLimit();

                if (connectionLimit != null)
                {
                    builder.element("Set", "name", "maxThreads", connectionLimit);
                }
                builder.element("Set", "name", "detailedDump", false);
            }
            builder.end();
        }
        builder.end();
    }

    @Override
    protected void buildHttpConfig(DOMBuilder builder)
    {
        // nothing to do
    }

    @Override
    protected void buildHttpsConfig(DOMBuilder builder)
    {
        if (getSslPort() != null)
        {
            builder.begin("New").attribute("id", "sslContextFactory")
                .attribute("class", "org.eclipse.jetty.http.ssl.SslContextFactory");
            {
                builder.element("Set", "name", "KeyStore", getKeyStorePath());
                builder.element("Set", "name", "KeyStorePassword", getKeyStorePassword());
                builder.element("Set", "name", "KeyManagerPassword", getKeyManagerPassword());
                builder.element("Set", "name", "TrustStore", getKeyStorePath());
                builder.element("Set", "name", "TrustStorePassword", getKeyStorePassword());
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
            "org.eclipse.jetty.webapp.JettyWebXmlConfiguration", "org.eclipse.jetty.webapp.TagLibConfiguration");
    }

    @Override
    protected void buildJMX(DOMBuilder builder)
    {
        if (isJmx())
        {
            builder.begin("Call").attribute("id", "MBeanServer")
                .attribute("class", "java.lang.management.ManagementFactory")
                .attribute("name", "getPlatformMBeanServer").end();

            builder.begin("New").attribute("id", "MBeanContainer")
                .attribute("class", "org.eclipse.jetty.jmx.MBeanContainer");
            {
                builder.begin("Arg");
                {
                    builder.element("Ref", "id", "MBeanServer");
                }
                builder.end();

                builder.element("Call", "name", "start");
            }
            builder.end();

            builder.begin("Get").attribute("id", "Container").attribute("name", "container");
            {
                builder.begin("Call").attribute("name", "addEventListener");
                {
                    builder.begin("Arg");
                    {
                        builder.element("Ref", "id", "MBeanContainer");
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
                    builder.element("Ref", "id", "MBeanContainer");
                }
                builder.end();

                builder.element("Arg", "type", "boolean", true);
            }
            builder.end();

            builder.begin("Ref").attribute("id", "MBeanContainer");
            {
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
            builder.end();
        }
    }

    @Override
    protected String getClassToConfigure()
    {
        return "org.eclipse.jetty.server.Server";
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
                    builder.begin("New").attribute("class", "org.eclipse.jetty.server.nio.SelectChannelConnector");
                    {
                        builder.element("Set", "name", "port", getPort());
                        builder.element("Set", "name", "maxIdleTime", 30000);
                        builder.element("Set", "name", "Acceptors", 2);
                        builder.element("Set", "name", "statsOn", false);
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
            builder.begin("Call").attribute("name", "addConnector");
            {
                builder.begin("Arg");
                {
                    builder.begin("New").attribute("class", "org.eclipse.jetty.server.ssl.SslSelectChannelConnector");
                    {
                        builder.begin("Arg");
                        {
                            builder.element("Ref", "id", "sslContextFactory");
                        }
                        builder.end();
                        builder.element("Set", "name", "Port", getSslPort());
                        builder.element("Set", "name", "maxIdleTime", 30000);
                        builder.element("Set", "name", "Acceptors", 2);
                        builder.element("Set", "name", "AcceptQueueSize", 100);
                    }
                    builder.end();
                }
                builder.end();
            }
            builder.end();
        }
    }

    @Override
    protected String getDefaultHandlerClass()
    {
        return "org.eclipse.jetty.webapp.WebAppContext";
    }

    @Override
    protected void buildExtraOptions(DOMBuilder builder)
    {
        builder.element("Set", "name", "stopAtShutdown", true);
        builder.element("Set", "name", "sendServerVersion", true);
        builder.element("Set", "name", "sendDateHeader", true);
        builder.element("Set", "name", "gracefulShutdown", 1000);
        builder.element("Set", "name", "dumpAfterStart", false);
        builder.element("Set", "name", "dumpBeforeStop", false);
    }

}
