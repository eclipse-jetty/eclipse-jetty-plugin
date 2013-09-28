// Licensed under the Apache License, Version 2.0 (the "License");
package net.sourceforge.eclipsejetty.starter.embedded;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;

import net.sourceforge.eclipsejetty.starter.common.AbstractJettyLauncherMain;
import net.sourceforge.eclipsejetty.starter.common.ServerAdapter;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.xml.XmlConfiguration;

/**
 * Main for embedded Jetty
 * 
 * @author Christian K&ouml;berl
 * @author Manfred Hantschel
 */
public class JettyEmbeddedLauncherMain extends AbstractJettyLauncherMain
{

    public static void main(String[] args) throws Exception
    {
        new JettyEmbeddedLauncherMain().launch(args);
    }

    @Override
    protected void printLogo(PrintStream out)
    {
        out.println("   ____    ___                   __    __  __         ___");
        out.println("  / __/___/ (_)__  ___ ___   __ / /__ / /_/ /___ __  ( _ )");
        out.println(" / _// __/ / / _ \\(_-</ -_) / // / -_) __/ __/ // / / _  |");
        out.println("/___/\\__/_/_/ .__/___/\\__/  \\___/\\__/\\__/\\__/\\_, /  \\___/");
        out.println("           /_/                              /___/");
    }

    @Override
    protected ServerAdapter createAdapter(File[] configurationFiles, boolean showInfo) throws Exception
    {
        return new JettyEmbeddedAdapter(new Server());
    }

    @Override
    protected void configure(FileInputStream in, Class<?> type, ServerAdapter adapter) throws Exception
    {
        Server server = (Server) adapter.getServer();
        XmlConfiguration configuration = new XmlConfiguration(in);

        if (type.isInstance(server))
        {
            configuration.configure(server);

            return;
        }

        boolean success = false;

        for (Handler handler : server.getHandlers())
        {
            if (type.isInstance(handler))
            {
                configuration.configure(handler);

                success = true;
            }
        }

        if (success)
        {
            return;
        }

        throw new IllegalArgumentException("Failed to run configuration for " + type
            + ". No matching object found in server.");
    }

}
