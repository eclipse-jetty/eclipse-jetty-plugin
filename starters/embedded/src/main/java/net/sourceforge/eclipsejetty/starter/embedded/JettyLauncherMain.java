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
package net.sourceforge.eclipsejetty.starter.embedded;

import net.sourceforge.eclipsejetty.starter.jetty9.Jetty9LauncherMain;

/**
 * Main for Jetty 9
 * 
 * @author Christian K&ouml;berl
 * @author Manfred Hantschel
 */
public class JettyLauncherMain extends Jetty9LauncherMain
{

    /**
     * Calls the {@link #launch(String[])} method
     * 
     * @param args the arguments
     * @throws Exception on occasion
     */
    public static void main(String[] args) throws Exception
    {
        new JettyLauncherMain().launch(args);
    }
}
