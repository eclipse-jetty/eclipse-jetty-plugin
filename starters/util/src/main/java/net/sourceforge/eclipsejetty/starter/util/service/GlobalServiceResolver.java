// Licensed under the Apache License, Version 2.0 (the "License");
package net.sourceforge.eclipsejetty.starter.util.service;

import java.util.ArrayList;
import java.util.List;

public class GlobalServiceResolver implements ServiceResolver
{

    public static final GlobalServiceResolver INSTANCE = new GlobalServiceResolver();

    private final List<Object> instances = new ArrayList<Object>();

    private GlobalServiceResolver()
    {
        super();
    }

    public void register(Object instance)
    {
        instances.add(instance);
    }

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
