package net.sourceforge.eclipsejetty.jetty;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class JettyConfig
{

    private final String path;
    private final JettyConfigType type;
    private final JettyConfigScope scope;

    private boolean active;

    public JettyConfig(String path, JettyConfigType type, JettyConfigScope scope, boolean active)
    {
        super();

        this.path = path;
        this.type = type;
        this.scope = scope;
        this.active = active;
    }

    public String getPath()
    {
        return path;
    }

    public JettyConfigType getType()
    {
        return type;
    }

    public JettyConfigScope getScope()
    {
        return scope;
    }

    public boolean isActive()
    {
        return active;
    }

    public void setActive(boolean active)
    {
        this.active = active;
    }

    public IFile getFile(IWorkspace workspace)
    {
        return getFile(workspace, type, path);
    }

    public boolean isValid(IWorkspace workspace)
    {
        try
        {
            IFile file = getFile(workspace);

            return (file == null) || file.exists();
        }
        catch (IllegalArgumentException e)
        {
            return false;
        }
    }

    public static IFile getFile(IWorkspace workspace, JettyConfigType type, String path)
    {
        switch (type)
        {
            case DEFAULT:
                return null;

            case PATH:
                return workspace.getRoot().getFileForLocation(new Path(path));

            case WORKSPACE:
                return workspace.getRoot().getFile(new Path(path));
        }

        return null;
    }

    public static JettyConfigScope determineScope(IFile file)
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;

        try
        {
            builder = factory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e)
        {
            e.printStackTrace(System.err);
            return JettyConfigScope.UNKNOWN;
        }

        Document document;

        try
        {
            document = builder.parse(new InputSource(file.getContents()));
        }
        catch (SAXException e)
        {
            e.printStackTrace(System.err);
            return JettyConfigScope.UNKNOWN;
        }
        catch (IOException e)
        {
            e.printStackTrace(System.err);
            return JettyConfigScope.UNKNOWN;
        }
        catch (CoreException e)
        {
            e.printStackTrace(System.err);
            return JettyConfigScope.UNKNOWN;
        }

        NodeList nodeList = document.getElementsByTagName("Configure");

        if (nodeList.getLength() < 1)
        {
            return JettyConfigScope.UNKNOWN;
        }

        Node node = nodeList.item(0);
        NamedNodeMap attributes = node.getAttributes();

        if (attributes == null)
        {
            return JettyConfigScope.UNKNOWN;
        }

        Node item = attributes.getNamedItem("class");

        if (item == null)
        {
            return JettyConfigScope.UNKNOWN;
        }

        String value = item.getNodeValue().trim();

        if (value.endsWith("Server"))
        {
            return JettyConfigScope.SERVER;
        }
        else if (value.endsWith("WebAppContext"))
        {
            return JettyConfigScope.WEBAPPCONTEXT;
        }

        return JettyConfigScope.UNKNOWN;
    }

}
