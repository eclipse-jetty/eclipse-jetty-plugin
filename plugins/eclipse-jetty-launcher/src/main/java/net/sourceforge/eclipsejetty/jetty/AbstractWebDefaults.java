// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package net.sourceforge.eclipsejetty.jetty;

import net.sourceforge.eclipsejetty.util.DOMBuilder;

/**
 * An abstract builder for the web defaults file.
 * 
 * @author Manfred Hantschel
 */
public abstract class AbstractWebDefaults extends AbstractBuilder
{

    private boolean serverCacheEnabled = true;
    private boolean clientCacheEnabled = true;

    public AbstractWebDefaults()
    {
        super();
    }

    /**
     * Returns true if the server cache should be enabled.
     * 
     * @return true if the server cache should be enabled
     */
    public boolean isServerCacheEnabled()
    {
        return serverCacheEnabled;
    }

    /**
     * Set to true if the server cache should be enabled.
     * 
     * @param serverCacheEnabled true if the server cache should be enabled
     */
    public void setServerCacheEnabled(boolean serverCacheEnabled)
    {
        this.serverCacheEnabled = serverCacheEnabled;
    }

    /**
     * Returns true if the client cache should be enabled.
     * 
     * @return true if the client cache should be enabled
     */
    public boolean isClientCacheEnabled()
    {
        return clientCacheEnabled;
    }

    /**
     * Set to true if the client cache should be enabled.
     * 
     * @param clientCacheEnabled true if the client cache should be enabled
     */
    public void setClientCacheEnabled(boolean clientCacheEnabled)
    {
        this.clientCacheEnabled = clientCacheEnabled;
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.jetty.AbstractBuilder#buildBody(net.sourceforge.eclipsejetty.util.DOMBuilder)
     */
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

    /**
     * Builds the main content part.
     * 
     * @param builder the builder
     */
    protected void buildContent(DOMBuilder builder)
    {
        buildContextParams(builder);
        buildListeners(builder);
        buildDefaultServlet(builder);
        buildDefaultServletMapping(builder);
        buildJSPServlet(builder);
        buildJSPServletMapping(builder);
        buildSessionConfig(builder);
        buildWelcomeFileList(builder);
        buildLocaleEncodingMappingList(builder);
        buildSecurityConstraints(builder);
    }

    /**
     * Builds the context parameters
     * 
     * @param builder the builder
     */
    protected void buildContextParams(DOMBuilder builder)
    {
        appendContextParams(builder);
    }

    /**
     * Append default context parameters.
     * 
     * @param builder the builder
     */
    protected abstract void appendContextParams(DOMBuilder builder);

    /**
     * Append one context parameter.
     * 
     * @param builder the builder
     * @param name the name of the parameter
     * @param value the value of the parameter
     */
    protected void appendContextParam(DOMBuilder builder, String name, Object value)
    {
        builder.begin("context-param");
        {
            builder.element("param-name", name);
            builder.element("param-value", value);
        }
        builder.end();
    }

    /**
     * Build the listeners.
     * 
     * @param builder the builder
     */
    protected void buildListeners(DOMBuilder builder)
    {
        appendListeners(builder);
    }

    /**
     * Append the default set of listeners.
     * 
     * @param builder the builder
     */
    protected abstract void appendListeners(DOMBuilder builder);

    /**
     * Append one listener.
     * 
     * @param builder the builder
     * @param listenerClass the listener class
     */
    protected void appendListener(DOMBuilder builder, String listenerClass)
    {
        builder.begin("listener");
        {
            builder.element("listener-class", listenerClass);
        }
        builder.end();
    }

    /**
     * Build the default servlet.
     * 
     * @param builder the builder
     */
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

    /**
     * Add init parameters for the default servlet.
     * 
     * @param builder the builder
     */
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

    /**
     * Returns the max cache size, needed if server cache is enabled.
     * 
     * @return the max cache size
     */
    protected abstract int getMaxCacheSize();

    /**
     * Returns the max cached file size, needed if server cache is enabled.
     * 
     * @return the max cached file size
     */
    protected abstract int getMaxCachedFileSize();

    /**
     * Returns the number of max cached files, needed if server cache is enabled.
     * 
     * @return the max cached file size
     */
    protected abstract int getMaxCachedFiles();

    protected abstract String getDefaultServletClass();

    /**
     * Append one init parameter
     * 
     * @param builder the builder
     * @param name the name of the parameter
     * @param value the value of the parameter
     */
    protected void appendInitParam(DOMBuilder builder, String name, Object value)
    {
        builder.begin("init-param");
        {
            builder.element("param-name", name);
            builder.element("param-value", value);
        }
        builder.end();
    }

    /**
     * Build the default servlet mapping.
     * 
     * @param builder the builder
     */
    protected void buildDefaultServletMapping(DOMBuilder builder)
    {
        builder.begin("servlet-mapping");
        {
            builder.element("servlet-name", "default");
            appendURLPattern(builder, "/");
        }
        builder.end();
    }

    /**
     * Append the URL pattern.
     * 
     * @param builder the builder
     * @param urlPattern the pattern
     */
    protected void appendURLPattern(DOMBuilder builder, String urlPattern)
    {
        builder.element("url-pattern", urlPattern);
    }

    /**
     * Build the JSP servlet section.
     * 
     * @param builder the builder
     */
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

    /**
     * Build the JSP servlet mapping.
     * 
     * @param builder the builder
     */
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

    /**
     * Build the session config section.
     * 
     * @param builder the builder
     */
    protected void buildSessionConfig(DOMBuilder builder)
    {
        builder.begin("session-config");
        {
            builder.element("session-timeout", 30);
        }
        builder.end();
    }

    /**
     * Build the welcome file list.
     * 
     * @param builder the builder
     */
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

    /**
     * Add one welcome file.
     * 
     * @param builder the builder
     * @param welcomeFile the welcome file
     */
    protected void appendWelcomeFile(DOMBuilder builder, String welcomeFile)
    {
        builder.element("welcome-file", welcomeFile);
    }

    /**
     * Build the locale encoding mapping list.
     * 
     * @param builder the builder
     */
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

    /**
     * Append one locale encoding mapping.
     * 
     * @param builder the builder
     * @param locale the locale
     * @param encoding the encoding
     */
    protected void appendLocaleEncodingMapping(DOMBuilder builder, String locale, String encoding)
    {
        builder.begin("locale-encoding-mapping");
        {
            builder.element("locale", locale);
            builder.element("encoding", encoding);
        }
        builder.end();
    }

    /**
     * Build the security constraints.
     * 
     * @param builder the builder
     */
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
