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

import net.sourceforge.eclipsejetty.starter.console.util.CommandUtils;
import net.sourceforge.eclipsejetty.starter.console.util.WordWrap;

/**
 * Abstract base implementation of a {@link Command}.
 * 
 * @author Manfred Hantschel
 */
public abstract class AbstractCommand implements Command
{

    protected final ConsoleAdapter consoleAdapter;
    private final String[] names;

    /**
     * Creates the command using the specified console adapter and the specified names.
     * 
     * @param consoleAdapter the console adapter
     * @param names the names
     */
    public AbstractCommand(ConsoleAdapter consoleAdapter, String... names)
    {
        super();

        this.consoleAdapter = consoleAdapter;
        this.names = names;
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.console.Command#getNames()
     */
    public String[] getNames()
    {
        return names;
    }

    /**
     * The default implemenation always returns true.
     * 
     * @see net.sourceforge.eclipsejetty.starter.console.Command#isEnabled()
     */
    public boolean isEnabled()
    {
        return true;
    }

    /**
     * Common implementation of the help method. Build the description by printing the usage and synonyms and calls the
     * {@link #getHelpDescription()} method for the detailed description.
     * 
     * @see net.sourceforge.eclipsejetty.starter.console.Command#help(net.sourceforge.eclipsejetty.starter.console.Process)
     */
    public int help(Process process) throws Exception
    {
        process.out.println(new WordWrap().perform(
            String.format("Usage:    %s", CommandUtils.getFormatDescriptor(this)), consoleAdapter.getLineLength()));

        if (getNames().length > 1)
        {
            process.out.printf("Synonyms: %s\n", CommandUtils.getNameDescriptor(this, false));
        }

        process.out.println();

        process.out.println(new WordWrap().perform(getHelpDescription(), consoleAdapter.getLineLength()));
        process.out.println();

        return 0;
    }

    /**
     * Return the detailed description of the command. The result will get word wrapped.
     * 
     * @return the detailed description
     */
    protected abstract String getHelpDescription();

}
