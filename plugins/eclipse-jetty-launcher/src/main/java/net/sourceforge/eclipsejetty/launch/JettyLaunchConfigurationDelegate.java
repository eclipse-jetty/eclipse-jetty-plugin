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
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import net.sourceforge.eclipsejetty.JettyPlugin;
import net.sourceforge.eclipsejetty.JettyPluginConstants;
import net.sourceforge.eclipsejetty.JettyPluginUtils;
import net.sourceforge.eclipsejetty.jetty.AbstractServerConfiguration;
import net.sourceforge.eclipsejetty.jetty.JettyVersion;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaLaunchDelegate;
import org.eclipse.jdt.launching.JavaRuntime;

import static net.sourceforge.eclipsejetty.launch.JettyLaunchClasspathMatcher.*;

/**
 * Launch configuration delegate for Jetty. Based on {@link JavaLaunchDelegate}.
 * 
 * @author Christian K&ouml;berl
 * @author Manfred Hantschel
 */
public class JettyLaunchConfigurationDelegate extends JavaLaunchDelegate
{
    public static final String CONFIGURATION_KEY = "jetty.launcher.configuration";
    public static final String HIDE_LAUNCH_INFO_KEY = "jetty.launcher.hideLaunchInfo";

    public JettyLaunchConfigurationDelegate()
    {
        super();
    }

    @Override
    public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
        throws CoreException
    {
        super.launch(configuration, mode, launch, monitor);
    }

    @Override
    public String verifyMainTypeName(ILaunchConfiguration configuration) throws CoreException
    {
        return JettyPluginConstants.getMainTypeName(configuration);
    }

    @Override
    public String getVMArguments(ILaunchConfiguration configuration) throws CoreException
    {
        String[] webappClasspath = getWebappClasspath(configuration);
        final JettyVersion jettyVersion = JettyPluginConstants.getVersion(configuration);
        File file = createJettyConfigurationFile(configuration, jettyVersion, webappClasspath);
        String vmArguments = super.getVMArguments(configuration);

        vmArguments += " -D" + CONFIGURATION_KEY + "=" + file.getAbsolutePath();

        if (!JettyPluginConstants.isShowLauncherInfo(configuration))
        {
            vmArguments += " -D" + HIDE_LAUNCH_INFO_KEY;
        }

        return vmArguments;
    }

    @Override
    public String[] getClasspath(ILaunchConfiguration configuration) throws CoreException
    {
        return JettyPluginUtils.toLocationArray(getJettyClasspath(configuration, null));
    }

    public Collection<IRuntimeClasspathEntry> getCompleteWebappClasspathEntries(ILaunchConfiguration configuration)
        throws CoreException
    {
        IRuntimeClasspathEntry[] entries = JavaRuntime.computeUnresolvedRuntimeClasspath(configuration);

        entries = JavaRuntime.resolveRuntimeClasspath(entries, configuration);
        Collection<IRuntimeClasspathEntry> matchedEntries =
            userClasses().match(new LinkedHashSet<IRuntimeClasspathEntry>(Arrays.asList(entries)));

//        System.out.println("Classpath Entries");
//        System.out.println("=================");
//        for (IRuntimeClasspathEntry entry : matchedEntries)
//        {
//            if (entry.getLocation().contains("antlr-runtime"))
//            {
//                System.out.println(entry.getLocation());
//                System.out.println("\t Classpath Property: " + entry.getClasspathProperty());
//                System.out.println("\t Type: " + entry.getType());
//                System.out.println("\t Access Rules: " + Arrays.toString(entry.getClasspathEntry().getAccessRules()));
//                System.out.println("\t Inclusion Pattern: "
//                    + Arrays.toString(entry.getClasspathEntry().getInclusionPatterns()));
//                System.out.println("\t Exclusion Pattern: "
//                    + Arrays.toString(entry.getClasspathEntry().getExclusionPatterns()));
//                System.out.println("\t Exported: " + entry.getClasspathEntry().isExported());
//                System.out.println("\t Referencing Entry: " + entry.getClasspathEntry().getReferencingEntry());
//                System.out.println("\t Extra Attributes"
//                    + Arrays.toString(entry.getClasspathEntry().getExtraAttributes()));
//            }
//        }
//        System.out.println("----------------------------------------------------------------------");

        return matchedEntries;
    }

    public String[] getCompleteWebappClasspath(ILaunchConfiguration configuration) throws CoreException
    {
        return JettyPluginUtils.toLocationArray(getCompleteWebappClasspathEntries(configuration));
    }
    
    public Collection<IRuntimeClasspathEntry> getWebappClasspathEntries(ILaunchConfiguration configuration)
        throws CoreException
    {
        return getWebappClasspathEntries(configuration, getCompleteWebappClasspathEntries(configuration));
    }

    public String[] getWebappClasspath(ILaunchConfiguration configuration) throws CoreException
    {
        return getWebappClasspath(configuration, getWebappClasspathEntries(configuration));
    }

    public Collection<IRuntimeClasspathEntry> getWebappClasspathEntries(ILaunchConfiguration configuration, Collection<IRuntimeClasspathEntry> completeEntries)
        throws CoreException
    {
        return and(createWebappClasspathMatcher(configuration)).match(completeEntries);
    }

    public String[] getWebappClasspath(ILaunchConfiguration configuration, Collection<IRuntimeClasspathEntry> completeEntries) throws CoreException
    {
        return JettyPluginUtils.toLocationArray(getWebappClasspathEntries(configuration));
    }

    private static IRuntimeClasspathEntry[] getJettyClasspath(final ILaunchConfiguration configuration,
        final IRuntimeClasspathEntry[] existing) throws CoreException
    {
        final List<IRuntimeClasspathEntry> entries = new ArrayList<IRuntimeClasspathEntry>();

        if (existing != null)
        {
            entries.addAll(Arrays.asList(existing));
        }

        final String jettyPath = JettyPluginUtils.resolveVariables(JettyPluginConstants.getPath(configuration));
        final JettyVersion jettyVersion = JettyPluginConstants.getVersion(configuration);
        boolean jspSupport = JettyPluginConstants.isJspSupport(configuration);
        boolean jmxSupport = JettyPluginConstants.isJmxSupport(configuration);
        boolean jndiSupport = JettyPluginConstants.isJndiSupport(configuration);
        boolean ajpSupport = JettyPluginConstants.isAjpSupport(configuration);
        boolean annotationsSupport = JettyPluginConstants.isAnnotationsSupport(configuration);
        boolean plusSupport = JettyPluginConstants.isPlusSupport(configuration);
        boolean servletsSupport = JettyPluginConstants.isServletsSupport(configuration);

        try
        {
            entries.add(JavaRuntime.newArchiveRuntimeClasspathEntry(new Path(FileLocator.toFileURL(
                FileLocator.find(JettyPlugin.getDefault().getBundle(),
                    Path.fromOSString("lib/eclipse-jetty-starters-common.jar"), null)).getFile())));

            entries.add(JavaRuntime.newArchiveRuntimeClasspathEntry(new Path(FileLocator.toFileURL(
                FileLocator.find(JettyPlugin.getDefault().getBundle(), Path.fromOSString(jettyVersion.getJar()), null))
                .getFile())));

            for (final File jettyLib : jettyVersion.getLibStrategy().find(new File(jettyPath), jspSupport, jmxSupport,
                jndiSupport, ajpSupport, annotationsSupport, plusSupport, servletsSupport))
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

    private JettyLaunchClasspathMatcher createWebappClasspathMatcher(final ILaunchConfiguration configuration)
        throws CoreException
    {
        JettyLaunchClasspathMatcher vmClasspathMatcher = userClasses();
        String excludedLibs = JettyPluginConstants.getExcludedLibs(configuration);

        if ((excludedLibs != null) && (excludedLibs.trim().length() > 0))
        {
            vmClasspathMatcher = and(vmClasspathMatcher, notExcluded(excludedLibs.split("[,\\n\\r]")));
        }

        if (JettyPluginConstants.isScopeCompileExcluded(configuration))
        {
            vmClasspathMatcher = and(vmClasspathMatcher, not(withExtraAttribute("maven.scope", "compile")));
        }

        if (JettyPluginConstants.isScopeProvidedExcluded(configuration))
        {
            vmClasspathMatcher = and(vmClasspathMatcher, not(withExtraAttribute("maven.scope", "provided")));
        }

        if (JettyPluginConstants.isScopeRuntimeExcluded(configuration))
        {
            vmClasspathMatcher = and(vmClasspathMatcher, not(withExtraAttribute("maven.scope", "runtime")));
        }

        if (JettyPluginConstants.isScopeSystemExcluded(configuration))
        {
            vmClasspathMatcher = and(vmClasspathMatcher, not(withExtraAttribute("maven.scope", "system")));
        }

        if (JettyPluginConstants.isScopeTestExcluded(configuration))
        {
            vmClasspathMatcher =
                and(vmClasspathMatcher, not(withExtraAttribute("maven.scope", "test")), notExcluded(".*/test-classes"));
        }

        String includedLibs = JettyPluginConstants.getIncludedLibs(configuration);

        if ((includedLibs != null) && (includedLibs.trim().length() > 0))
        {
            vmClasspathMatcher = or(isIncluded(includedLibs.split("[,\\n\\r]")), vmClasspathMatcher);
        }

        return vmClasspathMatcher;
    }

    private File createJettyConfigurationFile(ILaunchConfiguration configuration, JettyVersion version,
        String[] classpath) throws CoreException
    {
        AbstractServerConfiguration serverConfiguration = version.createServerConfiguration();

        serverConfiguration.setDefaultContextPath(JettyPluginConstants.getContext(configuration));
        serverConfiguration.setDefaultWar(JettyPluginConstants.getWebAppDir(configuration));
        serverConfiguration.setPort(Integer.valueOf(JettyPluginConstants.getPort(configuration)));
        serverConfiguration.addDefaultClasspath(classpath);

        File file;

        try
        {
            file = File.createTempFile("jettyLauncherConfiguration", ".xml");

            serverConfiguration.write(file);
        }
        catch (IOException e)
        {
            throw new CoreException(new Status(IStatus.ERROR, JettyPlugin.PLUGIN_ID,
                "Failed to store tmp file with Jetty launch configuration"));
        }

        return file;
    }

}
