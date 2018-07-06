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
package net.sourceforge.eclipsejetty.launch.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.eclipsejetty.JettyPluginUtils;
import net.sourceforge.eclipsejetty.launch.configuration.JettyLaunchDependencyEntry.Kind;
import net.sourceforge.eclipsejetty.launch.configuration.JettyLaunchDependencyEntry.Type;
import net.sourceforge.eclipsejetty.launch.util.JettyLaunchConfigurationAdapter;
import net.sourceforge.eclipsejetty.util.Dependency;
import net.sourceforge.eclipsejetty.util.RegularMatcher;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Table;

/**
 * Holds the list of table entries in the advances configuration tabl
 * 
 * @author Manfred Hantschel
 */
public class JettyLaunchDependencyEntryList
{

    private final Map<String, JettyLaunchDependencyEntry> entries;
    private final SelectionListener listener;

    private int configHash;

    public JettyLaunchDependencyEntryList(SelectionListener listener)
    {
        super();

        entries = new HashMap<String, JettyLaunchDependencyEntry>();

        this.listener = listener;
    }

    /**
     * @deprecated The regular expression based including/excluding mechanism was replaced by a generic id based one
     *             with 3.5.1.
     */
    @Deprecated
    public String createExcludedLibs()
    {
        StringBuilder result = new StringBuilder();

        for (JettyLaunchDependencyEntry entry : getSortedList())
        {
            if (entry.getType() == Type.ALWAYS_EXCLUDED)
            {
                if (result.length() > 0)
                {
                    result.append(", "); //$NON-NLS-1$
                }

                result.append(entry.createMatcher());
            }
        }

        return result.toString();
    }

    /**
     * @deprecated The regular expression based including/excluding mechanism was replaced by a generic id based one
     *             with 3.5.1.
     */
    @Deprecated
    public String createIncludedLibs()
    {
        StringBuilder result = new StringBuilder();

        for (JettyLaunchDependencyEntry entry : getSortedList())
        {
            if (entry.getType() == Type.ALWAYS_INCLUDED)
            {
                if (result.length() > 0)
                {
                    result.append(", "); //$NON-NLS-1$
                }

                result.append(entry.createMatcher());
            }
        }

        return result.toString();
    }

    /**
     * Creates a collection of excluded generic ids for storing in the configuration
     * 
     * @return a collection of excluded generic ids
     */
    public Collection<String> createExcludedGenericIds()
    {
        Collection<String> result = new HashSet<String>();

        for (JettyLaunchDependencyEntry entry : getSortedList())
        {
            if (entry.getType() == Type.ALWAYS_EXCLUDED)
            {
                result.add(entry.getGenericId());
            }
        }

        return result;
    }

    /**
     * Creates a collection of included generic ids for storing in the configuration
     * 
     * @return a collection of included generic ids
     */
    public Collection<String> createIncludedGenericIds()
    {
        Collection<String> result = new HashSet<String>();

        for (JettyLaunchDependencyEntry entry : getSortedList())
        {
            if (entry.getType() == Type.ALWAYS_INCLUDED)
            {
                result.add(entry.getGenericId());
            }
        }

        return result;
    }

    /**
     * @deprecated The regular expression based global mechanism was replaced by a generic id based one with 3.5.1.
     */
    @Deprecated
    public String createGlobalLibs()
    {
        StringBuilder result = new StringBuilder();

        for (JettyLaunchDependencyEntry entry : getSortedList())
        {
            if (entry.isGlobal())
            {
                if (result.length() > 0)
                {
                    result.append(", "); //$NON-NLS-1$
                }

                result.append(entry.createMatcher());
            }
        }

        return result.toString();
    }

    /**
     * Creates a collection of global generic ids for storing in the configuration
     * 
     * @return a collection of global generic ids
     */
    public Collection<String> createGlobalGenericIds()
    {
        Collection<String> result = new HashSet<String>();

        for (JettyLaunchDependencyEntry entry : getSortedList())
        {
            if (entry.isGlobal())
            {
                result.add(entry.getGenericId());
            }
        }

        return result;
    }

    /**
     * Updates/fills the table if necessary
     * 
     * @param adapter the configuration adapter
     * @param table the table
     * @param dependencies the list of dependencies
     * @param includedClasspathEntries all included classpath entries (by default)
     * @param globalClasspathEntries all classpath entries marked as being global
     * @param updateType true to update the type of the entry
     * @param filterPattern the filter pattern for the entries
     * @return true if updated
     * @throws CoreException on occasion
     */
    public boolean update(JettyLaunchConfigurationAdapter adapter, final Table table,
        Collection<Dependency> dependencies, Collection<Dependency> includedClasspathEntries,
        Collection<Dependency> globalClasspathEntries, boolean updateType, String filterPattern) throws CoreException
    {
        if (configHash != adapter.getConfiguration().hashCode())
        {
            clear(table);
            configHash = adapter.getConfiguration().hashCode();
        }

        boolean updated = false;
        List<RegularMatcher> excludedLibs = null;
        List<RegularMatcher> includedLibs = null;
        Set<String> excludedGenericIds = new HashSet<String>();
        Set<String> includedGenericIds = new HashSet<String>();
        boolean genericIdsSupported = adapter.isGenericIdsSupported();

        if (genericIdsSupported)
        {
            excludedGenericIds.addAll(adapter.getExcludedGenericIds());
            includedGenericIds.addAll(adapter.getIncludedGenericIds());
        }
        else
        {
            excludedLibs = deprecatedGetExcludedLibs(adapter);
            includedLibs = deprecatedGetIncludedLibs(adapter);
        }

        // mark all as obsolete, will be reactivated later
        setObsolete(true);

        // create a set of all really included entries
        Collection<String> includedEntries = JettyPluginUtils.toLocationCollectionFromScoped(includedClasspathEntries);

        // create a set of all global entries
        Collection<String> globalEntries = JettyPluginUtils.toLocationCollectionFromScoped(globalClasspathEntries);

        // run through all entries and update the state of the entry
        for (Dependency dependency : dependencies)
        {
            String location = JettyPluginUtils.toLocation(dependency);

            if (location != null)
            {
                JettyLaunchDependencyEntry entry = entries.get(location);

                if (entry == null)
                {
                    Kind kind;

                    if (dependency.isProjectDependent())
                    {
                        kind = Kind.PROJECT;
                    }
                    else
                    {
                        switch (dependency.getRuntimeClasspathEntry().getType())
                        {
                            case IRuntimeClasspathEntry.PROJECT:
                                kind = Kind.PROJECT;
                                break;

                            case IRuntimeClasspathEntry.ARCHIVE:
                            case IRuntimeClasspathEntry.CONTAINER:
                                kind = Kind.JAR;
                                break;

                            case IRuntimeClasspathEntry.VARIABLE:
                            default:
                                kind = Kind.OTHER;
                                break;
                        }
                    }

                    entry =
                        new JettyLaunchDependencyEntry(dependency.getGenericId(), JettyPluginUtils.getPath(location),
                            JettyPluginUtils.getName(location), kind, Type.DEFAULT);

                    entries.put(location, entry);
                }

                if (updateType)
                {
                    if (genericIdsSupported)
                    {
                        if (excludedGenericIds.contains(entry.getGenericId()))
                        {
                            entry.setType(Type.ALWAYS_EXCLUDED);
                        }

                        if (includedGenericIds.contains(entry.getGenericId()))
                        {
                            entry.setType(Type.ALWAYS_INCLUDED);
                        }
                    }
                    else
                    {
                        if (matches(excludedLibs, location))
                        {
                            entry.setType(Type.ALWAYS_EXCLUDED);
                        }

                        if (matches(includedLibs, location))
                        {
                            entry.setType(Type.ALWAYS_INCLUDED);
                        }
                    }

                    entry.setGlobal(globalEntries.contains(location));
                }

                entry.setIncluded(includedEntries.contains(location));
                entry.setScope(dependency.getScope().key());
                entry.setObsolete(false);
            }
        }

        // sort the entries and update the table if entry has changed
        final List<JettyLaunchDependencyEntry> list = getSortedList();
        int index = 0;

        for (JettyLaunchDependencyEntry entry : list)
        {
            if ((!entry.isObsolete()) && (entry.matches(filterPattern)))
            {
                updated |= entry.initItem(table, listener, index);
                index += 1;
            }
            else
            {
                entry.deleteItem(table);
                updated |= true;
            }
        }

        table.getDisplay().syncExec(new Runnable()
        {
            public void run()
            {
                for (JettyLaunchDependencyEntry entry : list)
                {
                    entry.updateItem(table);
                }
            }
        });
        
        // remove those, that were not hit from the local table
        Iterator<Map.Entry<String, JettyLaunchDependencyEntry>> iterator = entries.entrySet().iterator();
        while (iterator.hasNext())
        {
            if (iterator.next().getValue().isObsolete())
            {
                iterator.remove();
            }
        }

        return updated;
    }

    @SuppressWarnings("deprecation")
    private List<RegularMatcher> deprecatedGetIncludedLibs(JettyLaunchConfigurationAdapter adapter)
        throws CoreException
    {
        return createRegularMatcherList(adapter.getIncludedLibs());
    }

    @SuppressWarnings("deprecation")
    private List<RegularMatcher> deprecatedGetExcludedLibs(JettyLaunchConfigurationAdapter adapter)
        throws CoreException
    {
        return createRegularMatcherList(adapter.getExcludedLibs());
    }

    /**
     * Returns all entries (sorted)
     * 
     * @return all entries
     */
    private List<JettyLaunchDependencyEntry> getSortedList()
    {
        List<JettyLaunchDependencyEntry> list = new ArrayList<JettyLaunchDependencyEntry>(entries.values());

        Collections.sort(list);

        return list;
    }

    private List<RegularMatcher> createRegularMatcherList(String libs)
    {
        List<RegularMatcher> result = new ArrayList<RegularMatcher>();

        if ((libs != null) && (libs.trim().length() > 0))
        {
            for (String lib : JettyPluginUtils.fromCommaSeparatedString(libs))
            {
                result.add(new RegularMatcher(lib.trim()));
            }
        }

        return result;
    }

    /**
     * @deprecated replaced by the generic id method
     */
    @Deprecated
    private boolean matches(List<RegularMatcher> list, String location)
    {
        if (list == null)
        {
            return false;
        }

        for (RegularMatcher matcher : list)
        {
            if (matcher.matches(location))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Set all entries as being obsolete and should get deleted on the next update
     * 
     * @param obsolete
     */
    private void setObsolete(boolean obsolete)
    {
        for (JettyLaunchDependencyEntry entry : entries.values())
        {
            entry.setObsolete(obsolete);
        }
    }

    /**
     * Resets all include/exclude definitions of all entries
     */
    public void reset()
    {
        for (JettyLaunchDependencyEntry entry : entries.values())
        {
            entry.setType(Type.DEFAULT);
        }
    }

    /**
     * Clears the table
     * 
     * @param table the table
     */
    public void clear(Table table)
    {
        for (JettyLaunchDependencyEntry entry : entries.values())
        {
            entry.deleteItem(table);
        }

        entries.clear();
    }

}
