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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
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
    public static final String DISABLE_CONSOLE_KEY = "jetty.launcher.disableConsole";

    private static final long DEFAULT_LIFESPAN = 5 * 1000; // 10 seconds

    private class CacheEntry
    {
        private final ILaunchConfiguration configuration;
        private final Object object;

        private long endOfLife;

        public CacheEntry(ILaunchConfiguration configuration, Object object) throws CoreException
        {
            super();

            this.configuration = configuration.copy(configuration.getName());
            this.object = object;

            extendLife();
        }

        public ILaunchConfiguration getConfiguration()
        {
            return configuration;
        }

        public Object getObject()
        {
            return object;
        }

        public void extendLife()
        {
            endOfLife = System.currentTimeMillis() + DEFAULT_LIFESPAN;
        }

        public boolean isAlive()
        {
            return System.currentTimeMillis() < endOfLife;
        }
    }

    private final Map<String, CacheEntry> cache = new HashMap<String, CacheEntry>();

    public JettyLaunchConfigurationDelegate()
    {
        super();
    }

    protected Object getCached(String key, ILaunchConfiguration configuration)
    {
        CacheEntry cacheEntry = cache.get(key);

        if (cacheEntry == null)
        {
            // System.err.println("Fail! " + key);
            return null;
        }

        if ((cacheEntry.isAlive()) && (configuration.contentsEqual(cacheEntry.getConfiguration())))
        {
            // System.out.println("Hit! " + key);
            cacheEntry.extendLife();

            return cacheEntry.getObject();
        }

        // System.err.println("Don't want! " + key);
        cache.remove(key);

        return null;
    }

    protected void putCached(String key, ILaunchConfiguration configuration, Object object) throws CoreException
    {
        cache.put(key, new CacheEntry(configuration, object));
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
        File defaultFile = createJettyConfigurationFile(configuration, false);
        String vmArguments = super.getVMArguments(configuration);

        vmArguments += " -D" + CONFIGURATION_KEY + "=" + getConfigurationParameter(configuration, defaultFile);

        if (!JettyPluginConstants.isShowLauncherInfo(configuration))
        {
            vmArguments += " -D" + HIDE_LAUNCH_INFO_KEY;
        }

        if (!JettyPluginConstants.isConsoleEnabled(configuration))
        {
            vmArguments += " -D" + DISABLE_CONSOLE_KEY;
        }

        if (JettyPluginConstants.isJmxSupport(configuration))
        {
            vmArguments += " -Dcom.sun.management.jmxremote";
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

                switch (config.getType())
                {
                    case DEFAULT:
                        configurationParam.append(defaultFile.getAbsolutePath());
                        break;

                    case PATH:
                        configurationParam.append(new File(config.getPath()).getAbsolutePath());
                        break;

                    case WORKSPACE:
                        configurationParam.append(ResourcesPlugin.getWorkspace().getRoot()
                            .getFile(new Path(config.getPath())).getLocation().toOSString());
                        break;
                }
            }
        }

        return configurationParam.toString();
    }

    @Override
    public String[] getClasspath(ILaunchConfiguration configuration) throws CoreException
    {
        String[] result = (String[]) getCached("Classpath", configuration);

        if (result != null)
        {
            return result;
        }

        result =
            JettyPluginUtils.toLocationArray(getJettyClasspath(
                configuration,
                getGlobalWebappClasspathEntries(configuration,
                    getWebappClasspathEntries(configuration, getOriginalClasspathEntries(configuration)))));

        putCached("Classpath", configuration, result);

        return result;
    }

    public Collection<Dependency> getOriginalClasspathEntries(ILaunchConfiguration configuration) throws CoreException
    {
        @SuppressWarnings("unchecked")
        Collection<Dependency> result = (Collection<Dependency>) getCached("OriginalClasspathEntries", configuration);

        if (result != null)
        {
            return result;
        }

        IRuntimeClasspathEntry[] entries = JavaRuntime.computeUnresolvedRuntimeClasspath(configuration);

        entries = JavaRuntime.resolveRuntimeClasspath(entries, configuration);

        MavenDependencyInfoMap mavenScopeCollection = new MavenDependencyInfoMap(configuration, entries);
        Set<Dependency> scopedClasspathEntries = new LinkedHashSet<Dependency>();

        for (IRuntimeClasspathEntry entry : entries)
        {
            scopedClasspathEntries.add(Dependency.create(mavenScopeCollection, entry));
        }

        result = Collections.unmodifiableCollection(userClasses().match(scopedClasspathEntries));

        //        System.out.println("Classpath Entries");
        //        System.out.println("=================");
        //        for (IRuntimeClasspathEntry entry : result)
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

        putCached("OriginalClasspathEntries", configuration, result);

        return result;
    }

    public String[] getOriginalClasspath(ILaunchConfiguration configuration) throws CoreException
    {
        String[] result = (String[]) getCached("OriginalClasspath", configuration);

        if (result != null)
        {
            return result;
        }

        result = JettyPluginUtils.toLocationArrayFromScoped(getOriginalClasspathEntries(configuration));

        putCached("OriginalClasspath", configuration, result);

        return result;
    }

    public Collection<Dependency> getWebappClasspathEntries(ILaunchConfiguration configuration,
        Collection<Dependency> originalEntries) throws CoreException
    {
        @SuppressWarnings("unchecked")
        Collection<Dependency> result = (Collection<Dependency>) getCached("WebappClasspathEntries", configuration);

        if (result != null)
        {
            return result;
        }

        result =
            Collections.unmodifiableCollection(and(createWebappClasspathMatcher(configuration)).match(
                new LinkedHashSet<Dependency>(originalEntries)));

        putCached("WebappClasspathEntries", configuration, result);

        return result;
    }

    public String[] getWebappClasspath(ILaunchConfiguration configuration, Collection<Dependency> originalEntries)
        throws CoreException
    {
        String[] result = (String[]) getCached("WebappClasspath", configuration);

        if (result != null)
        {
            return result;
        }

        result = JettyPluginUtils.toLocationArrayFromScoped(getWebappClasspathEntries(configuration, originalEntries));

        putCached("WebappClasspath", configuration, result);

        return result;
    }

    public Collection<Dependency> getLocalWebappClasspathEntries(ILaunchConfiguration configuration,
        Collection<Dependency> webappEntries) throws CoreException
    {
        @SuppressWarnings("unchecked")
        Collection<Dependency> result =
            (Collection<Dependency>) getCached("LocalWebappClasspathEntries", configuration);

        if (result != null)
        {
            return result;
        }

        if (JettyPluginConstants.isGenericIdsSupported(configuration))
        {
            Collection<String> globalGenericIds = JettyPluginConstants.getGlobalGenericIds(configuration);

            if ((globalGenericIds == null) || (globalGenericIds.size() <= 0))
            {
                result = Collections.unmodifiableCollection(new LinkedHashSet<Dependency>(webappEntries));
            }
            else
            {
                result =
                    Collections.unmodifiableCollection(notExcludedGenericIds(globalGenericIds).match(
                        new LinkedHashSet<Dependency>(webappEntries)));
            }
        }
        else
        {
            result =
                Collections.unmodifiableCollection(deprecatedGetLocalWebappClasspathEntries(configuration,
                    webappEntries));
        }

        putCached("LocalWebappClasspathEntries", configuration, result);

        return result;
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
        String[] result = (String[]) getCached("LocalWebappClasspath", configuration);

        if (result != null)
        {
            return result;
        }

        result =
            JettyPluginUtils.toLocationArrayFromScoped(getLocalWebappClasspathEntries(configuration, webappEntries));

        putCached("LocalWebappClasspath", configuration, result);

        return result;
    }

    public Collection<Dependency> getGlobalWebappClasspathEntries(ILaunchConfiguration configuration,
        Collection<Dependency> webappEntries) throws CoreException
    {
        @SuppressWarnings("unchecked")
        Collection<Dependency> result =
            (Collection<Dependency>) getCached("GlobalWebappClasspathEntries", configuration);

        if (result != null)
        {
            return result;
        }

        if (JettyPluginConstants.isGenericIdsSupported(configuration))
        {
            Collection<String> globalGenericIds = JettyPluginConstants.getGlobalGenericIds(configuration);

            if ((globalGenericIds == null) || (globalGenericIds.size() <= 0))
            {
                result = Collections.<Dependency> emptyList();
            }
            else
            {
                result =
                    Collections.unmodifiableCollection(isIncludedGenericId(globalGenericIds).match(
                        new LinkedHashSet<Dependency>(webappEntries)));
            }
        }
        else
        {
            result =
                Collections.unmodifiableCollection(deprecatedGetGlobalWebappClasspathEntries(configuration,
                    webappEntries));
        }

        putCached("GlobalWebappClasspathEntries", configuration, result);

        return result;
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
        String[] result = (String[]) getCached("GlobalWebappClasspath", configuration);

        if (result != null)
        {
            return result;
        }

        result =
            JettyPluginUtils.toLocationArrayFromScoped(getGlobalWebappClasspathEntries(configuration, webappEntries));

        putCached("GlobalWebappClasspath", configuration, result);

        return result;
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
        boolean consoleEnabled = JettyPluginConstants.isConsoleEnabled(configuration);

        try
        {
            entries.add(JavaRuntime.newArchiveRuntimeClasspathEntry(new Path(FileLocator.toFileURL(
                FileLocator.find(JettyPlugin.getDefault().getBundle(),
                    Path.fromOSString("lib/eclipse-jetty-starters-common.jar"), null)).getFile())));

            entries.add(JavaRuntime.newArchiveRuntimeClasspathEntry(new Path(FileLocator.toFileURL(
                FileLocator.find(JettyPlugin.getDefault().getBundle(),
                    Path.fromOSString("lib/eclipse-jetty-starters-util.jar"), null)).getFile())));

            if (consoleEnabled)
            {
                entries.add(JavaRuntime.newArchiveRuntimeClasspathEntry(new Path(FileLocator.toFileURL(
                    FileLocator.find(JettyPlugin.getDefault().getBundle(),
                        Path.fromOSString("lib/eclipse-jetty-starters-console.jar"), null)).getFile())));
            }

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
            JettyPlugin.error("Failed to detect jetty classpath", e);
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

    public File createJettyConfigurationFile(ILaunchConfiguration configuration, boolean formatted)
        throws CoreException
    {
        String[] webappClasspath =
            getLocalWebappClasspath(configuration,
                getWebappClasspathEntries(configuration, getOriginalClasspathEntries(configuration)));
        JettyVersion jettyVersion = JettyPluginConstants.getVersion(configuration);

        return createJettyConfigurationFile(configuration, jettyVersion, formatted, webappClasspath);
    }

    private File createJettyConfigurationFile(ILaunchConfiguration configuration, JettyVersion version,
        boolean formatted, String[] classpath) throws CoreException
    {
        AbstractServerConfiguration serverConfiguration = version.createServerConfiguration();

        serverConfiguration.setDefaultContextPath(JettyPluginConstants.getContext(configuration));
        serverConfiguration.setDefaultWar(JettyPluginConstants.getWebAppDir(configuration));
        serverConfiguration.setPort(Integer.valueOf(JettyPluginConstants.getPort(configuration)));

        if (JettyPluginConstants.isHttpsEnabled(configuration))
        {
            serverConfiguration.setSslPort(Integer.valueOf(JettyPluginConstants.getHttpsPort(configuration)));

            File defaultKeystoreFile = JettyPlugin.getDefaultKeystoreFile();

            if ((defaultKeystoreFile == null) || (!defaultKeystoreFile.exists()) || (!defaultKeystoreFile.canRead()))
            {
                defaultKeystoreFile = createDefaultKeystoreFile(defaultKeystoreFile);
            }

            serverConfiguration.setKeyStorePath(defaultKeystoreFile.getAbsolutePath());
            serverConfiguration.setKeyStorePassword("correct horse battery staple");
            serverConfiguration.setKeyManagerPassword("correct horse battery staple");
        }

        serverConfiguration.setJndi(JettyPluginConstants.isJndiSupport(configuration));
        serverConfiguration.setJmx(JettyPluginConstants.isJmxSupport(configuration));

        if (JettyPluginConstants.isConnectionLimitEnabled(configuration))
        {
            serverConfiguration.setConnectionLimit(JettyPluginConstants.getConnectionLimitCount(configuration));
        }

        serverConfiguration.addDefaultClasspath(classpath);

        File file;

        try
        {
            file = JettyPluginUtils.getNonRandomTempFile("eclipseJettyPlugin.", configuration.getName().trim(), ".xml");

            serverConfiguration.write(file, formatted);

            file.deleteOnExit();
        }
        catch (IOException e)
        {
            throw new CoreException(new Status(IStatus.ERROR, JettyPlugin.PLUGIN_ID,
                "Failed to store tmp file with Jetty launch configuration"));
        }

        return file;
    }

    private File createDefaultKeystoreFile(File defaultKeystoreFile) throws CoreException
    {
        try
        {
            defaultKeystoreFile = File.createTempFile("eclipseJettyPlugin.", ".keystore");

            InputStream in = getClass().getResourceAsStream("eclipseJettyPlugin.keystore");

            try
            {
                OutputStream out = new FileOutputStream(defaultKeystoreFile);

                try
                {
                    JettyPluginUtils.copy(in, out);
                }
                finally
                {
                    out.close();
                }
            }
            finally
            {
                in.close();
            }

            defaultKeystoreFile.deleteOnExit();

            JettyPlugin.setDefaultKeystoreFile(defaultKeystoreFile);
        }
        catch (IOException e)
        {
            throw new CoreException(new Status(IStatus.ERROR, JettyPlugin.PLUGIN_ID,
                "Failed to store tmp file with keystore"));
        }
        return defaultKeystoreFile;
    }
}
