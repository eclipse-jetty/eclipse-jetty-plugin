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
package net.sourceforge.eclipsejetty.launch;

import static net.sourceforge.eclipsejetty.launch.JettyLaunchClasspathMatcher.*;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.eclipsejetty.JettyPlugin;
import net.sourceforge.eclipsejetty.JettyPluginConstants;
import net.sourceforge.eclipsejetty.jetty.JettyConfiguration;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.ExecutionArguments;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.JavaLaunchDelegate;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.VMRunnerConfiguration;

/**
 * Launch configuration delegate for Jetty. Based on {@link JavaLaunchDelegate}.
 * 
 * @author Christian K&ouml;berl
 * @author Manfred Hantschel
 */
public class JettyLaunchConfigurationDelegate extends AbstractJavaLaunchConfigurationDelegate
{
    public JettyLaunchConfigurationDelegate()
    {
        super();
    }

    /* (non-Javadoc)
     * @see org.eclipse.debug.core.model.ILaunchConfigurationDelegate#launch(org.eclipse.debug.core.ILaunchConfiguration, java.lang.String, org.eclipse.debug.core.ILaunch, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void launch(final ILaunchConfiguration configuration, final String mode, final ILaunch launch,
        IProgressMonitor monitor) throws CoreException
    {
        configuration.getWorkingCopy().setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH_PROVIDER,
            JettyPluginConstants.CLASSPATH_PROVIDER_JETTY);

        if (monitor == null)
        {
            monitor = new NullProgressMonitor();
        }

        monitor.beginTask(MessageFormat.format("{0}...", configuration.getName()), 3); //$NON-NLS-1$

        // check for cancellation
        if (monitor.isCanceled())
        {
            return;
        }

        try
        {
            monitor.subTask("verifying installation");

            //            String jettyPath = JettyPluginConstants.getPath(configuration);
            //            JettyVersion jettyVersion = JettyPluginUtils.detectJettyVersion(jettyPath, JettyPluginConstants.getVersion(configuration));

            final String mainTypeName = JettyPluginConstants.getMainTypeName(configuration);
            final IVMRunner runner = getVMRunner(configuration, mode);

            final File workingDir = verifyWorkingDirectory(configuration);
            String workingDirName = null;
            if (workingDir != null)
            {
                workingDirName = workingDir.getAbsolutePath();
            }

            // Environment variables
            final String[] envp = getEnvironment(configuration);

            // Program & VM arguments
            final String pgmArgs = getProgramArguments(configuration);
            final String vmArgs = getVMArguments(configuration);
            final ExecutionArguments execArgs = new ExecutionArguments(vmArgs, pgmArgs);

            // VM-specific attributes
            @SuppressWarnings("rawtypes")
            final Map vmAttributesMap = getVMSpecificAttributesMap(configuration);

            JettyLaunchClasspathMatcher vmClasspathMatcher = userClasses();
            
            if (JettyPluginConstants.isScopeCompileExcluded(configuration)) {
                vmClasspathMatcher = and(vmClasspathMatcher, not(withExtraAttribute("maven.scope", "compile")));
            }
            
            if (JettyPluginConstants.isScopeProvidedExcluded(configuration)) {
                vmClasspathMatcher = and(vmClasspathMatcher, not(withExtraAttribute("maven.scope", "provided")));
            }
            
            if (JettyPluginConstants.isScopeRuntimeExcluded(configuration)) {
                vmClasspathMatcher = and(vmClasspathMatcher, not(withExtraAttribute("maven.scope", "runtime")));
            }
            
            if (JettyPluginConstants.isScopeSystemExcluded(configuration)) {
                vmClasspathMatcher = and(vmClasspathMatcher, not(withExtraAttribute("maven.scope", "system")));
            }
            
            if (JettyPluginConstants.isScopeTestExcluded(configuration)) {
                vmClasspathMatcher = and(vmClasspathMatcher, not(withExtraAttribute("maven.scope", "test")));
            }
            
            // Class path
            final String[] classpath =
                getClasspath(configuration, vmClasspathMatcher);

            File jettyConfigurationFile = createJettyConfigurationFile(configuration, classpath);

            // Create VM configuration
            final VMRunnerConfiguration runConfig = new VMRunnerConfiguration(mainTypeName, classpath);
            runConfig.setEnvironment(envp);
            runConfig.setVMArguments(execArgs.getVMArgumentsArray());

            final List<String> programArgs = new ArrayList<String>();
            programArgs.addAll(Arrays.asList(execArgs.getProgramArgumentsArray()));
            programArgs.add(jettyConfigurationFile.getAbsolutePath());
            runConfig.setProgramArguments(programArgs.toArray(new String[programArgs.size()]));

            runConfig.setWorkingDirectory(workingDirName);
            runConfig.setVMSpecificAttributesMap(vmAttributesMap);

            // Boot path
            runConfig.setBootClassPath(getBootpath(configuration));

            // check for cancellation
            if (monitor.isCanceled())
            {
                return;
            }

            // stop in main
            prepareStopInMain(configuration);

            // done the verification phase
            monitor.worked(1);

            monitor.subTask("Creating source locator");
            // set the default source locator if required
            setDefaultSourceLocator(launch, configuration);
            monitor.worked(1);

            // Launch the configuration - 1 unit of work
            runner.run(runConfig, launch, monitor);

            // check for cancellation
            if (monitor.isCanceled())
            {
                return;
            }
        }
        finally
        {
            monitor.done();
        }
    }

    private File createJettyConfigurationFile(final ILaunchConfiguration configuration, final String[] classpath)
        throws CoreException
    {
        JettyConfiguration jettyConfiguration;

        try
        {
            jettyConfiguration = JettyConfiguration.create();
        }
        catch (IOException e)
        {
            throw new CoreException(new Status(IStatus.ERROR, JettyPlugin.PLUGIN_ID,
                "Failed to create tmp file for storing Jetty launch configuration"));
        }

        jettyConfiguration.setContext(JettyPluginConstants.getContext(configuration));
        jettyConfiguration.setWebAppDir(JettyPluginConstants.getWebAppDir(configuration));
        jettyConfiguration.setPort(JettyPluginConstants.getPort(configuration));
        jettyConfiguration.setClasspath(classpath);

        try
        {
            jettyConfiguration.store();
        }
        catch (IOException e)
        {
            throw new CoreException(new Status(IStatus.ERROR, JettyPlugin.PLUGIN_ID,
                "Failed to store tmp file with Jetty launch configuration"));
        }
        return jettyConfiguration.getFile();
    }

    protected Collection<IRuntimeClasspathEntry> getClasspathEntries(ILaunchConfiguration configuration)
        throws CoreException
    {
        IRuntimeClasspathEntry[] entries = JavaRuntime.computeUnresolvedRuntimeClasspath(configuration);

        entries = JavaRuntime.resolveRuntimeClasspath(entries, configuration);

        return new HashSet<IRuntimeClasspathEntry>(Arrays.asList(entries));
    }

    public String[] getClasspath(ILaunchConfiguration configuration, JettyLaunchClasspathMatcher... matchers)
        throws CoreException
    {
        Set<String> results = new HashSet<String>();
        Collection<IRuntimeClasspathEntry> classpathEntries = getClasspathEntries(configuration);
        Collection<IRuntimeClasspathEntry> matchedClasspathEntries = and(matchers).match(classpathEntries);

        for (IRuntimeClasspathEntry entry : matchedClasspathEntries)
        {
            String location = entry.getLocation();

            if (location != null)
            {
                results.add(location);
            }
        }

        return results.toArray(new String[results.size()]);
    }

    @Override
    public String[] getClasspath(ILaunchConfiguration configuration) throws CoreException
    {
        return getClasspath(configuration, userClasses());
    }

}
