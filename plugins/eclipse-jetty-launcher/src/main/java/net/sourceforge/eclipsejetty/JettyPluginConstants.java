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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sourceforge.eclipsejetty.jetty.JettyConfig;
import net.sourceforge.eclipsejetty.jetty.JettyConfigType;
import net.sourceforge.eclipsejetty.jetty.JettyVersion;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.DefaultScope;
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
public class JettyPluginConstants
{

    public static final String LAUNCH_CONFIG_TYPE = JettyPlugin.PLUGIN_ID + ".launchConfigurationType";
    public static final String CLASSPATH_PROVIDER_JETTY = JettyPlugin.PLUGIN_ID + ".JettyLaunchClassPathProvider";

    private static final int CONFIG_VERSION = 1;

    private static final String ATTR_CONFIG_VERSION = JettyPlugin.PLUGIN_ID + ".configVersion";
    private static final String ATTR_CONTEXT = JettyPlugin.PLUGIN_ID + ".context";
    private static final String ATTR_WEBAPPDIR = JettyPlugin.PLUGIN_ID + ".webappdir";
    private static final String ATTR_PORT = JettyPlugin.PLUGIN_ID + ".port";
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

    public static int getConfigVersion(ILaunchConfiguration configuration) throws CoreException
    {
        return configuration.getAttribute(ATTR_CONFIG_VERSION, 0);
    }

    public static void updateConfigVersion(ILaunchConfigurationWorkingCopy configuration)
    {
        configuration.setAttribute(ATTR_CONFIG_VERSION, CONFIG_VERSION);
    }

    /**
     * Returns the name of the selected eclipse project, that should be launched
     * 
     * @param configuration the configuration
     * @return the project
     * @throws CoreException on occasion
     */
    public static String getProject(ILaunchConfiguration configuration) throws CoreException
    {
        return configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "");
    }

    /**
     * Sets the name of the selected eclipse project, that should be launched
     * 
     * @param configuration the configuration
     * @param project the project
     */
    public static void setProject(ILaunchConfigurationWorkingCopy configuration, String project)
    {
        configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, project);
    }

    /**
     * Returns the context path (path part of the URL) of the application
     * 
     * @param configuration the configuration
     * @return the context path
     * @throws CoreException on occasion
     */
    public static String getContext(ILaunchConfiguration configuration) throws CoreException
    {
        return configuration.getAttribute(ATTR_CONTEXT, "/");
    }

    /**
     * Sets the context path (path part of the URL) of the application
     * 
     * @param configuration the configuration
     * @param context the context
     */
    public static void setContext(ILaunchConfigurationWorkingCopy configuration, String context)
    {
        configuration.setAttribute(ATTR_CONTEXT, context);
    }

    /**
     * Returns the location of the webapp directory in the workspace
     * 
     * @param configuration the configuration
     * @return the location of the webapp directory
     * @throws CoreException on occasion
     */
    public static String getWebAppDir(ILaunchConfiguration configuration) throws CoreException
    {
        return configuration.getAttribute(ATTR_WEBAPPDIR, "src/main/webapp");
    }

    /**
     * Sets the location of the webapp directory in the workspace
     * 
     * @param configuration the configuration
     * @param webappdir the location of the webapp directory
     */
    public static void setWebAppDir(ILaunchConfigurationWorkingCopy configuration, String webappdir)
    {
        configuration.setAttribute(ATTR_WEBAPPDIR, webappdir);
    }

    /**
     * Returns the (HTTP) port
     * 
     * @param configuration the configuration
     * @return the port
     * @throws CoreException on occasion
     */
    public static String getPort(ILaunchConfiguration configuration) throws CoreException
    {
        return configuration.getAttribute(ATTR_PORT, "8080");
    }

    /**
     * Sets the (HTTP) port
     * 
     * @param configuration the configuration
     * @param port the port
     */
    public static void setPort(ILaunchConfigurationWorkingCopy configuration, String port)
    {
        configuration.setAttribute(ATTR_PORT, port);
    }

    public static String getPath(ILaunchConfiguration configuration) throws CoreException
    {
        return configuration.getAttribute(ATTR_JETTY_PATH,
            DefaultScope.INSTANCE.getNode(JettyPlugin.PLUGIN_ID).get(ATTR_JETTY_PATH, ""));
    }

    public static void setPath(ILaunchConfigurationWorkingCopy configuration, String path)
    {
        configuration.setAttribute(ATTR_JETTY_PATH, path);

        DefaultScope.INSTANCE.getNode(JettyPlugin.PLUGIN_ID).put(ATTR_JETTY_PATH, path);

        try
        {
            DefaultScope.INSTANCE.getNode(JettyPlugin.PLUGIN_ID).flush();
        }
        catch (BackingStoreException e)
        {
            // ignore
        }
    }

    public static boolean isEmbedded(ILaunchConfiguration configuration) throws CoreException
    {
        return configuration.getAttribute(ATTR_JETTY_EMBEDDED, DefaultScope.INSTANCE.getNode(JettyPlugin.PLUGIN_ID)
            .getBoolean(ATTR_JETTY_EMBEDDED, true));
    }

    public static void setEmbedded(ILaunchConfigurationWorkingCopy configuration, boolean extern)
    {
        configuration.setAttribute(ATTR_JETTY_EMBEDDED, extern);

        DefaultScope.INSTANCE.getNode(JettyPlugin.PLUGIN_ID).putBoolean(ATTR_JETTY_EMBEDDED, extern);

        try
        {
            DefaultScope.INSTANCE.getNode(JettyPlugin.PLUGIN_ID).flush();
        }
        catch (BackingStoreException e)
        {
            // ignore
        }
    }

    public static JettyVersion getVersion(ILaunchConfiguration configuration) throws CoreException
    {
        return JettyVersion.valueOf(configuration.getAttribute(ATTR_JETTY_VERSION, JettyVersion.JETTY_EMBEDDED.name()));
    }

    public static void setVersion(ILaunchConfigurationWorkingCopy configuration, JettyVersion jettyVersion)
    {
        configuration.setAttribute(ATTR_JETTY_VERSION, jettyVersion.name());
    }

    /**
     * Returns the configuration context holders
     * 
     * @param configuration the configuration
     * @return a list of {@link JettyConfig}s
     * @throws CoreException on occasion
     */
    public static List<JettyConfig> getConfigs(ILaunchConfiguration configuration) throws CoreException
    {
        List<JettyConfig> results = new ArrayList<JettyConfig>();
        int index = 0;

        while (true)
        {
            String path = configuration.getAttribute(ATTR_JETTY_CONFIG_PATH + index, (String) null);

            if (path == null)
            {
                break;
            }

            JettyConfigType type =
                JettyConfigType.valueOf(configuration.getAttribute(ATTR_JETTY_CONFIG_TYPE + index,
                    JettyConfigType.PATH.name()));
            boolean active = configuration.getAttribute(ATTR_JETTY_CONFIG_ACTIVE + index, true);

            results.add(new JettyConfig(path, type, active));
            index += 1;
        }

        if (results.size() == 0)
        {
            results.add(new JettyConfig("", JettyConfigType.DEFAULT, true));
        }

        return results;
    }

    public static void setConfigs(ILaunchConfigurationWorkingCopy configuration, List<JettyConfig> entries)
        throws CoreException
    {
        int index = 0;

        for (JettyConfig entry : entries)
        {
            configuration.setAttribute(ATTR_JETTY_CONFIG_PATH + index, entry.getPath());
            configuration.setAttribute(ATTR_JETTY_CONFIG_TYPE + index, entry.getType().name());
            configuration.setAttribute(ATTR_JETTY_CONFIG_ACTIVE + index, entry.isActive());

            index += 1;
        }

        while (configuration.getAttribute(ATTR_JETTY_CONFIG_PATH + index, (String) null) != null)
        {
            configuration.removeAttribute(ATTR_JETTY_CONFIG_PATH + index);
            configuration.removeAttribute(ATTR_JETTY_CONFIG_TYPE + index);
            configuration.removeAttribute(ATTR_JETTY_CONFIG_ACTIVE + index);

            index += 1;
        }
    }

    public static boolean isJspSupport(ILaunchConfiguration configuration) throws CoreException
    {
        return !"false".equals(configuration.getAttribute(ATTR_JSP_ENABLED, "true")); // string for backward compatibility
    }

    public static void setJspSupport(ILaunchConfigurationWorkingCopy configuration, boolean jspSupport)
    {
        configuration.setAttribute(ATTR_JSP_ENABLED, String.valueOf(jspSupport)); // string for backward compatibility
    }

    public static boolean isJmxSupport(ILaunchConfiguration configuration) throws CoreException
    {
        return "true".equals(configuration.getAttribute(ATTR_JMX_ENABLED, "false")); // string for backward compatibility
    }

    public static void setJmxSupport(ILaunchConfigurationWorkingCopy configuration, boolean jmxSupport)
    {
        configuration.setAttribute(ATTR_JMX_ENABLED, String.valueOf(jmxSupport)); // string for backward compatibility
    }

    public static boolean isJndiSupport(ILaunchConfiguration configuration) throws CoreException
    {
        return "true".equals(configuration.getAttribute(ATTR_JNDI_ENABLED, "false")); // string for backward compatibility
    }

    public static void setJndiSupport(ILaunchConfigurationWorkingCopy configuration, boolean jndiSupport)
    {
        configuration.setAttribute(ATTR_JNDI_ENABLED, String.valueOf(jndiSupport)); // string for backward compatibility
    }

    public static boolean isAjpSupport(ILaunchConfiguration configuration) throws CoreException
    {
        return "true".equals(configuration.getAttribute(ATTR_AJP_ENABLED, "false")); // string for backward compatibility
    }

    public static void setAjpSupport(ILaunchConfigurationWorkingCopy configuration, boolean ajpSupport)
    {
        configuration.setAttribute(ATTR_AJP_ENABLED, String.valueOf(ajpSupport)); // string for backward compatibility
    }

    public static boolean isScopeCompileExcluded(ILaunchConfiguration configuration) throws CoreException
    {
        return configuration.getAttribute(ATTR_EXCLUDE_SCOPE_COMPILE, false);
    }

    public static void setScopeCompileExcluded(ILaunchConfigurationWorkingCopy configuration, boolean value)
    {
        configuration.setAttribute(ATTR_EXCLUDE_SCOPE_COMPILE, value);
    }

    public static boolean isScopeProvidedExcluded(ILaunchConfiguration configuration) throws CoreException
    {
        return configuration.getAttribute(ATTR_EXCLUDE_SCOPE_PROVIDED, true);
    }

    public static void setScopeProvidedExcluded(ILaunchConfigurationWorkingCopy configuration, boolean value)
    {
        configuration.setAttribute(ATTR_EXCLUDE_SCOPE_PROVIDED, value);
    }

    public static boolean isScopeRuntimeExcluded(ILaunchConfiguration configuration) throws CoreException
    {
        return configuration.getAttribute(ATTR_EXCLUDE_SCOPE_RUNTIME, false);
    }

    public static void setScopeRuntimeExcluded(ILaunchConfigurationWorkingCopy configuration, boolean value)
    {
        configuration.setAttribute(ATTR_EXCLUDE_SCOPE_RUNTIME, value);
    }

    public static boolean isScopeTestExcluded(ILaunchConfiguration configuration) throws CoreException
    {
        return configuration.getAttribute(ATTR_EXCLUDE_SCOPE_TEST, true);
    }

    public static void setScopeTestExcluded(ILaunchConfigurationWorkingCopy configuration, boolean value)
    {
        configuration.setAttribute(ATTR_EXCLUDE_SCOPE_TEST, value);
    }

    public static boolean isScopeSystemExcluded(ILaunchConfiguration configuration) throws CoreException
    {
        return configuration.getAttribute(ATTR_EXCLUDE_SCOPE_SYSTEM, true);
    }

    public static void setScopeSystemExcluded(ILaunchConfigurationWorkingCopy configuration, boolean value)
    {
        configuration.setAttribute(ATTR_EXCLUDE_SCOPE_SYSTEM, value);
    }

    public static boolean isScopeImportExcluded(ILaunchConfiguration configuration) throws CoreException
    {
        return configuration.getAttribute(ATTR_EXCLUDE_SCOPE_IMPORT, true);
    }

    public static void setScopeImportExcluded(ILaunchConfigurationWorkingCopy configuration, boolean value)
    {
        configuration.setAttribute(ATTR_EXCLUDE_SCOPE_IMPORT, value);
    }

    public static boolean isScopeNoneExcluded(ILaunchConfiguration configuration) throws CoreException
    {
        return configuration.getAttribute(ATTR_EXCLUDE_SCOPE_NONE, false);
    }

    public static void setScopeNoneExcluded(ILaunchConfigurationWorkingCopy configuration, boolean value)
    {
        configuration.setAttribute(ATTR_EXCLUDE_SCOPE_NONE, value);
    }

    /**
     * @deprecated Replaced by mechanism using generic ids
     */
    @Deprecated
    public static String getExcludedLibs(ILaunchConfiguration configuration) throws CoreException
    {
        return configuration.getAttribute(ATTR_EXCLUDED_LIBS, ".*servlet-api.*");
    }

    /**
     * @deprecated Replaced by mechanism using generic ids
     */
    @Deprecated
    public static void setExcludedLibs(ILaunchConfigurationWorkingCopy configuration, String excludedLibs)
    {
        configuration.setAttribute(ATTR_EXCLUDED_LIBS, excludedLibs);
    }

    /**
     * @deprecated Replaced by mechanism using generic ids
     */
    @Deprecated
    public static String getIncludedLibs(ILaunchConfiguration configuration) throws CoreException
    {
        return configuration.getAttribute(ATTR_INCLUDED_LIBS, "");
    }

    /**
     * @deprecated Replaced by mechanism using generic ids
     */
    @Deprecated
    public static void setIncludedLibs(ILaunchConfigurationWorkingCopy configuration, String includedLibs)
    {
        configuration.setAttribute(ATTR_INCLUDED_LIBS, includedLibs);
    }

    public static Collection<String> getExcludedGenericIds(ILaunchConfiguration configuration) throws CoreException
    {
        return JettyPluginUtils.fromCommaSeparatedString(configuration.getAttribute(ATTR_EXCLUDED_GENERIC_IDS, ""));
    }

    public static void setExcludedGenericIds(ILaunchConfigurationWorkingCopy configuration,
        Collection<String> excludedGenericIds)
    {
        configuration.setAttribute(ATTR_EXCLUDED_GENERIC_IDS,
            JettyPluginUtils.toCommaSeparatedString(excludedGenericIds));
    }

    public static Collection<String> getIncludedGenericIds(ILaunchConfiguration configuration) throws CoreException
    {
        return JettyPluginUtils.fromCommaSeparatedString(configuration.getAttribute(ATTR_INCLUDED_GENERIC_IDS, ""));
    }

    public static void setIncludedGenericIds(ILaunchConfigurationWorkingCopy configuration,
        Collection<String> includedGenericIds)
    {
        configuration.setAttribute(ATTR_INCLUDED_GENERIC_IDS,
            JettyPluginUtils.toCommaSeparatedString(includedGenericIds));
    }

    /**
     * @deprecated Replaced by mechanism using generic ids
     */
    @Deprecated
    public static String getGlobalLibs(ILaunchConfiguration configuration) throws CoreException
    {
        return configuration.getAttribute(ATTR_GLOBAL_LIBS, "");
    }

    /**
     * @deprecated Replaced by mechanism using generic ids
     */
    @Deprecated
    public static void setGlobalLibs(ILaunchConfigurationWorkingCopy configuration, String globalLibs)
    {
        configuration.setAttribute(ATTR_GLOBAL_LIBS, globalLibs);
    }

    public static Collection<String> getGlobalGenericIds(ILaunchConfiguration configuration) throws CoreException
    {
        return JettyPluginUtils.fromCommaSeparatedString(configuration.getAttribute(ATTR_GLOBAL_GENERIC_IDS, ""));
    }

    public static void setGlobalGenericIds(ILaunchConfigurationWorkingCopy configuration,
        Collection<String> globalGenericIds)
    {
        configuration.setAttribute(ATTR_GLOBAL_GENERIC_IDS, JettyPluginUtils.toCommaSeparatedString(globalGenericIds));
    }

    public static boolean isShowLauncherInfo(ILaunchConfiguration configuration) throws CoreException
    {
        return configuration.getAttribute(ATTR_SHOW_LAUNCHER_INFO, true);
    }

    public static void setShowLauncherInfo(ILaunchConfigurationWorkingCopy configuration, boolean value)
    {
        configuration.setAttribute(ATTR_SHOW_LAUNCHER_INFO, value);
    }

    public static void setClasspathProvider(ILaunchConfigurationWorkingCopy configuration, String classpathProvider)
    {
        configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH_PROVIDER, classpathProvider);
    }

    public static String getMainTypeName(ILaunchConfiguration configuration) throws CoreException
    {
        return configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, "");
    }

    public static void setMainTypeName(ILaunchConfigurationWorkingCopy configuration, JettyVersion jettyVersion)
    {
        configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, jettyVersion.getMainClass());
    }

    public static boolean isGenericIdsSupported(ILaunchConfiguration configuration) throws CoreException
    {
        return getConfigVersion(configuration) >= 1;
    }

}
