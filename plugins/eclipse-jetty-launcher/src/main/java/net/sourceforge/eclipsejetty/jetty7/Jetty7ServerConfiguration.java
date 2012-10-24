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
    protected List<String> getJNDIItems()
    {
        return Arrays.asList("org.eclipse.jetty.webapp.WebInfConfiguration",
            "org.eclipse.jetty.webapp.WebXmlConfiguration", "org.eclipse.jetty.webapp.MetaInfConfiguration",
            "org.eclipse.jetty.webapp.FragmentConfiguration", "org.eclipse.jetty.plus.webapp.EnvConfiguration",
            "org.eclipse.jetty.plus.webapp.PlusConfiguration", "org.eclipse.jetty.annotations.AnnotationConfiguration",
            "org.eclipse.jetty.webapp.JettyWebXmlConfiguration", "org.eclipse.jetty.webapp.TagLibConfiguration");
    }

    @Override
    protected String getConfigurationKey()
    {
        return "org.eclipse.jetty.webapp.configuration";
    }

    @Override
    protected String getClassToConfigure()
    {
        return "org.eclipse.jetty.server.Server";
    }

    @Override
    protected String getConnectorClass()
    {
        return "org.eclipse.jetty.server.bio.SocketConnector";
    }

    @Override
    protected String getSSLConnectorClass()
    {
        return "org.eclipse.jetty.server.ssl.SslSocketConnector";
    }

    @Override
    protected String getDefaultHandlerClass()
    {
        return "org.eclipse.jetty.webapp.WebAppContext";
    }

}
