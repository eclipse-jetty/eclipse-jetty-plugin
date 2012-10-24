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
    public static final String WEBAPP_CONFIGURATION_KEY = "jetty.launcher.webAppConfiguration";
    public static final String HIDE_LAUNCH_INFO_KEY = "jetty.launcher.hideLaunchInfo";

    protected void launch(String[] args) throws Exception
    {
        boolean showInfo = System.getProperty(HIDE_LAUNCH_INFO_KEY) == null;

        if (showInfo)
        {
            printLogo();
            System.out.println();
        }

        String configurationFileDef = System.getProperty(CONFIGURATION_KEY);

        if (configurationFileDef == null)
        {
            throw new IOException("-D" + CONFIGURATION_KEY + " missing");
        }

        File[] configurationFiles = getConfigurationFiles(configurationFileDef);
        File[] webAppConfigurationFiles = getConfigurationFiles(System.getProperty(WEBAPP_CONFIGURATION_KEY));
        
        start(configurationFiles, webAppConfigurationFiles, showInfo);
    }

    protected abstract void start(File[] configurationFiles, File[] webAppConfigurationFiles, boolean showInfo) throws Exception;

    protected abstract void printLogo();

    private static File[] getConfigurationFiles(String definition) throws IOException
    {
        String[] definitions = definition.split(File.pathSeparator);
        File[] files = new File[definitions.length];

        for (int i = 0; i < definitions.length; i += 1)
        {
            files[i] = new File(definitions[i]);

            if (!files[i].canRead())
            {
                throw new IOException("Cannot read configuration file: " + files[i]);
            }
        }
        return files;
    }

}
