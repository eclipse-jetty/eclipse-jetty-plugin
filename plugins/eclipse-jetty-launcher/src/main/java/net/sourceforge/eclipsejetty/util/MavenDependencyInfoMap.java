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
package net.sourceforge.eclipsejetty.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sourceforge.eclipsejetty.JettyPlugin;
import net.sourceforge.eclipsejetty.JettyPluginM2EUtils;
import net.sourceforge.eclipsejetty.JettyPluginUtils;
import net.sourceforge.eclipsejetty.Messages;
import net.sourceforge.eclipsejetty.launch.util.JettyLaunchConfigurationAdapter;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.ArtifactRef;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;

/**
 * A map holding all {@link MavenDependencyInfo}s
 * 
 * @author Manfred Hantschel
 */
public class MavenDependencyInfoMap
{

    private final Map<String, MavenDependencyInfo> dependencies = new HashMap<String, MavenDependencyInfo>();

    /**
     * Creates the infos from the classpath
     * 
     * @param adapter the configuration adapter
     * @param runtimeClasspathEntries the classpath entries
     * @throws CoreException on occasion
     */
    public MavenDependencyInfoMap(JettyLaunchConfigurationAdapter adapter,
        IRuntimeClasspathEntry... runtimeClasspathEntries) throws CoreException
    {
        super();

        if (!JettyPluginM2EUtils.isM2EAvailable())
        {
            return;
        }

        IMavenProjectRegistry mavenProjectRegistry = MavenPlugin.getMavenProjectRegistry();
        IProject project = adapter.getProject();

        if (project != null)
        {
            IProject[] referencedProjects = project.getReferencedProjects();

            if (referencedProjects != null)
            {
                for (IProject referencedProject : referencedProjects)
                {
                    IMavenProjectFacade facade = mavenProjectRegistry.getProject(referencedProject);

                    if (facade != null)
                    {
                        buildLocations(facade);
                    }
                }
            }
        }

        IMavenProjectFacade facade = JettyPluginM2EUtils.getMavenProjectFacade(adapter);

        if (facade == null)
        {
            return;
        }

        buildLocations(facade);

        Set<ArtifactRef> artifactsRefs = facade.getMavenProjectArtifacts();

        for (ArtifactRef artifactRef : artifactsRefs)
        {
            buildArtifact(artifactRef);
        }
    }

    private void buildLocations(IMavenProjectFacade facade)
    {
        addLocations(facade, "output", MavenScope.COMPILE, facade.getOutputLocation()); //$NON-NLS-1$
        addLocations(facade, "resource", MavenScope.RUNTIME, facade.getResourceLocations()); //$NON-NLS-1$
        addLocations(facade, "test-output", MavenScope.TEST, facade.getTestOutputLocation()); //$NON-NLS-1$
        addLocations(facade, "test-resource", MavenScope.TEST, facade.getTestResourceLocations()); //$NON-NLS-1$
    }

    private void addLocations(IMavenProjectFacade facade, String variant, MavenScope scope, IPath... paths)
    {
        if ((paths == null) || (paths.length == 0))
        {
            return;
        }

        MavenDependencyInfo dependency = MavenDependencyInfo.create(facade, variant, scope);

        for (IPath path : paths)
        {
            String location = toLocation(facade, path);

            if (location != null)
            {
                dependencies.put(location, dependency);
            }
        }
    }

    private void buildArtifact(ArtifactRef artifactRef)
    {
        String portableString = artifactRef.getArtifactKey().toPortableString();

        dependencies.put(portableString, MavenDependencyInfo.create(artifactRef));
    }

    /**
     * Returns the info for the specified classpath entry
     * 
     * @param runtimeClasspathEntry the entry
     * @return the info, null if not found
     */
    public MavenDependencyInfo resolve(IRuntimeClasspathEntry runtimeClasspathEntry)
    {
        MavenDependencyInfo suspectedMavenDependency = MavenDependencyInfo.create(runtimeClasspathEntry);
        String location = JettyPluginUtils.toLocation(runtimeClasspathEntry);

        if (suspectedMavenDependency != null)
        {
            String suspectedMavenDependencyPortableString = suspectedMavenDependency.toPortableString();
            MavenDependencyInfo mavenDependency = dependencies.get(suspectedMavenDependencyPortableString);

            if (mavenDependency != null)
            {
                if (!JettyPluginUtils.equals(suspectedMavenDependency.getScope(), mavenDependency.getScope()))
                {
                    JettyPlugin.info(String.format(Messages.dependencyMap_fixedScope, location,
                        suspectedMavenDependency.getScope(), mavenDependency.getScope()));
                }

                return mavenDependency;
            }
        }

        MavenDependencyInfo mavenDependency = dependencies.get(location);

        if (mavenDependency != null)
        {
            return mavenDependency;
        }

        // add more checks here 

        return null;
    }

    /**
     * Converts the path to an absolute location in the file system. Returns null if the path could not be determined.
     * 
     * @param facade the facade of the reference project
     * @param path the path
     * @return the absolute location, null if unknown
     */
    public static String toLocation(IMavenProjectFacade facade, IPath path)
    {
        if (path == null)
        {
            return null;
        }

        IProject project = facade.getProject();

        if (project == null)
        {
            JettyPlugin.info(String.format("Failed to determine project reference of %s", facade.getArtifactKey()
                .toPortableString()));

            return null;
        }

        IPath location = project.getLocation();

        if (location == null)
        {
            JettyPlugin.info(String.format("Failed to determine location of project %s", project.getName()));

            return null;
        }

        return location.append(path.makeRelativeTo(facade.getFullPath())).toString(); 
    }

}
