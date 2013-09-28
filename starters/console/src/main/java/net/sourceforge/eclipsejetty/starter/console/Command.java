// Licensed under the Apache License, Version 2.0 (the "License");
package net.sourceforge.eclipsejetty.starter.console;



public interface Command
{

    String[] getNames();
    
    String getFormat();
    
    String getDescription();

    int help(Context context) throws Exception;
    
    int getOrdinal();
    
    boolean isEnabled();
    
    int execute(Context context) throws Exception;
}
