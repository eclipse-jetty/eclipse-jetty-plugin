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

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.sourceforge.eclipsejetty.JettyPlugin;
import net.sourceforge.eclipsejetty.JettyPluginUtils;
import net.sourceforge.eclipsejetty.jetty.IJettyLibStrategy;
import net.sourceforge.eclipsejetty.jetty.JspSupport;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Resolve libs for Jetty 6
 * 
 * @author Christian K&ouml;berl
 * @author Manfred Hantschel
 */
public class Jetty6LibStrategy implements IJettyLibStrategy
{

    /* (non-Javadoc)
     * @see net.sourceforge.eclipsejetty.IJettyLibStrategy#find(java.io.File)
     */
    public Collection<File> find(File jettyPath, JspSupport jspSupport) throws CoreException
    {
        final File jettyLibDir = new File(jettyPath, "lib");

        if (!jettyLibDir.exists() || !jettyLibDir.isDirectory())
        {
            throw new CoreException(new Status(IStatus.ERROR, JettyPlugin.PLUGIN_ID, "Could not find Jetty libs"));
        }

        final List<File> jettyLibs = new ArrayList<File>();

        jettyLibs.addAll(Arrays.asList(jettyLibDir.listFiles(new FilenameFilter()
        {
            public boolean accept(final File dir, final String name)
            {
                if ((name != null) && name.endsWith(".jar")
                    && (name.startsWith("jetty-") || name.startsWith("servlet-api")))
                {
                    return true;
                }

                return false;
            }
        })));

        File jettyLibJSPDir;

        switch (jspSupport)
        {
            case JSP_DISABLED:
                break;

            case JSP_2_0:
                jettyLibJSPDir = new File(jettyLibDir, "jsp-2.0");

                if ((jettyLibJSPDir.exists()) && (jettyLibJSPDir.isDirectory()))
                {
                    jettyLibs.addAll(Arrays.asList(jettyLibJSPDir.listFiles(JettyPluginUtils.JAR_FILTER)));
                }
                else
                {
                    throw new CoreException(new Status(IStatus.ERROR, JettyPlugin.PLUGIN_ID,
                        "Could not find JSP 2.0 libs"));
                }
                break;

            case JSP_2_1:
                jettyLibJSPDir = new File(jettyLibDir, "jsp-2.1");

                if (!jettyLibJSPDir.exists())
                {
                    jettyLibJSPDir = new File(jettyLibDir, "jsp");
                }

                if ((jettyLibJSPDir.exists()) && (jettyLibJSPDir.isDirectory()))
                {
                    jettyLibs.addAll(Arrays.asList(jettyLibJSPDir.listFiles(JettyPluginUtils.JAR_FILTER)));
                }
                else
                {
                    throw new CoreException(new Status(IStatus.ERROR, JettyPlugin.PLUGIN_ID,
                        "Could not find JSP 2.1 libs"));
                }
                break;
        }

        return jettyLibs;
    }

}
