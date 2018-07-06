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
package net.sourceforge.eclipsejetty.jetty;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.Path;

/**
 * Reference to a Jetty configuration xml file.
 * 
 * @author Manfred Hantschel
 */
public class JettyConfig
{

    private final String path;
    private final JettyConfigType type;

    private boolean active;

    /**
     * Creates the configuration
     * 
     * @param path the path to the file
     * @param type the type of link (filesystem or workspace)
     * @param active true if active
     */
    public JettyConfig(String path, JettyConfigType type, boolean active)
    {
        super();

        this.path = path;
        this.type = type;
        this.active = active;
    }

    /**
     * Returns the path to the configuration file
     * 
     * @return the path
     */
    public String getPath()
    {
        return path;
    }

    /**
     * Returns the type of the configuration
     * 
     * @return the type
     */
    public JettyConfigType getType()
    {
        return type;
    }

    /**
     * Returns true if the configuration is active.
     * 
     * @return true if active
     */
    public boolean isActive()
    {
        return active;
    }

    /**
     * Set to true, to activate the configuration.
     * 
     * @param active true, to activate the configuration
     */
    public void setActive(boolean active)
    {
        this.active = active;
    }

    /**
     * Checks the configuration for validity, e.g. that the file is accessible.
     * 
     * @param workspace the workspace
     * @return true if valid
     */
    public boolean isValid(IWorkspace workspace)
    {
        switch (type)
        {
            case DEFAULT:
                return true;

            case PATH:
                return new File(path).exists();

            case WORKSPACE:
                IFile file = workspace.getRoot().getFile(new Path(path));

                return (file != null) && (file.exists());
        }

        return false;
    }

}
