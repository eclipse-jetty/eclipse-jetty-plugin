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

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import net.sourceforge.eclipsejetty.starter.console.AbstractCommand;
import net.sourceforge.eclipsejetty.starter.console.ArgumentException;
import net.sourceforge.eclipsejetty.starter.console.ConsoleAdapter;
import net.sourceforge.eclipsejetty.starter.console.Process;

/**
 * Redirects output.
 * 
 * @author Manfred Hantschel
 */
public class PipeCommand extends AbstractCommand
{

    public PipeCommand(ConsoleAdapter consoleAdapter)
    {
        super(consoleAdapter, ">", ">>");
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.console.Command#getFormat()
     */
    public String getFormat()
    {
        return "[file]";
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.console.Command#getDescription()
     */
    public String getDescription()
    {
        return "Writes the input stream to the file.";
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
    public int execute(String commandName, Process process) throws Exception
    {
        boolean append = ">>".equals(commandName);
        File file = process.args.consumeFile();

        PrintStream out;

        if (file != null)
        {
            process.out.printf("Redirecting output to %s\n", file.getAbsolutePath());

            out = new PrintStream(new FileOutputStream(file, append));
        }
        else
        {
            out = System.out;
        }

        try
        {
            if (process.args.size() > 0)
            {
                throw new ArgumentException("Too many arguments");
            }

            if (process.parent != null)
            {
                process.parent.redirect(process.in, out, out).execute();
            }
        }
        finally
        {
            if (out != System.out)
            {
                out.close();
            }
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
        return "Writes the input stream to the specified file. " //
            + "This command is used for > and >> pipe commands. " //
            + "If -a is specified, it will append the output to the specified file.";
    }

}
