package net.sourceforge.eclipsejetty.launch.util;

import net.sourceforge.eclipsejetty.JettyPlugin;
import net.sourceforge.eclipsejetty.JettyPluginUtils;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.DebugPlugin;

public class JettyLaunchUtils
{

    public static String generateLaunchConfigurationName(IProject project)
    {
        return generateLaunchConfigurationName(project.getName());
    }

    public static String generateLaunchConfigurationName(String name)
    {
        return DebugPlugin.getDefault().getLaunchManager().generateLaunchConfigurationName(name);
    }

    public static IPath findWebappDir(IProject project)
    {
        IPath path = null;

        try
        {
            IResource resource = findWebXML(project);

            if (resource != null)
            {
                path = resource.getFullPath().removeLastSegments(2);
            }
        }
        catch (CoreException e)
        {
            JettyPlugin.warning("Failed to scan project", e);
        }

        return path;
    }

    public static IResource findWebXML(IResource resource) throws CoreException
    {
        if (resource == null)
        {
            return null;
        }

        if (resource instanceof IContainer)
        {
            return JettyPluginUtils.findResource((IContainer) resource, "WEB-INF", "web.xml");
        }

        return JettyPluginUtils.findResource(resource.getParent(), "WEB-INF", "web.xml");
    }

}
