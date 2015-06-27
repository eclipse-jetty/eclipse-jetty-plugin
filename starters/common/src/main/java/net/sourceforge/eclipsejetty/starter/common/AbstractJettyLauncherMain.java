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
package net.sourceforge.eclipsejetty.starter.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.sourceforge.eclipsejetty.starter.console.Console;
import net.sourceforge.eclipsejetty.starter.util.Utils;
import net.sourceforge.eclipsejetty.starter.util.service.GlobalServiceResolver;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Abstract base class for the Jetty launcher
 * 
 * @author Manfred Hantschel
 */
public abstract class AbstractJettyLauncherMain
{
    public static final String CONFIGURATION_KEY = "jetty.launcher.configuration";
    public static final String HIDE_LAUNCH_INFO_KEY = "jetty.launcher.hideLaunchInfo";
    public static final String DISABLE_CONSOLE_KEY = "jetty.launcher.disableConsole";

    /**
     * Launches the server. This method has to be called by the main method of the implementations for the various Jetty
     * version.
     * 
     * @param args the arguments
     * @throws Exception on occasion
     */
    protected void launch(String[] args) throws Exception
    {
        long millis = System.currentTimeMillis();
        boolean showInfo = System.getProperty(HIDE_LAUNCH_INFO_KEY) == null;
        boolean consoleEnabled = System.getProperty(DISABLE_CONSOLE_KEY) == null;

        if (showInfo)
        {
            printLogo(System.out);
        }

        String configurationFileDef = System.getProperty(CONFIGURATION_KEY);

        if (configurationFileDef == null)
        {
            throw new IOException(String.format("-D%s missing", CONFIGURATION_KEY));
        }

        File[] configurationFiles = getConfigurationFiles(configurationFileDef);
        ServerAdapter adapter = createAdapter(configurationFiles, showInfo);

        configure(System.out, adapter, configurationFiles, showInfo);

        if (showInfo)
        {
            adapter.info(System.out);
        }

        initConsole(consoleEnabled, adapter);

        adapter.start();

        if (showInfo)
        {
            printStartupTime(System.out, millis, consoleEnabled);
        }
    }

    /**
     * Initializes the console, if enabled.
     * 
     * @param consoleEnabled true if enabled
     * @param adapter the server adapter
     */
    private void initConsole(boolean consoleEnabled, ServerAdapter adapter)
    {
        if (consoleEnabled)
        {
            try
            {
                Class.forName("net.sourceforge.eclipsejetty.starter.console.Console");

                GlobalServiceResolver serviceResolver = GlobalServiceResolver.INSTANCE;

                serviceResolver.register(adapter);

                Console console = Console.INSTANCE;

                console.initialize(serviceResolver);
                console.start();
            }
            catch (ClassNotFoundException e)
            {
                // ignore
            }
        }
    }

    protected abstract ServerAdapter createAdapter(File[] configurationFiles, boolean showInfo) throws Exception;

    protected abstract void printLogo(PrintStream out);

    protected void configure(PrintStream out, ServerAdapter adapter, File[] configurationFiles, boolean showInfo)
        throws Exception
    {
        for (int i = 0; i < configurationFiles.length; i += 1)
        {
            File configurationFile = configurationFiles[i];

            if (showInfo)
            {
                out.println(String.format("%18s%s", (i == 0) ? "Configuration: " : Utils.EMPTY,
                    configurationFile.getAbsolutePath()));
            }

            Class<?> type = determineClass(configurationFile);
            FileInputStream in = new FileInputStream(configurationFile);

            try
            {
                configure(in, type, adapter);
            }
            finally
            {
                in.close();
            }
        }
    }

    protected abstract void configure(FileInputStream in, Class<?> type, ServerAdapter adapter) throws Exception;

    protected static File[] getConfigurationFiles(String definitionList) throws IOException
    {
        String[] definitions = definitionList.split(File.pathSeparator);
        List<File> files = new ArrayList<File>();

        for (String definition : definitions)
        {
            if (definition.trim().length() <= 0)
            {
                continue;
            }

            File file = new File(definition.trim());

            files.add(file);

            if (!file.canRead())
            {
                throw new IOException(String.format("Cannot read configuration file: %s", file));
            }
        }

        return files.toArray(new File[files.size()]);
    }

    protected static Class<?> determineClass(File file) throws IOException
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setExpandEntityReferences(false);
        try
        {
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        }
        catch (ParserConfigurationException e1)
        {
            // ignore
        }

        DocumentBuilder builder;
        try
        {
            builder = factory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e)
        {
            throw new IOException(String.format("Failed to configure parser: %s", e.getMessage()));
        }

        Document document;
        try
        {
            document = builder.parse(new InputSource(new FileInputStream(file)));
        }
        catch (SAXException e)
        {
            throw new IOException(String.format("Failed to parse %s: %s", file, e.getMessage()));
        }
        catch (IOException e)
        {
            throw new IOException(String.format("Failed to read %s: %s", file, e.getMessage()));
        }

        NodeList nodeList = document.getElementsByTagName("Configure");

        if (nodeList.getLength() < 1)
        {
            throw new IOException(String.format("Failed to find <Configure> element in %s", file));
        }

        Node node = nodeList.item(0);
        NamedNodeMap attributes = node.getAttributes();

        if (attributes == null)
        {
            throw new IOException(String.format("Failed to class argument in <Configure> element in %s", file));
        }

        Node item = attributes.getNamedItem("class");

        if (item == null)
        {
            throw new IOException(String.format("Failed to class argument in <Configure> element in %s", file));
        }

        String value = item.getNodeValue().trim();

        try
        {
            return Class.forName(value);
        }
        catch (ClassNotFoundException e)
        {
            throw new IOException(String.format("Unknown class %s in <Configure> element in %s", value, file));
        }
    }

    protected void printStartupTime(PrintStream out, long millis, boolean consoleEnabled)
    {
        Runtime runtime = Runtime.getRuntime();

        System.gc();

        double seconds = ((double) System.currentTimeMillis() - millis) / 1000d;
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();

        String duration = String.format("Jetty startup finished in %s.", Utils.formatSeconds(seconds));
        String console = (consoleEnabled) ? "Console available: type \"help\"." : Utils.EMPTY;
        String memory =
            String.format("Used memory: %s of %s (%s maximum)", Utils.formatBytes(totalMemory - freeMemory),
                Utils.formatBytes(totalMemory), Utils.formatBytes(maxMemory));

        String line = Utils.repeat("-", Math.max(duration.length(), Math.max(memory.length(), console.length())));

        out.println(line);
        out.println(duration);
        out.println(memory);

        if (console.length() > 0)
        {
            out.println(console);
        }

        out.println(line);
    }

}
