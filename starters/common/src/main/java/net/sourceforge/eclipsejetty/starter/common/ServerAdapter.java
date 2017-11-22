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

import net.sourceforge.eclipsejetty.starter.util.service.ServiceResolver;

/**
 * Adapter for the Jetty server. Defined in the {@link ServiceResolver}.
 * 
 * @author Manfred Hantschel
 */
public interface ServerAdapter
{

    /**
     * Returns the server. The type depends on the Jetty version.
     * 
     * @return the server object
     */
    Object getServer();

    /**
     * Starts the server.
     * 
     * @throws Exception on occasion
     */
    void start() throws Exception;

    /**
     * Stops the server.
     * 
     * @throws Exception on occasion
     */
    void stop() throws Exception;

    /**
     * Prints the default Jetty Plugin info to the specified stream.
     * 
     * @param out the strem
     */
    void info(PrintStream out);

    /**
     * Returns true if the server is running.
     * 
     * @return true if running
     */
    boolean isRunning();

    /**
     * Returns a list of ports (unsecured ones).
     * 
     * @return the ports, never null
     */
    Collection<Integer> getPorts();

    /**
     * Returns a list of secured ports.
     * 
     * @return the ports, never null
     */
    Collection<Integer> getSecurePorts();

    /**
     * Returns a list of context paths.
     * 
     * @return the context paths, never null
     */
    Collection<String> getContextPaths();

}
