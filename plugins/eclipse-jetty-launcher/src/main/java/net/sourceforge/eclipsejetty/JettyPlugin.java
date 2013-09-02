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

import java.net.URL;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
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

    private static final String JETTY_ICON = PLUGIN_ID + ".jettyIcon";
    private static final String JETTY_ADVANCED_ICON = PLUGIN_ID + ".jettyAdvancedIcon";
    private static final String ADD_CONTEXT_ICON = PLUGIN_ID + ".addContext";
    private static final String REMOVE_CONTEXT_ICON = PLUGIN_ID + ".removeContext";
    private static final String MOVE_UP_CONTEXT_ICON = PLUGIN_ID + ".moveUpContext";
    private static final String MOVE_DOWN_CONTEXT_ICON = PLUGIN_ID + ".moveDownContext";

    // The shared instance
    private static JettyPlugin plugin;

    /**
     * The constructor
     */
    public JettyPlugin()
    {
        super();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(final BundleContext context) throws Exception
    {
        super.start(context);
        plugin = this;
    }

    /*
     * (non-Javadoc)
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

    @Override
    protected void initializeImageRegistry(final ImageRegistry reg)
    {
        initializeImageRegistry(reg, JETTY_ICON, "/icons/jetty.png");
        initializeImageRegistry(reg, JETTY_ADVANCED_ICON, "/icons/jetty-advanced.png");

        initializeImageRegistry(reg, ADD_CONTEXT_ICON, "/icons/add_context.gif");
        initializeImageRegistry(reg, REMOVE_CONTEXT_ICON, "/icons/remove_context.gif");
        initializeImageRegistry(reg, MOVE_UP_CONTEXT_ICON, "/icons/move_up_context.gif");
        initializeImageRegistry(reg, MOVE_DOWN_CONTEXT_ICON, "/icons/move_down_context.gif");
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
            error("Resource " + url + " was not found");
        }
    }

    public static Image getJettyIcon()
    {
        return plugin.getImageRegistry().get(JETTY_ICON);
    }

    public static Image getJettyAdvancedIcon()
    {
        return plugin.getImageRegistry().get(JETTY_ADVANCED_ICON);
    }

    public static Image getAddContextIcon()
    {
        return plugin.getImageRegistry().get(ADD_CONTEXT_ICON);
    }

    public static Image getRemoveContextIcon()
    {
        return plugin.getImageRegistry().get(REMOVE_CONTEXT_ICON);
    }

    public static Image getMoveUpContextIcon()
    {
        return plugin.getImageRegistry().get(MOVE_UP_CONTEXT_ICON);
    }

    public static Image getMoveDownContextIcon()
    {
        return plugin.getImageRegistry().get(MOVE_DOWN_CONTEXT_ICON);
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

}
