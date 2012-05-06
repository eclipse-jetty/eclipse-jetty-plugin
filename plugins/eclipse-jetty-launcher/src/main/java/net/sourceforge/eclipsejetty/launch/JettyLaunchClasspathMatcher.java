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
package net.sourceforge.eclipsejetty.launch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.eclipsejetty.JettyPlugin;
import net.sourceforge.eclipsejetty.JettyPluginUtils;
import net.sourceforge.eclipsejetty.util.RegularMatcher;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;

/**
 * Matchers for classpath entries.
 * 
 * @author Manfred Hantschel
 */
public abstract class JettyLaunchClasspathMatcher
{

    /**
     * Matches all entries.
     * 
     * @return the matcher
     */
    public static JettyLaunchClasspathMatcher all()
    {
        return new JettyLaunchClasspathMatcher()
        {

            @Override
            public Collection<IRuntimeClasspathEntry> match(Collection<IRuntimeClasspathEntry> entries)
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
    public static JettyLaunchClasspathMatcher and(final JettyLaunchClasspathMatcher... matchers)
    {
        if ((matchers == null) || (matchers.length == 0))
        {
            return all();
        }

        if (matchers.length == 1)
        {
            return matchers[0];
        }

        return new JettyLaunchClasspathMatcher()
        {

            @Override
            public Collection<IRuntimeClasspathEntry> match(Collection<IRuntimeClasspathEntry> entries)
            {
                Collection<IRuntimeClasspathEntry> results = new LinkedHashSet<IRuntimeClasspathEntry>(entries);

                for (JettyLaunchClasspathMatcher matcher : matchers)
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
    public static JettyLaunchClasspathMatcher or(final JettyLaunchClasspathMatcher... matchers)
    {
        if ((matchers == null) || (matchers.length == 0))
        {
            return all();
        }

        if (matchers.length == 1)
        {
            return matchers[0];
        }

        return new JettyLaunchClasspathMatcher()
        {

            @Override
            public Collection<IRuntimeClasspathEntry> match(Collection<IRuntimeClasspathEntry> entries)
            {
                Collection<IRuntimeClasspathEntry> results = new LinkedHashSet<IRuntimeClasspathEntry>();

                for (JettyLaunchClasspathMatcher matcher : matchers)
                {
                    results.addAll(matcher.match(new ArrayList<IRuntimeClasspathEntry>(entries)));
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
    public static JettyLaunchClasspathMatcher not(final JettyLaunchClasspathMatcher matcher)
    {
        return new JettyLaunchClasspathMatcher()
        {

            @Override
            public Collection<IRuntimeClasspathEntry> match(Collection<IRuntimeClasspathEntry> entries)
            {
                entries.removeAll(matcher.match(new ArrayList<IRuntimeClasspathEntry>(entries)));

                return entries;
            }

            @Override
            public String toString()
            {
                return "not[" + matcher + "]";
            }

        };
    }

    public static JettyLaunchClasspathMatcher bootstrapClasses()
    {
        return withClasspathProperty(IRuntimeClasspathEntry.BOOTSTRAP_CLASSES);
    }

    public static JettyLaunchClasspathMatcher standardClasses()
    {
        return withClasspathProperty(IRuntimeClasspathEntry.STANDARD_CLASSES);
    }

    public static JettyLaunchClasspathMatcher userClasses()
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
    public static JettyLaunchClasspathMatcher withClasspathProperty(final int classpathProperty)
    {
        return new JettyLaunchClasspathMatcher()
        {

            @Override
            public Collection<IRuntimeClasspathEntry> match(Collection<IRuntimeClasspathEntry> entries)
            {
                Iterator<IRuntimeClasspathEntry> iterator = entries.iterator();

                while (iterator.hasNext())
                {
                    IRuntimeClasspathEntry entry = iterator.next();

                    if (classpathProperty != entry.getClasspathProperty())
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

    public static JettyLaunchClasspathMatcher ofTypeArchive(final int type)
    {
        return ofType(IRuntimeClasspathEntry.ARCHIVE);
    }

    public static JettyLaunchClasspathMatcher ofTypeContainer(final int type)
    {
        return ofType(IRuntimeClasspathEntry.CONTAINER);
    }

    public static JettyLaunchClasspathMatcher ofTypeOther(final int type)
    {
        return ofType(IRuntimeClasspathEntry.OTHER);
    }

    public static JettyLaunchClasspathMatcher ofTypeProject(final int type)
    {
        return ofType(IRuntimeClasspathEntry.PROJECT);
    }

    public static JettyLaunchClasspathMatcher ofTypeVariable(final int type)
    {
        return ofType(IRuntimeClasspathEntry.VARIABLE);
    }

    /**
     * Matches only those entries with the specified type: ARCHIVE, CONTAINER, OTHER, PROJECT, VARIABLE.
     * 
     * @param type the type as defined in the {@link IRuntimeClasspathEntry} class
     * @return all matching entries
     */
    public static JettyLaunchClasspathMatcher ofType(final int type)
    {
        return new JettyLaunchClasspathMatcher()
        {

            @Override
            public Collection<IRuntimeClasspathEntry> match(Collection<IRuntimeClasspathEntry> entries)
            {
                Iterator<IRuntimeClasspathEntry> iterator = entries.iterator();

                while (iterator.hasNext())
                {
                    IRuntimeClasspathEntry entry = iterator.next();

                    if (type != entry.getType())
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
     * Matches all entries, that contain the specified attribute.
     * 
     * @param name the name, regular expression
     * @param value the value, regular expression
     * @return all matching entries
     */
    public static JettyLaunchClasspathMatcher withExtraAttribute(String name, String value)
    {
        final RegularMatcher nameMatcher = new RegularMatcher(name);
        final RegularMatcher valueMatcher = new RegularMatcher(value);

        return new JettyLaunchClasspathMatcher()
        {

            @Override
            public Collection<IRuntimeClasspathEntry> match(Collection<IRuntimeClasspathEntry> entries)
            {
                Iterator<IRuntimeClasspathEntry> iterator = entries.iterator();

                entry: while (iterator.hasNext())
                {
                    IRuntimeClasspathEntry entry = iterator.next();
                    IClasspathEntry classpathEntry = entry.getClasspathEntry();

                    if (classpathEntry == null)
                    {
                        iterator.remove();
                        continue;
                    }

                    IClasspathAttribute[] extraAttributes = classpathEntry.getExtraAttributes();

                    for (IClasspathAttribute extraAttribute : extraAttributes)
                    {
                        if ((nameMatcher.matches(extraAttribute.getName()))
                            && (valueMatcher.matches(extraAttribute.getValue())))
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
                return "mavenTestScope";
            }

        };
    }

    /**
     * Matches all entries, that are in the specified collection
     * 
     * @param excludedEntries a collection of {@link IRuntimeClasspathEntry}s
     * @return all matching entries
     */
    public static JettyLaunchClasspathMatcher notIn(Collection<IRuntimeClasspathEntry> excludedEntries)
        throws CoreException
    {
        final Set<IRuntimeClasspathEntry> excludedEntriesSet = new LinkedHashSet<IRuntimeClasspathEntry>(excludedEntries);

        return new JettyLaunchClasspathMatcher()
        {

            @Override
            public Collection<IRuntimeClasspathEntry> match(Collection<IRuntimeClasspathEntry> entries)
            {
                Iterator<IRuntimeClasspathEntry> iterator = entries.iterator();

                while (iterator.hasNext())
                {
                    if (excludedEntriesSet.contains(iterator.next()))
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
     * Matches all entries, that are not excluded
     * 
     * @param excluded a list of regular expression of file or path names
     * @return all matching entries
     * @throws CoreException if the excluded list cannot be parsed
     */
    public static JettyLaunchClasspathMatcher notExcluded(String... excluded) throws CoreException
    {
        final List<RegularMatcher> excludedLibs = new ArrayList<RegularMatcher>();

        // excludedLibs.add(Pattern.compile(".*org\\.mortbay\\.jetty.*"));

        try
        {
            JettyPluginUtils.extractPatterns(excludedLibs, excluded);
        }
        catch (final IllegalArgumentException e)
        {
            throw new CoreException(new Status(IStatus.ERROR, JettyPlugin.PLUGIN_ID, e.getMessage(), e));
        }

        return new JettyLaunchClasspathMatcher()
        {

            @Override
            public Collection<IRuntimeClasspathEntry> match(Collection<IRuntimeClasspathEntry> entries)
            {
                Iterator<IRuntimeClasspathEntry> iterator = entries.iterator();

                entry: while (iterator.hasNext())
                {
                    IRuntimeClasspathEntry entry = iterator.next();
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

    public abstract Collection<IRuntimeClasspathEntry> match(Collection<IRuntimeClasspathEntry> entries);

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract String toString();

}
