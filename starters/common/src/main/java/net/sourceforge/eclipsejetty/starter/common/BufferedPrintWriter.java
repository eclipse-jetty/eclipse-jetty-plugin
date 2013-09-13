// Licensed under the Apache License, Version 2.0 (the "License");
package net.sourceforge.eclipsejetty.starter.common;

import java.io.PrintWriter;
import java.io.StringWriter;

public class BufferedPrintWriter extends PrintWriter
{

    public BufferedPrintWriter()
    {
        super(new StringWriter());
    }

    @Override
    public String toString()
    {
        return out.toString();
    }
}
