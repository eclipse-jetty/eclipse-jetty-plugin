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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sourceforge.eclipsejetty.JettyPluginM2EUtils;
import net.sourceforge.eclipsejetty.JettyPluginUtils;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.m2e.core.embedder.ArtifactRef;
import org.eclipse.m2e.core.project.IMavenProjectFacade;

public class MavenScopeCollection
{

    private static final Map<String, String> scopes = new HashMap<String, String>();

    public static MavenScopeCollection create(ILaunchConfiguration configuration) throws CoreException
    {
        if (JettyPluginM2EUtils.isM2EAvailable())
        {
            IMavenProjectFacade facade = JettyPluginM2EUtils.getMavenProjectFacade(configuration);

            if (facade != null)
            {
                MavenScopeCollection collection = new MavenScopeCollection();

                collection.build(facade);

                return collection;
            }
        }

        return null;
    }

    private String outputLocation;
    private Set<String> resourceLocations;
    private String testOutputLocation;
    private Set<String> testResourceLocations;

    public MavenScopeCollection()
    {
        super();
    }

    public void build(IMavenProjectFacade facade)
    {
        Set<ArtifactRef> artifactsRefs = facade.getMavenProjectArtifacts();

        for (ArtifactRef artifactRef : artifactsRefs)
        {
            scopes.put(artifactRef.getArtifactKey().toPortableString(), artifactRef.getScope());
        }

        outputLocation = toLocation(facade, facade.getOutputLocation());
        resourceLocations = toLocations(facade, facade.getResourceLocations());

        testOutputLocation = toLocation(facade, facade.getTestOutputLocation());
        testResourceLocations = toLocations(facade, facade.getTestResourceLocations());
    }

    public MavenScope resolve(IRuntimeClasspathEntry entry)
    {
        IClasspathEntry classpathEntry = entry.getClasspathEntry();

        if (classpathEntry == null)
        {
            return MavenScope.NONE;
        }

        IClasspathAttribute[] extraAttributes = classpathEntry.getExtraAttributes();

        String groupId = null;
        String artifactId = null;
        String version = null;
        String classifier = null;
        MavenScope scope = null;

        for (IClasspathAttribute extraAttribute : extraAttributes)
        {
            if ("maven.groupId".equals(extraAttribute.getName()))
            {
                groupId = extraAttribute.getValue();
            }
            else if ("maven.artifactId".equals(extraAttribute.getName()))
            {
                artifactId = extraAttribute.getValue();
            }
            else if ("maven.version".equals(extraAttribute.getName()))
            {
                version = extraAttribute.getValue();
            }
            else if ("maven.classifier".equals(extraAttribute.getName()))
            {
                classifier = extraAttribute.getValue();
            }
            else if ("maven.scope".equals(extraAttribute.getName()))
            {
                scope = MavenScope.to(extraAttribute.getValue());
            }
        }

        MavenScope resolvedScope =
            resolve(JettyPluginUtils.toLocation(entry),
                JettyPluginM2EUtils.toPortableString(groupId, artifactId, version, classifier));

        if (resolvedScope != null)
        {
            if (!JettyPluginUtils.equals(resolvedScope, scope))
            {
                System.err.println("Wrong scope: " + scope);
                System.err.println("Right scope: " + resolvedScope);
            }

            scope = resolvedScope;
        }

        return (scope != null) ? scope : MavenScope.NONE;
    }

    public MavenScope resolve(String location, String portableString)
    {
        MavenScope scope = MavenScope.to(scopes.get(portableString));

        if (scope == null)
        {
            if ((outputLocation != null) && (outputLocation.equals(location)))
            {
                scope = MavenScope.COMPILE;
            }
            else if ((resourceLocations != null) && (resourceLocations.contains(location)))
            {
                scope = MavenScope.RUNTIME;
            }
            else if ((testOutputLocation != null) && (testOutputLocation.equals(location)))
            {
                scope = MavenScope.TEST;
            }
            else if ((testResourceLocations != null) && (testResourceLocations.contains(location)))
            {
                scope = MavenScope.TEST;
            }
        }

        return scope;
    }

    private Set<String> toLocations(IMavenProjectFacade facade, IPath[] locations)
    {
        if (locations == null)
        {
            return null;
        }

        Set<String> results = new HashSet<String>();

        for (IPath location : locations)
        {
            results.add(toLocation(facade, location));
        }

        return results;
    }

    public static String toLocation(IMavenProjectFacade facade, IPath location)
    {
        return facade.getProject().getLocation().append(location.makeRelativeTo(facade.getFullPath())).toString(); // wow!
    }

}
