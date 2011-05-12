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
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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
		public boolean accept(final File dir, final String name)
		{
			if ((name != null) && name.endsWith(".jar"))
			{
				return true;
			}
			return false;
		}
	};

	public JettyLaunchConfigurationClassPathProvider()
	{
		super();
	}

	@Override
	public IRuntimeClasspathEntry[] computeUnresolvedClasspath(final ILaunchConfiguration configuration)
		throws CoreException
	{
		IRuntimeClasspathEntry[] classpath = super.computeUnresolvedClasspath(configuration);
		final boolean useDefault =
			configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH, true);
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
	public IRuntimeClasspathEntry[] resolveClasspath(final IRuntimeClasspathEntry[] entries,
		final ILaunchConfiguration configuration) throws CoreException
	{
		IRuntimeClasspathEntry[] resolvedEntries = super.resolveClasspath(entries, configuration);

		final List<Pattern> excludedLibs = new ArrayList<Pattern>();

		excludedLibs.add(Pattern.compile(".*org\\.mortbay\\.jetty.*"));

		try
		{
			extractPatterns(excludedLibs,
				configuration.getAttribute(JettyPluginConstants.ATTR_LAUNCHER_EXCLUDED_LIBS, ""));
		}
		catch (final IllegalArgumentException e)
		{
			throw new CoreException(new Status(IStatus.ERROR, JettyPlugin.PLUGIN_ID, e.getMessage(), e));
		}

		// filter Jetty and Servlet API
		final List<IRuntimeClasspathEntry> filteredEntries =
			new ArrayList<IRuntimeClasspathEntry>(resolvedEntries.length);

		entryLoop: for (final IRuntimeClasspathEntry entry : resolvedEntries)
		{
			final String path = entry.getLocation();

			for (final Pattern excludedLib : excludedLibs)
			{
				if (excludedLib.matcher(path).matches())
				{
					continue entryLoop;
				}
			}

			filteredEntries.add(entry);
		}
		resolvedEntries = filteredEntries.toArray(new IRuntimeClasspathEntry[filteredEntries.size()]);

		// add Jetty and bootstrap libs
		resolvedEntries = addJettyAndBootstrap(resolvedEntries, configuration);

		return resolvedEntries;
	}

	private IRuntimeClasspathEntry[] addJettyAndBootstrap(final IRuntimeClasspathEntry[] existing,
		final ILaunchConfiguration configuration) throws CoreException
	{
		final List<IRuntimeClasspathEntry> entries = new ArrayList<IRuntimeClasspathEntry>();
		entries.addAll(Arrays.asList(existing));
		final String jettyUrl = configuration.getAttribute(JettyPluginConstants.ATTR_JETTY_PATH, (String) null);
		final String jettyVersion =
			detectJettyVersion(jettyUrl, configuration.getAttribute(JettyPluginConstants.ATTR_JETTY_VERSION, "auto"));
		final String jspEnabled =
			configuration.getAttribute(JettyPluginConstants.ATTR_JSP_ENABLED,
				JettyPluginConstants.ATTR_JSP_ENABLED_DEFAULT);

		try
		{
			entries.add(JavaRuntime.newArchiveRuntimeClasspathEntry(new Path(FileLocator.toFileURL(
				JettyLauncherMain.class.getResource("/")).getFile())));

			for (final File jettyLib : findJettyLibs(jettyUrl, jettyVersion, jspEnabled))
			{
				entries.add(JavaRuntime.newArchiveRuntimeClasspathEntry(new Path(jettyLib.getCanonicalPath())));
			}
		}
		catch (final IOException e)
		{
			JettyPlugin.logError(e);
		}
		return entries.toArray(new IRuntimeClasspathEntry[entries.size()]);
	}

	/**
	 * Find the jetty libs for given version
	 */
	private Iterable<File> findJettyLibs(final String jettyUrl, final String jettyVersion, final String jspEnabled)
		throws CoreException
	{
		if ("5".equals(jettyVersion))
		{
			return findJettyLibs5(jettyUrl);
		}

		return findJettyLibs67(jettyUrl, jspEnabled);
	}

	/**
	 * Find the jetty libs for Jetty 6 and 7.
	 */
	private List<File> findJettyLibs67(final String jettyUrl, final String jspEnabled) throws CoreException
	{
		final File jettyLibDir = new File(jettyUrl, "lib");

		if (!jettyLibDir.exists() || !jettyLibDir.isDirectory())
		{
			throw new CoreException(new Status(IStatus.ERROR, JettyPlugin.PLUGIN_ID, "Could not find Jetty libs"));
		}

		final List<File> jettyLibs = new ArrayList<File>();

		jettyLibs.addAll(Arrays.asList(jettyLibDir.listFiles(new FilenameFilter()
		{
			public boolean accept(final File dir, final String name)
			{
				if ((name != null) && name.endsWith(".jar")
					&& (name.startsWith("jetty-") || name.startsWith("servlet-api")))
				{
					return true;
				}

				return false;
			}
		})));

		if ("2.0".equals(jspEnabled))
		{
			final File jettyLibJSPDir = new File(jettyLibDir, "jsp-2.0");

			if ((jettyLibJSPDir.exists()) && (jettyLibJSPDir.isDirectory()))
			{
				jettyLibs.addAll(Arrays.asList(jettyLibJSPDir.listFiles(JAR_FILTER)));
			}
			else
			{
				throw new CoreException(new Status(IStatus.ERROR, JettyPlugin.PLUGIN_ID, "Could not find JSP 2.0 libs"));
			}
		}
		else if (("true".equals(jspEnabled)) || ("2.1".equals(jspEnabled)))
		{
			File jettyLibJSPDir = new File(jettyLibDir, "jsp-2.1");
			if (!jettyLibJSPDir.exists())
			{
				jettyLibJSPDir = new File(jettyLibDir, "jsp");
			}

			if ((jettyLibJSPDir.exists()) && (jettyLibJSPDir.isDirectory()))
			{
				jettyLibs.addAll(Arrays.asList(jettyLibJSPDir.listFiles(JAR_FILTER)));
			}
			else
			{
				throw new CoreException(new Status(IStatus.ERROR, JettyPlugin.PLUGIN_ID, "Could not find JSP 2.1 libs"));
			}
		}

		return jettyLibs;
	}

	/**
	 * Find the jetty libs for Jetty 5.
	 */
	private List<File> findJettyLibs5(final String jettyUrl) throws CoreException
	{
		final File jettyLibDir = new File(jettyUrl, "lib");
		final File jettyExtDir = new File(jettyUrl, "ext");
		if (!jettyLibDir.exists() || !jettyLibDir.isDirectory() || !jettyExtDir.exists() || !jettyExtDir.isDirectory())
		{
			throw new CoreException(new Status(IStatus.ERROR, JettyPlugin.PLUGIN_ID, "Could not find Jetty libs"));
		}

		final List<File> jettyLibs = new ArrayList<File>();
		Collections.addAll(jettyLibs, jettyLibDir.listFiles(JAR_FILTER));
		Collections.addAll(jettyLibs, jettyExtDir.listFiles(JAR_FILTER));
		return jettyLibs;
	}

	private IRuntimeClasspathEntry[] filterWebInfLibs(final IRuntimeClasspathEntry[] defaults,
		final ILaunchConfiguration configuration)
	{

		final IJavaModel javaModel = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot());
		String projectName = null;
		String webAppDirName = null;
		try
		{
			projectName = configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "");
			webAppDirName = configuration.getAttribute(JettyPluginConstants.ATTR_WEBAPPDIR, "");
		}
		catch (final CoreException e)
		{
			JettyPlugin.logError(e);
		}

		if ((projectName == null) || projectName.trim().equals("") || (webAppDirName == null)
			|| webAppDirName.trim().equals(""))
		{
			return defaults;
		}

		final IJavaProject project = javaModel.getJavaProject(projectName);
		if (project == null)
		{
			return defaults;
		}

		// this should be fine since the plugin checks whether WEB-INF exists
		final IFolder webInfDir = project.getProject().getFolder(new Path(webAppDirName)).getFolder("WEB-INF");
		if ((webInfDir == null) || !webInfDir.exists())
		{
			return defaults;
		}
		final IFolder lib = webInfDir.getFolder("lib");
		if ((lib == null) || !lib.exists())
		{
			return defaults;
		}

		// ok, so we have a WEB-INF/lib dir, which means that we should filter
		// out the entries in there since if the user wants those entries, they
		// should be part of the project definition already
		final List<IRuntimeClasspathEntry> keep = new ArrayList<IRuntimeClasspathEntry>();
		for (final IRuntimeClasspathEntry default1 : defaults)
		{
			if (default1.getType() != IRuntimeClasspathEntry.ARCHIVE)
			{
				keep.add(default1);
				continue;
			}
			final IResource resource = default1.getResource();
			if ((resource != null) && !resource.getParent().equals(lib))
			{
				keep.add(default1);
				continue;
			}
		}

		return keep.toArray(new IRuntimeClasspathEntry[keep.size()]);
	}

	public static String detectJettyVersion(final String jettyUrl, final String jettyVersion)
	{
		if (!"auto".equals(jettyVersion))
		{
			return jettyVersion;
		}

		final File jettyPath = new File(jettyUrl);

		if (!jettyPath.exists())
		{
			throw new IllegalArgumentException("Invalid path: " + jettyPath.getAbsolutePath());
		}

		final String jettyFile = jettyPath.getName();

		if (jettyFile.contains("-5."))
		{
			return "5";
		}
		else if (jettyFile.contains("-6."))
		{
			return "6";
		}
		else if (jettyFile.contains("-7."))
		{
			return "7";
		}
		else
		{
			throw new IllegalArgumentException("Failed to detect Jetty version.");
		}
	}

	public static List<Pattern> extractPatterns(final List<Pattern> list, final String text)
		throws IllegalArgumentException
	{
		for (final String entry : text.split("[,\\n]"))
		{
			if (entry.trim().length() > 0)
			{
				try
				{
					list.add(Pattern.compile(entry));
				}
				catch (final PatternSyntaxException e)
				{
					throw new IllegalArgumentException("Invalid pattern: " + entry + " (" + e.getMessage() + ")", e);
				}
			}
		}

		return list;
	}

}
