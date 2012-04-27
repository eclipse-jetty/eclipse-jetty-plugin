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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Persistent Jetty configuration
 * 
 * @author Manfred Hantschel
 */
public class JettyConfiguration extends Properties
{

    private static final long serialVersionUID = 2891158206622454362L;

    public static JettyConfiguration load(File file) throws IOException
    {
        JettyConfiguration result = new JettyConfiguration(file);
        FileInputStream in = new FileInputStream(file);

        try
        {
            result.load(in);
        }
        finally
        {
            in.close();
        }

        return result;
    }

    public static JettyConfiguration create() throws IOException
    {
        return new JettyConfiguration(File.createTempFile("jettyLaunchConfiguration", ".properties"));
    }

    private final File file;

    private JettyConfiguration(File file)
    {
        super();

        this.file = file;
    }

    public File getFile()
    {
        return file;
    }

    public void delete()
    {
        file.delete();
    }

    public void store() throws IOException
    {
        FileOutputStream out = new FileOutputStream(file);

        try
        {
            store(out, "Jetty Launch Configuration");
        }
        finally
        {
            out.close();
        }
    }

    public String getContext()
    {
        return getProperty("context", "/");
    }

    public void setContext(String context)
    {
        setProperty("context", context);
    }

    public String getWebAppDir()
    {
        return getProperty("webAppDir");
    }

    public void setWebAppDir(String webapp)
    {
        setProperty("webAppDir", webapp);
    }

    public int getPort()
    {
        return Integer.parseInt(getProperty("port", "8080"));
    }

    public void setPort(String port)
    {
        setProperty("port", port);
    }

    public String[] getClasspath()
    {
        List<String> result = new ArrayList<String>();
        int i = 0;
        String current;

        while ((current = getProperty("classpath." + i)) != null)
        {
            result.add(current);
            i += 1;
        }

        return result.toArray(new String[result.size()]);
    }

    public void setClasspath(String[] classpath)
    {
        for (int i = 0; i < classpath.length; i += 1)
        {
            setProperty("classpath." + i, classpath[i]);
        }
    }

}
