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
import java.util.Collection;

import org.eclipse.core.runtime.CoreException;

/**
 * Strategy to resolve Jetty dependencies
 * 
 * @author Manfred Hantschel
 */
public interface IJettyLibStrategy
{

    Collection<File> find(File jettyPath, boolean jspSupport, boolean jmxSupport, boolean jndiSupport, boolean ajpSupport) throws CoreException;
    
}
