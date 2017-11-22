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
package net.sourceforge.eclipsejetty.starter.common.command;

import net.sourceforge.eclipsejetty.starter.common.ServerAdapter;
import net.sourceforge.eclipsejetty.starter.console.AbstractCommand;
import net.sourceforge.eclipsejetty.starter.console.ConsoleAdapter;
import net.sourceforge.eclipsejetty.starter.console.Process;
import net.sourceforge.eclipsejetty.starter.util.Utils;

/**
 * Prints the info
 * 
 * @author Manfred Hantschel
 */
public class InfoCommand extends AbstractCommand
{

    private final ServerAdapter serverAdapter;

    public InfoCommand(ConsoleAdapter consoleAdapter, ServerAdapter serverAdapter)
    {
        super(consoleAdapter, "info", "i");

        this.serverAdapter = serverAdapter;
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.console.Command#getFormat()
     */
    public String getFormat()
    {
        return Utils.EMPTY;
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.console.Command#getDescription()
     */
    public String getDescription()
    {
        return "Show the launcher info.";
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.console.AbstractCommand#getHelpDescription()
     */
    @Override
    protected String getHelpDescription()
    {
        return "Show the launcher info.  You can use \"info > file.txt\" to write the info to a file.";
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.console.Command#getOrdinal()
     */
    public int getOrdinal()
    {
        return 9010;
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.console.Command#execute(java.lang.String,
     *      net.sourceforge.eclipsejetty.starter.console.Process)
     */
    public int execute(String commandName, Process process) throws Exception
    {
        serverAdapter.info(process.out);

        return 0;
    }

}
