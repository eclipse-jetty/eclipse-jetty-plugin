package net.sourceforge.eclipsejetty.jetty;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.Path;

public class JettyConfig
{

    private final String path;
    private final JettyConfigType type;

    private boolean active;

    public JettyConfig(String path, JettyConfigType type, boolean active)
    {
        super();

        this.path = path;
        this.type = type;
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

}
