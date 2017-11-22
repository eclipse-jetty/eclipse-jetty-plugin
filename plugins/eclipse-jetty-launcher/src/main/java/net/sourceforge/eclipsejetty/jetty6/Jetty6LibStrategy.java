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
import java.util.Collection;

import net.sourceforge.eclipsejetty.jetty.FileBasedJettyLibStrategy;

/**
 * Resolve libs for Jetty 6
 * 
 * @author Christian K&ouml;berl
 * @author Manfred Hantschel
 */
public class Jetty6LibStrategy extends FileBasedJettyLibStrategy
{

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.jetty.DependencyBasedJettyLibStrategy#addServerDependencies(java.util.Collection)
     */
    @Override
    protected void addServerDependencies(Collection<String> dependencies)
    {
        dependencies.add(".*/jetty-[\\d\\.]*jar");
        dependencies.add(".*/jetty-util-.*\\.jar");
        dependencies.add(".*/servlet-api-.*\\.jar");
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.jetty.DependencyBasedJettyLibStrategy#addJSPDependencies(java.util.Collection)
     */
    @Override
    protected void addJSPDependencies(Collection<String> dependencies)
    {
        dependencies.add(".*/ant-.*\\.jar");
        dependencies.add(".*/core-.*\\.jar");
        dependencies.add(".*/jsp-2.1-.*\\.jar");
        dependencies.add(".*/jsp-api-.*\\.jar");
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.jetty.FileBasedJettyLibStrategy#isPathIncluded(java.io.File,
     *      java.util.Collection)
     */
    @Override
    protected boolean isPathIncluded(File path, Collection<String> dependencies)
    {
        return !"jsp-2.0".equals(path.getName());
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.jetty.DependencyBasedJettyLibStrategy#addJMXDependencies(java.util.Collection)
     */
    @Override
    protected void addJMXDependencies(Collection<String> dependencies)
    {
        dependencies.add(".*/management/jetty-management-.*\\.jar");
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.jetty.DependencyBasedJettyLibStrategy#addJNDIDependencies(java.util.Collection)
     */
    @Override
    protected void addJNDIDependencies(Collection<String> dependencies)
    {
        dependencies.add(".*/naming/jetty-naming-.*\\.jar");
        dependencies.add(".*/naming/mail-.*\\.jar");
        dependencies.add(".*/plus/jetty-plus-.*\\.jar");
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.jetty.DependencyBasedJettyLibStrategy#addAnnotationsDependencies(java.util.Collection)
     */
    @Override
    protected void addAnnotationsDependencies(Collection<String> dependencies)
    {
        dependencies.add(".*/annotations/geronimo-annotation_1.0_spec-.*\\.jar");
        dependencies.add(".*/annotations/jetty-annotations-.*\\.jar");
        dependencies.add(".*/plus/jetty-plus-.*\\.jar");
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.jetty.DependencyBasedJettyLibStrategy#addAJPDependencies(java.util.Collection)
     */
    @Override
    protected void addAJPDependencies(Collection<String> dependencies)
    {
        dependencies.add(".*/ext/jetty-ajp-.*\\.jar");
    }

    @Override
    protected void addWebsocketSupport(Collection<String> dependencies)
    {
        // not supported
    }
}
