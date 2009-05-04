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
package net.sourceforge.eclipsejetty;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.sourceforge.eclipsejetty.launch.JettyLauncherMain;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.StandardClasspathProvider;

/**
 * ClasspathProvider for Jetty.
 * 
 * @author Christian K&ouml;berl
 */
public class JettyLaunchConfigurationClassPathProvider extends StandardClasspathProvider
{
	private static final FilenameFilter JAR_FILTER = new FilenameFilter()
	{
		public boolean accept(File dir, String name)
		{
			if (name != null && name.endsWith(".jar"))
				return true;
			return false;
		}
	};

	public JettyLaunchConfigurationClassPathProvider()
	{
	}

	@Override
	public IRuntimeClasspathEntry[] computeUnresolvedClasspath(ILaunchConfiguration configuration) throws CoreException
	{
		IRuntimeClasspathEntry[] classpath = super.computeUnresolvedClasspath(configuration);
		boolean useDefault = configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH, true);
		if (useDefault)
		{
			classpath = filterWebInfLibs(classpath, configuration);
		}
		else
		{
			// recover persisted classpath
			return recoverRuntimePath(configuration, IJavaLaunchConfigurationConstants.ATTR_CLASSPATH);
		}
		return classpath;
	}

	@Override
	public IRuntimeClasspathEntry[] resolveClasspath(IRuntimeClasspathEntry[] entries,
		ILaunchConfiguration configuration) throws CoreException
	{
		IRuntimeClasspathEntry[] resolvedEntries = super.resolveClasspath(entries, configuration);

		// filter Jetty and Servlet API
		List<IRuntimeClasspathEntry> filteredEntries = new ArrayList<IRuntimeClasspathEntry>(resolvedEntries.length);
		for (IRuntimeClasspathEntry entry : resolvedEntries)
		{
			String path = entry.getLocation();
			if (path != null
				&& (path.contains("org.mortbay.jetty") || path.contains("servlet-api") || path
					.matches(".*selenium-server.*standalone\\.jar")))
				continue;
			filteredEntries.add(entry);
		}
		resolvedEntries = filteredEntries.toArray(new IRuntimeClasspathEntry[filteredEntries.size()]);

		// add Jetty and bootstrap libs
		resolvedEntries = addJettyAndBootstrap(resolvedEntries, configuration);

		return resolvedEntries;
	}

	private IRuntimeClasspathEntry[] addJettyAndBootstrap(IRuntimeClasspathEntry[] existing,
		ILaunchConfiguration configuration) throws CoreException
	{
		List<IRuntimeClasspathEntry> entries = new ArrayList<IRuntimeClasspathEntry>();
		entries.addAll(Arrays.asList(existing));
		String jettyUrl = configuration.getAttribute(JettyPluginConstants.ATTR_JETTY_PATH, (String) null);
		String jettyVersion = configuration.getAttribute(JettyPluginConstants.ATTR_JETTY_VERSION, "6");
		String jspVersion =  configuration.getAttribute(JettyPluginConstants.ATTR_JSP_VERSION, JettyPluginConstants.ATTR_JSP_VERSION_NO);

		try
		{
			entries.add(JavaRuntime.newArchiveRuntimeClasspathEntry(new Path(FileLocator.toFileURL(JettyLauncherMain.class.getResource("/")).getFile())));

			for (File jettyLib : findJettyLibs(jettyUrl, jettyVersion, jspVersion))
			{
				entries.add(JavaRuntime.newArchiveRuntimeClasspathEntry(new Path(jettyLib.getCanonicalPath())));
			}
		}
		catch (IOException e)
		{
			JettyPlugin.logError(e);
		}
		return entries.toArray(new IRuntimeClasspathEntry[entries.size()]);
	}

	/**
	 * Find the jetty libs for given version
	 */
	private Iterable<File> findJettyLibs(String jettyUrl, String jettyVersion, String jspVersion) throws CoreException
	{
		if("5".equals(jettyVersion))
			return findJettyLibs5(jettyUrl);
		else
			return findJettyLibs67(jettyUrl, jspVersion);
	}

	/**
	 * Find the jetty libs for Jetty 6 and 7.
	 */
	private Iterable<File> findJettyLibs67(String jettyUrl, String jspVersion) throws CoreException
	{
		File jettyLibDir = new File(jettyUrl, "lib");
		if (!jettyLibDir.exists() || !jettyLibDir.isDirectory())
			throw new CoreException(new Status(IStatus.ERROR, JettyPlugin.PLUGIN_ID, "Could not find Jetty libs"));

		File[] jettyLibs = jettyLibDir.listFiles(new FilenameFilter()
		{
			public boolean accept(File dir, String name)
			{
				if (name != null && name.endsWith(".jar")
					&& (name.startsWith("jetty-") || name.startsWith("servlet-api")))
					return true;

				return false;
			}
		});
		return Arrays.asList(jettyLibs);
	}

	/**
	 * Find the jetty libs for Jetty 5.
	 */
	private Iterable<File> findJettyLibs5(String jettyUrl) throws CoreException
	{
		File jettyLibDir = new File(jettyUrl, "lib");
		File jettyExtDir = new File(jettyUrl, "ext");
		if (!jettyLibDir.exists() || !jettyLibDir.isDirectory() || !jettyExtDir.exists() || !jettyExtDir.isDirectory())
			throw new CoreException(new Status(IStatus.ERROR, JettyPlugin.PLUGIN_ID, "Could not find Jetty libs"));

		List<File> jettyLibs = new ArrayList<File>();
		Collections.addAll(jettyLibs, jettyLibDir.listFiles(JAR_FILTER));
		Collections.addAll(jettyLibs, jettyExtDir.listFiles(JAR_FILTER));
		return jettyLibs;
	}

	private IRuntimeClasspathEntry[] filterWebInfLibs(IRuntimeClasspathEntry[] defaults,
		ILaunchConfiguration configuration)
	{

		IJavaModel javaModel = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot());
		String projectName = null;
		String webAppDirName = null;
		try
		{
			projectName = configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "");
			webAppDirName = configuration.getAttribute(JettyPluginConstants.ATTR_WEBAPPDIR, "");
		}
		catch (CoreException e)
		{
			JettyPlugin.logError(e);
		}

		if (projectName == null || projectName.trim().equals("") || webAppDirName == null
			|| webAppDirName.trim().equals(""))
		{
			return defaults;
		}

		IJavaProject project = javaModel.getJavaProject(projectName);
		if (project == null)
		{
			return defaults;
		}

		// this should be fine since the plugin checks whether WEB-INF exists
		IFolder webInfDir = project.getProject().getFolder(new Path(webAppDirName)).getFolder("WEB-INF");
		if (webInfDir == null || !webInfDir.exists())
		{
			return defaults;
		}
		IFolder lib = webInfDir.getFolder("lib");
		if (lib == null || !lib.exists())
		{
			return defaults;
		}

		// ok, so we have a WEB-INF/lib dir, which means that we should filter
		// out the entries in there since if the user wants those entries, they
		// should be part of the project definition already
		List<IRuntimeClasspathEntry> keep = new ArrayList<IRuntimeClasspathEntry>();
		for (int i = 0; i < defaults.length; i++)
		{
			if (defaults[i].getType() != IRuntimeClasspathEntry.ARCHIVE)
			{
				keep.add(defaults[i]);
				continue;
			}
			IResource resource = defaults[i].getResource();
			if (resource != null && !resource.getParent().equals(lib))
			{
				keep.add(defaults[i]);
				continue;
			}
		}

		return keep.toArray(new IRuntimeClasspathEntry[keep.size()]);
	}
}
