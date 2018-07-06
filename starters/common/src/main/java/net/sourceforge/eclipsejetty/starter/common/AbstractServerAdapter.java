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
package net.sourceforge.eclipsejetty.starter.common;

import java.io.PrintStream;
import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * Abstract base class for {@link ServerAdapter}s
 * 
 * @author Manfred Hantschel
 */
public abstract class AbstractServerAdapter implements ServerAdapter
{

    public AbstractServerAdapter()
    {
        super();
    }

    /**
     * Prints the info. Gathers the data using various abstract methods.
     * 
     * @see net.sourceforge.eclipsejetty.starter.common.ServerAdapter#info(java.io.PrintStream)
     */
    public void info(PrintStream out)
    {
        out.println(String.format("         Version: %s", getVersionDescription()));
        out.println(String.format("         Context: %s", getContextPathDescription()));
        out.println(String.format("            Port: %s", getPortDescription()));
        out.println(String.format("       Classpath: %s",
            getClassPathDescription().replaceAll("\\n", "\n                  ")));
    }

    /**
     * Returns the Jetty version
     * 
     * @return the Jetty version
     */
    protected abstract String getVersionDescription();

    /**
     * Creates a description of context paths gathered from the {@link #getContextPaths()} method.
     * 
     * @return a description of context paths
     */
    protected String getContextPathDescription()
    {
        StringBuilder builder = new StringBuilder();
        Collection<String> contextPaths = getContextPaths();

        for (String contextPath : contextPaths)
        {
            if (builder.length() > 0)
            {
                builder.append(", ");
            }

            builder.append(contextPath);
        }

        return builder.toString();
    }

    /**
     * Creates a description of ports gathered from the {@link #getPorts()} method.
     * 
     * @return a description of ports
     */
    protected String getPortDescription()
    {
        StringBuilder builder = new StringBuilder();
        Collection<Integer> ports = new LinkedHashSet<Integer>();

        ports.addAll(getPorts());
        ports.addAll(getSecurePorts());

        for (Integer port : ports)
        {
            if (builder.length() > 0)
            {
                builder.append(", ");
            }

            builder.append(port);
        }

        return builder.toString();
    }

    /**
     * Creates a description of the used classpath.
     * 
     * @return a description of the used classpath
     */
    protected abstract String getClassPathDescription();

}
