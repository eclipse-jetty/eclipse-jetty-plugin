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

import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

public class JettyLauncherMain
{
	public static void main(String[] args) throws Exception
	{
		if (args == null || args.length == 0)
		{
			System.out.println("Usage: java JettyLauncherMain -port {port} -context {context} -webapp {path to webapp}");
			return;
		}

		int port = 8080;
		String context = "/";
		String webappPath = "src/main/webapp";
		for(int i = 0; i < args.length; i++)
		{
			if("-port".equals(args[i]))
				port = Integer.parseInt(args[++i]);
			if("-context".equals(args[i]))
				context = args[++i];
			if("-webapp".equals(args[i]))
				webappPath = args[++i];
		}

		System.out.printf("Launching Jetty with port %s, context %s and webapp path %s", port, context, webappPath);
		System.out.println();

		launch(port, context, webappPath);
	}

	public static void launch(int port, String contextPath, String webappPath) throws Exception
	{
		if (contextPath == null || contextPath.trim().equals(""))
		{
			contextPath = "/";
		}

		// setup server
		Server server = new Server(port);
		WebAppContext webAppContext = new WebAppContext(webappPath, contextPath);
		webAppContext.setServerClasses(new String[]{"-org.mortbay.jetty.plus.jaas.", "org.mortbay.jetty."});
		server.addHandler(webAppContext);

		// start server
		server.start();
		server.join();
	}
}
