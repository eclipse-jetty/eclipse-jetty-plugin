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
import java.util.Collection;

import net.sourceforge.eclipsejetty.util.DOMBuilder;

public abstract class AbstractConfiguration
{

    public AbstractConfiguration()
    {
        super();
    }

    public DOMBuilder build()
    {
        DOMBuilder builder = new DOMBuilder();

        builder.begin("Configure").attribute("id", getIdToConfigure()).attribute("class", getClassToConfigure());
        buildContent(builder);
        builder.end();

        return builder;
    }

    public void write(File file) throws IOException
    {
        FileOutputStream out = new FileOutputStream(file);

        try
        {
            build().write(out);
        }
        finally
        {
            out.close();
        }
    }

    protected abstract String getDocType();

    protected abstract String getIdToConfigure();

    protected abstract String getClassToConfigure();

    protected abstract void buildContent(DOMBuilder builder);

    protected String link(Collection<String> values)
    {
        return link(values.toArray(new String[values.size()]));
    }

    protected String link(String... values)
    {
        StringBuilder result = new StringBuilder();

        if (values != null)
        {
            for (int i = 0; i < values.length; i += 1)
            {
                if (i > 0)
                {
                    result.append(File.pathSeparator);
                }

                result.append(values[i]);
            }
        }

        return result.toString();
    }
}
