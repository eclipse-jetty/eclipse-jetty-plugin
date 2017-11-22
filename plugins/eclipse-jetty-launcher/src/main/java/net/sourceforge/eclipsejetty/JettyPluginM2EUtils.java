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
package net.sourceforge.eclipsejetty;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.ArtifactKey;
import org.eclipse.m2e.core.project.IMavenProjectFacade;

import net.sourceforge.eclipsejetty.launch.util.JettyLaunchConfigurationAdapter;

/**
 * Some utilities
 *
 * @author Christian K&ouml;berl
 * @author Manfred Hantschel
 */
public class JettyPluginM2EUtils
{

    /**
     * Returns true if the m2e plugin is available.
     *
     * @return true if the m2e plugin is available
     */
    public static boolean isM2EAvailable()
    {
        try
        {
            Class.forName("org.eclipse.m2e.core.MavenPlugin");
        }
        catch (ClassNotFoundException e)
        {
            return false;
        }

        return true;
    }

    /**
     * Returns the {@link IMavenProjectFacade} for the specified project
     *
     * @param adapter the configuration adapter
     * @return the {@link IMavenProjectFacade} if available, null otherwise
     * @throws CoreException on occasion
     */
    public static IMavenProjectFacade getMavenProjectFacade(JettyLaunchConfigurationAdapter adapter)
        throws CoreException
    {
        IProject project = adapter.getProject();

        if (project == null)
        {
            return null;
        }

        if (!isM2EAvailable())
        {
            return null;
        }

        return MavenPlugin.getMavenProjectRegistry().getProject(project);
    }

    /**
     * Returns the Maven portable string
     *
     * @param groupId the group
     * @param artifactId the artifact
     * @param version the version
     * @param classifier the classifier
     * @return the portable string
     */
    public static String toPortableString(String groupId, String artifactId, String version, String classifier)
    {
        return toPortableString(groupId, artifactId, version, classifier, null);
    }

    /**
     * Returns the Maven portable string with the varaint
     *
     * @param groupId the group
     * @param artifactId the artifact
     * @param version the version
     * @param classifier the classifier
     * @param variant the variant (output, resource, test-output, test-resource)
     * @return the portable string
     */
    public static String toPortableString(String groupId, String artifactId, String version, String classifier,
        String variant)
    {
        StringBuilder builder = new StringBuilder();

        if (groupId != null)
        {
            builder.append(groupId);
        }

        builder.append(':');

        if (artifactId != null)
        {
            builder.append(artifactId);
        }

        builder.append(':');

        if (version != null)
        {
            builder.append(version);
        }

        builder.append(':');

        if (classifier != null)
        {
            builder.append(classifier);
        }

        builder.append(':');

        if (variant != null)
        {
            builder.append(variant);
        }

        return builder.toString();
    }

    /**
     * Returns the suspected Maven path for the artifact
     *
     * @param artifactKey the artifact key
     * @return the path
     */
    public static String toPath(ArtifactKey artifactKey)
    {
        StringBuilder builder = new StringBuilder();

        if (artifactKey.getGroupId() != null)
        {
            builder.append(artifactKey.getGroupId().replace('.', '/'));
        }

        if (artifactKey.getVersion() != null)
        {
            if (builder.length() > 0)
            {
                builder.append("/");
            }

            builder.append(artifactKey.getVersion());
        }

        if (builder.length() > 0)
        {
            builder.append("/");
        }

        builder.append(artifactKey.getArtifactId());

        if (artifactKey.getVersion() != null)
        {
            builder.append("-").append(artifactKey.getVersion());
        }

        // TODO where's the classifier!?

        return builder.toString();
    }

}
