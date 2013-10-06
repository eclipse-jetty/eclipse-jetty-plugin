// Licensed under the Apache License, Version 2.0 (the "License");
package net.sourceforge.eclipsejetty.starter.console;

public class ArgumentException extends ConsoleException
{

    private static final long serialVersionUID = 4671208137366239322L;

    public ArgumentException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ArgumentException(String message)
    {
        super(message);
    }

}
