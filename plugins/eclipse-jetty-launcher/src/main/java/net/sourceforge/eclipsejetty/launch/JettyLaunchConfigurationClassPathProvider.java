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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.eclipsejetty.JettyPlugin;
import net.sourceforge.eclipsejetty.JettyPluginConstants;
import net.sourceforge.eclipsejetty.JettyPluginUtils;
import net.sourceforge.eclipsejetty.jetty.JettyVersion;
import net.sourceforge.eclipsejetty.jetty.JspSupport;
import net.sourceforge.eclipsejetty.jetty5.Jetty5LauncherMain;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IAccessRule;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.launching.RuntimeClasspathEntry;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.StandardClasspathProvider;

/**
 * ClasspathProvider for Jetty.
 * 
 * @author Christian K&ouml;berl
 * @author Manfred Hantschel
 */
public class JettyLaunchConfigurationClassPathProvider extends StandardClasspathProvider
{
    public final static IClasspathAttribute[] JETTY_EXTRA_ATTRIBUTES = {new JettyClasspathAttribute()};

    public final static IAccessRule[] NO_ACCESS_RULES = {};

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

        // add Jetty and bootstrap libs
        resolvedEntries = addJettyAndBootstrap(configuration, resolvedEntries);

        return resolvedEntries;
    }

    private static IRuntimeClasspathEntry[] addJettyAndBootstrap(final ILaunchConfiguration configuration,
        final IRuntimeClasspathEntry[] existing) throws CoreException
    {
        final List<IRuntimeClasspathEntry> entries = new ArrayList<IRuntimeClasspathEntry>();

        entries.addAll(Arrays.asList(existing));

        final String jettyPath = JettyPluginUtils.resolveVariables(JettyPluginConstants.getPath(configuration));
        final JettyVersion jettyVersion;

        try
        {
            jettyVersion =
                JettyPluginUtils.detectJettyVersion(jettyPath, JettyPluginConstants.getVersion(configuration));
        }
        catch (IllegalArgumentException e)
        {
            throw new CoreException(new Status(IStatus.ERROR, JettyPlugin.PLUGIN_ID, e.getMessage()));
        }

        final JspSupport jspSupport = JettyPluginConstants.getJspSupport(configuration);

        try
        {
            entries.add(JavaRuntime.newArchiveRuntimeClasspathEntry(new Path(FileLocator.toFileURL(
                Jetty5LauncherMain.class.getResource("/")).getFile())));

            for (final File jettyLib : findJettyLibs(jettyPath, jettyVersion, jspSupport))
            {
                Path path = new Path(jettyLib.getCanonicalPath());
                IClasspathEntry entry =
                    JavaCore.newLibraryEntry(path, null, null, NO_ACCESS_RULES, JETTY_EXTRA_ATTRIBUTES, false);

                IRuntimeClasspathEntry runtimeEntry = new RuntimeClasspathEntry(entry);

                entries.add(runtimeEntry);
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
    private static Iterable<File> findJettyLibs(final String jettyPath, final JettyVersion jettyVersion,
        final JspSupport jspSupport) throws CoreException
    {
        return jettyVersion.getLibStrategy().find(new File(jettyPath), jspSupport);
    }

    private IRuntimeClasspathEntry[] filterWebInfLibs(final IRuntimeClasspathEntry[] defaults,
        final ILaunchConfiguration configuration)
    {

        final IJavaModel javaModel = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot());
        String projectName = null;
        String webAppDirName = null;
        try
        {
            projectName = JettyPluginConstants.getProject(configuration);
            webAppDirName = JettyPluginConstants.getWebAppDir(configuration);
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

}
