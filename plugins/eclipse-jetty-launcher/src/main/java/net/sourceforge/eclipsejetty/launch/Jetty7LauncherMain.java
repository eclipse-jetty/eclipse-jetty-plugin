package net.sourceforge.eclipsejetty.launch;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class Jetty7LauncherMain
{
	public static void main(String[] args) throws Exception
	{
		if (args == null || args.length == 0)
		{
			System.out.println("Usage: java Jetty7LauncherMain -port {port} -context {context} -webapp {path to webapp}");
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

		WebAppContext context = new WebAppContext(webappPath, contextPath);
		context.setClassLoader(Thread.currentThread().getContextClassLoader());
		server.setHandler(context);

		// start server
		server.start();
		server.join();
	}

}
