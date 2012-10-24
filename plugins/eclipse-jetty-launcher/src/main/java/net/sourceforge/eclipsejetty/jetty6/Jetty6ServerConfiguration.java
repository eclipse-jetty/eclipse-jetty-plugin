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

import java.util.Arrays;
import java.util.List;

import net.sourceforge.eclipsejetty.jetty.AbstractServerConfiguration;

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

    @Override
    protected String getDocType()
    {
        // <!DOCTYPE Configure PUBLIC "-//Mort Bay Consulting//DTD Configure//EN" "http://jetty.mortbay.org/configure.dtd">
        return null;
    }

    @Override
    protected List<String> getJNDIItems()
    {
        return Arrays.asList("org.mortbay.jetty.webapp.WebInfConfiguration",
            "org.mortbay.jetty.webapp.WebXmlConfiguration", "org.mortbay.jetty.webapp.MetaInfConfiguration",
            "org.mortbay.jetty.webapp.FragmentConfiguration", "org.mortbay.jetty.plus.webapp.EnvConfiguration",
            "org.mortbay.jetty.plus.webapp.PlusConfiguration", "org.mortbay.jetty.annotations.AnnotationConfiguration",
            "org.mortbay.jetty.webapp.JettyWebXmlConfiguration", "org.mortbay.jetty.webapp.TagLibConfiguration");
    }

    @Override
    protected String getConfigurationKey()
    {
        return "org.mortbay.jetty.webapp.configuration";
    }

    @Override
    protected String getClassToConfigure()
    {
        return "org.mortbay.jetty.Server";
    }

    @Override
    protected String getConnectorClass()
    {
        return "org.mortbay.jetty.bio.SocketConnector";
    }

    @Override
    protected String getSSLConnectorClass()
    {
        return "org.mortbay.jetty.bio.SSLSocketConnector";
    }

    @Override
    protected String getDefaultHandlerClass()
    {
        return "org.mortbay.jetty.webapp.WebAppContext";
    }

}
