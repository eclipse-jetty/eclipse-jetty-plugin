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
	public static final String PLUGIN_ID = "net.sourceforge.eclipse-jetty-launcher";

	private static final String JETTY_ICON = PLUGIN_ID + ".jettyIcon";

	// The shared instance
	private static JettyPlugin plugin;

	/**
	 * The constructor
	 */
	public JettyPlugin()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
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
	protected void initializeImageRegistry(ImageRegistry reg)
	{
		URL imageURL = getBundle().getEntry("/icons/jetty.gif");
		if (imageURL != null)
		{
			ImageDescriptor descriptor = ImageDescriptor.createFromURL(imageURL);
			reg.put(JETTY_ICON, descriptor);
		}
		else
		{
			logError("resource " + "/icons/jetty.gif" + " was not found");
		}
	}

	public static Image getJettyIcon()
	{
		return plugin.getImageRegistry().get(JETTY_ICON);
	}

	public static void logError(Exception e)
	{
		e.printStackTrace();
	}

	public static void logError(String error)
	{
		System.err.println(error);
	}
}
