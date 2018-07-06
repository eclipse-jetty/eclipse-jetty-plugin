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

import net.sourceforge.eclipsejetty.starter.console.AbstractCommand;
import net.sourceforge.eclipsejetty.starter.console.ConsoleAdapter;
import net.sourceforge.eclipsejetty.starter.console.Process;
import net.sourceforge.eclipsejetty.starter.util.Utils;

/**
 * Executes a process asynchoniously.
 * 
 * @author Manfred Hantschel
 */
public class AmpersandCommand extends AbstractCommand
{

    public AmpersandCommand(ConsoleAdapter consoleAdapter)
    {
        super(consoleAdapter, "&");
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
        return "Executes a process asynchoniously.";
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.console.Command#getOrdinal()
     */
    public int getOrdinal()
    {
        return -1;
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.console.Command#execute(java.lang.String,
     *      net.sourceforge.eclipsejetty.starter.console.Process)
     */
    public int execute(String commandName, final Process process) throws Exception
    {
        Thread thread = new Thread(new Runnable()
        {

            public void run()
            {
                int result = -1;

                try
                {
                    result = process.parent.execute();
                }
                finally
                {
                    process.out.printf("Finished [%s]: %s\n", Thread.currentThread().getId(), result);
                }
            }

        }, "Eclipse Jetty Console Process");

        thread.start();

        process.out.printf("Started [%s]\n", thread.getId());

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
        return "Executes a process asynchoniously.";
    }

}
