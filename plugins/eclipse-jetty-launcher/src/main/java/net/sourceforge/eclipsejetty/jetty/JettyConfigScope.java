package net.sourceforge.eclipsejetty.jetty;

public enum JettyConfigScope
{

    UNKNOWN("Unknown"),

    SERVER("Server"),

    WEBAPPCONTEXT("Web-App");

    private final String description;

    private JettyConfigScope(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }

}
