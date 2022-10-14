package net.sourceforge.eclipsejetty.jetty10;

import net.sourceforge.eclipsejetty.jetty.JettyConfigBuilder;
import net.sourceforge.eclipsejetty.jetty.JettyVersionType;
import net.sourceforge.eclipsejetty.jetty9.Jetty9ServerConfiguration;

public class Jetty10ServerConfiguration extends Jetty9ServerConfiguration {
	
	@Override
	protected JettyVersionType getJettyVersionType() {
		return JettyVersionType.JETTY_10;
	}
	
	@Override
	protected void buildHttpsConfig(JettyConfigBuilder builder) {
		super.buildHttpsConfig(builder, "org.eclipse.jetty.util.ssl.SslContextFactory$Server");
	}
	
	@Override
	protected void buildAnnotations(JettyConfigBuilder builder) {
	}
	
	@Override
	protected void buildJNDI(JettyConfigBuilder builder) {
	}
}
