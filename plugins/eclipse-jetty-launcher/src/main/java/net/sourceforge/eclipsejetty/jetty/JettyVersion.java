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

import net.sourceforge.eclipsejetty.jetty.embedded.JettyEmbeddedLibStrategy;
import net.sourceforge.eclipsejetty.jetty.embedded.JettyEmbeddedServerConfiguration;
import net.sourceforge.eclipsejetty.jetty6.Jetty6LibStrategy;
import net.sourceforge.eclipsejetty.jetty6.Jetty6ServerConfiguration;
import net.sourceforge.eclipsejetty.jetty7.Jetty7LibStrategy;
import net.sourceforge.eclipsejetty.jetty7.Jetty7ServerConfiguration;
import net.sourceforge.eclipsejetty.jetty8.Jetty8LibStrategy;
import net.sourceforge.eclipsejetty.jetty8.Jetty8ServerConfiguration;

/**
 * Describes the version of the Jetty
 * 
 * @author Manfred Hantschel
 */
public enum JettyVersion
{

    JETTY_EMBEDDED("net.sourceforge.eclipsejetty.starter.embedded.JettyEmbeddedLauncherMain",
        "lib/eclipse-jetty-starters-embedded.jar", JettyEmbeddedServerConfiguration.class,
        new JettyEmbeddedLibStrategy()),

    JETTY_6("net.sourceforge.eclipsejetty.starter.jetty6.Jetty6LauncherMain", "lib/eclipse-jetty-starters-jetty6.jar",
        Jetty6ServerConfiguration.class, new Jetty6LibStrategy()),

    JETTY_7("net.sourceforge.eclipsejetty.starter.jetty7.Jetty7LauncherMain", "lib/eclipse-jetty-starters-jetty7.jar",
        Jetty7ServerConfiguration.class, new Jetty7LibStrategy()),

    JETTY_8("net.sourceforge.eclipsejetty.starter.jetty8.Jetty8LauncherMain", "lib/eclipse-jetty-starters-jetty8.jar",
        Jetty8ServerConfiguration.class, new Jetty8LibStrategy());

    private final String mainClass;
    private final String jar;
    private final Class<? extends AbstractServerConfiguration> serverConfigurationClass;
    private final IJettyLibStrategy libStrategy;

    private JettyVersion(String mainClass, String jar,
        Class<? extends AbstractServerConfiguration> serverConfigurationClass, IJettyLibStrategy libStrategy)
    {
        this.mainClass = mainClass;
        this.jar = jar;
        this.serverConfigurationClass = serverConfigurationClass;
        this.libStrategy = libStrategy;
    }

    public String getMainClass()
    {
        return mainClass;
    }

    public String getJar()
    {
        return jar;
    }

    public AbstractServerConfiguration createServerConfiguration()
    {
        try
        {
            return serverConfigurationClass.newInstance();
        }
        catch (InstantiationException e)
        {
            throw new RuntimeException("Failed to instantiate server configration: " + serverConfigurationClass, e);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException("Failed to access server configration: " + serverConfigurationClass, e);
        }
    }

    public IJettyLibStrategy getLibStrategy()
    {
        return libStrategy;
    }

}
