package net.sourceforge.eclipsejetty.jetty6;

import net.sourceforge.eclipsejetty.jetty.AbstractWebDefaults;
import net.sourceforge.eclipsejetty.util.DOMBuilder;

public class Jetty6WebDefaults extends AbstractWebDefaults
{

    public Jetty6WebDefaults()
    {
        super();
    }

    @Override
    protected void appendContextParams(DOMBuilder builder)
    {
        appendContextParam(builder, "org.mortbay.jetty.webapp.NoTLDJarPattern",
            "start.jar|ant-.*\\.jar|dojo-.*\\.jar|jetty-.*\\.jar|jsp-api-.*\\.jar|junit-.*\\.jar|servlet-api-.*\\.jar|"
                + "dnsns\\.jar|rt\\.jar|jsse\\.jar|tools\\.jar|sunpkcs11\\.jar|sunjce_provider\\.jar|xerces.*\\.jar");
    }

    @Override
    protected void appendListeners(DOMBuilder builder)
    {
        // intentionally left blank
    }

    @Override
    protected String getDefaultServletClass()
    {
        return "org.mortbay.jetty.servlet.DefaultServlet";
    }

    @Override
    protected void appendInitParams(DOMBuilder builder)
    {
        super.appendInitParams(builder);

        appendInitParam(builder, "aliases", false);
        appendInitParam(builder, "cacheType", "both");
    }

    @Override
    protected int getMaxCacheSize()
    {
        return 256000000;
    }

    @Override
    protected int getMaxCachedFileSize()
    {
        return 10000000;
    }

    @Override
    protected int getMaxCachedFiles()
    {
        return 1000;
    }
    
}
