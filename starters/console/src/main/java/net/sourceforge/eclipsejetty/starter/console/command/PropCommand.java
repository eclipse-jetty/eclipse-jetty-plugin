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
package net.sourceforge.eclipsejetty.starter.console.command;

import java.util.Map.Entry;
import java.util.Properties;

import net.sourceforge.eclipsejetty.starter.console.AbstractCommand;
import net.sourceforge.eclipsejetty.starter.console.ConsoleAdapter;
import net.sourceforge.eclipsejetty.starter.console.Process;
import net.sourceforge.eclipsejetty.starter.console.util.WildcardUtils;

/**
 * Prints and modifies properties.
 * 
 * @author Manfred Hantschel
 */
public class PropCommand extends AbstractCommand
{

    public PropCommand(ConsoleAdapter consoleAdapter)
    {
        super(consoleAdapter, "prop", "p");
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.console.Command#getFormat()
     */
    public String getFormat()
    {
        return "[<KEY> [<VALUE>]]";
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.console.Command#getDescription()
     */
    public String getDescription()
    {
        return "Manage system properties.";
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.console.Command#getOrdinal()
     */
    public int getOrdinal()
    {
        return 530;
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.console.Command#execute(java.lang.String,
     *      net.sourceforge.eclipsejetty.starter.console.Process)
     */
    public int execute(String commandName, Process process) throws Exception
    {
        String key = process.args.consumeString();

        if (key == null)
        {
            key = "*";
        }

        String value = process.args.consumeString();

        if (value == null)
        {
            Properties properties = System.getProperties();

            for (Entry<Object, Object> entry : properties.entrySet())
            {
                if (WildcardUtils.match(String.valueOf(entry.getKey()).toLowerCase(), key.toLowerCase()))
                {
                    process.out.printf("%s = %s\n", entry.getKey(), entry.getValue());
                }
            }
        }
        else
        {
            System.setProperty(key, value);

            process.out.println("Property set.");
        }

        return 0;
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.console.AbstractCommand#getHelpDescription()
     */
    @Override
    protected String getHelpDescription()
    {
        return "Called without arguments, it will display all system properties. "
            + "If one argument is specified, it will display the specified property (the "
            + "key may then contain wildcards). If two arguments are specified, it will "
            + "try to set the specified property to the specified value.";
    }

}
