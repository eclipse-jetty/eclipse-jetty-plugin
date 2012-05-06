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
import java.util.LinkedHashSet;

import org.eclipse.core.runtime.CoreException;

/**
 * Abstract implementation of the {@link IJettyLibStrategy} using dependencies. In the first pass, all dependencies are
 * gathered, in the second pass, the dependencies are resolved.
 * 
 * @author Manfred Hantschel
 */
public abstract class DependencyBasedJettyLibStrategy implements IJettyLibStrategy
{

    /* (non-Javadoc)
     * @see net.sourceforge.eclipsejetty.jetty.IJettyLibStrategy#find(java.io.File, boolean)
     */
    public Collection<File> find(File path, boolean jspSupport) throws CoreException
    {
        Collection<String> dependencies = new LinkedHashSet<String>();

        addServerDependencies(dependencies);

        if (jspSupport)
        {
            addJSPDependencies(dependencies);
        }

        Collection<File> results = new LinkedHashSet<File>();

        resolveDependencies(results, path, dependencies);

        return results;
    }

    /**
     * Add all core dependencies of the server
     * 
     * @param dependencies the dependencies to be filled
     */
    protected abstract void addServerDependencies(Collection<String> dependencies);

    /**
     * Add all dependencies for JSPs
     * 
     * @param dependencies the dependencies to be filled
     */
    protected abstract void addJSPDependencies(Collection<String> dependencies);

    /**
     * Resolves all dependencies an addes the files to the results.
     * 
     * @param results the results to be filled
     * @param path the path of the jetty
     * @param dependencies the dependencies
     * @throws CoreException TODO
     */
    protected abstract void resolveDependencies(Collection<File> results, File path, Collection<String> dependencies) throws CoreException;

}
