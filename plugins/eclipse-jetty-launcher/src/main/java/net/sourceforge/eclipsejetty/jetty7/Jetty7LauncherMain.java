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
package net.sourceforge.eclipsejetty.jetty7;

import net.sourceforge.eclipsejetty.jetty.AbstractJettyLauncherMain;
import net.sourceforge.eclipsejetty.jetty.JettyConfiguration;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * Main for Jetty 7
 * 
 * @author Christian K&ouml;berl
 * @author Manfred Hantschel
 */
public class Jetty7LauncherMain extends AbstractJettyLauncherMain
{
    public static void main(final String[] args) throws Exception
    {
        new Jetty7LauncherMain().launch(args);
    }

    /* (non-Javadoc)
     * @see net.sourceforge.eclipsejetty.jetty.AbstractJettyLauncherMain#printLogo()
     */
    @Override
    protected void printLogo()
    {
        System.out.println("   ____    ___                   __    __  __         ____");
        System.out.println("  / __/___/ (_)__  ___ ___   __ / /__ / /_/ /___ __  /_  /");
        System.out.println(" / _// __/ / / _ \\(_-</ -_) / // / -_) __/ __/ // /   / /");
        System.out.println("/___/\\__/_/_/ .__/___/\\__/  \\___/\\__/\\__/\\__/\\_, /   /_/");
        System.out.println("           /_/                              /___/");
    }

    /* (non-Javadoc)
     * @see net.sourceforge.eclipsejetty.jetty.AbstractJettyLauncherMain#start(net.sourceforge.eclipsejetty.jetty.JettyConfiguration)
     */
    @Override
    protected void start(JettyConfiguration configuration) throws Exception
    {
        Server server = new Server(configuration.getPort());
        WebAppContext context = new WebAppContext(configuration.getWebAppDir(), configuration.getContext());

        context.setExtraClasspath(link(configuration.getClasspath()));

        server.setHandler(context);
        server.start();
        server.join();
    }

}
