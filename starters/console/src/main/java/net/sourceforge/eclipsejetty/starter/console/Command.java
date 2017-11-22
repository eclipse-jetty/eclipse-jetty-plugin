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

/**
 * A command can be executed by the console. If has a list of name, and some help associated with it.
 * 
 * @author Manfred Hantschel
 */
public interface Command
{

    /**
     * A list of names. The first one is the default one, like 'help'. Other name are usually abbreviations like 'h' and
     * '?'.
     * 
     * @return a list of names, never null, at least one entry.
     */
    String[] getNames();

    /**
     * Returns the (informational) format, used by the help command.
     * 
     * @return the format
     */
    String getFormat();

    /**
     * Returns a description, used by the help command.
     * 
     * @return
     */
    String getDescription();

    /**
     * Called by the help command.
     * 
     * @param process the process
     * @return the result of the execution, usually 0
     * @throws Exception on occasion
     */
    int help(Process process) throws Exception;

    /**
     * Returns the ordinal for sorting commands. Return < 0 to remove the command from the help overview.
     * 
     * @return the ordinal
     */
    int getOrdinal();

    /**
     * Returns true if the command should be enabled by default. Commands can, disable themself if, e.g. prerequisits
     * are not met, like a class or library is missing.
     * 
     * @return true if enabled.
     */
    boolean isEnabled();

    /**
     * Executes the command using the specified process. As the command may be called using different names, the command
     * name is the exact name, that was used to execute the command.
     * 
     * @param commandName the name of the command
     * @param process the process
     * @return the result of the command
     * @throws Exception on occasion
     */
    int execute(String commandName, Process process) throws Exception;
}
