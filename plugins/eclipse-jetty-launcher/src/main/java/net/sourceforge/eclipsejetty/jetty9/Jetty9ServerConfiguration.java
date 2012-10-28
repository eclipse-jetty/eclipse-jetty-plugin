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
        builder.begin("Arg").attribute("name", "threadpool");
        builder.begin("New").attribute("id", "threadpool")
            .attribute("class", "org.eclipse.jetty.util.thread.QueuedThreadPool");
        builder.element("Set", "name", "minThreads", 2);
        builder.element("Set", "name", "maxThreads", 10);
        builder.element("Set", "name", "detailedDump", false);
        builder.end();
        builder.end();
    }

    @Override
    protected void buildHttpConfig(DOMBuilder builder)
    {
        builder.begin("New").attribute("id", "httpConfig")
            .attribute("class", "org.eclipse.jetty.server.HttpChannelConfig");
        builder.element("Set", "name", "secureScheme", "https");
        builder.element("Set", "name", "securePort", 8443);
        builder.element("Set", "name", "outputBufferSize", 32768);
        builder.element("Set", "name", "requestHeaderSize", 8192);
        builder.element("Set", "name", "responseHeaderSize", 8192);
        builder.end();
    }

    @Override
    protected void buildConnector(DOMBuilder builder)
    {
        if (getPort() != null)
        {
            builder.begin("Call").attribute("name", "addConnector");
            builder.begin("Arg");
            builder.begin("New").attribute("class", "org.eclipse.jetty.server.ServerConnector");
            builder.begin("Arg").attribute("name", "server").element("Ref", "id", "Server").end();
            builder.begin("Arg").attribute("name", "factories");
            builder.begin("Array").attribute("type", "org.eclipse.jetty.server.ConnectionFactory");
            builder.begin("Item");
            builder.begin("New").attribute("class", "org.eclipse.jetty.server.HttpConnectionFactory");
            builder.begin("Arg").attribute("name", "config").element("Ref", "id", "httpConfig").end();
            builder.end();
            builder.end();
            builder.end();
            builder.end();
            builder.element("Set", "name", "port", getPort());
            builder.element("Set", "name", "idleTimeout", 30000);
            builder.end();
            builder.end();
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
    protected void buildExtraOptions(DOMBuilder builder)
    {
        builder.element("Set", "name", "stopAtShutdown", true);
        builder.element("Set", "name", "sendServerVersion", true);
        builder.element("Set", "name", "sendDateHeader", true);
        builder.element("Set", "name", "stopTimeout", 1000);
        builder.element("Set", "name", "dumpAfterStart", false);
        builder.element("Set", "name", "dumpBeforeStop", false);
    }

}
