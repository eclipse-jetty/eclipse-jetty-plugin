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

public class Dependency
{

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

    public Dependency(String genericId, String location, IRuntimeClasspathEntry runtimeClasspathEntry,
        MavenDependencyInfo mavenDependencyInfo)
    {
        super();

        this.genericId = genericId;
        this.location = location;
        this.runtimeClasspathEntry = runtimeClasspathEntry;
        this.mavenDependencyInfo = mavenDependencyInfo;
    }

    public String getGenericId()
    {
        return genericId;
    }

    public String getLocation()
    {
        return location;
    }

    public IRuntimeClasspathEntry getRuntimeClasspathEntry()
    {
        return runtimeClasspathEntry;
    }

    public MavenDependencyInfo getMavenDependencyInfo()
    {
        return mavenDependencyInfo;
    }

    public boolean isProjectDependent()
    {
        if (mavenDependencyInfo != null)
        {
            return mavenDependencyInfo.isProjectDependent();
        }

        return false;
    }

    public MavenScope getScope()
    {
        if (mavenDependencyInfo != null)
        {
            return mavenDependencyInfo.getScope();
        }

        return MavenScope.NONE;
    }

    @Override
    public String toString()
    {
        return "Dependency(" + genericId + ", " + location + ")";
    }

}
