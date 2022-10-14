package net.sourceforge.eclipsejetty.jetty11;

import net.sourceforge.eclipsejetty.jetty.JettyVersionType;
import net.sourceforge.eclipsejetty.jetty10.Jetty10ServerConfiguration;

public class Jetty11ServerConfiguration extends Jetty10ServerConfiguration 
{
	@Override
	protected JettyVersionType getJettyVersionType()
	{
		return JettyVersionType.JETTY_11;
	}
}
