// Licensed under the Apache License, Version 2.0 (the "License");
package net.sourceforge.eclipsejetty.starter.console.util;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Tokenizer
{

    private final Scanner scanner;

    public Tokenizer(Scanner scanner)
    {
        super();

        this.scanner = scanner;
    }

    public List<String> read() throws IOException
    {
        List<String> tokens = new ArrayList<String>();

        try
        {
            while (true)
            {
                char ch = scanner.next();

                if (ch == '\n')
                {
                    scanner.resetOffset();
                    return tokens;
                }

                if (!isWhitespace(ch))
                {
                    if (ch == '\'')
                    {
                        StringBuilder builder = new StringBuilder();

                        while (true)
                        {
                            ch = scanner.next();

                            if (ch == '\'')
                            {
                                tokens.add(builder.toString());
                                break;
                            }

                            builder.append(ch);
                        }
                    }
                    else if (ch == '\"')
                    {
                        StringBuilder builder = new StringBuilder();

                        while (true)
                        {
                            ch = scanner.next();

                            if (ch == '\"')
                            {
                                tokens.add(builder.toString());
                                break;
                            }

                            builder.append(ch);
                        }
                    }
                    else
                    {
                        StringBuilder builder = new StringBuilder();

                        builder.append(ch);

                        while (true)
                        {
                            ch = scanner.next();

                            if (ch == '\n')
                            {
                                tokens.add(builder.toString());
                                return tokens;
                            }
                            else if (isWhitespace(ch))
                            {
                                tokens.add(builder.toString());
                                break;
                            }

                            builder.append(ch);
                        }
                    }
                }
            }
        }
        catch (EOFException e)
        {
            return null;
        }
    }

    public void close() throws IOException
    {
        scanner.close();
    }

    protected static boolean isWhitespace(char ch)
    {
        return (ch == ' ') || (ch == '\t') || (ch == '\r') || (ch == '\n');
    }
}
