package net.sourceforge.eclipsejetty.jetty;

import net.sourceforge.eclipsejetty.util.DOMBuilder;

public abstract class AbstractWebDefaults extends AbstractBuilder
{

    private boolean serverCacheEnabled = true;
    private boolean clientCacheEnabled = true;

    public AbstractWebDefaults()
    {
        super();
    }

    public boolean isServerCacheEnabled()
    {
        return serverCacheEnabled;
    }

    public void setServerCacheEnabled(boolean serverCacheEnabled)
    {
        this.serverCacheEnabled = serverCacheEnabled;
    }

    public boolean isClientCacheEnabled()
    {
        return clientCacheEnabled;
    }

    public void setClientCacheEnabled(boolean clientCacheEnabled)
    {
        this.clientCacheEnabled = clientCacheEnabled;
    }

    @Override
    protected void buildBody(DOMBuilder builder)
    {
        builder.begin("web-app");
        builder.attribute("xmlns", "http://java.sun.com/xml/ns/javaee");
        builder.attribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        builder.attribute("xsi:schemaLocation",
            "http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd");
        builder.attribute("metadata-complete", "true");
        builder.attribute("version", "2.5");
        {
            buildContent(builder);
        }
        builder.end();
    }

    protected void buildContent(DOMBuilder builder)
    {
        buildContextParams(builder);
        buildListeners(builder);
        buildDefaultServlet(builder);
        buildDefaultServletMapping(builder);
        buildJSPServlet(builder);
        buildJSPServletMapping(builder);
        buildSessioConfig(builder);
        buildWelcomeFileList(builder);
        buildLocaleEncodingMappingList(builder);
        buildSecurityConstraints(builder);
    }

    protected void buildContextParams(DOMBuilder builder)
    {
        appendContextParams(builder);
    }

    protected abstract void appendContextParams(DOMBuilder builder);

    protected void appendContextParam(DOMBuilder builder, String name, Object value)
    {
        builder.begin("context-param");
        {
            builder.element("param-name", name);
            builder.element("param-value", value);
        }
        builder.end();
    }

    protected void buildListeners(DOMBuilder builder)
    {
        appendListeners(builder);
    }

    protected abstract void appendListeners(DOMBuilder builder);

    protected void appendListener(DOMBuilder builder, String listenerClass)
    {
        builder.begin("listener");
        {
            builder.element("listener-class", listenerClass);
        }
        builder.end();
    }

    private void buildDefaultServlet(DOMBuilder builder)
    {
        builder.begin("servlet");
        {
            builder.element("servlet-name", "default");
            builder.element("servlet-class", getDefaultServletClass());

            appendInitParams(builder);

            builder.element("load-on-startup", 0);
        }
        builder.end();
    }

    protected void appendInitParams(DOMBuilder builder)
    {
        appendInitParam(builder, "acceptRanges", true);
        appendInitParam(builder, "dirAllowed", true);
        appendInitParam(builder, "welcomeServlets", false);
        appendInitParam(builder, "redirectWelcome", false);
        appendInitParam(builder, "maxCacheSize", (serverCacheEnabled) ? getMaxCacheSize() : 0);
        appendInitParam(builder, "maxCachedFileSize", (serverCacheEnabled) ? getMaxCachedFileSize() : 0);
        appendInitParam(builder, "maxCachedFiles", (serverCacheEnabled) ? getMaxCachedFiles() : 0);
        appendInitParam(builder, "gzip", true);
        appendInitParam(builder, "useFileMappedBuffer", false);

        if (!clientCacheEnabled)
        {
            appendInitParam(builder, "cacheControl", "max-age=0, public");
        }
    }

    protected abstract int getMaxCacheSize();

    protected abstract int getMaxCachedFileSize();

    protected abstract int getMaxCachedFiles();

    protected abstract String getDefaultServletClass();

    protected void appendInitParam(DOMBuilder builder, String name, Object value)
    {
        builder.begin("init-param");
        {
            builder.element("param-name", name);
            builder.element("param-value", value);
        }
        builder.end();
    }

    protected void buildDefaultServletMapping(DOMBuilder builder)
    {
        builder.begin("servlet-mapping");
        {
            builder.element("servlet-name", "default");
            appendURLPattern(builder, "/");
        }
        builder.end();
    }

    protected void appendURLPattern(DOMBuilder builder, String urlPattern)
    {
        builder.element("url-pattern", urlPattern);
    }

    protected void buildJSPServlet(DOMBuilder builder)
    {
        builder.begin("servlet").attribute("id", "jsp");
        {
            builder.element("servlet-name", "jsp");
            builder.element("servlet-class", "org.apache.jasper.servlet.JspServlet");

            appendInitParam(builder, "logVerbosityLevel", "DEBUG");
            appendInitParam(builder, "fork", false);
            appendInitParam(builder, "xpoweredBy", false);

            builder.element("load-on-startup", 0);
        }
        builder.end();
    }

    protected void buildJSPServletMapping(DOMBuilder builder)
    {
        builder.begin("servlet-mapping");
        {
            builder.element("servlet-name", "jsp");
            appendURLPattern(builder, "*.jsp");
            appendURLPattern(builder, "*.jspf");
            appendURLPattern(builder, "*.jspx");
            appendURLPattern(builder, "*.xsp");
            appendURLPattern(builder, "*.JSP");
            appendURLPattern(builder, "*.JSPF");
            appendURLPattern(builder, "*.JSPX");
            appendURLPattern(builder, "*.XSP");
        }
        builder.end();
    }

    protected void buildSessioConfig(DOMBuilder builder)
    {
        builder.begin("session-config");
        {
            builder.element("session-timeout", 30);
        }
        builder.end();
    }

    protected void buildWelcomeFileList(DOMBuilder builder)
    {
        builder.begin("welcome-file-list");
        {
            appendWelcomeFile(builder, "index.html");
            appendWelcomeFile(builder, "index.htm");
            appendWelcomeFile(builder, "index.jsp");
        }
        builder.end();
    }

    protected void appendWelcomeFile(DOMBuilder builder, String welcomeFile)
    {
        builder.element("welcome-file", welcomeFile);
    }

    protected void buildLocaleEncodingMappingList(DOMBuilder builder)
    {
        builder.begin("locale-encoding-mapping-list");
        {
            appendLocaleEncodingMapping(builder, "ar", "ISO-8859-6");
            appendLocaleEncodingMapping(builder, "be", "ISO-8859-5");
            appendLocaleEncodingMapping(builder, "bg", "ISO-8859-5");
            appendLocaleEncodingMapping(builder, "ca", "ISO-8859-1");
            appendLocaleEncodingMapping(builder, "cs", "ISO-8859-2");
            appendLocaleEncodingMapping(builder, "da", "ISO-8859-1");
            appendLocaleEncodingMapping(builder, "de", "ISO-8859-1");
            appendLocaleEncodingMapping(builder, "el", "ISO-8859-7");
            appendLocaleEncodingMapping(builder, "en", "ISO-8859-1");
            appendLocaleEncodingMapping(builder, "es", "ISO-8859-1");
            appendLocaleEncodingMapping(builder, "et", "ISO-8859-1");
            appendLocaleEncodingMapping(builder, "fi", "ISO-8859-1");
            appendLocaleEncodingMapping(builder, "fr", "ISO-8859-1");
            appendLocaleEncodingMapping(builder, "hr", "ISO-8859-2");
            appendLocaleEncodingMapping(builder, "hu", "ISO-8859-2");
            appendLocaleEncodingMapping(builder, "is", "ISO-8859-1");
            appendLocaleEncodingMapping(builder, "it", "ISO-8859-1");
            appendLocaleEncodingMapping(builder, "iw", "ISO-8859-8");
            appendLocaleEncodingMapping(builder, "ja", "Shift_JIS");
            appendLocaleEncodingMapping(builder, "ko", "EUC-KR");
            appendLocaleEncodingMapping(builder, "lt", "ISO-8859-2");
            appendLocaleEncodingMapping(builder, "lv", "ISO-8859-2");
            appendLocaleEncodingMapping(builder, "mk", "ISO-8859-5");
            appendLocaleEncodingMapping(builder, "nl", "ISO-8859-1");
            appendLocaleEncodingMapping(builder, "no", "ISO-8859-1");
            appendLocaleEncodingMapping(builder, "pl", "ISO-8859-2");
            appendLocaleEncodingMapping(builder, "pt", "ISO-8859-1");
            appendLocaleEncodingMapping(builder, "ro", "ISO-8859-2");
            appendLocaleEncodingMapping(builder, "ru", "ISO-8859-5");
            appendLocaleEncodingMapping(builder, "sh", "ISO-8859-5");
            appendLocaleEncodingMapping(builder, "sk", "ISO-8859-2");
            appendLocaleEncodingMapping(builder, "sl", "ISO-8859-2");
            appendLocaleEncodingMapping(builder, "sq", "ISO-8859-2");
            appendLocaleEncodingMapping(builder, "sr", "ISO-8859-5");
            appendLocaleEncodingMapping(builder, "sv", "ISO-8859-1");
            appendLocaleEncodingMapping(builder, "tr", "ISO-8859-9");
            appendLocaleEncodingMapping(builder, "uk", "ISO-8859-5");
            appendLocaleEncodingMapping(builder, "zh", "GB2312");
            appendLocaleEncodingMapping(builder, "zh_TW", "Big5");
        }
        builder.end();
    }

    protected void appendLocaleEncodingMapping(DOMBuilder builder, String locale, String encoding)
    {
        builder.begin("locale-encoding-mapping");
        {
            builder.element("locale", locale);
            builder.element("encoding", encoding);
        }
        builder.end();
    }

    protected void buildSecurityConstraints(DOMBuilder builder)
    {
        builder.begin("security-constraint");
        {
            builder.begin("web-resource-collection");
            {
                builder.element("web-resource-name", "Disable TRACE");
                appendURLPattern(builder, "/");
                builder.element("http-method", "TRACE");
            }
            builder.end();

            builder.element("auth-constraint");
        }
        builder.end();
    }

}
