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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sourceforge.eclipsejetty.JettyPlugin;
import net.sourceforge.eclipsejetty.JettyPluginM2EUtils;
import net.sourceforge.eclipsejetty.JettyPluginUtils;
import net.sourceforge.eclipsejetty.Messages;
import net.sourceforge.eclipsejetty.jetty.JettyConfig;
import net.sourceforge.eclipsejetty.jetty.JettyConfigType;
import net.sourceforge.eclipsejetty.jetty.JettyVersion;
import net.sourceforge.eclipsejetty.jetty.JettyVersionType;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.osgi.service.prefs.BackingStoreException;

/**
 * Constants for the Jetty plugin and an adapter for the configuration.
 * 
 * @author Christian K&ouml;berl
 * @author Manfred Hantschel
 */
public class JettyLaunchConfigurationAdapter
{

    public static final String LAUNCH_CONFIG_TYPE = JettyPlugin.PLUGIN_ID + ".launchConfigurationType"; //$NON-NLS-1$
    public static final String CLASSPATH_PROVIDER_JETTY = JettyPlugin.PLUGIN_ID + ".JettyLaunchClassPathProvider"; //$NON-NLS-1$

    private static final int CONFIG_VERSION = 1;

    private static final String ATTR_CONFIG_VERSION = JettyPlugin.PLUGIN_ID + ".configVersion"; //$NON-NLS-1$
    private static final String ATTR_CONTEXT = JettyPlugin.PLUGIN_ID + ".context"; //$NON-NLS-1$
    private static final String ATTR_WEBAPPDIR = JettyPlugin.PLUGIN_ID + ".webappdir"; //$NON-NLS-1$
    private static final String ATTR_PORT = JettyPlugin.PLUGIN_ID + ".port"; //$NON-NLS-1$
    private static final String ATTR_HTTPS_PORT = JettyPlugin.PLUGIN_ID + ".httpsPort"; //$NON-NLS-1$
    private static final String ATTR_HTTPS_ENABLED = JettyPlugin.PLUGIN_ID + ".httpsEnabled"; //$NON-NLS-1$
    private static final String ATTR_JETTY_PATH = JettyPlugin.PLUGIN_ID + ".jetty.path"; //$NON-NLS-1$
    private static final String ATTR_JETTY_EMBEDDED = JettyPlugin.PLUGIN_ID + ".jetty.embedded"; //$NON-NLS-1$
    private static final String ATTR_JETTY_VERSION = JettyPlugin.PLUGIN_ID + ".jetty.version"; //$NON-NLS-1$
    private static final String ATTR_JETTY_MAJOR_VERSION = JettyPlugin.PLUGIN_ID + ".jetty.majorVersion"; //$NON-NLS-1$
    private static final String ATTR_JETTY_MINOR_VERSION = JettyPlugin.PLUGIN_ID + ".jetty.minorVersion"; //$NON-NLS-1$
    private static final String ATTR_JETTY_MICRO_VERSION = JettyPlugin.PLUGIN_ID + ".jetty.microVersion"; //$NON-NLS-1$
    private static final String ATTR_JETTY_CONFIG_PATH = JettyPlugin.PLUGIN_ID + ".jetty.config.path."; //$NON-NLS-1$
    private static final String ATTR_JETTY_CONFIG_TYPE = JettyPlugin.PLUGIN_ID + ".jetty.config.type."; //$NON-NLS-1$
    private static final String ATTR_JETTY_CONFIG_ACTIVE = JettyPlugin.PLUGIN_ID + ".jetty.config.active."; //$NON-NLS-1$
    private static final String ATTR_ANNOTATIONS_ENABLED = JettyPlugin.PLUGIN_ID + ".annotations.enabled"; //$NON-NLS-1$
    private static final String ATTR_JSP_ENABLED = JettyPlugin.PLUGIN_ID + ".jsp.enabled"; //$NON-NLS-1$
    private static final String ATTR_JMX_ENABLED = JettyPlugin.PLUGIN_ID + ".jmx.enabled"; //$NON-NLS-1$
    private static final String ATTR_JNDI_ENABLED = JettyPlugin.PLUGIN_ID + ".jndi.enabled"; //$NON-NLS-1$
    private static final String ATTR_AJP_ENABLED = JettyPlugin.PLUGIN_ID + ".ajp.enabled"; //$NON-NLS-1$
    private static final String ATTR_WEBSOCKET_ENABLED = JettyPlugin.PLUGIN_ID + ".websocket.enabled"; //$NON-NLS-1$
    private static final String ATTR_THREAD_POOL_LIMIT_ENABLED = JettyPlugin.PLUGIN_ID + ".threadPool.limit.enabled"; //$NON-NLS-1$
    private static final String ATTR_THREAD_POOL_LIMIT_COUNT = JettyPlugin.PLUGIN_ID + ".threadPool.limit.count"; //$NON-NLS-1$
    private static final String ATTR_ACCEPTOR_LIMIT_ENABLED = JettyPlugin.PLUGIN_ID + ".acceptor.limit.enabled"; //$NON-NLS-1$
    private static final String ATTR_ACCEPTOR_LIMIT_COUNT = JettyPlugin.PLUGIN_ID + ".acceptor.limit.count"; //$NON-NLS-1$
    private static final String ATTR_GRACEFUL_SHUTDOWN_OVERRIDE_ENABLED = JettyPlugin.PLUGIN_ID
        + ".gracefulShutdown.override.enabled"; //$NON-NLS-1$
    private static final String ATTR_GRACEFUL_SHUTDOWN_OVERRIDE_TIMEOUT = JettyPlugin.PLUGIN_ID
        + ".gracefulShutdown.override.timeout"; //$NON-NLS-1$
    private static final String ATTR_SERVER_CACHE_ENABLED = JettyPlugin.PLUGIN_ID + ".cache.server.enabled"; //$NON-NLS-1$
    private static final String ATTR_CLIENT_CACHE_ENABLED = JettyPlugin.PLUGIN_ID + ".cache.client.enabled"; //$NON-NLS-1$
    private static final String ATTR_CUSTOM_WEB_DEFAULTS_ENABLED = JettyPlugin.PLUGIN_ID + ".customWebDefaults.enabled"; //$NON-NLS-1$
    private static final String ATTR_CUSTOM_WEB_DEFAULTS_RESOURCE = JettyPlugin.PLUGIN_ID
        + ".customWebDefaults.resource"; //$NON-NLS-1$
    private static final String ATTR_EXCLUDE_SCOPE_COMPILE = JettyPlugin.PLUGIN_ID + ".scope.compile.exclude"; //$NON-NLS-1$
    private static final String ATTR_EXCLUDE_SCOPE_PROVIDED = JettyPlugin.PLUGIN_ID + ".scope.provided.exclude"; //$NON-NLS-1$
    private static final String ATTR_EXCLUDE_SCOPE_RUNTIME = JettyPlugin.PLUGIN_ID + ".scope.runtime.exclude"; //$NON-NLS-1$
    private static final String ATTR_EXCLUDE_SCOPE_TEST = JettyPlugin.PLUGIN_ID + ".scope.test.exclude"; //$NON-NLS-1$
    private static final String ATTR_EXCLUDE_SCOPE_SYSTEM = JettyPlugin.PLUGIN_ID + ".scope.system.exclude"; //$NON-NLS-1$
    private static final String ATTR_EXCLUDE_SCOPE_IMPORT = JettyPlugin.PLUGIN_ID + ".scope.import.exclude"; //$NON-NLS-1$
    private static final String ATTR_EXCLUDE_SCOPE_NONE = JettyPlugin.PLUGIN_ID + ".scope.none.exclude"; //$NON-NLS-1$
    /**
     * @deprecated Replaced by mechanism using generic ids
     */
    @Deprecated
    private static final String ATTR_EXCLUDED_LIBS = JettyPlugin.PLUGIN_ID + ".launcher.excludeLibs"; //$NON-NLS-1$
    /**
     * @deprecated Replaced by mechanism using generic ids
     */
    @Deprecated
    private static final String ATTR_INCLUDED_LIBS = JettyPlugin.PLUGIN_ID + ".launcher.includeLibs"; //$NON-NLS-1$
    private static final String ATTR_EXCLUDED_GENERIC_IDS = JettyPlugin.PLUGIN_ID + ".launcher.excludeGenericIds"; //$NON-NLS-1$
    private static final String ATTR_INCLUDED_GENERIC_IDS = JettyPlugin.PLUGIN_ID + ".launcher.includeGenericIds"; //$NON-NLS-1$
    /**
     * @deprecated Replaced by mechanism using generic ids
     */
    @Deprecated
    private static final String ATTR_GLOBAL_LIBS = JettyPlugin.PLUGIN_ID + ".launcher.globalLibs"; //$NON-NLS-1$
    private static final String ATTR_GLOBAL_GENERIC_IDS = JettyPlugin.PLUGIN_ID + ".launcher.globalGenericIds"; //$NON-NLS-1$
    private static final String ATTR_SHOW_LAUNCHER_INFO = JettyPlugin.PLUGIN_ID + ".launcher.info"; //$NON-NLS-1$
    private static final String ATTR_CONSOLE_ENABLED = JettyPlugin.PLUGIN_ID + ".console.enabled"; //$NON-NLS-1$

    /**
     * Creates an readable configuration adapter.
     * 
     * @param configuration the configuration, must not be null
     * @return the adapter
     */
    public static JettyLaunchConfigurationAdapter getInstance(ILaunchConfiguration configuration)
    {
        return new JettyLaunchConfigurationAdapter(configuration);
    }

    /**
     * Creates an read and writable configuration adapter.
     * 
     * @param configuration the configuration, must not be null
     * @return the adapter
     */
    public static JettyLaunchConfigurationAdapter getInstance(ILaunchConfigurationWorkingCopy configuration)
    {
        return new JettyLaunchConfigurationAdapter(configuration);
    }

    private final ILaunchConfiguration configuration;

    /**
     * Creates the adapter.
     * 
     * @param configuration the configuration
     */
    protected JettyLaunchConfigurationAdapter(ILaunchConfiguration configuration)
    {
        super();

        this.configuration = configuration;
    }

    /**
     * Returns the readable configuration.
     * 
     * @return the configuration
     */
    public ILaunchConfiguration getConfiguration()
    {
        return configuration;
    }

    /**
     * Returns the read and writable configuration.
     * 
     * @return the configuration
     * @throws CoreException if configuration is only readable
     */
    public ILaunchConfigurationWorkingCopy getConfigurationWorkingCopy() throws CoreException
    {
        try
        {
            return (ILaunchConfigurationWorkingCopy) configuration;
        }
        catch (ClassCastException e)
        {
            throw new CoreException(new Status(IStatus.ERROR, JettyPlugin.PLUGIN_ID,
                Messages.adapter_configurationOnlyReadable));
        }
    }

    /**
     * Initializes a new launch configuration.
     * 
     * @param project the project, must not be null
     * @param webAppPath the web application path, must not be null
     * @throws CoreException on occasion
     */
    public void initialize(IProject project, File webAppPath) throws CoreException
    {
        ILaunchConfigurationWorkingCopy configuration = getConfigurationWorkingCopy();
        String projectName = (project != null) ? project.getName() : JettyPluginUtils.EMPTY;

        setProjectName(projectName);
        setClasspathProvider(CLASSPATH_PROVIDER_JETTY);
        updateConfigVersion();

        String launchConfigName = projectName;

        if ((launchConfigName == null) || (launchConfigName.length() == 0))
        {
            launchConfigName = Messages.adapter_defaultConfigName;
        }

        launchConfigName = JettyLaunchUtils.generateLaunchConfigurationName(launchConfigName);

        configuration.rename(launchConfigName);

        setContext(getContext());

        if (webAppPath == null)
        {
            try
            {
                List<IResource> path = JettyLaunchUtils.findWebXMLs(project, 1);

                if (path.size() > 0)
                {
                    IPath webAppResource = path.get(0).getFullPath().removeLastSegments(2);

                    webAppPath = JettyPluginUtils.resolveFolder(project, webAppResource.toString());
                }
            }
            catch (CoreException e)
            {
                // ignore
            }
        }

        if (webAppPath != null)
        {
            setWebAppString(JettyPluginUtils.toRelativePath(project, webAppPath.toString()));
        }
        else
        {
            setWebAppString("src/main/webapp"); //$NON-NLS-1$
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
            JettyVersion jettyVersion = JettyVersion.detect(JettyPluginUtils.resolveVariables(jettyPath), embedded);

            setMainTypeName(jettyVersion);
            setVersion(jettyVersion);
            setMinorVersion(jettyVersion);
            setMicroVersion(jettyVersion);
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
        setGracefulShutdownOverrideEnabled(isGracefulShutdownOverrideEnabled());
        setGracefulShutdownOverrideTimeout(getGracefulShutdownOverrideTimeout());

        setServerCacheEnabled(isServerCacheEnabled());
        setClientCacheEnabled(isClientCacheEnabled());

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

    /**
     * Returns the configuration version to distinguish between versions of the plugin.
     * 
     * @return the configuration version
     * @throws CoreException on occasion
     */
    public int getConfigVersion() throws CoreException
    {
        return getAttribute(false, ATTR_CONFIG_VERSION, 0);
    }

    /**
     * Updates the configuration version to the one supported by the plugin.
     * 
     * @throws CoreException on occasion
     */
    public void updateConfigVersion() throws CoreException
    {
        setAttribute(false, ATTR_CONFIG_VERSION, CONFIG_VERSION);
    }

    /**
     * Returns the name of the selected eclipse project, that should be launched.
     * 
     * @return the project
     * @throws CoreException on occasion
     */
    public String getProjectName() throws CoreException
    {
        return getAttribute(false, IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, JettyPluginUtils.EMPTY);
    }

    /**
     * Sets the name of the selected eclipse project, that should be launched.
     * 
     * @param project the project
     * @throws CoreException on occasion
     */
    public void setProjectName(String project) throws CoreException
    {
        setAttribute(false, IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, project);
    }

    /**
     * Returns the project as defined by the project name.
     * 
     * @return the project, null if unable to locate
     */
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
     * Returns the context path (path part of the URL) of the application.
     * 
     * @return the context path
     * @throws CoreException on occasion
     */
    public String getContext() throws CoreException
    {
        return getAttribute(false, ATTR_CONTEXT, "/"); //$NON-NLS-1$
    }

    /**
     * Sets the context path (path part of the URL) of the application.
     * 
     * @param context the context
     * @throws CoreException on occasion
     */
    public void setContext(String context) throws CoreException
    {
        setAttribute(false, ATTR_CONTEXT, context);
    }

    /**
     * Returns the location of the web application directory in the workspace.
     * 
     * @return the location of the web application directory
     * @throws CoreException on occasion
     */
    public String getWebAppString() throws CoreException
    {
        return getAttribute(false, ATTR_WEBAPPDIR, "src/main/webapp"); //$NON-NLS-1$
    }

    /**
     * Sets the location of the web application directory in the workspace.
     * 
     * @param webappdir the location of the web application directory
     * @throws CoreException on occasion
     */
    public void setWebAppString(String webappdir) throws CoreException
    {
        setAttribute(false, ATTR_WEBAPPDIR, webappdir);
    }

    /**
     * Tries to determine the web application path from the web application directory.
     * 
     * @return the path, null if unable to determine
     */
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
     * Returns the (HTTP) port.
     * 
     * @return the port
     * @throws CoreException on occasion
     */
    public int getPort() throws CoreException
    {
        try
        {
            return Integer.parseInt(getAttribute(true, ATTR_PORT, "8080")); // string for backward compatibility //$NON-NLS-1$
        }
        catch (NumberFormatException e)
        {
            return 8080;
        }
    }

    /**
     * Sets the (HTTP) port.
     * 
     * @param port the port
     * @throws CoreException on occasion
     */
    public void setPort(int port) throws CoreException
    {
        setAttribute(true, ATTR_PORT, String.valueOf(port)); // string for backward compatibility
    }

    /**
     * Returns the (HTTPs) port.
     * 
     * @return the port
     * @throws CoreException on occasion
     */
    public int getHttpsPort() throws CoreException
    {
        try
        {
            return Integer.parseInt(getAttribute(true, ATTR_HTTPS_PORT, "8443")); // string for backward compatibility //$NON-NLS-1$
        }
        catch (NumberFormatException e)
        {
            return 8443;
        }
    }

    /**
     * Sets the (HTTPs) port.
     * 
     * @param httpsPort the port
     * @throws CoreException on occasion
     */
    public void setHttpsPort(int httpsPort) throws CoreException
    {
        setAttribute(true, ATTR_HTTPS_PORT, String.valueOf(httpsPort)); // string for backward compatibility
    }

    /**
     * Returns true if HTTPs is enabled.
     * 
     * @return true if enabled
     * @throws CoreException on occasion
     */
    public boolean isHttpsEnabled() throws CoreException
    {
        return getAttribute(true, ATTR_HTTPS_ENABLED, false);
    }

    /**
     * Set to true, if the HTTPs is enabled.
     * 
     * @param httpsEnabled true if enabled
     * @throws CoreException on occasion
     */
    public void setHttpsEnabled(boolean httpsEnabled) throws CoreException
    {
        setAttribute(true, ATTR_HTTPS_ENABLED, httpsEnabled);
    }

    /**
     * Returns the path to an optionally available Jetty.
     * 
     * @return the path
     * @throws CoreException on occasion
     */
    public String getPathString() throws CoreException
    {
        return getAttribute(true, ATTR_JETTY_PATH, JettyPluginUtils.EMPTY);
    }

    /**
     * Sets the path to an optionally available Jetty.
     * 
     * @param path the path
     * @throws CoreException on occasion
     */
    public void setPathString(String path) throws CoreException
    {
        setAttribute(true, ATTR_JETTY_PATH, path);
    }

    /**
     * Tries to determine the path to an optionally available Jetty.
     * 
     * @return the path, null if unable to determine
     */
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

    /**
     * Returns true, if the embedded Jetty should be used.
     * 
     * @return true, if the embedded Jetty should be used.
     * @throws CoreException on occasion
     */
    public boolean isEmbedded() throws CoreException
    {
        return getAttribute(true, ATTR_JETTY_EMBEDDED, true);
    }

    /**
     * Set to true, if the embedded Jetty should be used.
     * 
     * @param embedded true, if the embedded Jetty should be used
     * @throws CoreException on occasion
     */
    public void setEmbedded(boolean embedded) throws CoreException
    {
        setAttribute(true, ATTR_JETTY_EMBEDDED, embedded);
    }

    /**
     * Returns the version of the Jetty.
     * 
     * @return the version of the Jetty
     * @throws CoreException on occasion
     */
    public JettyVersionType getVersion() throws CoreException
    {
        return JettyVersionType.valueOf(getAttribute(true, ATTR_JETTY_VERSION, JettyVersionType.JETTY_EMBEDDED.name()));
    }

    /**
     * Sets the version of the Jetty.
     * 
     * @param jettyVersion the version
     * @throws CoreException on occasion
     */
    public void setVersion(JettyVersion jettyVersion) throws CoreException
    {
        setAttribute(true, ATTR_JETTY_VERSION, jettyVersion.getType().name());
    }

    /**
     * Returns the major version of the Jetty.
     * 
     * @return the major version of the Jetty
     * @throws CoreException on occasion
     */
    public int getMajorVersion() throws CoreException
    {
        return getAttribute(true, ATTR_JETTY_MAJOR_VERSION, -1);
    }

    /**
     * Sets the major version of the Jetty.
     * 
     * @param jettyVersion the version
     * @throws CoreException on occasion
     */
    public void setMajorVersion(JettyVersion jettyVersion) throws CoreException
    {
        setAttribute(true, ATTR_JETTY_MAJOR_VERSION, (jettyVersion.getMajorVersion() != null) ? jettyVersion
            .getMajorVersion().intValue() : -1);
    }

    /**
     * Returns the minor version of the Jetty.
     * 
     * @return the minor version of the Jetty
     * @throws CoreException on occasion
     */
    public int getMinorVersion() throws CoreException
    {
        return getAttribute(true, ATTR_JETTY_MINOR_VERSION, -1);
    }

    /**
     * Sets the minor version of the Jetty.
     * 
     * @param jettyVersion the version
     * @throws CoreException on occasion
     */
    public void setMinorVersion(JettyVersion jettyVersion) throws CoreException
    {
        setAttribute(true, ATTR_JETTY_MINOR_VERSION, (jettyVersion.getMinorVersion() != null) ? jettyVersion
            .getMinorVersion().intValue() : -1);
    }

    /**
     * Returns the micro version of the Jetty.
     * 
     * @return the micro version of the Jetty
     * @throws CoreException on occasion
     */
    public int getMicroVersion() throws CoreException
    {
        return getAttribute(true, ATTR_JETTY_MICRO_VERSION, -1);
    }

    /**
     * Sets the micro version of the Jetty.
     * 
     * @param jettyVersion the version
     * @throws CoreException on occasion
     */
    public void setMicroVersion(JettyVersion jettyVersion) throws CoreException
    {
        setAttribute(true, ATTR_JETTY_MICRO_VERSION, (jettyVersion.getMicroVersion() != null) ? jettyVersion
            .getMicroVersion().intValue() : -1);
    }

    /**
     * Returns the configuration context holders.
     * 
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
            results.add(new JettyConfig(JettyPluginUtils.EMPTY, JettyConfigType.DEFAULT, true));
        }

        return results;
    }

    /**
     * Sets the configuration context holders.
     * 
     * @param entries the entries
     * @throws CoreException on occasion
     */
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

    /**
     * Returns true, if annotations should be supported.
     * 
     * @return true, if annotations should be supported
     * @throws CoreException on occasion
     */
    public boolean isAnnotationsSupport() throws CoreException
    {
        return !"false".equals(getAttribute(true, ATTR_ANNOTATIONS_ENABLED, "true")); // string for backward compatibility //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Set to true, if JSPs should be supported.
     * 
     * @param annotationsSupport true, if JSPs should be supported
     * @throws CoreException on occasion
     */
    public void setAnnotationsSupport(boolean annotationsSupport) throws CoreException
    {
        setAttribute(true, ATTR_ANNOTATIONS_ENABLED, String.valueOf(annotationsSupport)); // string for backward compatibility
    }

    /**
     * Returns true, if JSPs should be supported.
     * 
     * @return true, if JSPs should be supported
     * @throws CoreException on occasion
     */
    public boolean isJspSupport() throws CoreException
    {
        return !"false".equals(getAttribute(true, ATTR_JSP_ENABLED, "true")); // string for backward compatibility //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Set to true, if JSPs should be supported.
     * 
     * @param jspSupport true, if JSPs should be supported
     * @throws CoreException on occasion
     */
    public void setJspSupport(boolean jspSupport) throws CoreException
    {
        setAttribute(true, ATTR_JSP_ENABLED, String.valueOf(jspSupport)); // string for backward compatibility
    }

    /**
     * Returns true, if JMX should be supported.
     * 
     * @return true, if JMX should be supported
     * @throws CoreException on occasion
     */
    public boolean isJmxSupport() throws CoreException
    {
        return "true".equals(getAttribute(true, ATTR_JMX_ENABLED, "false")); // string for backward compatibility //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Set to true, if JMX should be supported.
     * 
     * @param jmxSupport true, if JMX should be supported
     * @throws CoreException on occasion
     */
    public void setJmxSupport(boolean jmxSupport) throws CoreException
    {
        setAttribute(true, ATTR_JMX_ENABLED, String.valueOf(jmxSupport)); // string for backward compatibility
    }

    /**
     * Returns true, if JNDI should be supported.
     * 
     * @return true, if JNDI should be supported
     * @throws CoreException on occasion
     */
    public boolean isJndiSupport() throws CoreException
    {
        return "true".equals(getAttribute(true, ATTR_JNDI_ENABLED, "false")); // string for backward compatibility //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Set to true, if JNDI should be supported.
     * 
     * @param jndiSupport true, if JNDI should be supported
     * @throws CoreException on occasion
     */
    public void setJndiSupport(boolean jndiSupport) throws CoreException
    {
        setAttribute(true, ATTR_JNDI_ENABLED, String.valueOf(jndiSupport)); // string for backward compatibility
    }

    /**
     * Returns true, if an AJP connector should be supported.
     * 
     * @return true, if an AJP connector should be supported
     * @throws CoreException on occasion
     */
    public boolean isAjpSupport() throws CoreException
    {
        return "true".equals(getAttribute(true, ATTR_AJP_ENABLED, "false")); // string for backward compatibility //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Set to true, if an AJP connector should be supported.
     * 
     * @param ajpSupport true, if an AJP connector should be supported.
     * @throws CoreException on occasion
     */
    public void setAjpSupport(boolean ajpSupport) throws CoreException
    {
        setAttribute(true, ATTR_AJP_ENABLED, String.valueOf(ajpSupport)); // string for backward compatibility
    }

    /**
     * Returns true, if Websockets should be supported.
     * 
     * @return true, if Websockets should be supported
     * @throws CoreException on occasion
     */
    public boolean isWebsocketSupport() throws CoreException
    {
        return "true".equals(getAttribute(true, ATTR_WEBSOCKET_ENABLED, "false")); // string for backward compatibility //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Set to true, if Websockets should be supported.
     * 
     * @param websocketSupport true, if Websockets should be supported.
     * @throws CoreException on occasion
     */
    public void setWebsocketSupport(boolean websocketSupport) throws CoreException
    {
        setAttribute(true, ATTR_WEBSOCKET_ENABLED, String.valueOf(websocketSupport)); // string for backward compatibility
    }

    /**
     * Returns true, if the size of Jetty's thread pool is limited.
     * 
     * @return true, if the size of Jetty's thread pool is limited
     * @throws CoreException on occasion
     */
    public boolean isThreadPoolLimitEnabled() throws CoreException
    {
        return getAttribute(true, ATTR_THREAD_POOL_LIMIT_ENABLED, false);
    }

    /**
     * Set to true, if the size of Jetty's thread pool is limited.
     * 
     * @param value true, if the size of Jetty's thread pool is limited
     * @throws CoreException on occasion
     */
    public void setThreadPoolLimitEnabled(boolean value) throws CoreException
    {
        setAttribute(true, ATTR_THREAD_POOL_LIMIT_ENABLED, value);
    }

    /**
     * Returns the maximum size of Jetty's thead pool.
     * 
     * @return the maximum size of Jetty's thead pool
     * @throws CoreException on occasion
     */
    public int getThreadPoolLimitCount() throws CoreException
    {
        return getAttribute(true, ATTR_THREAD_POOL_LIMIT_COUNT, 16);
    }

    /**
     * Sets the maximum size of Jetty's thead pool
     * 
     * @param value the maximum size of Jetty's thead pool
     * @throws CoreException on occasion
     */
    public void setThreadPoolLimitCount(int value) throws CoreException
    {
        setAttribute(true, ATTR_THREAD_POOL_LIMIT_COUNT, value);
    }

    /**
     * Returns true, if the number of Jetty's acceptors is limited.
     * 
     * @return true, if the number of Jetty's acceptors is limited
     * @throws CoreException on occasion
     */
    public boolean isAcceptorLimitEnabled() throws CoreException
    {
        return getAttribute(true, ATTR_ACCEPTOR_LIMIT_ENABLED, false);
    }

    /**
     * Set to true, if the number of Jetty's acceptors is limited
     * 
     * @param value true, if the number of Jetty's acceptors is limited
     * @throws CoreException on occasion
     */
    public void setAcceptorLimitEnabled(boolean value) throws CoreException
    {
        setAttribute(true, ATTR_ACCEPTOR_LIMIT_ENABLED, value);
    }

    /**
     * Returns the maximum number of acceptors Jetty should use.
     * 
     * @return the maximum number of acceptors Jetty should use
     * @throws CoreException on occasion
     */
    public int getAcceptorLimitCount() throws CoreException
    {
        return getAttribute(true, ATTR_ACCEPTOR_LIMIT_COUNT, 8);
    }

    /**
     * Sets the the maximum number of acceptors Jetty should use.
     * 
     * @param value the maximum number of acceptors Jetty should use
     * @throws CoreException on occasion
     */
    public void setAcceptorLimitCount(int value) throws CoreException
    {
        setAttribute(true, ATTR_ACCEPTOR_LIMIT_COUNT, value);
    }

    /**
     * Return true, if the graceful shutdown override is enabled.
     * 
     * @return true, if the graceful shutdown override is enabled
     * @throws CoreException on occasion
     */
    public boolean isGracefulShutdownOverrideEnabled() throws CoreException
    {
        return getAttribute(true, ATTR_GRACEFUL_SHUTDOWN_OVERRIDE_ENABLED, false);
    }

    /**
     * Set to true, if the graceful shutdown override is enabled.
     * 
     * @param value true, if the graceful shutdown override is enabled
     * @throws CoreException on occasion
     */
    public void setGracefulShutdownOverrideEnabled(boolean value) throws CoreException
    {
        setAttribute(true, ATTR_GRACEFUL_SHUTDOWN_OVERRIDE_ENABLED, value);
    }

    /**
     * Returns the timeout for the graceful shutdown (in milliseconds).
     * 
     * @return the timeout for the graceful shutdown (in milliseconds)
     * @throws CoreException on occasion
     */
    public int getGracefulShutdownOverrideTimeout() throws CoreException
    {
        return getAttribute(true, ATTR_GRACEFUL_SHUTDOWN_OVERRIDE_TIMEOUT, 1000);
    }

    /**
     * Sets the timeout for the graceful shutdown (in milliseconds)
     * 
     * @param value the timeout for the graceful shutdown (in milliseconds)
     * @throws CoreException on occasion
     */
    public void setGracefulShutdownOverrideTimeout(int value) throws CoreException
    {
        setAttribute(true, ATTR_GRACEFUL_SHUTDOWN_OVERRIDE_TIMEOUT, value);
    }

    /**
     * Returns true, if Jetty's server cache is enabled.
     * 
     * @return true, if Jetty's server cache is enabled
     * @throws CoreException on occasion
     */
    public boolean isServerCacheEnabled() throws CoreException
    {
        return getAttribute(true, ATTR_SERVER_CACHE_ENABLED, true);
    }

    /**
     * Set to true, if Jetty's server cache is enabled.
     * 
     * @param enabled true, if Jetty's server cache is enabled
     * @throws CoreException on occasion
     */
    public void setServerCacheEnabled(boolean enabled) throws CoreException
    {
        setAttribute(true, ATTR_SERVER_CACHE_ENABLED, enabled);
    }

    /**
     * Returns true, if the cache pragma no cache should not be sent.
     * 
     * @return true, if the cache pragma no cache should not be sent
     * @throws CoreException on occasion
     */
    public boolean isClientCacheEnabled() throws CoreException
    {
        return getAttribute(true, ATTR_CLIENT_CACHE_ENABLED, true);
    }

    /**
     * Set to true, if the cache pragma no cache should not be sent.
     * 
     * @param enabled true, if the cache pragma no cache should not be sent
     * @throws CoreException on occasion
     */
    public void setClientCacheEnabled(boolean enabled) throws CoreException
    {
        setAttribute(true, ATTR_CLIENT_CACHE_ENABLED, enabled);
    }

    /**
     * Returns true, if a custom default web.xml should be used.
     * 
     * @return true, if a custom default web.xml should be used
     * @throws CoreException on occasion
     */
    public boolean isCustomWebDefaultsEnabled() throws CoreException
    {
        return getAttribute(false, ATTR_CUSTOM_WEB_DEFAULTS_ENABLED, false);
    }

    /**
     * Set to true, if a custom default web.xml should be used
     * 
     * @param value true, if a custom default web.xml should be used
     * @throws CoreException on occasion
     */
    public void setCustomWebDefaultsEnabled(boolean value) throws CoreException
    {
        setAttribute(false, ATTR_CUSTOM_WEB_DEFAULTS_ENABLED, value);
    }

    /**
     * Returns the custom default web.xml.
     * 
     * @return the custom default web.xml
     * @throws CoreException on occasion
     */
    public String getCustomWebDefaultsResource() throws CoreException
    {
        return getAttribute(false, ATTR_CUSTOM_WEB_DEFAULTS_RESOURCE, JettyPluginUtils.EMPTY);
    }

    /**
     * Sets the custom default web.xml
     * 
     * @param value the custom default web.xml
     * @throws CoreException on occasion
     */
    public void setCustomWebDefaultsResource(String value) throws CoreException
    {
        setAttribute(false, ATTR_CUSTOM_WEB_DEFAULTS_RESOURCE, value);
    }

    /**
     * Tries to determine the custom web.xml file from the resource.
     * 
     * @return the custom web.xml file, null if not found or not enabled
     */
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

    /**
     * Returns true, if Maven dependencies with the compile scope should be excluded.
     * 
     * @return true, if Maven dependencies with the compile scope should be excluded
     * @throws CoreException on occasion
     */
    public boolean isScopeCompileExcluded() throws CoreException
    {
        return getAttribute(false, ATTR_EXCLUDE_SCOPE_COMPILE, false);
    }

    /**
     * Set to true, if Maven dependencies with the compile scope should be excluded.
     * 
     * @param value true, if Maven dependencies with the compile scope should be excluded
     * @throws CoreException on occasion
     */
    public void setScopeCompileExcluded(boolean value) throws CoreException
    {
        setAttribute(false, ATTR_EXCLUDE_SCOPE_COMPILE, value);
    }

    /**
     * Returns true, if Maven dependencies with the provided scope should be excluded.
     * 
     * @return true, if Maven dependencies with the provided scope should be excluded
     * @throws CoreException on occasion
     */
    public boolean isScopeProvidedExcluded() throws CoreException
    {
        return getAttribute(false, ATTR_EXCLUDE_SCOPE_PROVIDED, true);
    }

    /**
     * Set to true, if Maven dependencies with the provided scope should be excluded.
     * 
     * @param value true, if Maven dependencies with the provided scope should be excluded
     * @throws CoreException on occasion
     */
    public void setScopeProvidedExcluded(boolean value) throws CoreException
    {
        setAttribute(false, ATTR_EXCLUDE_SCOPE_PROVIDED, value);
    }

    /**
     * Returns true, if Maven dependencies with the runtime scope should be excluded.
     * 
     * @return true, if Maven dependencies with the runtime scope should be excluded
     * @throws CoreException on occasion
     */
    public boolean isScopeRuntimeExcluded() throws CoreException
    {
        return getAttribute(false, ATTR_EXCLUDE_SCOPE_RUNTIME, false);
    }

    /**
     * Set to true, if Maven dependencies with the runtime scope should be excluded.
     * 
     * @param value true, if Maven dependencies with the runtime scope should be excluded
     * @throws CoreException on occasion
     */
    public void setScopeRuntimeExcluded(boolean value) throws CoreException
    {
        setAttribute(false, ATTR_EXCLUDE_SCOPE_RUNTIME, value);
    }

    /**
     * Returns true, if Maven dependencies with the test scope should be excluded.
     * 
     * @return true, if Maven dependencies with the test scope should be excluded
     * @throws CoreException on occasion
     */
    public boolean isScopeTestExcluded() throws CoreException
    {
        return getAttribute(false, ATTR_EXCLUDE_SCOPE_TEST, true);
    }

    /**
     * Set to true, if Maven dependencies with the test scope should be excluded.
     * 
     * @param value true, if Maven dependencies with the test scope should be excluded
     * @throws CoreException on occasion
     */
    public void setScopeTestExcluded(boolean value) throws CoreException
    {
        setAttribute(false, ATTR_EXCLUDE_SCOPE_TEST, value);
    }

    /**
     * Returns true, if Maven dependencies with the system scope should be excluded.
     * 
     * @return true, if Maven dependencies with the system scope should be excluded
     * @throws CoreException on occasion
     */
    public boolean isScopeSystemExcluded() throws CoreException
    {
        return getAttribute(false, ATTR_EXCLUDE_SCOPE_SYSTEM, true);
    }

    /**
     * Set to true, if Maven dependencies with the system scope should be excluded.
     * 
     * @param value true, if Maven dependencies with the system scope should be excluded
     * @throws CoreException on occasion
     */
    public void setScopeSystemExcluded(boolean value) throws CoreException
    {
        setAttribute(false, ATTR_EXCLUDE_SCOPE_SYSTEM, value);
    }

    /**
     * Returns true, if Maven dependencies with the import scope should be excluded.
     * 
     * @return true, if Maven dependencies with the import scope should be excluded
     * @throws CoreException on occasion
     */
    public boolean isScopeImportExcluded() throws CoreException
    {
        return getAttribute(false, ATTR_EXCLUDE_SCOPE_IMPORT, true);
    }

    /**
     * Set to true, if Maven dependencies with the import scope should be excluded.
     * 
     * @param value true, if Maven dependencies with the import scope should be excluded
     * @throws CoreException on occasion
     */
    public void setScopeImportExcluded(boolean value) throws CoreException
    {
        setAttribute(false, ATTR_EXCLUDE_SCOPE_IMPORT, value);
    }

    /**
     * Returns true, if dependencies unknown to Maven should be excluded.
     * 
     * @return true, if dependencies unknown to Maven should be excluded
     * @throws CoreException on occasion
     */
    public boolean isScopeNoneExcluded() throws CoreException
    {
        if (!hasAttribute(ATTR_EXCLUDE_SCOPE_NONE))
        {
            return JettyPluginM2EUtils.getMavenProjectFacade(this) != null;
        }

        return getAttribute(false, ATTR_EXCLUDE_SCOPE_NONE, false);
    }

    /**
     * Set to true, if dependencies unknown to Maven should be excluded.
     * 
     * @param value true, if dependencies unknown to Maven should be excluded
     * @throws CoreException on occasion
     */
    public void setScopeNoneExcluded(boolean value) throws CoreException
    {
        setAttribute(false, ATTR_EXCLUDE_SCOPE_NONE, value);
    }

    /**
     * @deprecated Replaced by mechanism using generic ids
     */
    @Deprecated
    public String getExcludedLibs() throws CoreException
    {
        return getAttribute(false, ATTR_EXCLUDED_LIBS, ".*servlet-api.*"); //$NON-NLS-1$
    }

    /**
     * @deprecated Replaced by mechanism using generic ids
     */
    @Deprecated
    public void setExcludedLibs(String excludedLibs) throws CoreException
    {
        setAttribute(false, ATTR_EXCLUDED_LIBS, excludedLibs);
    }

    /**
     * @deprecated Replaced by mechanism using generic ids
     */
    @Deprecated
    public String getIncludedLibs() throws CoreException
    {
        return getAttribute(false, ATTR_INCLUDED_LIBS, JettyPluginUtils.EMPTY);
    }

    /**
     * @deprecated Replaced by mechanism using generic ids
     */
    @Deprecated
    public void setIncludedLibs(String includedLibs) throws CoreException
    {
        setAttribute(false, ATTR_INCLUDED_LIBS, includedLibs);
    }

    /**
     * Returns all generic ids of dependencies, that should be explicitly excluded.
     * 
     * @return all generic ids of dependencies, that should be explicitly excluded
     * @throws CoreException on occasion
     */
    public Collection<String> getExcludedGenericIds() throws CoreException
    {
        return JettyPluginUtils.fromCommaSeparatedString(getAttribute(false, ATTR_EXCLUDED_GENERIC_IDS,
            JettyPluginUtils.EMPTY));
    }

    /**
     * Sets all generic ids of dependencies, that should be explicitly excluded.
     * 
     * @param excludedGenericIds all generic ids of dependencies, that should be explicitly excluded
     * @throws CoreException on occasion
     */
    public void setExcludedGenericIds(Collection<String> excludedGenericIds) throws CoreException
    {
        setAttribute(false, ATTR_EXCLUDED_GENERIC_IDS, JettyPluginUtils.toCommaSeparatedString(excludedGenericIds));
    }

    /**
     * Returns all generic ids of dependencies, that should be explicitly included.
     * 
     * @return all generic ids of dependencies, that should be explicitly included
     * @throws CoreException on occasion
     */
    public Collection<String> getIncludedGenericIds() throws CoreException
    {
        return JettyPluginUtils.fromCommaSeparatedString(getAttribute(false, ATTR_INCLUDED_GENERIC_IDS,
            JettyPluginUtils.EMPTY));
    }

    /**
     * Sets all generic ids of dependencies, that should be explicitly included.
     * 
     * @param includedGenericIds all generic ids of dependencies, that should be explicitly included
     * @throws CoreException on occasion
     */
    public void setIncludedGenericIds(Collection<String> includedGenericIds) throws CoreException
    {
        setAttribute(false, ATTR_INCLUDED_GENERIC_IDS, JettyPluginUtils.toCommaSeparatedString(includedGenericIds));
    }

    /**
     * @deprecated Replaced by mechanism using generic ids
     */
    @Deprecated
    public String getGlobalLibs() throws CoreException
    {
        return getAttribute(false, ATTR_GLOBAL_LIBS, JettyPluginUtils.EMPTY);
    }

    /**
     * @deprecated Replaced by mechanism using generic ids
     */
    @Deprecated
    public void setGlobalLibs(String globalLibs) throws CoreException
    {
        setAttribute(false, ATTR_GLOBAL_LIBS, globalLibs);
    }

    /**
     * Returns all generic ids of dependencies, that should be part of the Jetty classpath, rather than the web
     * application classpath.
     * 
     * @return all generic ids of dependencies, that should be part of the Jetty classpath, rather than the web
     *         application classpath
     * @throws CoreException on occasion
     */
    public Collection<String> getGlobalGenericIds() throws CoreException
    {
        return JettyPluginUtils.fromCommaSeparatedString(getAttribute(false, ATTR_GLOBAL_GENERIC_IDS,
            JettyPluginUtils.EMPTY));
    }

    /**
     * Sets all generic ids of dependencies, that should be part of the Jetty classpath, rather than the web application
     * classpath.
     * 
     * @param globalGenericIds all generic ids of dependencies, that should be part of the Jetty classpath, rather than
     *            the web application classpath
     * @throws CoreException on occasion
     */
    public void setGlobalGenericIds(Collection<String> globalGenericIds) throws CoreException
    {
        setAttribute(false, ATTR_GLOBAL_GENERIC_IDS, JettyPluginUtils.toCommaSeparatedString(globalGenericIds));
    }

    /**
     * Returns true, if the launch should display it's launch info.
     * 
     * @return true, if the launch should display it's launch info
     * @throws CoreException on occasion
     */
    public boolean isShowLauncherInfo() throws CoreException
    {
        return getAttribute(true, ATTR_SHOW_LAUNCHER_INFO, true);
    }

    /**
     * Set to true, if the launch should display it's launch info
     * 
     * @param value true, if the launch should display it's launch info
     * @throws CoreException on occasion
     */
    public void setShowLauncherInfo(boolean value) throws CoreException
    {
        setAttribute(true, ATTR_SHOW_LAUNCHER_INFO, value);
    }

    /**
     * Returns true, if the console of the launcher is available.
     * 
     * @return true, if the console of the launcher is available
     * @throws CoreException on occasion
     */
    public boolean isConsoleEnabled() throws CoreException
    {
        return getAttribute(true, ATTR_CONSOLE_ENABLED, true);
    }

    /**
     * Set to true, if the console of the launcher is available
     * 
     * @param value true, if the console of the launcher is available
     * @throws CoreException on occasion
     */
    public void setConsoleEnabled(boolean value) throws CoreException
    {
        setAttribute(true, ATTR_CONSOLE_ENABLED, value);
    }

    /**
     * Sets the default classpath provider for the Jetty plugin
     * 
     * @param classpathProvider the classpath provider
     * @throws CoreException on occasion
     */
    public void setClasspathProvider(String classpathProvider) throws CoreException
    {
        setAttribute(false, IJavaLaunchConfigurationConstants.ATTR_CLASSPATH_PROVIDER, classpathProvider);
    }

    /**
     * Returns the main type name of the Jetty plugin.
     * 
     * @return the main type name of the Jetty plugin
     * @throws CoreException on occasion
     */
    public String getMainTypeName() throws CoreException
    {
        return getAttribute(false, IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, JettyPluginUtils.EMPTY);
    }

    /**
     * Sets the main type name of the Jetty plugin.
     * 
     * @param jettyVersion the version
     * @throws CoreException on occasion
     */
    public void setMainTypeName(JettyVersion jettyVersion) throws CoreException
    {
        setAttribute(false, IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, jettyVersion.getType()
            .getMainClass());
    }

    /**
     * Returns true if the generic id mechanis is available.
     * 
     * @return true if the generic id mechanis is available
     * @throws CoreException on occasion
     */
    public boolean isGenericIdsSupported() throws CoreException
    {
        return getConfigVersion() >= 1;
    }

    /**
     * Returns the specified attribute.
     * 
     * @param globalFallback true to fallback to the global definition
     * @param name the name of the attribute
     * @param defaultValue the default value
     * @return the value of the attribute, the default one if not found.
     * @throws CoreException on occasion
     */
    protected boolean getAttribute(boolean globalFallback, String name, boolean defaultValue) throws CoreException
    {
        return configuration.getAttribute(
            name,
            (globalFallback) ? JettyPlugin.getDefaultScope().getNode(JettyPlugin.PLUGIN_ID)
                .getBoolean(name, defaultValue) : defaultValue);
    }

    /**
     * Returns the specified attribute.
     * 
     * @param globalFallback true to fallback to the global definition
     * @param name the name of the attribute
     * @param defaultValue the default value
     * @return the value of the attribute, the default one if not found.
     * @throws CoreException on occasion
     */
    protected int getAttribute(boolean globalFallback, String name, int defaultValue) throws CoreException
    {
        return configuration.getAttribute(name,
            (globalFallback) ? JettyPlugin.getDefaultScope().getNode(JettyPlugin.PLUGIN_ID).getInt(name, defaultValue)
                : defaultValue);
    }

    /**
     * Returns the specified attribute.
     * 
     * @param globalFallback true to fallback to the global definition
     * @param name the name of the attribute
     * @param defaultValue the default value
     * @return the value of the attribute, the default one if not found.
     * @throws CoreException on occasion
     */
    protected String getAttribute(boolean globalFallback, String name, String defaultValue) throws CoreException
    {
        return configuration.getAttribute(name,
            (globalFallback) ? JettyPlugin.getDefaultScope().getNode(JettyPlugin.PLUGIN_ID).get(name, defaultValue)
                : defaultValue);
    }

    /**
     * Returns true if the specified attribute exists in the configuration.
     * 
     * @param name the name of the attribute
     * @return true if exists
     * @throws CoreException on occasion
     */
    protected boolean hasAttribute(String name) throws CoreException
    {
        try
        {
            configuration.getClass().getMethod("hasAttribute", String.class); //$NON-NLS-1$

            return configuration.hasAttribute(name);
        }
        catch (SecurityException e)
        {
            JettyPlugin.error(Messages.adapter_noHasAttribute, e);
        }
        catch (NoSuchMethodException e)
        {
            JettyPlugin.warning(Messages.adapter_noDefaultScope, e);
        }

        return configuration.getAttributes().containsKey(name);
    }

    /**
     * Sets the specified attribute.
     * 
     * @param globalFallback true if the value should be written to the global configuration, too
     * @param name the name of the attribute
     * @param value the value
     * @throws CoreException on occasion
     */
    protected void setAttribute(boolean globalFallback, String name, boolean value) throws CoreException
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

    /**
     * Sets the specified attribute.
     * 
     * @param globalFallback true if the value should be written to the global configuration, too
     * @param name the name of the attribute
     * @param value the value
     * @throws CoreException on occasion
     */
    protected void setAttribute(boolean globalFallback, String name, int value) throws CoreException
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

    /**
     * Sets the specified attribute.
     * 
     * @param globalFallback true if the value should be written to the global configuration, too
     * @param name the name of the attribute
     * @param value the value
     * @throws CoreException on occasion
     */
    protected void setAttribute(boolean globalFallback, String name, String value) throws CoreException
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
