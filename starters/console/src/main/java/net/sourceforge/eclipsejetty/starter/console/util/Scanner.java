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

import java.io.IOException;
import java.io.Reader;

/**
 * A scanner for the console
 * 
 * @author Manfred Hantschel
 */
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
