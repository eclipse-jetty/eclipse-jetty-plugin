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

import java.util.Collection;

import net.sourceforge.eclipsejetty.starter.console.AbstractCommand;
import net.sourceforge.eclipsejetty.starter.console.Command;
import net.sourceforge.eclipsejetty.starter.console.ConsoleAdapter;
import net.sourceforge.eclipsejetty.starter.console.Process;
import net.sourceforge.eclipsejetty.starter.console.util.CommandUtils;
import net.sourceforge.eclipsejetty.starter.console.util.WordWrap;
import net.sourceforge.eclipsejetty.starter.util.Utils;

/**
 * Prints help.
 * 
 * @author Manfred Hantschel
 */
public class HelpCommand extends AbstractCommand
{

    public HelpCommand(ConsoleAdapter consoleAdapter)
    {
        super(consoleAdapter, "help", "h", "?");
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.console.Command#getFormat()
     */
    public String getFormat()
    {
        return "[command {args}]";
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.console.Command#getDescription()
     */
    public String getDescription()
    {
        return "Shows a list of commands and provides help for each command.";
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.console.AbstractCommand#getHelpDescription()
     */
    @Override
    protected String getHelpDescription()
    {
        return "You can call this help, with or without a command.\n"
            + "If called without a command, it shows a list of all possible commands.\n"
            + "If called with a command, it shows a description of the specified command.";

    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.console.Command#getOrdinal()
     */
    public int getOrdinal()
    {
        return 0;
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.console.Command#execute(java.lang.String,
     *      net.sourceforge.eclipsejetty.starter.console.Process)
     */
    public int execute(String commandName, Process process) throws Exception
    {
        String command = process.args.consumeString();

        if (command != null)
        {
            showDetailHelp(process, command);
        }
        else
        {
            showHelp(process);
        }

        return 0;
    }

    private void showHelp(Process process)
    {
        Collection<Command> commands = consoleAdapter.getCommands();

        int maxNameLength = 0;

        for (Command command : commands)
        {
            maxNameLength = Math.max(maxNameLength, CommandUtils.getNameDescriptor(command, true).length());
        }

        String prefix = Utils.repeat(" ", maxNameLength + 3);

        for (Command command : commands)
        {
            if (command.getOrdinal() < 0)
            {
                continue;
            }

            showHelp(process, command, prefix);
        }

        process.out.println();
        process.out.println(new WordWrap().perform("Using > will pipe the output of any command to a file. "
            + "Arguments may contain ${..} placeholds, to access environment and system properties.",
            consoleAdapter.getLineLength()));
    }

    private void showHelp(Process process, Command command, String prefix)
    {
        process.out.printf("%-" + prefix.length() + "s", CommandUtils.getNameDescriptor(command, true));

        process.out.println(Utils.prefixLine(
            new WordWrap().perform(command.getDescription(), consoleAdapter.getLineLength() - prefix.length()), prefix,
            false));
    }

    private int showDetailHelp(Process process, String name) throws Exception
    {
        Command command = consoleAdapter.getCommand(name);

        if (command == null)
        {
            process.err.printf("Unknown command: %s\n", name);

            return -1;
        }

        return command.help(process);
    }

}
