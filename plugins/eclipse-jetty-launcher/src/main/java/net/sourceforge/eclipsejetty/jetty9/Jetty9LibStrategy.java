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
package net.sourceforge.eclipsejetty.jetty9;

import java.util.Collection;

import net.sourceforge.eclipsejetty.jetty8.Jetty8LibStrategy;

/**
 * Resolve libs for Jetty 9
 * 
 * @author Christian K&ouml;berl
 * @author Manfred Hantschel
 */
public class Jetty9LibStrategy extends Jetty8LibStrategy
{

    @Override
    protected void addJMXDependencies(Collection<String> dependencies)
    {
        dependencies.add(".*/jetty-jmx-.*\\.jar");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addJNDIDependencies(Collection<String> dependencies)
    {
        dependencies.add(".*/jetty-jndi-.*\\.jar");
        dependencies.add(".*/jndi/javax.mail.glassfish-.*\\.jar");
        dependencies.add(".*/jndi/javax.activation-.*\\.jar");
        dependencies.add(".*/jndi/javax.transaction-.*\\.jar");
        dependencies.add(".*/jetty-annotations-.*\\.jar");
        dependencies.add(".*/jetty-plus-.*\\.jar");
        dependencies.add(".*/jetty-webapp-.*\\.jar");
        dependencies.add(".*/annotations/javax.annotation-.*\\.jar");
        dependencies.add(".*/annotations/org.objectweb.asm-.*\\.jar");
    }

}
