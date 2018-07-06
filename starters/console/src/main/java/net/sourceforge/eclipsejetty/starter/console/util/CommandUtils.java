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
package net.sourceforge.eclipsejetty.starter.console.util;

import net.sourceforge.eclipsejetty.starter.console.Command;

/**
 * Common utils for the {@link Command}s
 * 
 * @author Manfred Hantschel
 */
public class CommandUtils
{

    /**
     * Creates a nicly formatted description of the command.
     * 
     * @param command the command
     * @param withFormat true to add the format descriptor
     * @return the description of the command
     */
    public static String getNameDescriptor(Command command, boolean withFormat)
    {
        StringBuilder result = new StringBuilder();

        for (String name : command.getNames())
        {
            if (result.length() > 0)
            {
                result.append(", ");
            }

            result.append(name);
        }

        if ((withFormat) && (command.getFormat().length() > 0))
        {
            result.append(" <arg>");
        }

        return result.toString();
    }

    /**
     * Returns the format description with the first name.
     * 
     * @param command the command
     * @return the format description
     */
    public static String getFormatDescriptor(Command command)
    {
        return command.getNames()[0] + " " + command.getFormat();
    }

}
