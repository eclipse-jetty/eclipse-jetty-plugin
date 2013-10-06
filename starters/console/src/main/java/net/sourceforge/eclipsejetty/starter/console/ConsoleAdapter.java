package net.sourceforge.eclipsejetty.starter.console;

import java.util.Collection;

public interface ConsoleAdapter
{

    Command getCommand(String name);

    Collection<Command> getCommands();

    int getLineLength();

}
