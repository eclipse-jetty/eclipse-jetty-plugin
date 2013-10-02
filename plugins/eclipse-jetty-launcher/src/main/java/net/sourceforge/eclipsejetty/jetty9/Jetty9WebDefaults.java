package net.sourceforge.eclipsejetty.jetty9;

import net.sourceforge.eclipsejetty.jetty7.Jetty7WebDefaults;
import net.sourceforge.eclipsejetty.util.DOMBuilder;

public class Jetty9WebDefaults extends Jetty7WebDefaults
{

    public Jetty9WebDefaults()
    {
        super();
    }

    @Override
    protected void appendInitParams(DOMBuilder builder)
    {
        super.appendInitParams(builder);
        
        appendInitParam(builder, "etags", true);
    }
    
    
}