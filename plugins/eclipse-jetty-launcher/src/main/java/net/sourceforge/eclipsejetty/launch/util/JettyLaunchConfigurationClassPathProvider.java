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

import org.eclipse.jdt.core.IAccessRule;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.launching.StandardClasspathProvider;

/**
 * ClasspathProvider for Jetty.
 * 
 * @author Christian K&ouml;berl
 * @author Manfred Hantschel
 */
// TODO remove me
public class JettyLaunchConfigurationClassPathProvider extends StandardClasspathProvider
{
    public final static IClasspathAttribute[] JETTY_EXTRA_ATTRIBUTES = {new JettyClasspathAttribute()};

    public final static IAccessRule[] NO_ACCESS_RULES = {};

    public JettyLaunchConfigurationClassPathProvider()
    {
        super();
    }

}
