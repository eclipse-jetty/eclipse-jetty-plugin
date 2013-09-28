// Licensed under the Apache License, Version 2.0 (the "License");
package net.sourceforge.eclipsejetty.starter.util.service;

public interface ServiceResolver
{
    <TYPE> TYPE resolve(Class<TYPE> type);
}
