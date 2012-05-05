// Licensed under the Apache License, Version 2.0 (the "License");
package net.sourceforge.eclipsejetty.starter.common;

import java.io.File;
import java.io.IOException;

/**
 * Abstract base class for the Jetty launcher
 * 
 * @author Manfred Hantschel
 */
public abstract class AbstractJettyLauncherMain
{

    public static final String CONFIGURATION_KEY = "jetty.launcher.configuration";
    public static final String HIDE_LAUNCH_INFO_KEY = "jetty.launcher.hideLaunchInfo";

    protected void launch(String[] args) throws Exception
    {
        if (System.getProperty(HIDE_LAUNCH_INFO_KEY) == null)
        {
            printLogo();
            System.out.println();
            //
            //            printConfiguration(configuration);
            //            System.out.println();
        }

        File configurationFile = new File(System.getProperty(CONFIGURATION_KEY));

        if (!configurationFile.canRead())
        {
            throw new IOException("Configuration file is missing: " + configurationFile);
        }

        start(configurationFile);

        configurationFile.delete();
    }

    protected abstract void start(File configurationFile) throws Exception;

    protected abstract void printLogo();

    //    protected void printConfiguration(JettyConfiguration configuration)
    //    {
    //        System.out.println("Context          = " + configuration.getContext());
    //        System.out.println("WebApp Directory = " + configuration.getWebAppDir());
    //        System.out.println("Port             = " + configuration.getPort());
    //
    //        String[] classpath = configuration.getClasspath();
    //
    //        for (int i = 0; i < classpath.length; i += 1)
    //        {
    //            if (i == 0)
    //            {
    //                System.out.println("Classpath        = " + classpath[i]);
    //            }
    //            else
    //            {
    //                System.out.println("                   " + classpath[i]);
    //            }
    //        }
    //    }

    //    protected String link(String[] values)
    //    {
    //        StringBuilder result = new StringBuilder();
    //
    //        if (values != null)
    //        {
    //            for (int i = 0; i < values.length; i += 1)
    //            {
    //                if (i > 0)
    //                {
    //                    result.append(File.pathSeparator);
    //                }
    //
    //                result.append(values[i]);
    //            }
    //        }
    //
    //        return result.toString();
    //    }

}
