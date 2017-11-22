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

import java.util.Collection;

import net.sourceforge.eclipsejetty.util.DOMBuilder;

/**
 * Abstract base class for building Jetty configurations
 * 
 * @author Manfred Hantschel
 */
public abstract class AbstractConfiguration extends AbstractBuilder
{

    public AbstractConfiguration()
    {
        super();
    }

    /**
     * Returns the version of the Jetty
     * 
     * @return the version of the Jetty
     */
    protected abstract JettyVersionType getJettyVersionType();

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.jetty.AbstractBuilder#buildBody(net.sourceforge.eclipsejetty.jetty.JettyConfigBuilder)
     */
    @Override
    protected void buildBody(DOMBuilder domBuilder)
    {
        JettyConfigBuilder builder = new JettyConfigBuilder(domBuilder, getJettyVersionType());

        builder.beginConfigure(getIdToConfigure(), getClassToConfigure());
        {
            buildContent(builder);
        }
        builder.end();
    }

    /**
     * Returns the doc type
     * 
     * @return the doc type
     */
    protected abstract String getDocType();

    /**
     * Returns the server id to be configured
     * 
     * @return the server id
     */
    protected abstract String getIdToConfigure();

    /**
     * Returns the server class to be configured
     * 
     * @return the server class
     */
    protected abstract String getClassToConfigure();

    /**
     * Builds the main content
     * 
     * @param builder the builder
     */
    protected abstract void buildContent(JettyConfigBuilder builder);

    /**
     * Creates a semicolon separated list of values.
     * 
     * @param values the values
     * @return the list
     */
    protected String link(Collection<String> values)
    {
        return link(values.toArray(new String[values.size()]));
    }

    /**
     * Creates a semicolon separated list of values.
     * 
     * @param values the values
     * @return the list
     */
    protected String link(String... values)
    {
        StringBuilder result = new StringBuilder();

        if (values != null)
        {
            for (int i = 0; i < values.length; i += 1)
            {
                if (i > 0)
                {
                    // result.append(File.pathSeparator); // it seems, Jetty was built for Windows
                    result.append(";");
                }

                result.append(values[i]);
            }
        }

        return result.toString();
    }
}
