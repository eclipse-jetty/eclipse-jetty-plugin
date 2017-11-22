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

import net.sourceforge.eclipsejetty.JettyPluginUtils;

import org.eclipse.jdt.launching.IRuntimeClasspathEntry;

/**
 * A dependency of the project, stuffed with information from Maven. The dependency contains an generic id, that is
 * either generated from the Maven info, or from the filename.
 * 
 * @author Manfred Hantschel
 */
public class Dependency
{

    /**
     * Creates an dependency
     * 
     * @param mavenDependencyInfoMap the maven dependency infos, null if m2e is missing
     * @param runtimeClasspathEntry the original classpath entry
     * @return the dependency
     */
    public static Dependency create(MavenDependencyInfoMap mavenDependencyInfoMap,
        IRuntimeClasspathEntry runtimeClasspathEntry)
    {
        String genericId = null;
        String location = JettyPluginUtils.toLocation(runtimeClasspathEntry);
        MavenDependencyInfo mavenDependencyInfo = mavenDependencyInfoMap.resolve(runtimeClasspathEntry);

        if (mavenDependencyInfo != null)
        {
            genericId = mavenDependencyInfo.toPortableString();
        }
        else
        {
            genericId = runtimeClasspathEntry.getPath().toFile().getName();
        }

        return new Dependency(genericId, location, runtimeClasspathEntry, mavenDependencyInfo);
    }

    private final String genericId;
    private final String location;
    private final IRuntimeClasspathEntry runtimeClasspathEntry;
    private final MavenDependencyInfo mavenDependencyInfo;

    private Dependency(String genericId, String location, IRuntimeClasspathEntry runtimeClasspathEntry,
        MavenDependencyInfo mavenDependencyInfo)
    {
        super();

        this.genericId = genericId;
        this.location = location;
        this.runtimeClasspathEntry = runtimeClasspathEntry;
        this.mavenDependencyInfo = mavenDependencyInfo;
    }

    /**
     * Returns the generic id
     * 
     * @return the generic id
     */
    public String getGenericId()
    {
        return genericId;
    }

    /**
     * Return the location
     * 
     * @return the location
     */
    public String getLocation()
    {
        return location;
    }

    /**
     * Returns the original classpath entry
     * 
     * @return the original classpath entry
     */
    public IRuntimeClasspathEntry getRuntimeClasspathEntry()
    {
        return runtimeClasspathEntry;
    }

    /**
     * Returns the Maven dependency info if available
     * 
     * @return the Maven dependency info, may be null
     */
    public MavenDependencyInfo getMavenDependencyInfo()
    {
        return mavenDependencyInfo;
    }

    /**
     * Returns true if there is a Maven dependency info, that defines the dependency as being a dependency to another
     * project.
     * 
     * @return true if project dependent
     */
    public boolean isProjectDependent()
    {
        if (mavenDependencyInfo != null)
        {
            return mavenDependencyInfo.isProjectDependent();
        }

        return false;
    }

    /**
     * Returns the Maven scope if available, {@link MavenScope#NONE} otherwise.
     * 
     * @return the Maven scope
     */
    public MavenScope getScope()
    {
        if (mavenDependencyInfo != null)
        {
            return mavenDependencyInfo.getScope();
        }

        return MavenScope.NONE;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("Dependency(%s, %s)", genericId, location); //$NON-NLS-1$
    }

}
