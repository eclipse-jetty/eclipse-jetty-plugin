// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package net.sourceforge.eclipsejetty.starter.console.util;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A tokenizer for the console
 * 
 * @author Manfred Hantschel
 */
public class Tokenizer
{

    private final Scanner scanner;

    public Tokenizer(Scanner scanner)
    {
        super();

        this.scanner = scanner;
    }

    public Arguments read() throws IOException
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

                    return new Arguments(tokens);
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

                                return new Arguments(tokens);
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
