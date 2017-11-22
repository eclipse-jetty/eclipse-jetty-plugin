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
package net.sourceforge.eclipsejetty.jetty6;

import net.sourceforge.eclipsejetty.jetty.AbstractWebDefaults;
import net.sourceforge.eclipsejetty.util.DOMBuilder;

public class Jetty6WebDefaults extends AbstractWebDefaults
{

    public Jetty6WebDefaults()
    {
        super();
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.jetty.AbstractWebDefaults#appendContextParams(net.sourceforge.eclipsejetty.util.DOMBuilder)
     */
    @Override
    protected void appendContextParams(DOMBuilder builder)
    {
        appendContextParam(builder, "org.mortbay.jetty.webapp.NoTLDJarPattern",
            "start.jar|ant-.*\\.jar|dojo-.*\\.jar|jetty-.*\\.jar|jsp-api-.*\\.jar|junit-.*\\.jar|servlet-api-.*\\.jar|"
                + "dnsns\\.jar|rt\\.jar|jsse\\.jar|tools\\.jar|sunpkcs11\\.jar|sunjce_provider\\.jar|xerces.*\\.jar");
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.jetty.AbstractWebDefaults#appendListeners(net.sourceforge.eclipsejetty.util.DOMBuilder)
     */
    @Override
    protected void appendListeners(DOMBuilder builder)
    {
        // intentionally left blank
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.jetty.AbstractWebDefaults#getDefaultServletClass()
     */
    @Override
    protected String getDefaultServletClass()
    {
        return "org.mortbay.jetty.servlet.DefaultServlet";
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.jetty.AbstractWebDefaults#appendInitParams(net.sourceforge.eclipsejetty.util.DOMBuilder)
     */
    @Override
    protected void appendInitParams(DOMBuilder builder)
    {
        super.appendInitParams(builder);

        appendInitParam(builder, "aliases", false);
        appendInitParam(builder, "cacheType", "both");
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.jetty.AbstractWebDefaults#getMaxCacheSize()
     */
    @Override
    protected int getMaxCacheSize()
    {
        return 256000000;
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.jetty.AbstractWebDefaults#getMaxCachedFileSize()
     */
    @Override
    protected int getMaxCachedFileSize()
    {
        return 10000000;
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.jetty.AbstractWebDefaults#getMaxCachedFiles()
     */
    @Override
    protected int getMaxCachedFiles()
    {
        return 1000;
    }

}
