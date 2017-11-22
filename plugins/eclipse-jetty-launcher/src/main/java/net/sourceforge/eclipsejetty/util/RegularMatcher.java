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
package net.sourceforge.eclipsejetty.util;

import java.io.Serializable;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * A wrapper for a regular expression
 * 
 * @author Manfred Hantschel
 */
public class RegularMatcher implements Serializable
{

    private static final long serialVersionUID = 2985303399762300662L;

    private final String regex;
    private final Pattern pattern;

    public RegularMatcher(String regex)
    {
        super();

        this.regex = regex;

        Pattern pattern;

        try
        {
            pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        }
        catch (PatternSyntaxException e)
        {
            pattern = null;
        }

        this.pattern = pattern;
    }

    public boolean matches(String value)
    {

        if ((pattern != null) && (pattern.matcher(value).matches()))
        {
            return true;
        }

        return regex.equalsIgnoreCase(value);
    }

    @Override
    public String toString()
    {
        return regex;
    }

}
