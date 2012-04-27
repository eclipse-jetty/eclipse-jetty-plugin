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

import net.sourceforge.eclipsejetty.jetty.JettyVersion;
import net.sourceforge.eclipsejetty.jetty.JspSupport;

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

    private static final String ATTR_CONTEXT = JettyPlugin.PLUGIN_ID + ".context";
    private static final String ATTR_WEBAPPDIR = JettyPlugin.PLUGIN_ID + ".webappdir";
    private static final String ATTR_PORT = JettyPlugin.PLUGIN_ID + ".port";
    private static final String ATTR_JETTY_PATH = JettyPlugin.PLUGIN_ID + ".jetty.path";
    private static final String ATTR_JETTY_VERSION = JettyPlugin.PLUGIN_ID + ".jetty.version";
    private static final String ATTR_JSP_ENABLED = JettyPlugin.PLUGIN_ID + ".jsp.enabled";
    private static final String ATTR_EXCLUDE_SCOPE_COMPILE = JettyPlugin.PLUGIN_ID + ".scope.compile.exclude";
    private static final String ATTR_EXCLUDE_SCOPE_PROVIDED = JettyPlugin.PLUGIN_ID + ".scope.provided.exclude";
    private static final String ATTR_EXCLUDE_SCOPE_RUNTIME = JettyPlugin.PLUGIN_ID + ".scope.runtime.exclude";
    private static final String ATTR_EXCLUDE_SCOPE_TEST = JettyPlugin.PLUGIN_ID + ".scope.test.exclude";
    private static final String ATTR_EXCLUDE_SCOPE_SYSTEM = JettyPlugin.PLUGIN_ID + ".scope.system.exclude";
    private static final String ATTR_EXCLUDED_LIBS = JettyPlugin.PLUGIN_ID + ".launcher.excludeLibs";

    public static String getProject(ILaunchConfiguration configuration) throws CoreException
    {
        return configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "");
    }

    public static void setProject(ILaunchConfigurationWorkingCopy configuration, String project)
    {
        configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, project);
    }

    public static String getContext(ILaunchConfiguration configuration) throws CoreException
    {
        return configuration.getAttribute(ATTR_CONTEXT, "/");
    }

    public static void setContext(ILaunchConfigurationWorkingCopy configuration, String context)
    {
        configuration.setAttribute(ATTR_CONTEXT, context);
    }

    public static String getWebAppDir(ILaunchConfiguration configuration) throws CoreException
    {
        return configuration.getAttribute(ATTR_WEBAPPDIR, "src/main/webapp");
    }

    public static void setWebAppDir(ILaunchConfigurationWorkingCopy configuration, String webappdir)
    {
        configuration.setAttribute(ATTR_WEBAPPDIR, webappdir);
    }

    public static String getPort(ILaunchConfiguration configuration) throws CoreException
    {
        return configuration.getAttribute(ATTR_PORT, "8080");
    }

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

    public static JettyVersion getVersion(ILaunchConfiguration configuration) throws CoreException
    {
        return JettyVersion.toJettyVersion(configuration.getAttribute(ATTR_JETTY_VERSION, (String) null));
    }

    public static void setVersion(ILaunchConfigurationWorkingCopy configuration, JettyVersion jettyVersion)
    {
        configuration.setAttribute(ATTR_JETTY_VERSION, jettyVersion.getValue());
    }

    public static JspSupport getJspSupport(ILaunchConfiguration configuration) throws CoreException
    {
        return JspSupport.toJspSupport(configuration.getAttribute(ATTR_JSP_ENABLED, (String) null));
    }

    public static void setJspSupport(ILaunchConfigurationWorkingCopy configuration, JspSupport jspSupport)
    {
        configuration.setAttribute(ATTR_JSP_ENABLED, jspSupport.getValue());
    }

    public static boolean isScopeCompileExcluded(ILaunchConfiguration configuration) throws CoreException
    {
        return configuration.getAttribute(ATTR_EXCLUDE_SCOPE_COMPILE, false);
    }

    public static void setScopeCompileExcluded(ILaunchConfigurationWorkingCopy configuration, boolean value)
        throws CoreException
    {
        configuration.setAttribute(ATTR_EXCLUDE_SCOPE_COMPILE, value);
    }

    public static boolean isScopeProvidedExcluded(ILaunchConfiguration configuration) throws CoreException
    {
        return configuration.getAttribute(ATTR_EXCLUDE_SCOPE_PROVIDED, false);
    }

    public static void setScopeProvidedExcluded(ILaunchConfigurationWorkingCopy configuration, boolean value)
        throws CoreException
    {
        configuration.setAttribute(ATTR_EXCLUDE_SCOPE_PROVIDED, value);
    }

    public static boolean isScopeRuntimeExcluded(ILaunchConfiguration configuration) throws CoreException
    {
        return configuration.getAttribute(ATTR_EXCLUDE_SCOPE_RUNTIME, true);
    }

    public static void setScopeRuntimeExcluded(ILaunchConfigurationWorkingCopy configuration, boolean value)
        throws CoreException
    {
        configuration.setAttribute(ATTR_EXCLUDE_SCOPE_RUNTIME, value);
    }

    public static boolean isScopeTestExcluded(ILaunchConfiguration configuration) throws CoreException
    {
        return configuration.getAttribute(ATTR_EXCLUDE_SCOPE_TEST, true);
    }

    public static void setScopeTestExcluded(ILaunchConfigurationWorkingCopy configuration, boolean value)
        throws CoreException
    {
        configuration.setAttribute(ATTR_EXCLUDE_SCOPE_TEST, value);
    }

    public static boolean isScopeSystemExcluded(ILaunchConfiguration configuration) throws CoreException
    {
        return configuration.getAttribute(ATTR_EXCLUDE_SCOPE_SYSTEM, true);
    }

    public static void setScopeSystemExcluded(ILaunchConfigurationWorkingCopy configuration, boolean value)
        throws CoreException
    {
        configuration.setAttribute(ATTR_EXCLUDE_SCOPE_SYSTEM, value);
    }

    public static String getExcludedLibs(ILaunchConfiguration configuration) throws CoreException
    {
        return configuration.getAttribute(ATTR_EXCLUDED_LIBS, ".*servlet-api.*"); //, .*selenium-server.*standalone\\.jar");
    }

    public static void setExcludedLibs(ILaunchConfigurationWorkingCopy configuration, String excludedLibs)
    {
        configuration.setAttribute(ATTR_EXCLUDED_LIBS, excludedLibs);
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
        configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
            jettyVersion.getMainClassName());
    }

}
