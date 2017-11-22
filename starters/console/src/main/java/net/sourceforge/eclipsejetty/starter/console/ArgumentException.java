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
package net.sourceforge.eclipsejetty.starter.console;

/**
 * Common exceptions with arguments.
 * 
 * @author Manfred Hantschel
 */
public class ArgumentException extends ProcessException
{

    private static final long serialVersionUID = 4671208137366239322L;

    public ArgumentException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ArgumentException(String message)
    {
        super(message);
    }

}
