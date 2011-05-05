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

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.sourceforge.eclipsejetty.JettyPluginConstants;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.ExecutionArguments;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.JavaLaunchDelegate;
import org.eclipse.jdt.launching.VMRunnerConfiguration;

/**
 * Launch configuration delegate for Jetty. Based on {@link JavaLaunchDelegate}.
 */
public class JettyLaunchConfigurationDelegate extends AbstractJavaLaunchConfigurationDelegate
{
	public JettyLaunchConfigurationDelegate()
	{
	}

	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
		throws CoreException
	{
		configuration.getWorkingCopy().setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH_PROVIDER, JettyPluginConstants.CLASSPATH_PROVIDER_JETTY);

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

			String mainTypeName = configuration.getAttribute(
					IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
					JettyPluginConstants.DEFAULT_BOOTSTRAP_CLASS_NAME);

			IVMRunner runner = getVMRunner(configuration, mode);

			File workingDir = verifyWorkingDirectory(configuration);
			String workingDirName = null;
			if (workingDir != null)
			{
				workingDirName = workingDir.getAbsolutePath();
			}

			// Environment variables
			String[] envp = getEnvironment(configuration);

			// Program & VM arguments
			String pgmArgs = getProgramArguments(configuration);
			String vmArgs = getVMArguments(configuration);
			ExecutionArguments execArgs = new ExecutionArguments(vmArgs, pgmArgs);

			// VM-specific attributes
			Map vmAttributesMap = getVMSpecificAttributesMap(configuration);

			// Class path
			String[] classpath = getClasspath(configuration);

			// Create VM configuration
			VMRunnerConfiguration runConfig = new VMRunnerConfiguration(mainTypeName, classpath);
			runConfig.setEnvironment(envp);
			runConfig.setVMArguments(execArgs.getVMArgumentsArray());

			List<String> programArgs = new ArrayList<String>();
			programArgs.addAll(Arrays.asList(execArgs.getProgramArgumentsArray()));
			programArgs.add("-context");
			programArgs.add(configuration.getAttribute(JettyPluginConstants.ATTR_CONTEXT, ""));
			programArgs.add("-webapp");
			programArgs.add(configuration.getAttribute(JettyPluginConstants.ATTR_WEBAPPDIR, ""));
			programArgs.add("-port");
			programArgs.add(configuration.getAttribute(JettyPluginConstants.ATTR_PORT, ""));
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
}
