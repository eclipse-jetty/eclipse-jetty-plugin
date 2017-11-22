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

    @Override
    protected void addServerDependencies(Collection<String> dependencies)
    {
        dependencies.add(".*/servlet-api-.*\\.jar");
        dependencies.add(".*/jetty-server-.*\\.jar");
        dependencies.add(".*/jetty-continuation-.*\\.jar");
        dependencies.add(".*/jetty-http-.*\\.jar");
        dependencies.add(".*/jetty-io-.*\\.jar");
        dependencies.add(".*/jetty-util-.*\\.jar");
        dependencies.add(".*/jetty-security-.*\\.jar");
        dependencies.add(".*/jetty-servlet-.*\\.jar");
        dependencies.add(".*/jetty-webapp-.*\\.jar");
        dependencies.add(".*/jetty-xml-.*\\.jar");
        dependencies.add(".*/jetty-util-.*\\.jar");
    }

    @Override
    protected void addJSPDependencies(Collection<String> dependencies)
    {
        dependencies.add(".*/com.sun.el-.*\\.jar");
        dependencies.add(".*/javax.el-.*\\.jar");
        dependencies.add(".*/javax.servlet.jsp.jstl-.*\\.jar");
        dependencies.add(".*/javax.servlet.jsp-.*\\.jar");
        dependencies.add(".*/org.apache.jasper.glassfish-.*\\.jar");
        dependencies.add(".*/org.apache.taglibs.standard.glassfish-.*\\.jar");
        dependencies.add(".*/org.eclipse.jdt.core-.*\\.jar");
    }

    @Override
    protected void addJMXDependencies(Collection<String> dependencies)
    {
        dependencies.add(".*/jetty-jmx-.*\\.jar");
    }

    @Override
    protected void addJNDIDependencies(Collection<String> dependencies)
    {
        dependencies.add(".*/jetty-jndi-.*\\.jar");
        dependencies.add(".*/jetty-plus-.*\\.jar");
        dependencies.add(".*/jndi/.*\\.jar");
    }

    @Override
    protected void addAnnotationsDependencies(Collection<String> dependencies)
    {
        dependencies.add(".*/jetty-annotations-.*\\.jar");
        dependencies.add(".*/jetty-plus-.*\\.jar");
        dependencies.add(".*/annotations/.*\\.jar");
    }

    @Override
    protected void addAJPDependencies(Collection<String> dependencies)
    {
        dependencies.add(".*/jetty-ajp-.*\\.jar");
    }
    
    @Override
    protected void addWebsocketSupport(Collection<String> dependencies)
    {
        // not supported
    }

}
