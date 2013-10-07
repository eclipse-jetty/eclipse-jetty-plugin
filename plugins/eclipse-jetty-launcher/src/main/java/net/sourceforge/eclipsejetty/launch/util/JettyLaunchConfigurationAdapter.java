package net.sourceforge.eclipsejetty.launch.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sourceforge.eclipsejetty.JettyPlugin;
import net.sourceforge.eclipsejetty.JettyPluginM2EUtils;
import net.sourceforge.eclipsejetty.JettyPluginUtils;
import net.sourceforge.eclipsejetty.jetty.JettyConfig;
import net.sourceforge.eclipsejetty.jetty.JettyConfigType;
import net.sourceforge.eclipsejetty.jetty.JettyVersion;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.osgi.service.prefs.BackingStoreException;

/**
 * Constants for the Jetty plugin.
 * 
 * @author Christian K&ouml;berl
 * @author Manfred Hantschel
 */
public class JettyLaunchConfigurationAdapter
{

    public static final String LAUNCH_CONFIG_TYPE = JettyPlugin.PLUGIN_ID + ".launchConfigurationType";
    public static final String CLASSPATH_PROVIDER_JETTY = JettyPlugin.PLUGIN_ID + ".JettyLaunchClassPathProvider";

    private static final int CONFIG_VERSION = 1;

    private static final String ATTR_CONFIG_VERSION = JettyPlugin.PLUGIN_ID + ".configVersion";
    private static final String ATTR_CONTEXT = JettyPlugin.PLUGIN_ID + ".context";
    private static final String ATTR_WEBAPPDIR = JettyPlugin.PLUGIN_ID + ".webappdir";
    private static final String ATTR_PORT = JettyPlugin.PLUGIN_ID + ".port";
    private static final String ATTR_HTTPS_PORT = JettyPlugin.PLUGIN_ID + ".httpsPort";
    private static final String ATTR_HTTPS_ENABLED = JettyPlugin.PLUGIN_ID + ".httpsEnabled";
    private static final String ATTR_JETTY_PATH = JettyPlugin.PLUGIN_ID + ".jetty.path";
    private static final String ATTR_JETTY_EMBEDDED = JettyPlugin.PLUGIN_ID + ".jetty.embedded";
    private static final String ATTR_JETTY_VERSION = JettyPlugin.PLUGIN_ID + ".jetty.version";
    private static final String ATTR_JETTY_CONFIG_PATH = JettyPlugin.PLUGIN_ID + ".jetty.config.path.";
    private static final String ATTR_JETTY_CONFIG_TYPE = JettyPlugin.PLUGIN_ID + ".jetty.config.type.";
    private static final String ATTR_JETTY_CONFIG_ACTIVE = JettyPlugin.PLUGIN_ID + ".jetty.config.active.";
    private static final String ATTR_JSP_ENABLED = JettyPlugin.PLUGIN_ID + ".jsp.enabled";
    private static final String ATTR_JMX_ENABLED = JettyPlugin.PLUGIN_ID + ".jmx.enabled";
    private static final String ATTR_JNDI_ENABLED = JettyPlugin.PLUGIN_ID + ".jndi.enabled";
    private static final String ATTR_AJP_ENABLED = JettyPlugin.PLUGIN_ID + ".ajp.enabled";
    private static final String ATTR_THREAD_POOL_LIMIT_ENABLED = JettyPlugin.PLUGIN_ID + ".threadPool.limit.enabled";
    private static final String ATTR_THREAD_POOL_LIMIT_COUNT = JettyPlugin.PLUGIN_ID + ".threadPool.limit.count";
    private static final String ATTR_ACCEPTOR_LIMIT_ENABLED = JettyPlugin.PLUGIN_ID + ".acceptor.limit.enabled";
    private static final String ATTR_ACCEPTOR_LIMIT_COUNT = JettyPlugin.PLUGIN_ID + ".acceptor.limit.count";
    private static final String ATTR_CUSTOM_WEB_DEFAULTS_ENABLED = JettyPlugin.PLUGIN_ID + ".customWebDefaults.enabled";
    private static final String ATTR_CUSTOM_WEB_DEFAULTS_RESOURCE = JettyPlugin.PLUGIN_ID
        + ".customWebDefaults.resource";
    private static final String ATTR_EXCLUDE_SCOPE_COMPILE = JettyPlugin.PLUGIN_ID + ".scope.compile.exclude";
    private static final String ATTR_EXCLUDE_SCOPE_PROVIDED = JettyPlugin.PLUGIN_ID + ".scope.provided.exclude";
    private static final String ATTR_EXCLUDE_SCOPE_RUNTIME = JettyPlugin.PLUGIN_ID + ".scope.runtime.exclude";
    private static final String ATTR_EXCLUDE_SCOPE_TEST = JettyPlugin.PLUGIN_ID + ".scope.test.exclude";
    private static final String ATTR_EXCLUDE_SCOPE_SYSTEM = JettyPlugin.PLUGIN_ID + ".scope.system.exclude";
    private static final String ATTR_EXCLUDE_SCOPE_IMPORT = JettyPlugin.PLUGIN_ID + ".scope.import.exclude";
    private static final String ATTR_EXCLUDE_SCOPE_NONE = JettyPlugin.PLUGIN_ID + ".scope.none.exclude";
    /**
     * @deprecated Replaced by mechanism using generic ids
     */
    @Deprecated
    private static final String ATTR_EXCLUDED_LIBS = JettyPlugin.PLUGIN_ID + ".launcher.excludeLibs";
    /**
     * @deprecated Replaced by mechanism using generic ids
     */
    @Deprecated
    private static final String ATTR_INCLUDED_LIBS = JettyPlugin.PLUGIN_ID + ".launcher.includeLibs";
    private static final String ATTR_EXCLUDED_GENERIC_IDS = JettyPlugin.PLUGIN_ID + ".launcher.excludeGenericIds";
    private static final String ATTR_INCLUDED_GENERIC_IDS = JettyPlugin.PLUGIN_ID + ".launcher.includeGenericIds";
    /**
     * @deprecated Replaced by mechanism using generic ids
     */
    @Deprecated
    private static final String ATTR_GLOBAL_LIBS = JettyPlugin.PLUGIN_ID + ".launcher.globalLibs";
    private static final String ATTR_GLOBAL_GENERIC_IDS = JettyPlugin.PLUGIN_ID + ".launcher.globalGenericIds";
    private static final String ATTR_SHOW_LAUNCHER_INFO = JettyPlugin.PLUGIN_ID + ".launcher.info";
    private static final String ATTR_CONSOLE_ENABLED = JettyPlugin.PLUGIN_ID + ".console.enabled";

    public static JettyLaunchConfigurationAdapter getInstance(ILaunchConfiguration configuration)
    {
        return new JettyLaunchConfigurationAdapter(configuration);
    }

    public static JettyLaunchConfigurationAdapter getInstance(ILaunchConfigurationWorkingCopy configuration)
    {
        return new JettyLaunchConfigurationAdapter(configuration);
    }

    private final ILaunchConfiguration configuration;

    public JettyLaunchConfigurationAdapter(ILaunchConfiguration configuration)
    {
        super();

        this.configuration = configuration;
    }

    public ILaunchConfiguration getConfiguration()
    {
        return configuration;
    }

    public ILaunchConfigurationWorkingCopy getConfigurationWorkingCopy()
    {
        return (ILaunchConfigurationWorkingCopy) configuration;
    }

    public void initialize(IProject project, File webAppPath) throws CoreException
    {
        ILaunchConfigurationWorkingCopy configuration = getConfigurationWorkingCopy();
        String projectName = (project != null) ? project.getName() : "";

        setProjectName(projectName);
        setClasspathProvider(CLASSPATH_PROVIDER_JETTY);
        updateConfigVersion();

        String launchConfigName = projectName;

        if ((launchConfigName == null) || (launchConfigName.length() == 0))
        {
            launchConfigName = "Jetty Webapp";
        }

        launchConfigName = JettyLaunchUtils.generateLaunchConfigurationName(launchConfigName);

        configuration.rename(launchConfigName);

        setContext(getContext());

        if (webAppPath == null)
        {
            IResource webXMLResource = null;

            try
            {
                webXMLResource = JettyLaunchUtils.findWebXML(project);
            }
            catch (CoreException e)
            {
                // ignore
            }

            if (webXMLResource != null)
            {
                IPath webAppResource = webXMLResource.getFullPath().removeLastSegments(2);

                webAppPath = JettyPluginUtils.resolveFolder(project, webAppResource.toString());
            }
        }

        if (webAppPath != null)
        {
            setWebAppString(JettyPluginUtils.toRelativePath(project, webAppPath.toString()));
        }
        else
        {
            setWebAppString("src/main/webapp");
        }

        setPort(getPort());
        setHttpsPort(getHttpsPort());
        setHttpsEnabled(isHttpsEnabled());

        boolean embedded = isEmbedded();

        setEmbedded(embedded);

        String jettyPath = getPathString();

        setPathString(jettyPath);

        try
        {
            JettyVersion jettyVersion =
                JettyPluginUtils.detectJettyVersion(embedded, JettyPluginUtils.resolveVariables(jettyPath));

            setMainTypeName(jettyVersion);
            setVersion(jettyVersion);
        }
        catch (IllegalArgumentException e)
        {
            // failed to detect
        }

        setJspSupport(isJspSupport());
        setJmxSupport(isJmxSupport());
        setJndiSupport(isJndiSupport());
        setAjpSupport(isAjpSupport());

        setThreadPoolLimitEnabled(isThreadPoolLimitEnabled());
        setThreadPoolLimitCount(getThreadPoolLimitCount());
        setAcceptorLimitEnabled(isAcceptorLimitEnabled());
        setAcceptorLimitCount(getAcceptorLimitCount());

        setShowLauncherInfo(isShowLauncherInfo());
        setConsoleEnabled(isConsoleEnabled());

        setCustomWebDefaultsEnabled(isCustomWebDefaultsEnabled());
        setCustomWebDefaultsResource(getCustomWebDefaultsResource());

        setConfigs(getConfigs());

        setScopeCompileExcluded(isScopeCompileExcluded());
        setScopeProvidedExcluded(isScopeProvidedExcluded());
        setScopeRuntimeExcluded(isScopeRuntimeExcluded());
        setScopeSystemExcluded(isScopeSystemExcluded());
        setScopeTestExcluded(isScopeTestExcluded());
        setScopeImportExcluded(isScopeImportExcluded());
        setScopeNoneExcluded(isScopeNoneExcluded());

        setExcludedGenericIds(getExcludedGenericIds());
        setIncludedGenericIds(getIncludedGenericIds());
        setGlobalGenericIds(getGlobalGenericIds());

        deprecatedInitialize();
    }

    private void deprecatedInitialize() throws CoreException
    {
        setExcludedLibs(getExcludedLibs());
        setIncludedLibs(getIncludedLibs());
        setGlobalLibs(getGlobalLibs());
    }

    public int getConfigVersion() throws CoreException
    {
        return getAttribute(false, ATTR_CONFIG_VERSION, 0);
    }

    public void updateConfigVersion()
    {
        setAttribute(false, ATTR_CONFIG_VERSION, CONFIG_VERSION);
    }

    /**
     * Returns the name of the selected eclipse project, that should be launched
     * 
     * @param configuration the configuration
     * @return the project
     * @throws CoreException on occasion
     */
    public String getProjectName() throws CoreException
    {
        return getAttribute(false, IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "");
    }

    /**
     * Sets the name of the selected eclipse project, that should be launched
     * 
     * @param configuration the configuration
     * @param project the project
     */
    public void setProjectName(String project)
    {
        setAttribute(false, IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, project);
    }

    public IProject getProject()
    {
        try
        {
            return JettyPluginUtils.getProject(getProjectName());
        }
        catch (CoreException e)
        {
            return null;
        }
    }

    /**
     * Returns the context path (path part of the URL) of the application
     * 
     * @param configuration the configuration
     * @return the context path
     * @throws CoreException on occasion
     */
    public String getContext() throws CoreException
    {
        return getAttribute(false, ATTR_CONTEXT, "/");
    }

    /**
     * Sets the context path (path part of the URL) of the application
     * 
     * @param configuration the configuration
     * @param context the context
     */
    public void setContext(String context)
    {
        setAttribute(false, ATTR_CONTEXT, context);
    }

    /**
     * Returns the location of the webapp directory in the workspace
     * 
     * @param configuration the configuration
     * @return the location of the webapp directory
     * @throws CoreException on occasion
     */
    public String getWebAppString() throws CoreException
    {
        return getAttribute(false, ATTR_WEBAPPDIR, "src/main/webapp");
    }

    /**
     * Sets the location of the webapp directory in the workspace
     * 
     * @param configuration the configuration
     * @param webappdir the location of the webapp directory
     */
    public void setWebAppString(String webappdir)
    {
        setAttribute(false, ATTR_WEBAPPDIR, webappdir);
    }

    public File getWebAppPath()
    {
        try
        {
            return JettyPluginUtils.resolveFolder(getProject(), getWebAppString());
        }
        catch (CoreException e)
        {
            return null;
        }
    }

    /**
     * Returns the (HTTP) port
     * 
     * @param configuration the configuration
     * @return the port
     * @throws CoreException on occasion
     */
    public int getPort() throws CoreException
    {
        try
        {
            return Integer.parseInt(getAttribute(true, ATTR_PORT, "8080")); // string for backward compatibility
        }
        catch (NumberFormatException e)
        {
            return 8080;
        }
    }

    /**
     * Sets the (HTTP) port
     * 
     * @param configuration the configuration
     * @param port the port
     */
    public void setPort(int port)
    {
        setAttribute(true, ATTR_PORT, String.valueOf(port)); // string for backward compatibility
    }

    /**
     * Returns the (HTTPs) port
     * 
     * @param configuration the configuration
     * @return the port
     * @throws CoreException on occasion
     */
    public int getHttpsPort() throws CoreException
    {
        try
        {
            return Integer.parseInt(getAttribute(true, ATTR_HTTPS_PORT, "8443")); // string for backward compatibility
        }
        catch (NumberFormatException e)
        {
            return 8443;
        }
    }

    /**
     * Sets the (HTTPs) port
     * 
     * @param configuration the configuration
     * @param httpsPort the port
     */
    public void setHttpsPort(int httpsPort)
    {
        setAttribute(true, ATTR_HTTPS_PORT, String.valueOf(httpsPort)); // string for backward compatibility
    }

    public boolean isHttpsEnabled() throws CoreException
    {
        return getAttribute(true, ATTR_HTTPS_ENABLED, false);
    }

    public void setHttpsEnabled(boolean httpsEnabled)
    {
        setAttribute(true, ATTR_HTTPS_ENABLED, httpsEnabled);
    }

    public String getPathString() throws CoreException
    {
        return getAttribute(true, ATTR_JETTY_PATH, "");
    }

    public void setPathString(String path)
    {
        setAttribute(true, ATTR_JETTY_PATH, path);
    }

    public File getPath()
    {
        try
        {
            return JettyPluginUtils.resolveFolder(getProject(), getPathString());
        }
        catch (CoreException e)
        {
            return null;
        }
    }

    public boolean isEmbedded() throws CoreException
    {
        return getAttribute(true, ATTR_JETTY_EMBEDDED, true);
    }

    public void setEmbedded(boolean extern)
    {
        setAttribute(true, ATTR_JETTY_EMBEDDED, extern);
    }

    public JettyVersion getVersion() throws CoreException
    {
        return JettyVersion.valueOf(getAttribute(true, ATTR_JETTY_VERSION, JettyVersion.JETTY_EMBEDDED.name()));
    }

    public void setVersion(JettyVersion jettyVersion)
    {
        setAttribute(true, ATTR_JETTY_VERSION, jettyVersion.name());
    }

    /**
     * Returns the configuration context holders
     * 
     * @param configuration the configuration
     * @return a list of {@link JettyConfig}s
     * @throws CoreException on occasion
     */
    public List<JettyConfig> getConfigs() throws CoreException
    {
        List<JettyConfig> results = new ArrayList<JettyConfig>();
        int index = 0;

        while (true)
        {
            String path = getAttribute(false, ATTR_JETTY_CONFIG_PATH + index, (String) null);

            if (path == null)
            {
                break;
            }

            JettyConfigType type =
                JettyConfigType
                    .valueOf(getAttribute(false, ATTR_JETTY_CONFIG_TYPE + index, JettyConfigType.PATH.name()));
            boolean active = getAttribute(false, ATTR_JETTY_CONFIG_ACTIVE + index, true);

            results.add(new JettyConfig(path, type, active));
            index += 1;
        }

        if (results.size() == 0)
        {
            results.add(new JettyConfig("", JettyConfigType.DEFAULT, true));
        }

        return results;
    }

    public void setConfigs(List<JettyConfig> entries) throws CoreException
    {
        int index = 0;

        for (JettyConfig entry : entries)
        {
            setAttribute(false, ATTR_JETTY_CONFIG_PATH + index, entry.getPath());
            setAttribute(false, ATTR_JETTY_CONFIG_TYPE + index, entry.getType().name());
            setAttribute(false, ATTR_JETTY_CONFIG_ACTIVE + index, entry.isActive());

            index += 1;
        }

        ILaunchConfigurationWorkingCopy configuration = getConfigurationWorkingCopy();

        while (configuration.getAttribute(ATTR_JETTY_CONFIG_PATH + index, (String) null) != null)
        {
            configuration.removeAttribute(ATTR_JETTY_CONFIG_PATH + index);
            configuration.removeAttribute(ATTR_JETTY_CONFIG_TYPE + index);
            configuration.removeAttribute(ATTR_JETTY_CONFIG_ACTIVE + index);

            index += 1;
        }
    }

    public boolean isJspSupport() throws CoreException
    {
        return !"false".equals(getAttribute(true, ATTR_JSP_ENABLED, "true")); // string for backward compatibility
    }

    public void setJspSupport(boolean jspSupport)
    {
        setAttribute(true, ATTR_JSP_ENABLED, String.valueOf(jspSupport)); // string for backward compatibility
    }

    public boolean isJmxSupport() throws CoreException
    {
        return "true".equals(getAttribute(true, ATTR_JMX_ENABLED, "false")); // string for backward compatibility
    }

    public void setJmxSupport(boolean jmxSupport)
    {
        setAttribute(true, ATTR_JMX_ENABLED, String.valueOf(jmxSupport)); // string for backward compatibility
    }

    public boolean isJndiSupport() throws CoreException
    {
        return "true".equals(getAttribute(true, ATTR_JNDI_ENABLED, "false")); // string for backward compatibility
    }

    public void setJndiSupport(boolean jndiSupport)
    {
        setAttribute(true, ATTR_JNDI_ENABLED, String.valueOf(jndiSupport)); // string for backward compatibility
    }

    public boolean isAjpSupport() throws CoreException
    {
        return "true".equals(getAttribute(true, ATTR_AJP_ENABLED, "false")); // string for backward compatibility
    }

    public void setAjpSupport(boolean ajpSupport)
    {
        setAttribute(true, ATTR_AJP_ENABLED, String.valueOf(ajpSupport)); // string for backward compatibility
    }

    public boolean isThreadPoolLimitEnabled() throws CoreException
    {
        return getAttribute(true, ATTR_THREAD_POOL_LIMIT_ENABLED, false);
    }

    public void setThreadPoolLimitEnabled(boolean value)
    {
        setAttribute(true, ATTR_THREAD_POOL_LIMIT_ENABLED, value);
    }

    public int getThreadPoolLimitCount() throws CoreException
    {
        return getAttribute(true, ATTR_THREAD_POOL_LIMIT_COUNT, 16);
    }

    public void setThreadPoolLimitCount(int value)
    {
        setAttribute(true, ATTR_THREAD_POOL_LIMIT_COUNT, value);
    }

    public boolean isAcceptorLimitEnabled() throws CoreException
    {
        return getAttribute(true, ATTR_ACCEPTOR_LIMIT_ENABLED, false);
    }

    public void setAcceptorLimitEnabled(boolean value)
    {
        setAttribute(true, ATTR_ACCEPTOR_LIMIT_ENABLED, value);
    }

    public int getAcceptorLimitCount() throws CoreException
    {
        return getAttribute(true, ATTR_ACCEPTOR_LIMIT_COUNT, 8);
    }

    public void setAcceptorLimitCount(int value)
    {
        setAttribute(true, ATTR_ACCEPTOR_LIMIT_COUNT, value);
    }

    public boolean isCustomWebDefaultsEnabled() throws CoreException
    {
        return getAttribute(false, ATTR_CUSTOM_WEB_DEFAULTS_ENABLED, false);
    }

    public void setCustomWebDefaultsEnabled(boolean value)
    {
        setAttribute(false, ATTR_CUSTOM_WEB_DEFAULTS_ENABLED, value);
    }

    public String getCustomWebDefaultsResource() throws CoreException
    {
        return getAttribute(false, ATTR_CUSTOM_WEB_DEFAULTS_RESOURCE, "");
    }

    public void setCustomWebDefaultsResource(String value)
    {
        setAttribute(false, ATTR_CUSTOM_WEB_DEFAULTS_RESOURCE, value);
    }

    public File getCustomWebDefaultFile()
    {
        try
        {
            if (!isCustomWebDefaultsEnabled())
            {
                return null;
            }

            return JettyPluginUtils.resolveFile(getProject(), getCustomWebDefaultsResource());
        }
        catch (CoreException e)
        {
            // ignore
        }

        return null;
    }

    public boolean isScopeCompileExcluded() throws CoreException
    {
        return getAttribute(false, ATTR_EXCLUDE_SCOPE_COMPILE, false);
    }

    public void setScopeCompileExcluded(boolean value)
    {
        setAttribute(false, ATTR_EXCLUDE_SCOPE_COMPILE, value);
    }

    public boolean isScopeProvidedExcluded() throws CoreException
    {
        return getAttribute(false, ATTR_EXCLUDE_SCOPE_PROVIDED, true);
    }

    public void setScopeProvidedExcluded(boolean value)
    {
        setAttribute(false, ATTR_EXCLUDE_SCOPE_PROVIDED, value);
    }

    public boolean isScopeRuntimeExcluded() throws CoreException
    {
        return getAttribute(false, ATTR_EXCLUDE_SCOPE_RUNTIME, false);
    }

    public void setScopeRuntimeExcluded(boolean value)
    {
        setAttribute(false, ATTR_EXCLUDE_SCOPE_RUNTIME, value);
    }

    public boolean isScopeTestExcluded() throws CoreException
    {
        return getAttribute(false, ATTR_EXCLUDE_SCOPE_TEST, true);
    }

    public void setScopeTestExcluded(boolean value)
    {
        setAttribute(false, ATTR_EXCLUDE_SCOPE_TEST, value);
    }

    public boolean isScopeSystemExcluded() throws CoreException
    {
        return getAttribute(false, ATTR_EXCLUDE_SCOPE_SYSTEM, true);
    }

    public void setScopeSystemExcluded(boolean value)
    {
        setAttribute(false, ATTR_EXCLUDE_SCOPE_SYSTEM, value);
    }

    public boolean isScopeImportExcluded() throws CoreException
    {
        return getAttribute(false, ATTR_EXCLUDE_SCOPE_IMPORT, true);
    }

    public void setScopeImportExcluded(boolean value)
    {
        setAttribute(false, ATTR_EXCLUDE_SCOPE_IMPORT, value);
    }

    public boolean isScopeNoneExcluded() throws CoreException
    {
        if (!hasAttribute(ATTR_EXCLUDE_SCOPE_NONE))
        {
            return JettyPluginM2EUtils.getMavenProjectFacade(this) != null;
        }

        return getAttribute(false, ATTR_EXCLUDE_SCOPE_NONE, false);
    }

    public void setScopeNoneExcluded(boolean value)
    {
        setAttribute(false, ATTR_EXCLUDE_SCOPE_NONE, value);
    }

    /**
     * @deprecated Replaced by mechanism using generic ids
     */
    @Deprecated
    public String getExcludedLibs() throws CoreException
    {
        return getAttribute(false, ATTR_EXCLUDED_LIBS, ".*servlet-api.*");
    }

    /**
     * @deprecated Replaced by mechanism using generic ids
     */
    @Deprecated
    public void setExcludedLibs(String excludedLibs)
    {
        setAttribute(false, ATTR_EXCLUDED_LIBS, excludedLibs);
    }

    /**
     * @deprecated Replaced by mechanism using generic ids
     */
    @Deprecated
    public String getIncludedLibs() throws CoreException
    {
        return getAttribute(false, ATTR_INCLUDED_LIBS, "");
    }

    /**
     * @deprecated Replaced by mechanism using generic ids
     */
    @Deprecated
    public void setIncludedLibs(String includedLibs)
    {
        setAttribute(false, ATTR_INCLUDED_LIBS, includedLibs);
    }

    public Collection<String> getExcludedGenericIds() throws CoreException
    {
        return JettyPluginUtils.fromCommaSeparatedString(getAttribute(false, ATTR_EXCLUDED_GENERIC_IDS, ""));
    }

    public void setExcludedGenericIds(Collection<String> excludedGenericIds)
    {
        setAttribute(false, ATTR_EXCLUDED_GENERIC_IDS, JettyPluginUtils.toCommaSeparatedString(excludedGenericIds));
    }

    public Collection<String> getIncludedGenericIds() throws CoreException
    {
        return JettyPluginUtils.fromCommaSeparatedString(getAttribute(false, ATTR_INCLUDED_GENERIC_IDS, ""));
    }

    public void setIncludedGenericIds(Collection<String> includedGenericIds)
    {
        setAttribute(false, ATTR_INCLUDED_GENERIC_IDS, JettyPluginUtils.toCommaSeparatedString(includedGenericIds));
    }

    /**
     * @deprecated Replaced by mechanism using generic ids
     */
    @Deprecated
    public String getGlobalLibs() throws CoreException
    {
        return getAttribute(false, ATTR_GLOBAL_LIBS, "");
    }

    /**
     * @deprecated Replaced by mechanism using generic ids
     */
    @Deprecated
    public void setGlobalLibs(String globalLibs)
    {
        setAttribute(false, ATTR_GLOBAL_LIBS, globalLibs);
    }

    public Collection<String> getGlobalGenericIds() throws CoreException
    {
        return JettyPluginUtils.fromCommaSeparatedString(getAttribute(false, ATTR_GLOBAL_GENERIC_IDS, ""));
    }

    public void setGlobalGenericIds(Collection<String> globalGenericIds)
    {
        setAttribute(false, ATTR_GLOBAL_GENERIC_IDS, JettyPluginUtils.toCommaSeparatedString(globalGenericIds));
    }

    public boolean isShowLauncherInfo() throws CoreException
    {
        return getAttribute(true, ATTR_SHOW_LAUNCHER_INFO, true);
    }

    public void setShowLauncherInfo(boolean value)
    {
        setAttribute(true, ATTR_SHOW_LAUNCHER_INFO, value);
    }

    public boolean isConsoleEnabled() throws CoreException
    {
        return getAttribute(true, ATTR_CONSOLE_ENABLED, true);
    }

    public void setConsoleEnabled(boolean value)
    {
        setAttribute(true, ATTR_CONSOLE_ENABLED, value);
    }

    public void setClasspathProvider(String classpathProvider)
    {
        setAttribute(false, IJavaLaunchConfigurationConstants.ATTR_CLASSPATH_PROVIDER, classpathProvider);
    }

    public String getMainTypeName() throws CoreException
    {
        return getAttribute(false, IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, "");
    }

    public void setMainTypeName(JettyVersion jettyVersion)
    {
        setAttribute(false, IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, jettyVersion.getMainClass());
    }

    public boolean isGenericIdsSupported() throws CoreException
    {
        return getConfigVersion() >= 1;
    }

    protected boolean getAttribute(boolean globalFallback, String name, boolean defaultValue) throws CoreException
    {
        return configuration.getAttribute(
            name,
            (globalFallback) ? JettyPlugin.getDefaultScope().getNode(JettyPlugin.PLUGIN_ID)
                .getBoolean(name, defaultValue) : defaultValue);
    }

    protected int getAttribute(boolean globalFallback, String name, int defaultValue) throws CoreException
    {
        return configuration.getAttribute(name,
            (globalFallback) ? JettyPlugin.getDefaultScope().getNode(JettyPlugin.PLUGIN_ID).getInt(name, defaultValue)
                : defaultValue);
    }

    protected String getAttribute(boolean globalFallback, String name, String defaultValue) throws CoreException
    {
        return configuration.getAttribute(name,
            (globalFallback) ? JettyPlugin.getDefaultScope().getNode(JettyPlugin.PLUGIN_ID).get(name, defaultValue)
                : defaultValue);
    }

    protected boolean hasAttribute(String name) throws CoreException
    {
        try
        {
            configuration.getClass().getMethod("hasAttribute", String.class);

            return configuration.hasAttribute(name);
        }
        catch (SecurityException e)
        {
            JettyPlugin.error("No hasAttribute (< Eclipse 3.4)", e);
        }
        catch (NoSuchMethodException e)
        {
            JettyPlugin.warning("No DefaultScope.INSTANCE (< Eclipse 3.4)", e);
        }

        return configuration.getAttributes().containsKey(name);
    }

    protected void setAttribute(boolean globalFallback, String name, boolean value)
    {
        getConfigurationWorkingCopy().setAttribute(name, value);

        if (!globalFallback)
        {
            return;
        }

        JettyPlugin.getDefaultScope().getNode(JettyPlugin.PLUGIN_ID).putBoolean(name, value);

        try
        {
            JettyPlugin.getDefaultScope().getNode(JettyPlugin.PLUGIN_ID).flush();
        }
        catch (BackingStoreException e)
        {
            // ignore
        }
    }

    protected void setAttribute(boolean globalFallback, String name, int value)
    {
        getConfigurationWorkingCopy().setAttribute(name, value);

        if (!globalFallback)
        {
            return;
        }

        JettyPlugin.getDefaultScope().getNode(JettyPlugin.PLUGIN_ID).putInt(name, value);

        try
        {
            JettyPlugin.getDefaultScope().getNode(JettyPlugin.PLUGIN_ID).flush();
        }
        catch (BackingStoreException e)
        {
            // ignore
        }
    }

    protected void setAttribute(boolean globalFallback, String name, String value)
    {
        getConfigurationWorkingCopy().setAttribute(name, value);

        if (!globalFallback)
        {
            return;
        }

        JettyPlugin.getDefaultScope().getNode(JettyPlugin.PLUGIN_ID).put(name, value);

        try
        {
            JettyPlugin.getDefaultScope().getNode(JettyPlugin.PLUGIN_ID).flush();
        }
        catch (BackingStoreException e)
        {
            // ignore
        }
    }

}
