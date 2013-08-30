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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.eclipsejetty.JettyPluginConstants;
import net.sourceforge.eclipsejetty.JettyPluginUtils;
import net.sourceforge.eclipsejetty.launch.JettyLaunchDependencyEntry.Type;
import net.sourceforge.eclipsejetty.util.RegularMatcher;
import net.sourceforge.eclipsejetty.util.ScopedClasspathEntry;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
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

    public String createExcludedLibs()
    {
        StringBuilder result = new StringBuilder();

        for (JettyLaunchDependencyEntry entry : getSortedList())
        {
            if (entry.getType() == Type.ALWAYS_EXCLUDED)
            {
                if (result.length() > 0)
                {
                    result.append(", ");
                }

                result.append(entry.createMatcher());
            }
        }

        return result.toString();
    }

    public String createIncludedLibs()
    {
        StringBuilder result = new StringBuilder();

        for (JettyLaunchDependencyEntry entry : getSortedList())
        {
            if (entry.getType() == Type.ALWAYS_INCLUDED)
            {
                if (result.length() > 0)
                {
                    result.append(", ");
                }

                result.append(entry.createMatcher());
            }
        }

        return result.toString();
    }

    public String createGlobalLibs()
    {
        StringBuilder result = new StringBuilder();

        for (JettyLaunchDependencyEntry entry : getSortedList())
        {
            if (entry.isGlobal())
            {
                if (result.length() > 0)
                {
                    result.append(", ");
                }

                result.append(entry.createMatcher());
            }
        }

        return result.toString();
    }

    public boolean update(ILaunchConfiguration configuration, Table table,
        Collection<ScopedClasspathEntry> classpathEntries, Collection<ScopedClasspathEntry> includedClasspathEntries,
        Collection<ScopedClasspathEntry> globalClasspathEntries, boolean updateType) throws CoreException
    {
        if (configHash != configuration.hashCode())
        {
            clear(table);
            configHash = configuration.hashCode();
        }

        boolean updated = false;
        List<RegularMatcher> excludedLibs =
            createRegularMatcherList(JettyPluginConstants.getExcludedLibs(configuration));
        List<RegularMatcher> includedLibs =
            createRegularMatcherList(JettyPluginConstants.getIncludedLibs(configuration));

        // mark all as obsolete, will be reactivated later
        setObsolete(true);

        // create a set of all really included entries
        Collection<String> includedEntries = JettyPluginUtils.toLocationCollectionFromScoped(includedClasspathEntries);

        // create a set of all global entries
        Collection<String> globalEntries = JettyPluginUtils.toLocationCollectionFromScoped(globalClasspathEntries);

        // run through all entries and update the state of the entry
        for (ScopedClasspathEntry classpathEntry : classpathEntries)
        {
            String location = JettyPluginUtils.toLocation(classpathEntry);

            if (location != null)
            {
                JettyLaunchDependencyEntry entry = entries.get(location);

                if (entry == null)
                {
                    entry =
                        new JettyLaunchDependencyEntry(JettyPluginUtils.getPath(location),
                            JettyPluginUtils.getName(location), Type.DEFAULT);

                    entries.put(location, entry);
                }

                if (updateType)
                {
                    if (matches(excludedLibs, location))
                    {
                        entry.setType(Type.ALWAYS_EXCLUDED);
                    }

                    if (matches(includedLibs, location))
                    {
                        entry.setType(Type.ALWAYS_INCLUDED);
                    }

                    entry.setGlobal(globalEntries.contains(location));
                }

                entry.setIncluded(includedEntries.contains(location));
                entry.setScope(classpathEntry.getScope().text());
                entry.setObsolete(false);
            }
        }

        // sort the entries and update the table if entry has changed
        List<JettyLaunchDependencyEntry> list = getSortedList();
        int index = 0;
        for (JettyLaunchDependencyEntry entry : list)
        {
            if (!entry.isObsolete())
            {
                updated |= entry.updateItem(table, listener, index);
                index += 1;
            }
            else
            {
                entry.deleteItem(table);
                updated |= true;
            }
        }

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
            for (String lib : libs.split("[,\\n\\r]"))
            {
                result.add(new RegularMatcher(lib.trim()));
            }
        }

        return result;
    }

    private boolean matches(List<RegularMatcher> list, String location)
    {
        for (RegularMatcher matcher : list)
        {
            if (matcher.matches(location))
            {
                return true;
            }
        }

        return false;
    }

    private void setObsolete(boolean obsolete)
    {
        for (JettyLaunchDependencyEntry entry : entries.values())
        {
            entry.setObsolete(obsolete);
        }
    }

    public void reset()
    {
        for (JettyLaunchDependencyEntry entry : entries.values())
        {
            entry.setType(Type.DEFAULT);
        }
    }

    public void clear(Table table)
    {
        for (JettyLaunchDependencyEntry entry : entries.values())
        {
            entry.deleteItem(table);
        }

        entries.clear();
    }

}
