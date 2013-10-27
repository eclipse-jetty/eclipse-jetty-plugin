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
package net.sourceforge.eclipsejetty.launch.util;

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
import net.sourceforge.eclipsejetty.JettyPluginUtils;
import net.sourceforge.eclipsejetty.Messages;
import net.sourceforge.eclipsejetty.jetty.AbstractServerConfiguration;
import net.sourceforge.eclipsejetty.jetty.AbstractWebDefaults;
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
    public static final String CONFIGURATION_KEY = "jetty.launcher.configuration"; //$NON-NLS-1$
    public static final String HIDE_LAUNCH_INFO_KEY = "jetty.launcher.hideLaunchInfo"; //$NON-NLS-1$
    public static final String DISABLE_CONSOLE_KEY = "jetty.launcher.disableConsole"; //$NON-NLS-1$

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

    /**
     * Tries to find the entry in the cache Returns it if the configuration still matches the entry and the end of life
     * has not yet been reached.
     * 
     * @param key the key
     * @param adapter the configuration adapter
     * @return the entry, null if not found
     */
    protected Object getCached(String key, JettyLaunchConfigurationAdapter adapter)
    {
        return getCached(key, adapter.getConfiguration());
    }

    /**
     * Tries to find the entry in the cache. Returns it if the configuration still matches the entry and the end of life
     * has not yet been reached.
     * 
     * @param key the key
     * @param configuration the configuration
     * @return the object
     */
    protected Object getCached(String key, ILaunchConfiguration configuration)
    {
        CacheEntry cacheEntry = cache.get(key);

        if (cacheEntry == null)
        {
            return null;
        }

        if ((cacheEntry.isAlive()) && (configuration.contentsEqual(cacheEntry.getConfiguration())))
        {
            cacheEntry.extendLife();

            return cacheEntry.getObject();
        }

        cache.remove(key);

        return null;
    }

    /**
     * Puts the object into the cache.
     * 
     * @param key the key
     * @param adapter the adapter
     * @param object the object
     * @throws CoreException on occasion
     */
    protected void putCached(String key, JettyLaunchConfigurationAdapter adapter, Object object) throws CoreException
    {
        putCached(key, adapter.getConfiguration(), object);
    }

    /**
     * Puts the object into the cache.
     * 
     * @param key the key
     * @param configuration the configuration
     * @param object the object
     * @throws CoreException on occasion
     */
    protected void putCached(String key, ILaunchConfiguration configuration, Object object) throws CoreException
    {
        cache.put(key, new CacheEntry(configuration, object));
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jdt.launching.JavaLaunchDelegate#launch(org.eclipse.debug.core.ILaunchConfiguration,
     *      java.lang.String, org.eclipse.debug.core.ILaunch, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
        throws CoreException
    {
        super.launch(configuration, mode, launch, monitor);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate#verifyMainTypeName(org.eclipse.debug.core.ILaunchConfiguration)
     */
    @Override
    public String verifyMainTypeName(ILaunchConfiguration configuration) throws CoreException
    {
        JettyLaunchConfigurationAdapter adapter = JettyLaunchConfigurationAdapter.getInstance(configuration);

        return adapter.getMainTypeName();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate#getVMArguments(org.eclipse.debug.core.ILaunchConfiguration)
     */
    @Override
    public String getVMArguments(ILaunchConfiguration configuration) throws CoreException
    {
        JettyLaunchConfigurationAdapter adapter = JettyLaunchConfigurationAdapter.getInstance(configuration);
        File defaultFile = createJettyConfigurationFile(adapter, false);
        String vmArguments = super.getVMArguments(configuration);

        vmArguments += String.format(" -D%s=%s", CONFIGURATION_KEY, getConfigurationParameter(adapter, defaultFile)); //$NON-NLS-1$

        if (!adapter.isShowLauncherInfo())
        {
            vmArguments += String.format(" -D%s", HIDE_LAUNCH_INFO_KEY); //$NON-NLS-1$
        }

        if (!adapter.isConsoleEnabled())
        {
            vmArguments += String.format(" -D%s", DISABLE_CONSOLE_KEY); //$NON-NLS-1$
        }

        if (adapter.isJmxSupport())
        {
            vmArguments += " -Dcom.sun.management.jmxremote"; //$NON-NLS-1$
        }

        return vmArguments;
    }

    /**
     * Lists all Jetty configuration XML files for the command line, when starting Jetty
     * 
     * @param adapter the configuration adapter
     * @param defaultFile the default file containing all the main stuff (was generated by the
     *            {@link AbstractServerConfiguration})
     * @return a File.pathSeparator separated list
     * @throws CoreException on occasion
     */
    private String getConfigurationParameter(JettyLaunchConfigurationAdapter adapter, File defaultFile)
        throws CoreException
    {
        StringBuilder configurationParam = new StringBuilder();
        List<JettyConfig> configs = adapter.getConfigs();

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

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate#getClasspath(org.eclipse.debug.core.ILaunchConfiguration)
     */
    @Override
    public String[] getClasspath(ILaunchConfiguration configuration) throws CoreException
    {
        String[] result = (String[]) getCached("Classpath", configuration); //$NON-NLS-1$

        if (result != null)
        {
            return result;
        }

        JettyLaunchConfigurationAdapter adapter = JettyLaunchConfigurationAdapter.getInstance(configuration);

        result =
            JettyPluginUtils.toLocationArray(getJettyClasspath(
                adapter,
                getGlobalWebappClasspathEntries(adapter,
                    getWebappClasspathEntries(adapter, getOriginalClasspathEntries(adapter)))));

        putCached("Classpath", configuration, result); //$NON-NLS-1$

        return result;
    }

    /**
     * Returns a collection with all dependencies of the Java project itself.
     * 
     * @param adapter the confiugration adapter
     * @return a collection of dependencies
     * @throws CoreException on occasion
     */
    public Collection<Dependency> getOriginalClasspathEntries(JettyLaunchConfigurationAdapter adapter)
        throws CoreException
    {
        @SuppressWarnings("unchecked")
        Collection<Dependency> result = (Collection<Dependency>) getCached("OriginalClasspathEntries", adapter); //$NON-NLS-1$

        if (result != null)
        {
            return result;
        }

        IRuntimeClasspathEntry[] entries = JavaRuntime.computeUnresolvedRuntimeClasspath(adapter.getConfiguration());

        entries = JavaRuntime.resolveRuntimeClasspath(entries, adapter.getConfiguration());

        MavenDependencyInfoMap mavenScopeCollection = new MavenDependencyInfoMap(adapter, entries);
        Set<Dependency> scopedClasspathEntries = new LinkedHashSet<Dependency>();

        for (IRuntimeClasspathEntry entry : entries)
        {
            scopedClasspathEntries.add(Dependency.create(mavenScopeCollection, entry));
        }

        result = Collections.unmodifiableCollection(userClasses().match(scopedClasspathEntries));

        putCached("OriginalClasspathEntries", adapter, result); //$NON-NLS-1$

        return result;
    }

    /**
     * Returns an array with all dependency locations of the Java project itself.
     * 
     * @param adapter the configuration adapter
     * @return an array of strings
     * @throws CoreException
     */
    public String[] getOriginalClasspath(JettyLaunchConfigurationAdapter adapter) throws CoreException
    {
        String[] result = (String[]) getCached("OriginalClasspath", adapter); //$NON-NLS-1$

        if (result != null)
        {
            return result;
        }

        result = JettyPluginUtils.toLocationArrayFromScoped(getOriginalClasspathEntries(adapter));

        putCached("OriginalClasspath", adapter, result); //$NON-NLS-1$

        return result;
    }

    /**
     * Returns a collection of all dependencies of the Java project itself, minus the excluded ones.
     * 
     * @param adapter the adapter
     * @param originalEntries the original entries as determined by the
     *            {@link #getOriginalClasspathEntries(JettyLaunchConfigurationAdapter)} method
     * @return a collection of dependencies
     * @throws CoreException on occasion
     */
    public Collection<Dependency> getWebappClasspathEntries(JettyLaunchConfigurationAdapter adapter,
        Collection<Dependency> originalEntries) throws CoreException
    {
        @SuppressWarnings("unchecked")
        Collection<Dependency> result = (Collection<Dependency>) getCached("WebappClasspathEntries", adapter); //$NON-NLS-1$

        if (result != null)
        {
            return result;
        }

        result =
            Collections.unmodifiableCollection(and(createWebappClasspathMatcher(adapter)).match(
                new LinkedHashSet<Dependency>(originalEntries)));

        putCached("WebappClasspathEntries", adapter, result); //$NON-NLS-1$

        return result;
    }

    /**
     * Returns an array of all dependencies as location strings of the Java project itself, minus the excluded ones.
     * 
     * @param adapter the confiugration adapter
     * @param originalEntries the original entries as determined by the
     *            {@link #getOriginalClasspathEntries(JettyLaunchConfigurationAdapter)} method
     * @return an array of strings
     * @throws CoreException on occasion
     */
    public String[] getWebappClasspath(JettyLaunchConfigurationAdapter adapter, Collection<Dependency> originalEntries)
        throws CoreException
    {
        String[] result = (String[]) getCached("WebappClasspath", adapter); //$NON-NLS-1$

        if (result != null)
        {
            return result;
        }

        result = JettyPluginUtils.toLocationArrayFromScoped(getWebappClasspathEntries(adapter, originalEntries));

        putCached("WebappClasspath", adapter, result); //$NON-NLS-1$

        return result;
    }

    /**
     * Returns a collection of all dependencies of the Java project itself, minus the excluded and minus the global
     * ones.
     * 
     * @param adapter the configuration adapter
     * @param webappEntries the dependencies as collected from
     *            {@link #getWebappClasspathEntries(JettyLaunchConfigurationAdapter, Collection)} method
     * @return a collection of dependencies
     * @throws CoreException on occasion
     */
    public Collection<Dependency> getLocalWebappClasspathEntries(JettyLaunchConfigurationAdapter adapter,
        Collection<Dependency> webappEntries) throws CoreException
    {
        @SuppressWarnings("unchecked")
        Collection<Dependency> result = (Collection<Dependency>) getCached("LocalWebappClasspathEntries", adapter); //$NON-NLS-1$

        if (result != null)
        {
            return result;
        }

        if (adapter.isGenericIdsSupported())
        {
            Collection<String> globalGenericIds = adapter.getGlobalGenericIds();

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
                Collections.unmodifiableCollection(deprecatedGetLocalWebappClasspathEntries(adapter, webappEntries));
        }

        putCached("LocalWebappClasspathEntries", adapter, result); //$NON-NLS-1$

        return result;
    }

    @SuppressWarnings("deprecation")
    private Collection<Dependency> deprecatedGetLocalWebappClasspathEntries(JettyLaunchConfigurationAdapter adapter,
        Collection<Dependency> webappEntries) throws CoreException
    {
        String globalLibs = adapter.getGlobalLibs();

        if ((globalLibs == null) || (globalLibs.trim().length() <= 0))
        {
            return new LinkedHashSet<Dependency>(webappEntries);
        }

        return notExcludedRegEx(JettyPluginUtils.fromCommaSeparatedString(globalLibs)).match(
            new LinkedHashSet<Dependency>(webappEntries));
    }

    /**
     * Returns an array of all dependencies as location strings of the Java project itself, minus the excluded and minus
     * the global ones.
     * 
     * @param adapter the configuration adapter
     * @param webappEntries the dependencies as collected from
     *            {@link #getWebappClasspathEntries(JettyLaunchConfigurationAdapter, Collection)} method
     * @return an array of strings
     * @throws CoreException
     */
    public String[] getLocalWebappClasspath(JettyLaunchConfigurationAdapter adapter,
        Collection<Dependency> webappEntries) throws CoreException
    {
        String[] result = (String[]) getCached("LocalWebappClasspath", adapter); //$NON-NLS-1$

        if (result != null)
        {
            return result;
        }

        result = JettyPluginUtils.toLocationArrayFromScoped(getLocalWebappClasspathEntries(adapter, webappEntries));

        putCached("LocalWebappClasspath", adapter, result); //$NON-NLS-1$

        return result;
    }

    /**
     * Returns a collection of all dependencies of the Java project itself, minus the excluded and just the global ones.
     * 
     * @param adapter the configuration adapter
     * @param webappEntries the dependencies as collected from
     *            {@link #getWebappClasspathEntries(JettyLaunchConfigurationAdapter, Collection)} method
     * @return a collection of dependencies
     * @throws CoreException on occasion
     */
    public Collection<Dependency> getGlobalWebappClasspathEntries(JettyLaunchConfigurationAdapter adapter,
        Collection<Dependency> webappEntries) throws CoreException
    {
        @SuppressWarnings("unchecked")
        Collection<Dependency> result = (Collection<Dependency>) getCached("GlobalWebappClasspathEntries", adapter); //$NON-NLS-1$

        if (result != null)
        {
            return result;
        }

        if (adapter.isGenericIdsSupported())
        {
            Collection<String> globalGenericIds = adapter.getGlobalGenericIds();

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
                Collections.unmodifiableCollection(deprecatedGetGlobalWebappClasspathEntries(adapter, webappEntries));
        }

        putCached("GlobalWebappClasspathEntries", adapter, result); //$NON-NLS-1$

        return result;
    }

    @SuppressWarnings("deprecation")
    private Collection<Dependency> deprecatedGetGlobalWebappClasspathEntries(JettyLaunchConfigurationAdapter adapter,
        Collection<Dependency> webappEntries) throws CoreException
    {
        String globalLibs = adapter.getGlobalLibs();

        if ((globalLibs == null) || (globalLibs.trim().length() <= 0))
        {
            return Collections.<Dependency> emptyList();
        }

        return isIncludedRegEx(JettyPluginUtils.fromCommaSeparatedString(globalLibs)).match(
            new LinkedHashSet<Dependency>(webappEntries));
    }

    /**
     * Returns an array of all dependencies as location strings of the Java project itself, minus the excluded and just
     * the global ones.
     * 
     * @param adapter the configuration adapter
     * @param webappEntries the dependencies as collected from
     *            {@link #getWebappClasspathEntries(JettyLaunchConfigurationAdapter, Collection)} method
     * @return an array of strings
     * @throws CoreException
     */
    public String[] getGlobalWebappClasspath(JettyLaunchConfigurationAdapter adapter,
        Collection<Dependency> webappEntries) throws CoreException
    {
        String[] result = (String[]) getCached("GlobalWebappClasspath", adapter); //$NON-NLS-1$

        if (result != null)
        {
            return result;
        }

        result = JettyPluginUtils.toLocationArrayFromScoped(getGlobalWebappClasspathEntries(adapter, webappEntries));

        putCached("GlobalWebappClasspath", adapter, result); //$NON-NLS-1$

        return result;
    }

    /**
     * Returns the Jetty classpath, the start classpath and the global dependencies.
     * 
     * @param adapter the collection adapter
     * @param collection a list of global and other non webapp dependencies
     * @return an array of classpath entries
     * @throws CoreException on occasion
     */
    private static IRuntimeClasspathEntry[] getJettyClasspath(JettyLaunchConfigurationAdapter adapter,
        Collection<Dependency> collection) throws CoreException
    {
        final List<IRuntimeClasspathEntry> entries = new ArrayList<IRuntimeClasspathEntry>();

        if (collection != null)
        {
            for (Dependency entry : collection)
            {
                entries.add(entry.getRuntimeClasspathEntry());
            }
        }

        final File jettyPath = adapter.getPath();
        final JettyVersion jettyVersion = adapter.getVersion();
        boolean jspSupport = adapter.isJspSupport();
        boolean jmxSupport = adapter.isJmxSupport();
        boolean jndiSupport = adapter.isJndiSupport();
        boolean ajpSupport = adapter.isAjpSupport();
        boolean consoleEnabled = adapter.isConsoleEnabled();

        try
        {
            entries.add(JavaRuntime.newArchiveRuntimeClasspathEntry(new Path(FileLocator.toFileURL(
                FileLocator.find(JettyPlugin.getDefault().getBundle(),
                    Path.fromOSString("lib/eclipse-jetty-starters-common.jar"), null)).getFile()))); //$NON-NLS-1$

            entries.add(JavaRuntime.newArchiveRuntimeClasspathEntry(new Path(FileLocator.toFileURL(
                FileLocator.find(JettyPlugin.getDefault().getBundle(),
                    Path.fromOSString("lib/eclipse-jetty-starters-util.jar"), null)).getFile()))); //$NON-NLS-1$

            if (consoleEnabled)
            {
                entries.add(JavaRuntime.newArchiveRuntimeClasspathEntry(new Path(FileLocator.toFileURL(
                    FileLocator.find(JettyPlugin.getDefault().getBundle(),
                        Path.fromOSString("lib/eclipse-jetty-starters-console.jar"), null)).getFile()))); //$NON-NLS-1$
            }

            entries.add(JavaRuntime.newArchiveRuntimeClasspathEntry(new Path(FileLocator.toFileURL(
                FileLocator.find(JettyPlugin.getDefault().getBundle(), Path.fromOSString(jettyVersion.getJar()), null))
                .getFile())));

            for (final File jettyLib : jettyVersion.getLibStrategy().find(jettyPath, jspSupport, jmxSupport,
                jndiSupport, ajpSupport))
            {
                entries.add(JavaRuntime.newArchiveRuntimeClasspathEntry(new Path(jettyLib.getCanonicalPath())));
            }
        }
        catch (final IOException e)
        {
            JettyPlugin.error("Failed to detect jetty classpath", e); //$NON-NLS-1$
        }

        return entries.toArray(new IRuntimeClasspathEntry[entries.size()]);
    }

    /**
     * Creates the matcher for the webapp
     * 
     * @param adapter the configuration adapter
     * @return the matcher
     * @throws CoreException on occasion
     */
    private DependencyMatcher createWebappClasspathMatcher(JettyLaunchConfigurationAdapter adapter)
        throws CoreException
    {
        DependencyMatcher vmClasspathMatcher = userClasses();

        if (adapter.isGenericIdsSupported())
        {
            Collection<String> excludedGenericIds = adapter.getExcludedGenericIds();

            if ((excludedGenericIds != null) && (excludedGenericIds.size() > 0))
            {
                vmClasspathMatcher = and(vmClasspathMatcher, notExcludedGenericIds(excludedGenericIds));
            }
        }
        else
        {
            vmClasspathMatcher = deprecatedCreateWebappClasspathMatcherExcludes(adapter, vmClasspathMatcher);
        }

        if (adapter.isScopeCompileExcluded())
        {
            vmClasspathMatcher = and(vmClasspathMatcher, not(withScope(MavenScope.COMPILE)));
        }

        if (adapter.isScopeProvidedExcluded())
        {
            vmClasspathMatcher = and(vmClasspathMatcher, not(withScope(MavenScope.PROVIDED)));
        }

        if (adapter.isScopeRuntimeExcluded())
        {
            vmClasspathMatcher = and(vmClasspathMatcher, not(withScope(MavenScope.RUNTIME)));
        }

        if (adapter.isScopeSystemExcluded())
        {
            vmClasspathMatcher = and(vmClasspathMatcher, not(withScope(MavenScope.SYSTEM)));
        }

        if (adapter.isScopeSystemExcluded())
        {
            vmClasspathMatcher = and(vmClasspathMatcher, not(withScope(MavenScope.IMPORT)));
        }

        if (adapter.isScopeTestExcluded())
        {
            vmClasspathMatcher = and(vmClasspathMatcher, not(withScope(MavenScope.TEST)));
        }

        if (adapter.isScopeNoneExcluded())
        {
            vmClasspathMatcher = and(vmClasspathMatcher, not(withScope(MavenScope.NONE)));
        }

        if (adapter.isGenericIdsSupported())
        {
            Collection<String> includedGenericIds = adapter.getIncludedGenericIds();

            if ((includedGenericIds != null) && (includedGenericIds.size() > 0))
            {
                vmClasspathMatcher = or(isIncludedGenericId(includedGenericIds), vmClasspathMatcher);
            }
        }
        else
        {
            vmClasspathMatcher = deprecatedCreateWebappClasspathMatcherIncludes(adapter, vmClasspathMatcher);
        }

        return vmClasspathMatcher;
    }

    @SuppressWarnings("deprecation")
    private DependencyMatcher deprecatedCreateWebappClasspathMatcherIncludes(JettyLaunchConfigurationAdapter adapter,
        DependencyMatcher vmClasspathMatcher) throws CoreException
    {
        String includedLibs = adapter.getIncludedLibs();

        if ((includedLibs != null) && (includedLibs.trim().length() > 0))
        {
            vmClasspathMatcher =
                or(isIncludedRegEx(JettyPluginUtils.fromCommaSeparatedString(includedLibs)), vmClasspathMatcher);
        }

        return vmClasspathMatcher;
    }

    @SuppressWarnings("deprecation")
    private DependencyMatcher deprecatedCreateWebappClasspathMatcherExcludes(JettyLaunchConfigurationAdapter adapter,
        DependencyMatcher vmClasspathMatcher) throws CoreException
    {
        String excludedLibs = adapter.getExcludedLibs();

        if ((excludedLibs != null) && (excludedLibs.trim().length() > 0))
        {
            vmClasspathMatcher =
                and(vmClasspathMatcher, notExcludedRegEx(JettyPluginUtils.fromCommaSeparatedString(excludedLibs)));
        }

        return vmClasspathMatcher;
    }

    /**
     * Creates the Jetty configuration file with the webapp classpath
     * 
     * @param adapter the configuration adapter
     * @param formatted true to create a formatted file
     * @return the file
     * @throws CoreException on occasion
     */
    public File createJettyConfigurationFile(JettyLaunchConfigurationAdapter adapter, boolean formatted)
        throws CoreException
    {
        String[] webappClasspath =
            getLocalWebappClasspath(adapter, getWebappClasspathEntries(adapter, getOriginalClasspathEntries(adapter)));
        JettyVersion jettyVersion = adapter.getVersion();

        return createJettyConfigurationFile(adapter, jettyVersion, formatted, webappClasspath);
    }

    /**
     * Create the Jetty configuration file with the webapp classpath
     * 
     * @param adapter the configuration adapter
     * @param version the Jetty version
     * @param formatted true to create a formatted file
     * @param classpath the webapp classpath
     * @return the file
     * @throws CoreException on occasion
     */
    private File createJettyConfigurationFile(JettyLaunchConfigurationAdapter adapter, JettyVersion version,
        boolean formatted, String[] classpath) throws CoreException
    {
        AbstractServerConfiguration serverConfiguration = version.createServerConfiguration();

        serverConfiguration.setDefaultContextPath(JettyPluginUtils.prepend(adapter.getContext().trim(), "/")); //$NON-NLS-1$
        serverConfiguration.setDefaultWar(adapter.getWebAppPath());
        serverConfiguration.setPort(Integer.valueOf(adapter.getPort()));

        if (adapter.isHttpsEnabled())
        {
            serverConfiguration.setSslPort(Integer.valueOf(adapter.getHttpsPort()));

            File defaultKeystoreFile = JettyPlugin.getDefaultKeystoreFile();

            if ((defaultKeystoreFile == null) || (!defaultKeystoreFile.exists()) || (!defaultKeystoreFile.canRead()))
            {
                defaultKeystoreFile = createDefaultKeystoreFile();
            }

            serverConfiguration.setKeyStorePath(defaultKeystoreFile.getAbsolutePath());
            serverConfiguration.setKeyStorePassword("correct horse battery staple"); //$NON-NLS-1$
            serverConfiguration.setKeyManagerPassword("correct horse battery staple"); //$NON-NLS-1$
        }

        serverConfiguration.setJndi(adapter.isJndiSupport());
        serverConfiguration.setJmx(adapter.isJmxSupport());

        if (adapter.isGracefulShutdownOverrideEnabled())
        {
            serverConfiguration.setGracefulShutdown(adapter.getGracefulShutdownOverrideTimeout());
        }
        
        if (adapter.isThreadPoolLimitEnabled())
        {
            serverConfiguration.setThreadPoolLimit(adapter.getThreadPoolLimitCount());
        }

        if (adapter.isAcceptorLimitEnabled())
        {
            serverConfiguration.setAcceptorLimit(adapter.getAcceptorLimitCount());
        }

        if (adapter.isCustomWebDefaultsEnabled())
        {
            serverConfiguration.setCustomWebDefaultsFile(adapter.getCustomWebDefaultFile());
        }
        else
        {
            AbstractWebDefaults webDefaults = version.createWebDefaults();

            webDefaults.setServerCacheEnabled(adapter.isServerCacheEnabled());
            webDefaults.setClientCacheEnabled(adapter.isClientCacheEnabled());

            File file;

            try
            {
                file =
                    JettyPluginUtils.getNonRandomTempFile("eclipseJettyPlugin.webDefaults.", adapter.getConfiguration() //$NON-NLS-1$
                        .getName().trim(), ".xml"); //$NON-NLS-1$

                webDefaults.write(file, formatted);

                file.deleteOnExit();
            }
            catch (IOException e)
            {
                throw new CoreException(new Status(IStatus.ERROR, JettyPlugin.PLUGIN_ID,
                    Messages.delegate_webDefaultsFailed));
            }

            serverConfiguration.setCustomWebDefaultsFile(file);
        }

        serverConfiguration.addDefaultClasspath(classpath);

        File file;

        try
        {
            file = JettyPluginUtils.getNonRandomTempFile("eclipseJettyPlugin.config.", adapter.getConfiguration() //$NON-NLS-1$
                .getName().trim(), ".xml"); //$NON-NLS-1$

            serverConfiguration.write(file, formatted);

            file.deleteOnExit();
        }
        catch (IOException e)
        {
            throw new CoreException(new Status(IStatus.ERROR, JettyPlugin.PLUGIN_ID,
                Messages.delegate_launchConfigurationFailed));
        }

        return file;
    }

    /**
     * Create the keystore file
     * 
     * @return the file
     * @throws CoreException on occasion
     */
    private File createDefaultKeystoreFile() throws CoreException
    {
        File defaultKeystoreFile;

        try
        {
            defaultKeystoreFile = File.createTempFile("eclipseJettyPlugin.", ".keystore"); //$NON-NLS-1$ //$NON-NLS-2$

            InputStream in = getClass().getResourceAsStream("eclipseJettyPlugin.keystore"); //$NON-NLS-1$

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
            throw new CoreException(new Status(IStatus.ERROR, JettyPlugin.PLUGIN_ID, Messages.delegate_keystoreFailed));
        }

        return defaultKeystoreFile;
    }
}
