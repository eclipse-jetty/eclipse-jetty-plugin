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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashSet;

/**
 * Common service utilities
 * 
 * @author Manfred Hantschel
 */
public class ServiceUtils
{

    /**
     * Reads the services form the services file at META-INF/services/<type>
     * 
     * @param type the type
     * @return a collection of strings from the file
     * @throws IOException on occasion
     */
    public static Collection<String> getContributions(Class<?> type) throws IOException
    {
        Collection<String> results = new LinkedHashSet<String>();
        Enumeration<URL> resources =
            ServiceUtils.class.getClassLoader().getResources("META-INF/services/" + type.getName());

        while (resources.hasMoreElements())
        {
            URL resource = resources.nextElement();
            InputStream in = resource.openStream();

            try
            {
                read(results, in);
            }
            finally
            {
                in.close();
            }
        }

        return results;
    }

    /**
     * Reads the services form the services file at META-INF/services/<type>. Instantiate the classes. Resolves
     * constructor parameters from the specified {@link ServiceResolver}.
     * 
     * @param type the type
     * @param resolver the resolver
     * @return the instantiates classes
     * @throws IOException on occasion
     */
    public static Collection<Object> instantiateContributions(Class<?> type, ServiceResolver resolver)
        throws IOException
    {
        Collection<String> contributions = ServiceUtils.getContributions(type);
        Collection<Object> results = new ArrayList<Object>();

        for (String contribution : contributions)
        {
            results.add(instantiate(contribution, resolver));
        }

        return results;
    }

    private static Object instantiate(String contribution, ServiceResolver resolver)
    {
        try
        {
            Class<?> type = Class.forName(contribution);
            Constructor<?>[] constructors = type.getConstructors();

            if (constructors.length > 1)
            {
                throw new IllegalArgumentException(String.format(
                    "Failed to instantiate %s. There must be exactly one constructor", contribution));
            }

            Constructor<?> constructor = constructors[0];
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            Object[] parameters = new Object[parameterTypes.length];

            for (int i = 0; i < parameterTypes.length; i += 1)
            {
                parameters[i] = resolver.resolve(parameterTypes[i]);
            }

            return constructor.newInstance(parameters);
        }
        catch (SecurityException e)
        {
            throw new IllegalArgumentException(String.format("Failed to access %s for security reasons", contribution),
                e);
        }
        catch (IllegalArgumentException e)
        {
            throw new IllegalArgumentException(String.format("Failed to instantiate %s", contribution), e);
        }
        catch (InstantiationException e)
        {
            throw new IllegalArgumentException(String.format("Failed to instantiate %s", contribution), e);
        }
        catch (IllegalAccessException e)
        {
            throw new IllegalArgumentException(String.format("Failed to access %s", contribution), e);
        }
        catch (InvocationTargetException e)
        {
            throw new IllegalArgumentException(String.format("Failed to instantiate %s", contribution), e);
        }
        catch (ClassNotFoundException e)
        {
            throw new IllegalArgumentException(
                String.format("Failed to instantiate %s. Class not found", contribution), e);
        }
    }

    private static void read(Collection<String> results, InputStream in) throws IOException
    {
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

            try
            {
                String line;

                while ((line = reader.readLine()) != null)
                {
                    if (line.trim().length() == 0)
                    {
                        continue;
                    }

                    if (line.startsWith("#"))
                    {
                        continue;
                    }

                    results.add(line.trim());
                }
            }
            finally
            {
                reader.close();
            }
        }
        catch (UnsupportedEncodingException e)
        {
            throw new IllegalArgumentException("Danger! Danger! Universe imploding!", e);
        }
    }
}
