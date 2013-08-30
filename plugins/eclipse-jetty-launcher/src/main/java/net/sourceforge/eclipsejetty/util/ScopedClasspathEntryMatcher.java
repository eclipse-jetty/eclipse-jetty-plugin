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
public abstract class ScopedClasspathEntryMatcher
{

    /**
     * Matches all entries.
     * 
     * @return the matcher
     */
    public static ScopedClasspathEntryMatcher all()
    {
        return new ScopedClasspathEntryMatcher()
        {

            @Override
            public Collection<ScopedClasspathEntry> match(Collection<ScopedClasspathEntry> entries)
            {
                return entries;
            }

            @Override
            public String toString()
            {
                return "all";
            }

        };
    }

    /**
     * Each entry must match each matcher. The result is the intersection of the result of all specified matchers.
     * 
     * @param matchers the matchers
     * @return the matcher
     */
    public static ScopedClasspathEntryMatcher and(final ScopedClasspathEntryMatcher... matchers)
    {
        if ((matchers == null) || (matchers.length == 0))
        {
            return all();
        }

        if (matchers.length == 1)
        {
            return matchers[0];
        }

        return new ScopedClasspathEntryMatcher()
        {

            @Override
            public Collection<ScopedClasspathEntry> match(Collection<ScopedClasspathEntry> entries)
            {
                Collection<ScopedClasspathEntry> results = new LinkedHashSet<ScopedClasspathEntry>(entries);

                for (ScopedClasspathEntryMatcher matcher : matchers)
                {
                    results = matcher.match(results);
                }

                return results;
            }

            @Override
            public String toString()
            {
                return "and" + Arrays.toString(matchers);
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
    public static ScopedClasspathEntryMatcher or(final ScopedClasspathEntryMatcher... matchers)
    {
        if ((matchers == null) || (matchers.length == 0))
        {
            return all();
        }

        if (matchers.length == 1)
        {
            return matchers[0];
        }

        return new ScopedClasspathEntryMatcher()
        {

            @Override
            public Collection<ScopedClasspathEntry> match(Collection<ScopedClasspathEntry> entries)
            {
                Collection<ScopedClasspathEntry> results = new LinkedHashSet<ScopedClasspathEntry>();

                for (ScopedClasspathEntryMatcher matcher : matchers)
                {
                    results.addAll(matcher.match(new ArrayList<ScopedClasspathEntry>(entries)));
                }

                return results;
            }

            @Override
            public String toString()
            {
                return "or" + Arrays.toString(matchers);
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
    public static ScopedClasspathEntryMatcher not(final ScopedClasspathEntryMatcher matcher)
    {
        return new ScopedClasspathEntryMatcher()
        {

            @Override
            public Collection<ScopedClasspathEntry> match(Collection<ScopedClasspathEntry> entries)
            {
                entries.removeAll(matcher.match(new ArrayList<ScopedClasspathEntry>(entries)));

                return entries;
            }

            @Override
            public String toString()
            {
                return "not[" + matcher + "]";
            }

        };
    }

    public static ScopedClasspathEntryMatcher bootstrapClasses()
    {
        return withClasspathProperty(IRuntimeClasspathEntry.BOOTSTRAP_CLASSES);
    }

    public static ScopedClasspathEntryMatcher standardClasses()
    {
        return withClasspathProperty(IRuntimeClasspathEntry.STANDARD_CLASSES);
    }

    public static ScopedClasspathEntryMatcher userClasses()
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
    public static ScopedClasspathEntryMatcher withClasspathProperty(final int classpathProperty)
    {
        return new ScopedClasspathEntryMatcher()
        {

            @Override
            public Collection<ScopedClasspathEntry> match(Collection<ScopedClasspathEntry> entries)
            {
                Iterator<ScopedClasspathEntry> iterator = entries.iterator();

                while (iterator.hasNext())
                {
                    ScopedClasspathEntry entry = iterator.next();

                    if (classpathProperty != entry.getEntry().getClasspathProperty())
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
                        return "bootstrapClasses";

                    case IRuntimeClasspathEntry.STANDARD_CLASSES:
                        return "standardClasses";

                    case IRuntimeClasspathEntry.USER_CLASSES:
                        return "userClasses";

                    default:
                        return "withClasspathProperty " + classpathProperty;
                }
            }

        };
    }

    public static ScopedClasspathEntryMatcher ofTypeArchive(final int type)
    {
        return ofType(IRuntimeClasspathEntry.ARCHIVE);
    }

    public static ScopedClasspathEntryMatcher ofTypeContainer(final int type)
    {
        return ofType(IRuntimeClasspathEntry.CONTAINER);
    }

    public static ScopedClasspathEntryMatcher ofTypeOther(final int type)
    {
        return ofType(IRuntimeClasspathEntry.OTHER);
    }

    public static ScopedClasspathEntryMatcher ofTypeProject(final int type)
    {
        return ofType(IRuntimeClasspathEntry.PROJECT);
    }

    public static ScopedClasspathEntryMatcher ofTypeVariable(final int type)
    {
        return ofType(IRuntimeClasspathEntry.VARIABLE);
    }

    /**
     * Matches only those entries with the specified type: ARCHIVE, CONTAINER, OTHER, PROJECT, VARIABLE.
     * 
     * @param type the type as defined in the {@link IRuntimeClasspathEntry} class
     * @return all matching entries
     */
    public static ScopedClasspathEntryMatcher ofType(final int type)
    {
        return new ScopedClasspathEntryMatcher()
        {

            @Override
            public Collection<ScopedClasspathEntry> match(Collection<ScopedClasspathEntry> entries)
            {
                Iterator<ScopedClasspathEntry> iterator = entries.iterator();

                while (iterator.hasNext())
                {
                    ScopedClasspathEntry entry = iterator.next();

                    if (type != entry.getEntry().getType())
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
                        return "of type archive";

                    case IRuntimeClasspathEntry.CONTAINER:
                        return "of type container";

                    case IRuntimeClasspathEntry.OTHER:
                        return "of type other";

                    case IRuntimeClasspathEntry.PROJECT:
                        return "of type project";

                    case IRuntimeClasspathEntry.VARIABLE:
                        return "of type variable";

                    default:
                        return "of type " + type;
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
    public static ScopedClasspathEntryMatcher withScope(final MavenScope scope)
    {
        return new ScopedClasspathEntryMatcher()
        {

            @Override
            public Collection<ScopedClasspathEntry> match(Collection<ScopedClasspathEntry> entries)
            {
                Iterator<ScopedClasspathEntry> iterator = entries.iterator();

                while (iterator.hasNext())
                {
                    ScopedClasspathEntry entry = iterator.next();

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
                return "with scope " + scope;
            }

        };
    }

    /**
     * Matches all entries, that are in the specified collection
     * 
     * @param excludedEntries a collection of {@link IRuntimeClasspathEntry}s
     * @return all matching entries
     */
    public static ScopedClasspathEntryMatcher notIn(Collection<IRuntimeClasspathEntry> excludedEntries)
        throws CoreException
    {
        final Set<IRuntimeClasspathEntry> excludedEntriesSet =
            new LinkedHashSet<IRuntimeClasspathEntry>(excludedEntries);

        return new ScopedClasspathEntryMatcher()
        {

            @Override
            public Collection<ScopedClasspathEntry> match(Collection<ScopedClasspathEntry> entries)
            {
                Iterator<ScopedClasspathEntry> iterator = entries.iterator();

                while (iterator.hasNext())
                {
                    if (excludedEntriesSet.contains(iterator.next().getEntry()))
                    {
                        iterator.remove();
                    }
                }

                return entries;
            }

            @Override
            public String toString()
            {
                return "notIn" + excludedEntriesSet;
            }

        };
    }

    /**
     * Matches all entries, that are included
     * 
     * @param included a list of regular expression of file or path names
     * @return all matching entries
     * @throws CoreException if the included list cannot be parsed
     */
    public static ScopedClasspathEntryMatcher isIncluded(String... included) throws CoreException
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

        return new ScopedClasspathEntryMatcher()
        {

            @Override
            public Collection<ScopedClasspathEntry> match(Collection<ScopedClasspathEntry> entries)
            {
                Iterator<ScopedClasspathEntry> iterator = entries.iterator();

                entry: while (iterator.hasNext())
                {
                    IRuntimeClasspathEntry entry = iterator.next().getEntry();
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
                return "notExcluded" + includedLibs;
            }

        };
    }

    /**
     * Matches all entries, that are not excluded
     * 
     * @param excluded a list of regular expression of file or path names
     * @return all matching entries
     * @throws CoreException if the excluded list cannot be parsed
     */
    public static ScopedClasspathEntryMatcher notExcluded(String... excluded) throws CoreException
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

        return new ScopedClasspathEntryMatcher()
        {

            @Override
            public Collection<ScopedClasspathEntry> match(Collection<ScopedClasspathEntry> entries)
            {
                Iterator<ScopedClasspathEntry> iterator = entries.iterator();

                entry: while (iterator.hasNext())
                {
                    IRuntimeClasspathEntry entry = iterator.next().getEntry();
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
                return "notExcluded" + excludedLibs;
            }

        };
    }

    public abstract Collection<ScopedClasspathEntry> match(Collection<ScopedClasspathEntry> entries);

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract String toString();

}
