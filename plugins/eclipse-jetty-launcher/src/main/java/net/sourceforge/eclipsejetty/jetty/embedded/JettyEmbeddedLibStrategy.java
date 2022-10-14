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
package net.sourceforge.eclipsejetty.jetty.embedded;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import net.sourceforge.eclipsejetty.JettyPlugin;
import net.sourceforge.eclipsejetty.jetty.DependencyBasedJettyLibStrategy;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

/**
 * Resolve libs for embedded Jetty
 * 
 * @author Manfred Hantschel
 */
public class JettyEmbeddedLibStrategy extends DependencyBasedJettyLibStrategy
{

    @Override
    protected void addServerDependencies(Collection<String> dependencies)
    {
        dependencies.add("javax.servlet-api");

        dependencies.add("jetty-continuation");
        dependencies.add("jetty-deploy");
        dependencies.add("jetty-http");
        dependencies.add("jetty-io");
        dependencies.add("jetty-security");
        dependencies.add("jetty-server");
        dependencies.add("jetty-servlet");
        dependencies.add("jetty-util");
        dependencies.add("jetty-webapp");
        dependencies.add("jetty-xml");
    }

    @Override
    protected void addJSPDependencies(Collection<String> dependencies)
    {
        dependencies.add("apache-jsp");
        dependencies.add("jetty-schemas");
        dependencies.add("apache-jsp/apache-jsp");
        dependencies.add("apache-jsp/apache-el");
        dependencies.add("ecj");
    }

    @Override
    protected void addJMXDependencies(Collection<String> dependencies)
    {
        dependencies.add("jetty-jmx");
    }

    @Override
    protected void addJNDIDependencies(Collection<String> dependencies)
    {
        dependencies.add("jetty-jndi");
        dependencies.add("javax.mail.glassfish");
        dependencies.add("javax.activation");
        dependencies.add("jetty-plus");
    }

    @Override
    protected void addAnnotationsDependencies(Collection<String> dependencies)
    {
        dependencies.add("jetty-annotations");
        dependencies.add("javax.annotation-api");
        dependencies.add("asm");
        dependencies.add("jetty-plus");
    }

    @Override
    protected void addAJPDependencies(Collection<String> dependencies)
    {
        // AJP not anymore supported by jetty9
    }

    @Override
    protected void addWebsocketSupport(Collection<String> dependencies)
    {
        dependencies.add("javax.websocket-api");
        dependencies.add("javax-websocket-server-impl");
        dependencies.add("javax-websocket-client-impl");
        dependencies.add("websocket-api");
        dependencies.add("websocket-common");
        dependencies.add("websocket-client");
        dependencies.add("websocket-server");
        dependencies.add("websocket-servlet");
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.jetty.DependencyBasedJettyLibStrategy#resolveDependencies(java.util.Collection,
     *      java.io.File, java.util.Collection)
     */
    @Override
    protected void resolveDependencies(Collection<File> results, File path, Collection<String> dependencies)
        throws CoreException
    {
        for (String dependency : dependencies)
        {
            results.add(resolveDependency(dependency));
        }
    }

    protected File resolveDependency(String dependency) throws CoreException
    {
        try
        {
            return new File(FileLocator.toFileURL(
                FileLocator.find(JettyPlugin.getDefault().getBundle(),
                    Path.fromOSString(String.format("lib/jetty/%s.jar", dependency)), null)).getFile());
        }
        catch (IOException e)
        {
            throw new CoreException(new Status(IStatus.ERROR, JettyPlugin.PLUGIN_ID,
                "Failed to include embedded Jetty libraries", e));
        }
        catch (Throwable t)
        {
            throw new CoreException(new Status(IStatus.ERROR, JettyPlugin.PLUGIN_ID,
                String.format("Failed to include embedded Jetty library: %s", dependency), t));
        }
    }

}
