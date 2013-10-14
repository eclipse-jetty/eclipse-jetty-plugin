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
package net.sourceforge.eclipsejetty.launch.util;

import net.sourceforge.eclipsejetty.JettyPlugin;
import net.sourceforge.eclipsejetty.Messages;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.DebugPlugin;

/**
 * Utilities for the Jetty Launch Interface
 * 
 * @author Manfred Hantschel
 */
public class JettyLaunchUtils
{

    private JettyLaunchUtils()
    {
        super();
    }

    /**
     * Generates a launch configuration name using the name of the specified project.
     * 
     * @param project the project, must not be null
     * @return an unique launch configuration name
     */
    public static String generateLaunchConfigurationName(IProject project)
    {
        return generateLaunchConfigurationName(project.getName());
    }

    /**
     * Generates a launch configuration name using the specified name.
     * 
     * @param name the name, must not be null
     * @return an unique launch configuration name
     */
    public static String generateLaunchConfigurationName(String name)
    {
        return DebugPlugin.getDefault().getLaunchManager().generateLaunchConfigurationName(name);
    }

    /**
     * Tries to locate the web application folder. First it searches for the WEB-INF/web.xml file, then it cuts the last
     * two segments.
     * 
     * @param project the project, may be null
     * @return the path to the web application folder, null if not found
     */
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
            JettyPlugin.warning(Messages.utils_scanFailed, e);
        }

        return path;
    }

    /**
     * Tries to locate the WEB-INF/web.xml file within the specified resource. Searches sub-folders if the resource is a
     * container. Makes sure, that the parent of the web.xml is a WEB-INF folder.
     * 
     * @param resource the resource, may be null
     * @return the web.xml as resource, null if not found
     * @throws CoreException on occasion
     */
    public static IResource findWebXML(IResource resource) throws CoreException
    {
        if (resource == null)
        {
            return null;
        }

        if (resource instanceof IContainer)
        {
            return findResource((IContainer) resource, "WEB-INF", "web.xml"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        return findResource(resource.getParent(), "WEB-INF", "web.xml"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Tries to locate the resource with the specified name in the specified container or any sub-container. If a folder
     * name is specified, it makes sure that the parent folder of the result resource has the specified folder name.
     * 
     * @param container the container, may be null
     * @param folderName the name of the folder, that should hold the resource. If null, no check is executed.
     * @param name the name of the resource
     * @return the resource, null if not found
     * @throws CoreException on occasion
     */
    public static IResource findResource(IContainer container, String folderName, String name) throws CoreException
    {
        if (container == null)
        {
            return null;
        }

        if (!container.exists())
        {
            return null;
        }

        for (IResource resource : container.members())
        {
            if (name.equalsIgnoreCase(resource.getName()))
            {
                if (folderName == null)
                {
                    return resource;
                }

                IContainer parent = resource.getParent();

                if ((parent != null) && (folderName.equalsIgnoreCase(parent.getName())))
                {
                    return resource;
                }
            }

            if (resource.getType() != IResource.FOLDER)
            {
                continue;
            }

            IResource result = findResource((IFolder) resource, folderName, name);

            if (result != null)
            {
                return result;
            }
        }

        return null;
    }

}
