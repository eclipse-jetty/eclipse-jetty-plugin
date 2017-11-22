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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.eclipsejetty.JettyPlugin;
import net.sourceforge.eclipsejetty.JettyPluginUtils;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;

/**
 * Matchers for classpath entries.
 * 
 * @author Manfred Hantschel
 */
public abstract class DependencyMatcher
{

    /**
     * Matches all entries.
     * 
     * @return the matcher
     */
    public static DependencyMatcher all()
    {
        return new DependencyMatcher()
        {

            @Override
            public Collection<Dependency> match(Collection<Dependency> entries)
            {
                return entries;
            }

            @Override
            public String toString()
            {
                return "all"; //$NON-NLS-1$
            }

        };
    }

    /**
     * Each entry must match each matcher. The result is the intersection of the result of all specified matchers.
     * 
     * @param matchers the matchers
     * @return the matcher
     */
    public static DependencyMatcher and(final DependencyMatcher... matchers)
    {
        if ((matchers == null) || (matchers.length == 0))
        {
            return all();
        }

        if (matchers.length == 1)
        {
            return matchers[0];
        }

        return new DependencyMatcher()
        {

            @Override
            public Collection<Dependency> match(Collection<Dependency> entries)
            {
                Collection<Dependency> results = new LinkedHashSet<Dependency>(entries);

                for (DependencyMatcher matcher : matchers)
                {
                    results = matcher.match(results);
                }

                return results;
            }

            @Override
            public String toString()
            {
                return "and" + Arrays.toString(matchers); //$NON-NLS-1$
            }

        };
    }

    /**
     * Each entry matches at least one of the specified matchers. The result is the union of the results of all
     * specified matchers.
     * 
     * @param matchers the matchers
     * @return the matcher
     */
    public static DependencyMatcher or(final DependencyMatcher... matchers)
    {
        if ((matchers == null) || (matchers.length == 0))
        {
            return all();
        }

        if (matchers.length == 1)
        {
            return matchers[0];
        }

        return new DependencyMatcher()
        {

            @Override
            public Collection<Dependency> match(Collection<Dependency> entries)
            {
                Collection<Dependency> results = new LinkedHashSet<Dependency>();

                for (DependencyMatcher matcher : matchers)
                {
                    results.addAll(matcher.match(new ArrayList<Dependency>(entries)));
                }

                return results;
            }

            @Override
            public String toString()
            {
                return "or" + Arrays.toString(matchers); //$NON-NLS-1$
            }

        };
    }

    /**
     * Keeps those entries, that do not match the specified matcher. The result is the inversion of the result of the
     * specified matcher.
     * 
     * @param matcher the matcher
     * @return the matcher
     */
    public static DependencyMatcher not(final DependencyMatcher matcher)
    {
        return new DependencyMatcher()
        {

            @Override
            public Collection<Dependency> match(Collection<Dependency> entries)
            {
                entries.removeAll(matcher.match(new ArrayList<Dependency>(entries)));

                return entries;
            }

            @Override
            public String toString()
            {
                return "not[" + matcher + "]"; //$NON-NLS-1$ //$NON-NLS-2$
            }

        };
    }

    public static DependencyMatcher bootstrapClasses()
    {
        return withClasspathProperty(IRuntimeClasspathEntry.BOOTSTRAP_CLASSES);
    }

    public static DependencyMatcher standardClasses()
    {
        return withClasspathProperty(IRuntimeClasspathEntry.STANDARD_CLASSES);
    }

    public static DependencyMatcher userClasses()
    {
        return withClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);
    }

    /**
     * Matches only those entries with the specified classpath property: BOOTSTRAP_CLASSES, STANDARD_CLASSES,
     * USER_CLASSES.
     * 
     * @param classpathProperty the property as defined in the {@link IRuntimeClasspathEntry} class
     * @return all matching entries
     */
    public static DependencyMatcher withClasspathProperty(final int classpathProperty)
    {
        return new DependencyMatcher()
        {

            @Override
            public Collection<Dependency> match(Collection<Dependency> entries)
            {
                Iterator<Dependency> iterator = entries.iterator();

                while (iterator.hasNext())
                {
                    Dependency entry = iterator.next();

                    if (classpathProperty != entry.getRuntimeClasspathEntry().getClasspathProperty())
                    {
                        iterator.remove();
                    }
                }

                return entries;
            }

            @Override
            public String toString()
            {
                switch (classpathProperty)
                {
                    case IRuntimeClasspathEntry.BOOTSTRAP_CLASSES:
                        return "bootstrapClasses"; //$NON-NLS-1$

                    case IRuntimeClasspathEntry.STANDARD_CLASSES:
                        return "standardClasses"; //$NON-NLS-1$

                    case IRuntimeClasspathEntry.USER_CLASSES:
                        return "userClasses"; //$NON-NLS-1$

                    default:
                        return "withClasspathProperty " + classpathProperty; //$NON-NLS-1$
                }
            }

        };
    }

    public static DependencyMatcher ofTypeArchive(final int type)
    {
        return ofType(IRuntimeClasspathEntry.ARCHIVE);
    }

    public static DependencyMatcher ofTypeContainer(final int type)
    {
        return ofType(IRuntimeClasspathEntry.CONTAINER);
    }

    public static DependencyMatcher ofTypeOther(final int type)
    {
        return ofType(IRuntimeClasspathEntry.OTHER);
    }

    public static DependencyMatcher ofTypeProject(final int type)
    {
        return ofType(IRuntimeClasspathEntry.PROJECT);
    }

    public static DependencyMatcher ofTypeVariable(final int type)
    {
        return ofType(IRuntimeClasspathEntry.VARIABLE);
    }

    /**
     * Matches only those entries with the specified type: ARCHIVE, CONTAINER, OTHER, PROJECT, VARIABLE.
     * 
     * @param type the type as defined in the {@link IRuntimeClasspathEntry} class
     * @return all matching entries
     */
    public static DependencyMatcher ofType(final int type)
    {
        return new DependencyMatcher()
        {

            @Override
            public Collection<Dependency> match(Collection<Dependency> entries)
            {
                Iterator<Dependency> iterator = entries.iterator();

                while (iterator.hasNext())
                {
                    Dependency entry = iterator.next();

                    if (type != entry.getRuntimeClasspathEntry().getType())
                    {
                        iterator.remove();
                    }
                }

                return entries;
            }

            @Override
            public String toString()
            {
                switch (type)
                {
                    case IRuntimeClasspathEntry.ARCHIVE:
                        return "of type archive"; //$NON-NLS-1$

                    case IRuntimeClasspathEntry.CONTAINER:
                        return "of type container"; //$NON-NLS-1$

                    case IRuntimeClasspathEntry.OTHER:
                        return "of type other"; //$NON-NLS-1$

                    case IRuntimeClasspathEntry.PROJECT:
                        return "of type project"; //$NON-NLS-1$

                    case IRuntimeClasspathEntry.VARIABLE:
                        return "of type variable"; //$NON-NLS-1$

                    default:
                        return "of type " + type; //$NON-NLS-1$
                }
            }

        };
    }

    /**
     * Matches all entries, that match the specified scope.
     * 
     * @param scope the scope
     * @return all matching entries
     */
    public static DependencyMatcher withScope(final MavenScope scope)
    {
        return new DependencyMatcher()
        {

            @Override
            public Collection<Dependency> match(Collection<Dependency> entries)
            {
                Iterator<Dependency> iterator = entries.iterator();

                while (iterator.hasNext())
                {
                    Dependency entry = iterator.next();

                    if (scope == entry.getScope())
                    {
                        continue;
                    }

                    iterator.remove();
                }

                return entries;
            }

            @Override
            public String toString()
            {
                return "with scope " + scope; //$NON-NLS-1$
            }

        };
    }

    /**
     * Matches all entries, that are in the specified collection
     * 
     * @param excludedEntries a collection of {@link IRuntimeClasspathEntry}s
     * @return all matching entries
     */
    public static DependencyMatcher notIn(Collection<IRuntimeClasspathEntry> excludedEntries) throws CoreException
    {
        final Set<IRuntimeClasspathEntry> excludedEntriesSet =
            new LinkedHashSet<IRuntimeClasspathEntry>(excludedEntries);

        return new DependencyMatcher()
        {

            @Override
            public Collection<Dependency> match(Collection<Dependency> entries)
            {
                Iterator<Dependency> iterator = entries.iterator();

                while (iterator.hasNext())
                {
                    if (excludedEntriesSet.contains(iterator.next().getRuntimeClasspathEntry()))
                    {
                        iterator.remove();
                    }
                }

                return entries;
            }

            @Override
            public String toString()
            {
                return "notIn" + excludedEntriesSet; //$NON-NLS-1$
            }

        };
    }

    /**
     * Matches all entries, that are included
     * 
     * @param included a list of regular expression of file or path names
     * @return all matching entries
     * @throws CoreException if the included list cannot be parsed
     * @deprecated replaced by a mechanism using generic ids
     */
    @Deprecated
    public static DependencyMatcher isIncludedRegEx(Collection<String> included) throws CoreException
    {
        final List<RegularMatcher> includedLibs = new ArrayList<RegularMatcher>();

        try
        {
            JettyPluginUtils.extractPatterns(includedLibs, included);
        }
        catch (final IllegalArgumentException e)
        {
            throw new CoreException(new Status(IStatus.ERROR, JettyPlugin.PLUGIN_ID, e.getMessage(), e));
        }

        return new DependencyMatcher()
        {

            @Override
            public Collection<Dependency> match(Collection<Dependency> entries)
            {
                Iterator<Dependency> iterator = entries.iterator();

                entry: while (iterator.hasNext())
                {
                    IRuntimeClasspathEntry entry = iterator.next().getRuntimeClasspathEntry();
                    String path = entry.getLocation();
                    String forwardSlashes = path.replace('\\', '/');
                    String backSlashes = path.replace('/', '\\');

                    for (final RegularMatcher includedLib : includedLibs)
                    {
                        if ((includedLib.matches(forwardSlashes)) || (includedLib.matches(backSlashes)))
                        {
                            continue entry;
                        }
                    }

                    iterator.remove();
                }

                return entries;
            }

            @Override
            public String toString()
            {
                return "included" + includedLibs; //$NON-NLS-1$
            }

        };
    }

    /**
     * Matches all entries, that are not excluded
     * 
     * @param excluded a list of regular expression of file or path names
     * @return all matching entries
     * @throws CoreException if the excluded list cannot be parsed
     * @deprecated replaced by a mechanism using generic ids
     */
    @Deprecated
    public static DependencyMatcher notExcludedRegEx(Collection<String> excluded) throws CoreException
    {
        final List<RegularMatcher> excludedLibs = new ArrayList<RegularMatcher>();

        try
        {
            JettyPluginUtils.extractPatterns(excludedLibs, excluded);
        }
        catch (final IllegalArgumentException e)
        {
            throw new CoreException(new Status(IStatus.ERROR, JettyPlugin.PLUGIN_ID, e.getMessage(), e));
        }

        return new DependencyMatcher()
        {

            @Override
            public Collection<Dependency> match(Collection<Dependency> entries)
            {
                Iterator<Dependency> iterator = entries.iterator();

                entry: while (iterator.hasNext())
                {
                    IRuntimeClasspathEntry entry = iterator.next().getRuntimeClasspathEntry();
                    String path = entry.getLocation();
                    String forwardSlashes = path.replace('\\', '/');
                    String backSlashes = path.replace('/', '\\');

                    for (final RegularMatcher excludedLib : excludedLibs)
                    {
                        if ((excludedLib.matches(forwardSlashes)) || (excludedLib.matches(backSlashes)))
                        {
                            iterator.remove();
                            continue entry;
                        }
                    }
                }

                return entries;
            }

            @Override
            public String toString()
            {
                return "notExcluded" + excludedLibs; //$NON-NLS-1$
            }

        };
    }

    /**
     * Matches all entries, that are included
     * 
     * @param includedGenericIds a list of generic ids
     * @return all matching entries
     * @throws CoreException if the included list cannot be parsed
     */
    public static DependencyMatcher isIncludedGenericId(final Collection<String> includedGenericIds)
        throws CoreException
    {
        return new DependencyMatcher()
        {

            @Override
            public Collection<Dependency> match(Collection<Dependency> entries)
            {
                Iterator<Dependency> iterator = entries.iterator();

                while (iterator.hasNext())
                {
                    Dependency entry = iterator.next();

                    if (includedGenericIds.contains(entry.getGenericId()))
                    {
                        continue;
                    }

                    iterator.remove();
                }

                return entries;
            }

            @Override
            public String toString()
            {
                return "included" + includedGenericIds; //$NON-NLS-1$
            }

        };
    }

    /**
     * Matches all entries, that are not excluded
     * 
     * @param excludedGenericIds a list of generic ids
     * @return all matching entries
     * @throws CoreException if the excluded list cannot be parsed
     */
    public static DependencyMatcher notExcludedGenericIds(final Collection<String> excludedGenericIds)
        throws CoreException
    {
        return new DependencyMatcher()
        {

            @Override
            public Collection<Dependency> match(Collection<Dependency> entries)
            {
                Iterator<Dependency> iterator = entries.iterator();

                while (iterator.hasNext())
                {
                    Dependency entry = iterator.next();

                    if (!excludedGenericIds.contains(entry.getGenericId()))
                    {
                        continue;
                    }

                    iterator.remove();
                }

                return entries;
            }

            @Override
            public String toString()
            {
                return "notExcluded" + excludedGenericIds; //$NON-NLS-1$
            }

        };
    }

    public abstract Collection<Dependency> match(Collection<Dependency> entries);

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract String toString();

}
