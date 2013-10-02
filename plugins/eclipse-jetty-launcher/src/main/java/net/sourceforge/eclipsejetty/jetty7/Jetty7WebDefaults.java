package net.sourceforge.eclipsejetty.jetty7;

import net.sourceforge.eclipsejetty.jetty.AbstractWebDefaults;
import net.sourceforge.eclipsejetty.util.DOMBuilder;

public class Jetty7WebDefaults extends AbstractWebDefaults
{

    public Jetty7WebDefaults()
    {
        super();
    }

    @Override
    protected void appendContextParams(DOMBuilder builder)
    {
        // intentionally left blank
    }

    @Override
    protected void appendListeners(DOMBuilder builder)
    {
        appendListener(builder, "org.eclipse.jetty.servlet.listener.ELContextCleaner");
        appendListener(builder, "org.eclipse.jetty.servlet.listener.IntrospectorCleaner");
    }

    @Override
    protected String getDefaultServletClass()
    {
        return "org.eclipse.jetty.servlet.DefaultServlet";
    }

    @Override
    protected int getMaxCacheSize()
    {
        return 256000000;
    }

    @Override
    protected int getMaxCachedFileSize()
    {
        return 200000000;
    }

    @Override
    protected int getMaxCachedFiles()
    {
        return 2048;
    }

}
