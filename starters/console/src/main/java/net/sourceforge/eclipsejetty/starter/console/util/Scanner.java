// Licensed under the Apache License, Version 2.0 (the "License");
package net.sourceforge.eclipsejetty.starter.console.util;

import java.io.IOException;
import java.io.Reader;

public class Scanner
{

    private final Reader reader;

    private int offset = 0;
    private char ch;
    private boolean skipLF = false;

    public Scanner(Reader reader)
    {
        super();

        this.reader = reader;
    }

    public int getOffset()
    {
        return offset;
    }

    public void resetOffset()
    {
        offset = 0;
    }

    public char get()
    {
        return ch;
    }

    public char next() throws IOException
    {
        ch = (char) reader.read();

        if ((ch == '\n') && (skipLF))
        {
            ch = (char) reader.read();
        }

        offset += 1;
        skipLF = false;

        if (ch == '\r')
        {
            ch = '\n';
            skipLF = true;
        }

        return ch;
    }

    public void close() throws IOException
    {
        reader.close();
    }
}
