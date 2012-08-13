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

import java.util.Collection;

import net.sourceforge.eclipsejetty.jetty.FileBasedJettyLibStrategy;

/**
 * Resolve libs for Jetty 7
 * 
 * @author Manfred Hantschel
 */
public class Jetty7LibStrategy extends FileBasedJettyLibStrategy
{

    /* (non-Javadoc)
     * @see net.sourceforge.eclipsejetty.jetty.DependencyBasedJettyLibStrategy#addServerDependencies(java.util.Collection)
     */
    @Override
    protected void addServerDependencies(Collection<String> dependencies)
    {
        dependencies.add("servlet-api-.*\\.jar");
        dependencies.add("jetty-server-.*\\.jar");
        dependencies.add("jetty-continuation-.*\\.jar");
        dependencies.add("jetty-http-.*\\.jar");
        dependencies.add("jetty-io-.*\\.jar");
        dependencies.add("jetty-util-.*\\.jar");
        dependencies.add("jetty-security-.*\\.jar");
        dependencies.add("jetty-servlet-.*\\.jar");
        dependencies.add("jetty-webapp-.*\\.jar");
        dependencies.add("jetty-xml-.*\\.jar");
        dependencies.add("jetty-util-.*\\.jar");
    }

    /* (non-Javadoc)
     * @see net.sourceforge.eclipsejetty.jetty.DependencyBasedJettyLibStrategy#addJSPDependencies(java.util.Collection)
     */
    @Override
    protected void addJSPDependencies(Collection<String> dependencies)
    {
        dependencies.add("com.sun.el-.*\\.jar");
        dependencies.add("javax.el-.*\\.jar");
        dependencies.add("javax.servlet.jsp.jstl-.*\\.jar");
        dependencies.add("javax.servlet.jsp-.*\\.jar");
        dependencies.add("org.apache.jasper.glassfish-.*\\.jar");
        dependencies.add("org.apache.taglibs.standard.glassfish-.*\\.jar");
        dependencies.add("org.eclipse.jdt.core-.*\\.jar");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addJMXDependencies(Collection<String> dependencies)
    {
        // TODO verify
        dependencies.add("jetty-jmx-.*\\.jar");
        dependencies.add("jetty-util-.*\\.jar");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addJNDIDependencies(Collection<String> dependencies)
    {
        // TODO verify
        dependencies.add("jetty-jndi-.*\\.jar");
        dependencies.add("jndi/javax.mail.glassfish-.*\\.jar");
        dependencies.add("jndi/javax.activation-.*\\.jar");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addAJPDependencies(Collection<String> dependencies)
    {
        //    private static final String[] FEATURE_AJP = {"jetty-ajp"};
        // TODO verify
        dependencies.add("jetty-ajp-.*\\.jar");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addAnnotationsDependencies(Collection<String> dependencies)
    {
        //    private static final String[] FEATURE_ANNOTATIONS = {"jetty-annotations", "jetty-plus", "jetty-webapp",
        //        "javax.annotation", "org.objectweb.asm"};
        // TODO verify
        dependencies.add("jetty-annotations-.*\\.jar");
        dependencies.add("jetty-plus-.*\\.jar");
        dependencies.add("jetty-webapp-.*\\.jar");
        dependencies.add("annotations/javax.annotation-.*\\.jar");
        dependencies.add("annotations/org.objectweb.asm-.*\\.jar");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addPlusDependencies(Collection<String> dependencies)
    {
        //    private static final String[] FEATURE_PLUS = {"jetty-plus", "javax.transaction", "jetty-webapp", "jetty-jndi"};
        // TODO verify
        dependencies.add("jetty-plus-.*\\.jar");
        dependencies.add("jta/javax.transaction-.*\\.jar");
        dependencies.add("jetty-webapp-.*\\.jar");
        dependencies.add("jetty-jndi-.*\\.jar");
        dependencies.add("jndi/javax.mail.glassfish-.*\\.jar");
        dependencies.add("jndi/javax.activation-.*\\.jar");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addServletsDependencies(Collection<String> dependencies)
    {
        //    private static final String[] FEATURE_SERVLETS = {"jetty-servlets", "jetty-continuation", "jetty-client",
        //        "jetty-http", "jetty-util"};
        // TODO verify
        dependencies.add("jetty-servlets-.*\\.jar");
        dependencies.add("jetty-continuation-.*\\.jar");
        dependencies.add("jetty-client-.*\\.jar");
        dependencies.add("jetty-http-.*\\.jar");
        dependencies.add("jetty-util-.*\\.jar");
    }

}
