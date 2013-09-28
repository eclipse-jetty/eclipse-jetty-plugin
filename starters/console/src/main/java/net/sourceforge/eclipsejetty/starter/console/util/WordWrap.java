// Licensed under the Apache License, Version 2.0 (the "License");
package net.sourceforge.eclipsejetty.starter.console.util;

/**
 * Provides word wrap functionality.
 * 
 * @author Manfred Hantschl
 * @author Thomas Moser
 */
public final class WordWrap
{
    private static final String DEFAULT_UNCUTABLE = "\"\'!$%()[]{}?.,:;";

    private String cutable = "";
    private String uncutable = DEFAULT_UNCUTABLE;
    private int tabWidth = 8;
    private String lineSeparator = System.getProperty("line.separator");

    /**
     * Creates a word wrap instance
     */
    public WordWrap()
    {
        super();
    }

    /**
     * Defines the character to indicate a cut position (after the character)
     * 
     * @param c the character
     * @return the instance
     */
    public WordWrap cutable(final char c)
    {
        if (cutable.indexOf(c) < 0)
        {
            cutable += 1;
        }

        return this;
    }

    /**
     * Defines the character to indicate a position with not cut-ability (after the character). Default is: all letters,
     * all digits and "'!$%()[]{}?.,:;
     * 
     * @param c the character
     * @return the instance
     */
    public WordWrap uncutable(final char c)
    {
        if (uncutable.indexOf(c) < 0)
        {
            uncutable += c;
        }

        return this;
    }

    private boolean isCutable(final char ch)
    {
        return (cutable.indexOf(ch) >= 0)
            || (!((Character.isLetter(ch)) || (Character.isDigit(ch)) || (uncutable.indexOf(ch) >= 0)));
    }

    /**
     * Defines the width of one tab, default is 8
     * 
     * @param withTabWidth the width of one tab
     * @return the instance
     */
    public WordWrap withTabWidth(final int withTabWidth)
    {
        tabWidth = withTabWidth;

        return this;
    }

    /**
     * Sets the line separator, default is defined by OS
     * 
     * @param withLineSeparator the line separator
     * @return the instance
     */
    public WordWrap withLineSeparator(final String withLineSeparator)
    {
        lineSeparator = withLineSeparator;

        return this;
    }

    /**
     * Performs a wordwrap on the text. The operation is thread-safe. You can use one {@link WordWrap} instance with the
     * same configuration for multiple threads.
     * 
     * @param text the text
     * @param length the length of each line
     * @return the warped text
     */
    //CHECKSTYLE:OFF fixing complexity would mean rewrite
    public String perform(final String text, final int length)
    {
        if (text == null)
        {
            return null;
        }

        StringBuilder result = new StringBuilder();

        int lineBegin = 0;
        int position = 0;
        int lineLength = 0;
        int possibleCutPosition = -1;
        boolean isCutable = false;
        boolean wasCutable = false;
        boolean readingIndent = true;
        String indent = "";

        while (position < text.length())
        {
            char ch = text.charAt(position);

            if (ch == '\n')
            {
                // the current char is a line feed. Reset the line.
                result.append(text.substring(lineBegin, position)).append(lineSeparator);

                position += 1;
                lineBegin = position;
                lineLength = 0;
                possibleCutPosition = -1;
                isCutable = false;
                wasCutable = false;
                readingIndent = true;
                indent = "";
            }
            else if (ch == '\r')
            {
                // the current char is a carriage return. Reset the line.
                result.append(text.substring(lineBegin, position)).append(lineSeparator);

                position += 1;
                lineBegin = position;
                lineLength = 0;
                possibleCutPosition = -1;
                isCutable = false;
                wasCutable = false;
                readingIndent = true;
                indent = "";

                if (((position + 1) < text.length()) && (text.charAt(position + 1) == '\n'))
                {
                    // we are in dos here, skip the carriage return.
                    position += 1;
                }
            }
            else
            {
                wasCutable = isCutable;

                if (ch == '\t')
                {
                    // the current char is a tab. This is a special cut position. Additional advance
                    // the length
                    possibleCutPosition = position;
                    lineLength += tabWidth;
                    isCutable = true;

                    if (readingIndent)
                    {
                        indent += ch;
                    }
                }
                else
                {
                    lineLength += 1;

                    if (ch == ' ')
                    {
                        // the current char is a space. This is a special cut position.
                        possibleCutPosition = position;
                        isCutable = true;

                        if (readingIndent)
                        {
                            indent += ch;
                        }
                    }
                    else
                    {
                        isCutable = isCutable(ch);
                        readingIndent = false;

                        if ((wasCutable) && (!isCutable))
                        {
                            // found cut position, because the last char was cutable, but the
                            // current isn't
                            possibleCutPosition = position;
                        }
                    }
                }

                if (lineLength > length)
                {
                    // we have to cut now
                    if (possibleCutPosition > -1)
                    {
                        // there was a position where i could cut
                        result.append(text.substring(lineBegin, possibleCutPosition)).append(lineSeparator);
                        // append the indent for the next line, TODO this is possibly wrong here
                        result.append(indent);

                        position = possibleCutPosition;

                        while ((position < text.length()) && (text.charAt(position) == ' '))
                        {
                            // skip the white-spaces
                            position += 1;
                        }

                        lineBegin = position;
                        lineLength = 0;
                        possibleCutPosition = -1;
                        isCutable = false;
                        wasCutable = false;
                    }
                    else
                    {
                        // there was no cut position, cut right now
                        result.append(text.substring(lineBegin, position)).append(lineSeparator);
                        // append the indent for the next line, TODO this is possibly wrong here
                        result.append(indent);

                        while ((position < text.length()) && (text.charAt(position) == ' '))
                        {
                            // skip the white-spaces
                            position += 1;
                        }

                        lineBegin = position;
                        lineLength = 0;
                        possibleCutPosition = -1;
                        isCutable = false;
                        wasCutable = false;
                    }
                }
                else
                {
                    position += 1;
                }
            }
        }

        if (lineBegin < position)
        {
            result.append(text.substring(lineBegin));
        }

        return result.toString();
    }

    //CHECKSTYLE:ON
}
