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

/**
 * Describes the version of the JSP support
 * 
 * @author Manfred Hantschel
 */
public enum JspSupport
{

    JSP_DISABLED("false"),

    JSP_2_0("2.0"),

    JSP_2_1("2.1", "true");

    private final String[] values;

    private JspSupport(String... values)
    {
        this.values = values;
    }

    public String getValue()
    {
        return values[0];
    }

    public boolean is(String value)
    {
        for (String s : values)
        {
            if (s.equals(value))
            {
                return true;
            }
        }

        return false;
    }

    public static JspSupport toJspSupport(String value)
    {
        for (JspSupport jspSupport : values())
        {
            if (jspSupport.is(value))
            {
                return jspSupport;
            }
        }

        // return default value
        return JSP_2_1;
    }

}
