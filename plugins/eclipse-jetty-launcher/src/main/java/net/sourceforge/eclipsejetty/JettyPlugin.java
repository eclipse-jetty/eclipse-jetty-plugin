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
import java.net.URL;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 * 
 * @author Christian K&ouml;berl
 */
public class JettyPlugin extends AbstractUIPlugin
{
    // The plug-in ID
    public static final String PLUGIN_ID = "net.sourceforge.eclipsejetty.launcher";

    public static final String JETTY_PLUGIN_LOGO = PLUGIN_ID + ".jettyPluginLogo";
    public static final String JETTY_PLUGIN_ADVANCED_LOGO = PLUGIN_ID + ".jettyPluginAdvancedLogo";
    public static final String JETTY_PLUGIN_DEPENDENCY_LOGO = PLUGIN_ID + ".jettyPluginDependencyLogo";
    public static final String JETTY_ICON = PLUGIN_ID + ".jettyIcon";
    public static final String JETTY_ADVANCED_ICON = PLUGIN_ID + ".jettyAdvancedIcon";
    public static final String JETTY_DEPENDENCY_ICON = PLUGIN_ID + ".jettyDependencyIcon";
    public static final String ADD_CONTEXT_ICON = PLUGIN_ID + ".addContext";
    public static final String REMOVE_CONTEXT_ICON = PLUGIN_ID + ".removeContext";
    public static final String MOVE_UP_CONTEXT_ICON = PLUGIN_ID + ".moveUpContext";
    public static final String MOVE_DOWN_CONTEXT_ICON = PLUGIN_ID + ".moveDownContext";
    public static final String FOLDER_ICON = PLUGIN_ID + ".folder";

    public static final String DEPENDENCY_OTHER = PLUGIN_ID + ".dependencyOther";
    public static final String DEPENDENCY_JAR = PLUGIN_ID + ".dependencyJar";
    public static final String DEPENDENCY_PROJECT = PLUGIN_ID + ".dependencyProject";
    public static final String DEPENDENCY_OTHER_DEACTIVATED = PLUGIN_ID + ".dependencyOtherDeactivated";
    public static final String DEPENDENCY_JAR_DEACTIVATED = PLUGIN_ID + ".dependencyJarDeactivated";
    public static final String DEPENDENCY_PROJECT_DEACTIVATED = PLUGIN_ID + ".dependencyProjectDeactivated";

    // The shared instance
    private static JettyPlugin plugin;

    private static File defaultKeystoreFile;
    private static IScopeContext defaultScope;

    /**
     * The constructor
     */
    public JettyPlugin()
    {
        super();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(final BundleContext context) throws Exception
    {
        super.start(context);
        plugin = this;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(final BundleContext context) throws Exception
    {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static JettyPlugin getDefault()
    {
        return plugin;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#initializeImageRegistry(org.eclipse.jface.resource.ImageRegistry)
     */
    @Override
    protected void initializeImageRegistry(final ImageRegistry reg)
    {
        initializeImageRegistry(reg, JETTY_PLUGIN_LOGO, "/icons/jetty-plugin-logo.png");
        initializeImageRegistry(reg, JETTY_PLUGIN_ADVANCED_LOGO, "/icons/jetty-plugin-advanced-logo.png");
        initializeImageRegistry(reg, JETTY_PLUGIN_DEPENDENCY_LOGO, "/icons/jetty-plugin-dependency-logo.png");

        initializeImageRegistry(reg, JETTY_ICON, "/icons/jetty.png");
        initializeImageRegistry(reg, JETTY_ADVANCED_ICON, "/icons/jetty-advanced.png");
        initializeImageRegistry(reg, JETTY_DEPENDENCY_ICON, "/icons/jetty-dependency.png");

        initializeImageRegistry(reg, ADD_CONTEXT_ICON, "/icons/add_context.gif");
        initializeImageRegistry(reg, REMOVE_CONTEXT_ICON, "/icons/remove_context.gif");
        initializeImageRegistry(reg, MOVE_UP_CONTEXT_ICON, "/icons/move_up_context.gif");
        initializeImageRegistry(reg, MOVE_DOWN_CONTEXT_ICON, "/icons/move_down_context.gif");
        initializeImageRegistry(reg, FOLDER_ICON, "/icons/folder.gif");

        initializeImageRegistry(reg, DEPENDENCY_OTHER, "/icons/dependency-other.gif");
        initializeImageRegistry(reg, DEPENDENCY_JAR, "/icons/dependency-jar.gif");
        initializeImageRegistry(reg, DEPENDENCY_PROJECT, "/icons/dependency-project.gif");
        initializeImageRegistry(reg, DEPENDENCY_OTHER_DEACTIVATED, "/icons/dependency-other-deactivated.gif");
        initializeImageRegistry(reg, DEPENDENCY_JAR_DEACTIVATED, "/icons/dependency-jar-deactivated.gif");
        initializeImageRegistry(reg, DEPENDENCY_PROJECT_DEACTIVATED, "/icons/dependency-project-deactivated.gif");
    }

    protected void initializeImageRegistry(ImageRegistry reg, String key, String url)
    {
        final URL imageURL = getBundle().getEntry(url);

        if (imageURL != null)
        {
            reg.put(key, ImageDescriptor.createFromURL(imageURL));
        }
        else
        {
            error(String.format("Resource %s not found", url));
        }
    }

    public static Image getIcon(String icon)
    {
        return plugin.getImageRegistry().get(icon);
    }

    public static Image getJettyPluginLogo()
    {
        return getIcon(JETTY_PLUGIN_LOGO);
    }

    public static Image getJettyIcon()
    {
        return getIcon(JETTY_ICON);
    }

    public static Image getJettyAdvancedIcon()
    {
        return getIcon(JETTY_ADVANCED_ICON);
    }

    public static Image getJettyDependencyIcon()
    {
        return getIcon(JETTY_DEPENDENCY_ICON);
    }

    public static Image getAddContextIcon()
    {
        return getIcon(ADD_CONTEXT_ICON);
    }

    public static Image getRemoveContextIcon()
    {
        return getIcon(REMOVE_CONTEXT_ICON);
    }

    public static Image getMoveUpContextIcon()
    {
        return getIcon(MOVE_UP_CONTEXT_ICON);
    }

    public static Image getMoveDownContextIcon()
    {
        return getIcon(MOVE_DOWN_CONTEXT_ICON);
    }

    public static void log(int severety, String message, Throwable e)
    {
        Status status;

        if (e == null)
        {
            status = new Status(severety, PLUGIN_ID, message);
        }
        else
        {
            status = new Status(severety, PLUGIN_ID, message, e);
        }

        getDefault().getLog().log(status);
    }

    public static void info(String message)
    {
        log(IStatus.INFO, message, null);
    }

    public static void info(String message, Throwable e)
    {
        log(IStatus.INFO, message, e);
    }

    public static void warning(String message)
    {
        log(IStatus.WARNING, message, null);
    }

    public static void warning(String message, Throwable e)
    {
        log(IStatus.WARNING, message, e);
    }

    public static void error(String message)
    {
        log(IStatus.ERROR, message, null);
    }

    public static void error(String message, Throwable e)
    {
        log(IStatus.ERROR, message, e);
    }

    public static File getDefaultKeystoreFile()
    {
        return defaultKeystoreFile;
    }

    public static void setDefaultKeystoreFile(File defaultKeystoreFile)
    {
        JettyPlugin.defaultKeystoreFile = defaultKeystoreFile;
    }

    @SuppressWarnings("deprecation")
    public static IScopeContext getDefaultScope()
    {
        if (defaultScope != null)
        {
            return defaultScope;
        }

        try
        {
            DefaultScope.class.getField("INSTANCE");

            defaultScope = DefaultScope.INSTANCE;

            return defaultScope;
        }
        catch (SecurityException e)
        {
            error("No DefaultScope.INSTANCE (< Eclipse 3.4)", e);
        }
        catch (NoSuchFieldException e)
        {
            warning("No DefaultScope.INSTANCE (< Eclipse 3.4)", e);
        }

        defaultScope = new DefaultScope();

        return defaultScope;
    }

}
