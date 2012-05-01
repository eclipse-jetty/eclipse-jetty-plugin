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

import net.sourceforge.eclipsejetty.jetty6.Jetty6LauncherMain;
import net.sourceforge.eclipsejetty.jetty6.Jetty6LibStrategy;
import net.sourceforge.eclipsejetty.jetty7.Jetty7LauncherMain;
import net.sourceforge.eclipsejetty.jetty7.Jetty7LibStrategy;
import net.sourceforge.eclipsejetty.jetty8.Jetty8LauncherMain;
import net.sourceforge.eclipsejetty.jetty8.Jetty8LibStrategy;

/**
 * Describes the version of the Jetty
 * 
 * @author Manfred Hantschel
 */
public enum JettyVersion
{

    JETTY_AUTO_DETECT("auto", null, null),

    JETTY_6("6", Jetty6LauncherMain.class, new Jetty6LibStrategy(), JspSupport.JSP_2_1, JspSupport.JSP_2_0),

    JETTY_7("7", Jetty7LauncherMain.class, new Jetty7LibStrategy(), JspSupport.JSP_2_1),

    JETTY_8("8", Jetty8LauncherMain.class, new Jetty8LibStrategy(), JspSupport.JSP_2_2);

    private final String value;
    private final Class<?> mainClass;
    private final IJettyLibStrategy libStrategy;
    private final JspSupport[] jspSupports;

    private JettyVersion(String value, Class<?> mainClass, IJettyLibStrategy libStrategy, JspSupport... jspSupports)
    {
        this.value = value;
        this.mainClass = mainClass;
        this.libStrategy = libStrategy;
        this.jspSupports = jspSupports;
    }

    public String getValue()
    {
        return value;
    }

    public Class<?> getMainClass()
    {
        return mainClass;
    }

    public String getMainClassName()
    {
        return (mainClass != null) ? mainClass.getName() : null;
    }

    public IJettyLibStrategy getLibStrategy()
    {
        return libStrategy;
    }

    public boolean isJspSupported()
    {
        return jspSupports.length > 0;
    }

    public boolean containsJspSupport(JspSupport jspSupport)
    {
        for (JspSupport current : jspSupports)
        {
            if (current == jspSupport)
            {
                return true;
            }
        }

        return false;
    }

    public static JettyVersion toJettyVersion(String value)
    {
        for (JettyVersion jettyVersion : values())
        {
            if (jettyVersion.value.equals(value))
            {
                return jettyVersion;
            }
        }

        // return default value
        return JETTY_AUTO_DETECT;
    }

}
