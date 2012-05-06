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
        boolean showInfo = System.getProperty(HIDE_LAUNCH_INFO_KEY) == null;
        
        if (showInfo)
        {
            printLogo();
            System.out.println();
        }

        File configurationFile = new File(System.getProperty(CONFIGURATION_KEY));

        if (!configurationFile.canRead())
        {
            throw new IOException("Configuration file is missing: " + configurationFile);
        }

        start(configurationFile, showInfo);

        configurationFile.delete();
    }

    protected abstract void start(File configurationFile, boolean showInfo) throws Exception;

    protected abstract void printLogo();

}
