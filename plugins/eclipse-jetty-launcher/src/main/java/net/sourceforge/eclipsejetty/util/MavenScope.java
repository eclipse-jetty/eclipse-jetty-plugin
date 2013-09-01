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

public enum MavenScope
{

    NONE(""),
    COMPILE("compile"),
    PROVIDED("provided"),
    RUNTIME("runtime"),
    TEST("test"),
    SYSTEM("system"),
    IMPORT("import");

    private final String key;

    private MavenScope(String key)
    {
        this.key = key;
    }

    public String key()
    {
        return key;
    }

    @Override
    public String toString()
    {
        return key;
    }

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

        throw new IllegalArgumentException("Unknown scope: " + key);
    }
}
