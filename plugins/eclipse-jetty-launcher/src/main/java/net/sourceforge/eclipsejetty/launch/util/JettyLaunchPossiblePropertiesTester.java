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

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

/**
 * Tests the project of a specified {@link IResource} to contains an WEB-INF/web.xml file
 * 
 * @author Manfred Hantschel
 */
public class JettyLaunchPossiblePropertiesTester extends PropertyTester
{

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object, java.lang.String, java.lang.Object[],
     *      java.lang.Object)
     */
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
    {
        if (!(receiver instanceof IResource))
        {
            return false;
        }

        IProject project = ((IResource) receiver).getProject();

        if (project == null)
        {
            return false;
        }

        if (!project.exists())
        {
            return false;
        }

        if (!project.isOpen())
        {
            return false;
        }

        try
        {
            return JettyLaunchUtils.findWebXMLs(project, 1).size() > 0;
        }
        catch (CoreException e)
        {
            return false;
        }
    }

}
