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
package net.sourceforge.eclipsejetty.starter.jetty8.command;

import java.awt.Desktop;
import java.net.URI;
import java.util.Iterator;

import net.sourceforge.eclipsejetty.starter.common.ServerAdapter;
import net.sourceforge.eclipsejetty.starter.console.AbstractCommand;
import net.sourceforge.eclipsejetty.starter.console.ConsoleAdapter;
import net.sourceforge.eclipsejetty.starter.console.Process;
import net.sourceforge.eclipsejetty.starter.util.Utils;

/**
 * Opens a browser.
 * 
 * @author Manfred Hantschel
 */
public class OpenCommand extends AbstractCommand
{

    private final ServerAdapter adapter;

    public OpenCommand(ConsoleAdapter consoleAdapter, ServerAdapter adapter)
    {
        super(consoleAdapter, "open", "o");

        this.adapter = adapter;
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.console.AbstractCommand#isEnabled()
     */
    @Override
    public boolean isEnabled()
    {
        try
        {
            Class.forName("java.awt.Desktop");
        }
        catch (ClassNotFoundException e)
        {
            return false;
        }

        if (Desktop.isDesktopSupported())
        {
            Desktop desktop = Desktop.getDesktop();

            return desktop.isSupported(Desktop.Action.BROWSE);
        }

        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.console.Command#getFormat()
     */
    @Override
    public String getFormat()
    {
        return Utils.EMPTY;
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.console.Command#getDescription()
     */
    @Override
    public String getDescription()
    {
        return "Opens a browser.";
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.console.AbstractCommand#getHelpDescription()
     */
    @Override
    protected String getHelpDescription()
    {
        return "Opens a browser.";
    }

    @Override
    public int getOrdinal()
    {
        return 3000;
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.console.Command#execute(java.lang.String,
     *      net.sourceforge.eclipsejetty.starter.console.Process)
     */
    @Override
    public int execute(String commandName, Process process) throws Exception
    {
        Iterator<Integer> portIterator = adapter.getPorts().iterator();

        if (!portIterator.hasNext())
        {
            process.err.println("No connector provided.");
            return -1;
        }

        String url = "http://localhost:" + portIterator.next();

        Iterator<String> pathIterator = adapter.getContextPaths().iterator();

        if (!pathIterator.hasNext())
        {
            url += "/";
        }
        else
        {
            url += pathIterator.next();
        }

        process.out.println(String.format("Opening %s...", url));

        Desktop desktop = Desktop.getDesktop();

        desktop.browse(new URI(url));

        return 0;
    }

}
