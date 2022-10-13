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
import net.sourceforge.eclipsejetty.jetty10.Jetty10LibStrategy;
import net.sourceforge.eclipsejetty.jetty10.Jetty10ServerConfiguration;
import net.sourceforge.eclipsejetty.jetty10.Jetty10WebDefaults;
import net.sourceforge.eclipsejetty.jetty11.Jetty11LibStrategy;
import net.sourceforge.eclipsejetty.jetty11.Jetty11ServerConfiguration;
import net.sourceforge.eclipsejetty.jetty11.Jetty11WebDefaults;
import net.sourceforge.eclipsejetty.jetty7.Jetty7LibStrategy;
import net.sourceforge.eclipsejetty.jetty7.Jetty7ServerConfiguration;
import net.sourceforge.eclipsejetty.jetty7.Jetty7WebDefaults;
import net.sourceforge.eclipsejetty.jetty8.Jetty8LibStrategy;
import net.sourceforge.eclipsejetty.jetty8.Jetty8ServerConfiguration;
import net.sourceforge.eclipsejetty.jetty8.Jetty8WebDefaults;
import net.sourceforge.eclipsejetty.jetty9.Jetty93LibStrategy;
import net.sourceforge.eclipsejetty.jetty9.Jetty94LibStrategy;
import net.sourceforge.eclipsejetty.jetty9.Jetty9LibStrategy;
import net.sourceforge.eclipsejetty.jetty9.Jetty9ServerConfiguration;
import net.sourceforge.eclipsejetty.jetty9.Jetty9WebDefaults;

/**
 * Describes the version of the Jetty
 * 
 * @author Manfred Hantschel
 */
public enum JettyVersionType
{
    /**
     * Embedded Jetty 9 config
     */
	JETTY_EMBEDDED("net.sourceforge.eclipsejetty.starter.jetty9.Jetty9LauncherMain", "lib/eclipse-jetty-starters-jetty9.jar",
        Jetty9ServerConfiguration.class, new JettyEmbeddedLibStrategy(), Jetty9WebDefaults.class),

    /**
     * A Jetty 7 at a specified path
     */
    JETTY_7("net.sourceforge.eclipsejetty.starter.jetty7.Jetty7LauncherMain", "lib/eclipse-jetty-starters-jetty7.jar",
        Jetty7ServerConfiguration.class, new Jetty7LibStrategy(), Jetty7WebDefaults.class),

    /**
     * A Jetty 8 at a specified path
     */
    JETTY_8("net.sourceforge.eclipsejetty.starter.jetty8.Jetty8LauncherMain", "lib/eclipse-jetty-starters-jetty8.jar",
        Jetty8ServerConfiguration.class, new Jetty8LibStrategy(), Jetty8WebDefaults.class),

    /**
     * A Jetty 9 at a specified path
     */
	JETTY_9("net.sourceforge.eclipsejetty.starter.jetty9.Jetty9LauncherMain", "lib/eclipse-jetty-starters-jetty9.jar",
        Jetty9ServerConfiguration.class, new Jetty9LibStrategy(), Jetty9WebDefaults.class),

    JETTY_9_3("net.sourceforge.eclipsejetty.starter.jetty9.Jetty9LauncherMain", "lib/eclipse-jetty-starters-jetty9.jar",
            Jetty9ServerConfiguration.class, new Jetty93LibStrategy(), Jetty9WebDefaults.class),
    
    JETTY_9_4("net.sourceforge.eclipsejetty.starter.jetty9.Jetty9LauncherMain", "lib/eclipse-jetty-starters-jetty9.jar",
            Jetty9ServerConfiguration.class, new Jetty94LibStrategy(), Jetty9WebDefaults.class),

    /**
     * Jetty 10 config
     */
	JETTY_10("net.sourceforge.eclipsejetty.starter.jetty10.Jetty10LauncherMain", "lib/eclipse-jetty-starters-jetty10.jar",
        Jetty10ServerConfiguration.class, new Jetty10LibStrategy(), Jetty10WebDefaults.class),

	/**
     * Jetty 11 config
     */
	JETTY_11("net.sourceforge.eclipsejetty.starter.jetty11.Jetty11LauncherMain", "lib/eclipse-jetty-starters-jetty11.jar",
        Jetty11ServerConfiguration.class, new Jetty11LibStrategy(), Jetty11WebDefaults.class);

    private final String mainClass;
    private final String jar;
    private final Class<? extends AbstractServerConfiguration> serverConfigurationClass;
    private final JettyLibStrategy libStrategy;
    private final Class<? extends AbstractWebDefaults> webDefaultsClass;

    private JettyVersionType(String mainClass, String jar,
        Class<? extends AbstractServerConfiguration> serverConfigurationClass, JettyLibStrategy libStrategy,
        Class<? extends AbstractWebDefaults> webDefaultsClass)
    {
        this.mainClass = mainClass;
        this.jar = jar;
        this.serverConfigurationClass = serverConfigurationClass;
        this.libStrategy = libStrategy;
        this.webDefaultsClass = webDefaultsClass;
    }

    /**
     * Returns the main class, as defined by the Jetty Plugin's Jetty starters.
     * 
     * @return the main class
     */
    public String getMainClass()
    {
        return mainClass;
    }

    /**
     * Returns the path to the needed jar file, that contains the Jetty Plugin's Jetty starter.
     * 
     * @return the path to the start jar
     */
    public String getJar()
    {
        return jar;
    }

    /**
     * Returns the instance of the needed server configuration class.
     * 
     * @return the instance of the needed server configuration class
     */
    public AbstractServerConfiguration createServerConfiguration()
    {
        try
        {
            return serverConfigurationClass.newInstance();
        }
        catch (InstantiationException e)
        {
            throw new RuntimeException(String.format("Failed to instantiate server configration: %s",
                serverConfigurationClass), e);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(String.format("Failed to access server configration: %s",
                serverConfigurationClass), e);
        }
    }

    /**
     * Returns the instance of the needed lib strategy.
     * 
     * @return the instance of the needed lib strategy
     */
    public JettyLibStrategy getLibStrategy()
    {
        return libStrategy;
    }

    /**
     * Returns the instance of the needed web defaults builder.
     * 
     * @return the instance of the needed web defaults builder
     */
    public AbstractWebDefaults createWebDefaults()
    {
        try
        {
            return webDefaultsClass.newInstance();
        }
        catch (InstantiationException e)
        {
            throw new RuntimeException(String.format("Failed to instantiate web defaults: %s", webDefaultsClass), e);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(String.format("Failed to access web defaults: %s", webDefaultsClass), e);
        }
    }

}
