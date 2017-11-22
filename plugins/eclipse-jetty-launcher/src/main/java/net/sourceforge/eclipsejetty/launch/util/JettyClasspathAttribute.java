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

import org.eclipse.jdt.core.IClasspathAttribute;

/**
 * Eclipse classpath attrubite for Jetty library
 * 
 * @author Manfred Hantschel
 */
public class JettyClasspathAttribute implements IClasspathAttribute
{

    public static final String NAME = "jetty"; //$NON-NLS-1$
    public static final String VALUE = "true"; //$NON-NLS-1$

    public JettyClasspathAttribute()
    {
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jdt.core.IClasspathAttribute#getName()
     */
    public String getName()
    {
        return NAME;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jdt.core.IClasspathAttribute#getValue()
     */
    public String getValue()
    {
        return VALUE;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return NAME + "=" + VALUE; //$NON-NLS-1$
    }
}