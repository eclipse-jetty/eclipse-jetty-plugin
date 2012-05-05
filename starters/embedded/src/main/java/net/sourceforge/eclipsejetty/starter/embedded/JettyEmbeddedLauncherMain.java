// Licensed under the Apache License, Version 2.0 (the "License");
package net.sourceforge.eclipsejetty.starter.embedded;

import java.io.File;
import java.io.FileInputStream;

import net.sourceforge.eclipsejetty.starter.common.AbstractJettyLauncherMain;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.xml.XmlConfiguration;

/**
 * Main for Jetty 6
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

    /* (non-Javadoc)
     * @see net.sourceforge.eclipsejetty.jetty5.Jetty5LauncherMain#printLogo()
     */
    @Override
    protected void printLogo()
    {
        System.out.println("   ____    ___                   __    __  __         ___");
        System.out.println("  / __/___/ (_)__  ___ ___   __ / /__ / /_/ /___ __  ( _ )");
        System.out.println(" / _// __/ / / _ \\(_-</ -_) / // / -_) __/ __/ // / / _  |");
        System.out.println("/___/\\__/_/_/ .__/___/\\__/  \\___/\\__/\\__/\\__/\\_, /  \\___/");
        System.out.println("           /_/                              /___/");
    }

    @Override
    protected void start(File configurationFile) throws Exception
    {
        XmlConfiguration configuration;
        FileInputStream in = new FileInputStream(configurationFile);

        try
        {
            configuration = new XmlConfiguration(in);
        }
        finally
        {
            in.close();
        }

        Server server = new Server();

        configuration.configure(server);

        server.start();
    }

}
