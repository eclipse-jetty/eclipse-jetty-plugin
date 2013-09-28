// Licensed under the Apache License, Version 2.0 (the "License");
package net.sourceforge.eclipsejetty.starter.common;

import java.io.PrintStream;
import java.util.Collection;

public interface ServerAdapter
{

    Object getServer();
    
    void start() throws Exception;

    void stop() throws Exception;

    void info(PrintStream out);
    
    Collection<Integer> getPorts();

    Collection<Integer> getSecurePorts();

    Collection<String> getContextPaths();
}
