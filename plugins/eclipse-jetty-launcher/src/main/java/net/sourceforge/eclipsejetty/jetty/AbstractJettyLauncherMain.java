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

import java.io.File;
import java.io.IOException;

/**
 * Abstract base class for the Jetty launcher
 * 
 * @author Manfred Hantschel
 */
public abstract class AbstractJettyLauncherMain
{

    public static final String CONFIGURATION_KEY = "jetty.launcher.configuration";

    protected void launch(String[] args) throws Exception
    {
        JettyConfiguration configuration = getConfiguration();

        if (!configuration.getHideLaunchInfo())
        {
            printLogo();
            System.out.println();

            printConfiguration(configuration);
            System.out.println();
        }

        configuration.delete();

        start(configuration);
    }

    protected abstract void start(JettyConfiguration configuration) throws Exception;

    protected abstract void printLogo();

    protected JettyConfiguration getConfiguration() throws IOException
    {
        return JettyConfiguration.load(new File(System.getProperty(CONFIGURATION_KEY)));
    }

    protected void printConfiguration(JettyConfiguration configuration)
    {
        System.out.println("Context          = " + configuration.getContext());
        System.out.println("WebApp Directory = " + configuration.getWebAppDir());
        System.out.println("Port             = " + configuration.getPort());

        String[] classpath = configuration.getClasspath();

        for (int i = 0; i < classpath.length; i += 1)
        {
            if (i == 0)
            {
                System.out.println("Classpath        = " + classpath[i]);
            }
            else
            {
                System.out.println("                   " + classpath[i]);
            }
        }
    }

    protected String link(String[] values)
    {
        StringBuilder result = new StringBuilder();

        if (values != null)
        {
            for (int i = 0; i < values.length; i += 1)
            {
                if (i > 0)
                {
                    result.append(File.pathSeparator);
                }

                result.append(values[i]);
            }
        }

        return result.toString();
    }

}
