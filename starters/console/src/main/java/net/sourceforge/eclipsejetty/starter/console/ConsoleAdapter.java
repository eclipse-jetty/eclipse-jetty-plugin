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
package net.sourceforge.eclipsejetty.starter.console;

import java.util.Collection;

import net.sourceforge.eclipsejetty.starter.util.service.ServiceResolver;

/**
 * Adapter for the console to be used in commands. Defined in the {@link ServiceResolver}.
 * 
 * @author Manfred Hantschel
 */
public interface ConsoleAdapter
{

    /**
     * Returns the command with the specified name, null if not found.
     * 
     * @param name the name of the command
     * @return the command with the specified name, null if not found
     */
    Command getCommand(String name);

    /**
     * Returns a (sorted) collection of all commands.
     * 
     * @return a collection of commands, never null
     */
    Collection<Command> getCommands();

    /**
     * Returns the default line length of the console.
     * 
     * @return the default line length
     */
    int getLineLength();

}
