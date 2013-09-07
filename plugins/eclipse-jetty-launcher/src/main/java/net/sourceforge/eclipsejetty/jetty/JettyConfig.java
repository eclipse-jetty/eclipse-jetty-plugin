package net.sourceforge.eclipsejetty.jetty;

import java.io.File;

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

    public boolean isValid(IWorkspace workspace)
    {
        switch (type)
        {
            case DEFAULT:
                return true;

            case PATH:
                return new File(path).exists();

            case WORKSPACE:
                IFile file = workspace.getRoot().getFile(new Path(path));

                return (file != null) && (file.exists());
        }

        return false;
    }

}
