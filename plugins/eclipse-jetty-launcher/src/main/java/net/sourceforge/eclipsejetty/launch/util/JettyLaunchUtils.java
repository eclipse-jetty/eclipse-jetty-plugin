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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
     * Tries to locate all the web application folders. First it searches for the WEB-INF/web.xml file, then it cuts the
     * last two segments.
     * 
     * @param project the project, may be null
     * @param maxResults the maximum number of results
     * @return a list of paths to all the web application folders, empty if not found
     */
    public static List<IPath> findWebappDirs(IProject project, int maxResults)
    {
        List<IResource> webXMLResources = null;

        try
        {
            webXMLResources = findWebXMLs(project, maxResults);
        }
        catch (CoreException e)
        {
            JettyPlugin.warning(Messages.utils_scanFailed, e);
        }

        return toWebappDirs(webXMLResources);
    }

    /**
     * Converts the web.xml resources to the web app directories
     * 
     * @param webXMLResources the web.xml resources
     * @return a list of paths to all the web application folders, empty if not found
     */
    public static List<IPath> toWebappDirs(List<IResource> webXMLResources)
    {
        List<IPath> results = new ArrayList<IPath>();

        if (webXMLResources != null)
        {
            for (IResource webXMLResource : webXMLResources)
            {
                results.add(webXMLResource.getFullPath().removeLastSegments(2));
            }
        }

        return results;
    }

    /**
     * Tries to locate all the WEB-INF/web.xml files within the specified resource. Searches sub-folders if the resource
     * is a container. Makes sure, that the parent of the web.xml is a WEB-INF folder. Only returns maxResults.
     * 
     * @param resource the resource, may be null
     * @param maxResults the maximum number of results
     * @return a list of the web.xml files as resource, empty if none was found
     * @throws CoreException on occasion
     */
    public static List<IResource> findWebXMLs(IResource resource, int maxResults) throws CoreException
    {
        if (resource == null)
        {
            return Collections.<IResource> emptyList();
        }

        if (resource instanceof IContainer)
        {
            return findResources(new ArrayList<IResource>(), (IContainer) resource, "WEB-INF", "web.xml", maxResults); //$NON-NLS-1$ //$NON-NLS-2$
        }

        return findResources(new ArrayList<IResource>(), resource.getParent(), "WEB-INF", "web.xml", maxResults); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Tries to locate all the resources with the specified name in the specified container or any sub-container. If a
     * folder name is specified, it makes sure that the parent folder of the result resource has the specified folder
     * name.
     * 
     * @param a list holding all results
     * @param container the container, may be null
     * @param folderName the name of the folder, that should hold the resource. If null, no check is executed.
     * @param name the name of the resource
     * @param maxResults the maximum number of results
     * @return the results themself
     * @throws CoreException on occasion
     */
    protected static List<IResource> findResources(List<IResource> results, IContainer container, String folderName,
        String name, int maxResults) throws CoreException
    {
        if (container == null)
        {
            return results;
        }

        if (!container.exists())
        {
            return results;
        }

        for (IResource resource : container.members())
        {
            if (name.equalsIgnoreCase(resource.getName()))
            {
                if (folderName == null)
                {
                    results.add(resource);

                    if (results.size() >= maxResults)
                    {
                        return results;
                    }

                    continue;
                }

                IContainer parent = resource.getParent();

                if ((parent != null) && (folderName.equalsIgnoreCase(parent.getName())))
                {
                    results.add(resource);

                    if (results.size() >= maxResults)
                    {
                        return results;
                    }

                    continue;
                }
            }

            if (resource.getType() != IResource.FOLDER)
            {
                continue;
            }

            findResources(results, (IFolder) resource, folderName, name, maxResults);

            if (results.size() >= maxResults)
            {
                return results;
            }
        }

        return results;
    }

    /**
     * Returns an array containing string representations of the elements in the collection.
     * 
     * @param collection the collection
     * @return the array
     */
    public static String[] toStringArray(Collection<?> collection)
    {
        String[] result = new String[collection.size()];
        int index = 0;

        for (Object entry : collection)
        {
            result[index] = String.valueOf(entry);

            index += 1;
        }

        return result;
    }
}
