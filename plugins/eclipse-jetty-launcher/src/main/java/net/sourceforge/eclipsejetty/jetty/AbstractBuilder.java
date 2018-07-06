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
package net.sourceforge.eclipsejetty.jetty;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import net.sourceforge.eclipsejetty.util.DOMBuilder;

/**
 * Abstract builder for configuration XML files
 * 
 * @author Manfred Hantschel
 */
public abstract class AbstractBuilder
{

    protected static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public AbstractBuilder()
    {
        super();
    }

    /**
     * Builds the xml.
     * 
     * @param warning true to add a warning, that the file automatically generated.
     * @return the builder
     */
    public DOMBuilder build(boolean warning)
    {
        DOMBuilder builder = new DOMBuilder();

        if (warning)
        {
            StringBuilder comment = new StringBuilder();

            comment.append(LINE_SEPARATOR);
            comment.append("This is a temporary file.");
            comment.append(LINE_SEPARATOR);
            comment.append("It was automatically created by the Eclipse Jetty Plugin.");
            comment.append(LINE_SEPARATOR);
            comment.append("There is no need, nor sense, to edit this file!");
            comment.append(LINE_SEPARATOR);

            builder.comment(comment);
        }

        buildBody(builder);

        return builder;
    }

    /**
     * Build the main body
     * 
     * @param builder the builder
     */
    protected abstract void buildBody(DOMBuilder builder);

    /**
     * Builds and write the content to the specified file
     * 
     * @param file the file
     * @param formatted true to be formatted
     * @throws IOException on occasion
     */
    public void write(File file, boolean formatted) throws IOException
    {
        FileOutputStream out = new FileOutputStream(file);

        try
        {
            build(formatted).write(out, formatted);
        }
        finally
        {
            out.close();
        }
    }

}
