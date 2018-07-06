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
package net.sourceforge.eclipsejetty.starter.util.service;

import java.util.ArrayList;
import java.util.List;

/**
 * A global implementation of the service resolver
 * 
 * @author Manfred Hantschel
 */
public class GlobalServiceResolver implements ServiceResolver
{

    public static final GlobalServiceResolver INSTANCE = new GlobalServiceResolver();

    private final List<Object> instances = new ArrayList<Object>();

    private GlobalServiceResolver()
    {
        super();
    }

    /**
     * Registers an service.
     * 
     * @param instance the instance of the service
     */
    public void register(Object instance)
    {
        instances.add(instance);
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.util.service.ServiceResolver#resolve(java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    public <TYPE> TYPE resolve(Class<TYPE> type)
    {
        for (Object instance : instances)
        {
            if (type.isInstance(instance))
            {
                return (TYPE) instance;
            }
        }

        return null;
    }

}
