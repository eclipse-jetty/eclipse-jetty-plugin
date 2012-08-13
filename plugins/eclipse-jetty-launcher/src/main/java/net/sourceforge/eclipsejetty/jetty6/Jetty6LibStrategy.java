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

    /* (non-Javadoc)
     * @see net.sourceforge.eclipsejetty.jetty.DependencyBasedJettyLibStrategy#addServerDependencies(java.util.Collection)
     */
    @Override
    protected void addServerDependencies(Collection<String> dependencies)
    {
        dependencies.add("jetty-[\\d\\.]*jar");
        dependencies.add("jetty-util-.*\\.jar");
        dependencies.add("servlet-api-.*\\.jar");
    }

    /* (non-Javadoc)
     * @see net.sourceforge.eclipsejetty.jetty.DependencyBasedJettyLibStrategy#addJSPDependencies(java.util.Collection)
     */
    @Override
    protected void addJSPDependencies(Collection<String> dependencies)
    {
        dependencies.add("ant-.*\\.jar");
        dependencies.add("core-.*\\.jar");
        dependencies.add("jsp-2.1-.*\\.jar");
        dependencies.add("jsp-api-.*\\.jar");
    }

    /* (non-Javadoc)
     * @see net.sourceforge.eclipsejetty.jetty.FileBasedJettyLibStrategy#isPathIncluded(java.io.File, java.util.Collection)
     */
    @Override
    protected boolean isPathIncluded(File path, Collection<String> dependencies)
    {
        return !"jsp-2.0".equals(path.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addJMXDependencies(Collection<String> dependencies)
    {
        // TODO verify
        dependencies.add("management/jetty-management-.*\\.jar");
        dependencies.add("management/mx4j/mx4j-.*\\.jar");
        dependencies.add("management/mx4j/mx4j-tools-.*\\.jar");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addJNDIDependencies(Collection<String> dependencies)
    {
        // TODO verify
        dependencies.add("naming/activation-.*\\.jar");
        dependencies.add("naming/jetty-naming-.*\\.jar");
        dependencies.add("naming/mail-.*\\.jar");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addAJPDependencies(Collection<String> dependencies)
    {
        // TODO verify
        dependencies.add("ext/jetty-ajp-.*\\.jar");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addAnnotationsDependencies(Collection<String> dependencies)
    {
        // TODO verify
        dependencies.add("annotations/geronimo-annotation_1.0_spec-.*\\.jar");
        dependencies.add("annotations/jetty-annotations-.*\\.jar");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addPlusDependencies(Collection<String> dependencies)
    {
        // TODO verify
        dependencies.add("plus/jetty-plus-.*\\.jar");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addServletsDependencies(Collection<String> dependencies)
    {
        // TODO verify
    }

}
