// Licensed under the Apache License, Version 2.0 (the "License");
package net.sourceforge.eclipsejetty.starter.console;

public interface Command
{

    String[] getNames();

    String getFormat();

    String getDescription();

    int help(Process process) throws Exception;

    int getOrdinal();

    boolean isEnabled();

    int execute(String processName, Process process) throws Exception;
}
