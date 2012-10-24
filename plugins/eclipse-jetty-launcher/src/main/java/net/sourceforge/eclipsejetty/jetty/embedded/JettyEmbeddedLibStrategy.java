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

    /* (non-Javadoc)
     * @see net.sourceforge.eclipsejetty.jetty.DependencyBasedJettyLibStrategy#addServerDependencies(java.util.Collection)
     */
    @Override
    protected void addServerDependencies(Collection<String> dependencies)
    {
        dependencies.add("javax.servlet");
        dependencies.add("jetty-server");
        dependencies.add("jetty-continuation");
        dependencies.add("jetty-http");
        dependencies.add("jetty-io");
        dependencies.add("jetty-util");
        dependencies.add("jetty-security");
        dependencies.add("jetty-servlet");
        dependencies.add("jetty-webapp");
        dependencies.add("jetty-xml");
        dependencies.add("jetty-util");
    }

    /* (non-Javadoc)
     * @see net.sourceforge.eclipsejetty.jetty.DependencyBasedJettyLibStrategy#addJSPDependencies(java.util.Collection)
     */
    @Override
    protected void addJSPDependencies(Collection<String> dependencies)
    {
        dependencies.add("jetty-jsp");
        dependencies.add("javax.servlet.jsp");
        dependencies.add("javax.servlet");
        dependencies.add("org.apache.jasper.glassfish");
        dependencies.add("javax.servlet.jsp.jstl");
        dependencies.add("org.apache.taglibs.standard.glassfish");
        dependencies.add("javax.el");
        dependencies.add("com.sun.el");
        dependencies.add("org.eclipse.jdt.core");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addJMXDependencies(Collection<String> dependencies)
    {
        //    private static final String[] FEATURE_JMX = {"jetty-jmx", "jetty-util"};
        // TODO verify
        dependencies.add("jetty-jmx");
        dependencies.add("jetty-util");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addJNDIDependencies(Collection<String> dependencies)
    {
        //    private static final String[] FEATURE_JNDI = {"jetty-jndi", "javax.mail.glassfish", "javax.activation"};
        // TODO verify
        dependencies.add("jetty-jndi");
        dependencies.add("javax.mail.glassfish");
        dependencies.add("javax.activation");
        dependencies.add("jetty-annotations");
        dependencies.add("jetty-plus");
        dependencies.add("jetty-webapp");
        dependencies.add("javax.annotation");
        dependencies.add("org.objectweb.asm");
        dependencies.add("javax.transaction");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addAJPDependencies(Collection<String> dependencies)
    {
        //    private static final String[] FEATURE_AJP = {"jetty-ajp"};
        // TODO verify
        dependencies.add("jetty-ajp");
    }

    /* (non-Javadoc)
     * @see net.sourceforge.eclipsejetty.jetty.DependencyBasedJettyLibStrategy#resolveDependencies(java.util.Collection, java.io.File, java.util.Collection)
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
                    Path.fromOSString("lib/jetty/" + dependency + ".jar"), null)).getFile());
        }
        catch (IOException e)
        {
            throw new CoreException(new Status(IStatus.ERROR, JettyPlugin.PLUGIN_ID,
                "Failed to include embedded Jetty libraries", e));
        }
    }

}
