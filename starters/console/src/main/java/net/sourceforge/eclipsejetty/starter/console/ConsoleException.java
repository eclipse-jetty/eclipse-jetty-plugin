// Licensed under the Apache License, Version 2.0 (the "License");
package net.sourceforge.eclipsejetty.starter.console;

public class ConsoleException extends RuntimeException
{

    private static final long serialVersionUID = 4671208137366239322L;

    public ConsoleException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ConsoleException(String message)
    {
        super(message);
    }

}
