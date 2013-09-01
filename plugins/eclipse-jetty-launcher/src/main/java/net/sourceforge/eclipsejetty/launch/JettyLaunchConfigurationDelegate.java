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

import static net.sourceforge.eclipsejetty.util.DependencyMatcher.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.eclipsejetty.JettyPlugin;
import net.sourceforge.eclipsejetty.JettyPluginConstants;
import net.sourceforge.eclipsejetty.JettyPluginUtils;
import net.sourceforge.eclipsejetty.jetty.AbstractServerConfiguration;
import net.sourceforge.eclipsejetty.jetty.JettyConfig;
import net.sourceforge.eclipsejetty.jetty.JettyVersion;
import net.sourceforge.eclipsejetty.util.Dependency;
import net.sourceforge.eclipsejetty.util.DependencyMatcher;
import net.sourceforge.eclipsejetty.util.MavenDependencyInfoMap;
import net.sourceforge.eclipsejetty.util.MavenScope;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
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
        String[] webappClasspath =
            getLocalWebappClasspath(configuration,
                getWebappClasspathEntries(configuration, getOriginalClasspathEntries(configuration)));

        final JettyVersion jettyVersion = JettyPluginConstants.getVersion(configuration);
        File defaultFile = createJettyConfigurationFile(configuration, jettyVersion, webappClasspath);
        String vmArguments = super.getVMArguments(configuration);

        vmArguments += " -D" + CONFIGURATION_KEY + "=" + getConfigurationParameter(configuration, defaultFile);

        if (!JettyPluginConstants.isShowLauncherInfo(configuration))
        {
            vmArguments += " -D" + HIDE_LAUNCH_INFO_KEY;
        }

        return vmArguments;
    }

    private String getConfigurationParameter(ILaunchConfiguration configuration, File defaultFile) throws CoreException
    {
        StringBuilder configurationParam = new StringBuilder();
        List<JettyConfig> configs = JettyPluginConstants.getConfigs(configuration);

        for (JettyConfig config : configs)
        {
            if (config.isActive())
            {
                if (configurationParam.length() > 0)
                {
                    configurationParam.append(File.pathSeparator);
                }

                IFile file = config.getFile(ResourcesPlugin.getWorkspace());

                if (file != null)
                {
                    configurationParam.append(file.getLocation().toOSString());
                }
                else
                {
                    configurationParam.append(defaultFile.getAbsolutePath());
                }
            }
        }

        return configurationParam.toString();
    }

    @Override
    public String[] getClasspath(ILaunchConfiguration configuration) throws CoreException
    {
        return JettyPluginUtils.toLocationArray(getJettyClasspath(
            configuration,
            getGlobalWebappClasspathEntries(configuration,
                getWebappClasspathEntries(configuration, getOriginalClasspathEntries(configuration)))));
    }

    public Collection<Dependency> getOriginalClasspathEntries(ILaunchConfiguration configuration) throws CoreException
    {
        IRuntimeClasspathEntry[] entries = JavaRuntime.computeUnresolvedRuntimeClasspath(configuration);

        entries = JavaRuntime.resolveRuntimeClasspath(entries, configuration);

        MavenDependencyInfoMap mavenScopeCollection = new MavenDependencyInfoMap(configuration, entries);
        Set<Dependency> scopedClasspathEntries = new LinkedHashSet<Dependency>();

        for (IRuntimeClasspathEntry entry : entries)
        {
            scopedClasspathEntries.add(Dependency.create(mavenScopeCollection, entry));
        }

        Collection<Dependency> matchedEntries = userClasses().match(scopedClasspathEntries);

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

    public String[] getOriginalClasspath(ILaunchConfiguration configuration) throws CoreException
    {
        return JettyPluginUtils.toLocationArrayFromScoped(getOriginalClasspathEntries(configuration));
    }

    public Collection<Dependency> getWebappClasspathEntries(ILaunchConfiguration configuration,
        Collection<Dependency> originalEntries) throws CoreException
    {
        return and(createWebappClasspathMatcher(configuration)).match(new LinkedHashSet<Dependency>(originalEntries));
    }

    public String[] getWebappClasspath(ILaunchConfiguration configuration, Collection<Dependency> originalEntries)
        throws CoreException
    {
        return JettyPluginUtils.toLocationArrayFromScoped(getWebappClasspathEntries(configuration, originalEntries));
    }

    public Collection<Dependency> getLocalWebappClasspathEntries(ILaunchConfiguration configuration,
        Collection<Dependency> webappEntries) throws CoreException
    {
        if (JettyPluginConstants.isGenericIdsSupported(configuration))
        {
            Collection<String> globalGenericIds = JettyPluginConstants.getGlobalGenericIds(configuration);

            if ((globalGenericIds == null) || (globalGenericIds.size() <= 0))
            {
                return new LinkedHashSet<Dependency>(webappEntries);
            }

            return notExcludedGenericIds(globalGenericIds).match(new LinkedHashSet<Dependency>(webappEntries));
        }

        return deprecatedGetLocalWebappClasspathEntries(configuration, webappEntries);
    }

    @SuppressWarnings("deprecation")
    private Collection<Dependency> deprecatedGetLocalWebappClasspathEntries(ILaunchConfiguration configuration,
        Collection<Dependency> webappEntries) throws CoreException
    {
        String globalLibs = JettyPluginConstants.getGlobalLibs(configuration);

        if ((globalLibs == null) || (globalLibs.trim().length() <= 0))
        {
            return new LinkedHashSet<Dependency>(webappEntries);
        }

        return notExcludedRegEx(JettyPluginUtils.fromCommaSeparatedString(globalLibs)).match(
            new LinkedHashSet<Dependency>(webappEntries));
    }

    public String[] getLocalWebappClasspath(ILaunchConfiguration configuration, Collection<Dependency> webappEntries)
        throws CoreException
    {
        return JettyPluginUtils.toLocationArrayFromScoped(getLocalWebappClasspathEntries(configuration, webappEntries));
    }

    public Collection<Dependency> getGlobalWebappClasspathEntries(ILaunchConfiguration configuration,
        Collection<Dependency> webappEntries) throws CoreException
    {
        if (JettyPluginConstants.isGenericIdsSupported(configuration))
        {
            Collection<String> globalGenericIds = JettyPluginConstants.getGlobalGenericIds(configuration);

            if ((globalGenericIds == null) || (globalGenericIds.size() <= 0))
            {
                return Collections.<Dependency> emptyList();
            }

            return isIncludedGenericId(globalGenericIds).match(new LinkedHashSet<Dependency>(webappEntries));
        }

        return deprecatedGetGlobalWebappClasspathEntries(configuration, webappEntries);
    }

    @SuppressWarnings("deprecation")
    private Collection<Dependency> deprecatedGetGlobalWebappClasspathEntries(ILaunchConfiguration configuration,
        Collection<Dependency> webappEntries) throws CoreException
    {
        String globalLibs = JettyPluginConstants.getGlobalLibs(configuration);

        if ((globalLibs == null) || (globalLibs.trim().length() <= 0))
        {
            return Collections.<Dependency> emptyList();
        }

        return isIncludedRegEx(JettyPluginUtils.fromCommaSeparatedString(globalLibs)).match(
            new LinkedHashSet<Dependency>(webappEntries));
    }

    public String[] getGlobalWebappClasspath(ILaunchConfiguration configuration, Collection<Dependency> webappEntries)
        throws CoreException
    {
        return JettyPluginUtils
            .toLocationArrayFromScoped(getGlobalWebappClasspathEntries(configuration, webappEntries));
    }

    private static IRuntimeClasspathEntry[] getJettyClasspath(final ILaunchConfiguration configuration,
        final Collection<Dependency> collection) throws CoreException
    {
        final List<IRuntimeClasspathEntry> entries = new ArrayList<IRuntimeClasspathEntry>();

        if (collection != null)
        {
            for (Dependency entry : collection)
            {
                entries.add(entry.getRuntimeClasspathEntry());
            }
        }

        final String jettyPath = JettyPluginUtils.resolveVariables(JettyPluginConstants.getPath(configuration));
        final JettyVersion jettyVersion = JettyPluginConstants.getVersion(configuration);
        boolean jspSupport = JettyPluginConstants.isJspSupport(configuration);
        boolean jmxSupport = JettyPluginConstants.isJmxSupport(configuration);
        boolean jndiSupport = JettyPluginConstants.isJndiSupport(configuration);
        boolean ajpSupport = JettyPluginConstants.isAjpSupport(configuration);

        try
        {
            entries.add(JavaRuntime.newArchiveRuntimeClasspathEntry(new Path(FileLocator.toFileURL(
                FileLocator.find(JettyPlugin.getDefault().getBundle(),
                    Path.fromOSString("lib/eclipse-jetty-starters-common.jar"), null)).getFile())));

            entries.add(JavaRuntime.newArchiveRuntimeClasspathEntry(new Path(FileLocator.toFileURL(
                FileLocator.find(JettyPlugin.getDefault().getBundle(), Path.fromOSString(jettyVersion.getJar()), null))
                .getFile())));

            for (final File jettyLib : jettyVersion.getLibStrategy().find(new File(jettyPath), jspSupport, jmxSupport,
                jndiSupport, ajpSupport))
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

    private DependencyMatcher createWebappClasspathMatcher(final ILaunchConfiguration configuration)
        throws CoreException
    {
        DependencyMatcher vmClasspathMatcher = userClasses();

        if (JettyPluginConstants.isGenericIdsSupported(configuration))
        {
            Collection<String> excludedGenericIds = JettyPluginConstants.getExcludedGenericIds(configuration);

            if ((excludedGenericIds != null) && (excludedGenericIds.size() > 0))
            {
                vmClasspathMatcher = and(vmClasspathMatcher, notExcludedGenericIds(excludedGenericIds));
            }
        }
        else
        {
            vmClasspathMatcher = deprecatedCreateWebappClasspathMatcherExcludes(configuration, vmClasspathMatcher);
        }

        if (JettyPluginConstants.isScopeCompileExcluded(configuration))
        {
            vmClasspathMatcher = and(vmClasspathMatcher, not(withScope(MavenScope.COMPILE)));
        }

        if (JettyPluginConstants.isScopeProvidedExcluded(configuration))
        {
            vmClasspathMatcher = and(vmClasspathMatcher, not(withScope(MavenScope.PROVIDED)));
        }

        if (JettyPluginConstants.isScopeRuntimeExcluded(configuration))
        {
            vmClasspathMatcher = and(vmClasspathMatcher, not(withScope(MavenScope.RUNTIME)));
        }

        if (JettyPluginConstants.isScopeSystemExcluded(configuration))
        {
            vmClasspathMatcher = and(vmClasspathMatcher, not(withScope(MavenScope.SYSTEM)));
        }

        if (JettyPluginConstants.isScopeSystemExcluded(configuration))
        {
            vmClasspathMatcher = and(vmClasspathMatcher, not(withScope(MavenScope.IMPORT)));
        }

        if (JettyPluginConstants.isScopeTestExcluded(configuration))
        {
            vmClasspathMatcher = and(vmClasspathMatcher, not(withScope(MavenScope.TEST)));
        }

        if (JettyPluginConstants.isScopeNoneExcluded(configuration))
        {
            vmClasspathMatcher = and(vmClasspathMatcher, not(withScope(MavenScope.NONE)));
        }

        if (JettyPluginConstants.isGenericIdsSupported(configuration))
        {
            Collection<String> includedGenericIds = JettyPluginConstants.getIncludedGenericIds(configuration);

            if ((includedGenericIds != null) && (includedGenericIds.size() > 0))
            {
                vmClasspathMatcher = or(isIncludedGenericId(includedGenericIds), vmClasspathMatcher);
            }
        }
        else
        {
            vmClasspathMatcher = deprecatedCreateWebappClasspathMatcherIncludes(configuration, vmClasspathMatcher);
        }

        return vmClasspathMatcher;
    }

    @SuppressWarnings("deprecation")
    private DependencyMatcher deprecatedCreateWebappClasspathMatcherIncludes(final ILaunchConfiguration configuration,
        DependencyMatcher vmClasspathMatcher) throws CoreException
    {
        String includedLibs = JettyPluginConstants.getIncludedLibs(configuration);

        if ((includedLibs != null) && (includedLibs.trim().length() > 0))
        {
            vmClasspathMatcher =
                or(isIncludedRegEx(JettyPluginUtils.fromCommaSeparatedString(includedLibs)), vmClasspathMatcher);
        }

        return vmClasspathMatcher;
    }

    @SuppressWarnings("deprecation")
    private DependencyMatcher deprecatedCreateWebappClasspathMatcherExcludes(final ILaunchConfiguration configuration,
        DependencyMatcher vmClasspathMatcher) throws CoreException
    {
        String excludedLibs = JettyPluginConstants.getExcludedLibs(configuration);

        if ((excludedLibs != null) && (excludedLibs.trim().length() > 0))
        {
            vmClasspathMatcher =
                and(vmClasspathMatcher, notExcludedRegEx(JettyPluginUtils.fromCommaSeparatedString(excludedLibs)));
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
        serverConfiguration.setJndi(JettyPluginConstants.isJndiSupport(configuration));
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
