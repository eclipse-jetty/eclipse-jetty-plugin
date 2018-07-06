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
import net.sourceforge.eclipsejetty.Messages;

/**
 * Represents a scope of Maven
 * 
 * @author Manfred Hantschel
 */
public enum MavenScope
{

    /**
     * Undetermined or missing scope
     */
    NONE(JettyPluginUtils.EMPTY),

    /**
     * The compile scope
     */
    COMPILE("compile"), //$NON-NLS-1$

    /**
     * The provided scope
     */
    PROVIDED("provided"), //$NON-NLS-1$

    /**
     * The runtime scope
     */
    RUNTIME("runtime"), //$NON-NLS-1$

    /**
     * The test scope
     */
    TEST("test"), //$NON-NLS-1$

    /**
     * The system scope
     */
    SYSTEM("system"), //$NON-NLS-1$

    /**
     * The mysical import scope
     */
    IMPORT("import"); //$NON-NLS-1$

    private final String key;

    private MavenScope(String key)
    {
        this.key = key;
    }

    /**
     * Returns the key, as defined by Maven
     * 
     * @return the key
     */
    public String key()
    {
        return key;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString()
    {
        return key;
    }

    /**
     * Finds the {@link MavenScope} for the specified key as defined by Maven
     * 
     * @param key the key, may be null
     * @return the scope
     */
    public static MavenScope to(String key)
    {
        if (key == null)
        {
            return null;
        }

        for (MavenScope value : MavenScope.values())
        {
            if (key.equalsIgnoreCase(value.key()))
            {
                return value;
            }
        }

        throw new IllegalArgumentException(String.format(Messages.mavenScope_unknown, key));
    }
}
