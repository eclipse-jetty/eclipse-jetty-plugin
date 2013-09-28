// Licensed under the Apache License, Version 2.0 (the "License");
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

public class ServiceUtils
{

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
                throw new IllegalArgumentException("Failed to instantiate " + contribution
                    + ". There must be exactly one constructor");
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
            throw new IllegalArgumentException("Failed to access " + contribution + " for security reasons", e);
        }
        catch (IllegalArgumentException e)
        {
            throw new IllegalArgumentException("Failed to instantiate " + contribution, e);
        }
        catch (InstantiationException e)
        {
            throw new IllegalArgumentException("Failed to instantiate " + contribution, e);
        }
        catch (IllegalAccessException e)
        {
            throw new IllegalArgumentException("Failed to access " + contribution, e);
        }
        catch (InvocationTargetException e)
        {
            throw new IllegalArgumentException("Failed to instantiate " + contribution, e);
        }
        catch (ClassNotFoundException e)
        {
            throw new IllegalArgumentException("Failed to instantiate " + contribution + ". Class not found", e);
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
