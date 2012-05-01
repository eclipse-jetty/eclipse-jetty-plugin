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

import static net.sourceforge.eclipsejetty.util.FilenameMatcher.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.eclipsejetty.JettyPlugin;
import net.sourceforge.eclipsejetty.jetty.JspSupport;
import net.sourceforge.eclipsejetty.jetty6.Jetty6LibStrategy;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Resolve libs for Jetty 7
 * 
 * @author Christian K&ouml;berl
 * @author Manfred Hantschel
 */
public class Jetty7LibStrategy extends Jetty6LibStrategy
{

    @Override
    protected void addJSPLibs(List<File> jettyLibs, File jettyPath, JspSupport jspSupport) throws CoreException
    {
        final File jettyLibJSPDir = new File(jettyPath, "lib/jsp");

        if (!jettyLibJSPDir.exists() || !jettyLibJSPDir.isDirectory())
        {
            throw new CoreException(new Status(IStatus.ERROR, JettyPlugin.PLUGIN_ID, "Could not find Jetty JSP libs"));
        }

        jettyLibs.addAll(Arrays.asList(jettyLibJSPDir.listFiles(named(".*\\.jar"))));
    }

}
