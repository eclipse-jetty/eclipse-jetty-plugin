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
import java.util.List;

import net.sourceforge.eclipsejetty.jetty.JettyConfig;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Table;

/**
 * Holds the list of {@link JettyLaunchConfigEntry}s for the UI table.
 * 
 * @author Manfred Hantschel
 */
public class JettyLaunchConfigEntryList
{

    private final List<JettyLaunchConfigEntry> entries = new ArrayList<JettyLaunchConfigEntry>();
    private final SelectionListener listener;

    private int configHash;

    public JettyLaunchConfigEntryList(SelectionListener listener)
    {
        super();

        this.listener = listener;
    }

    /**
     * Adds an entry to the table. Updates the entry if necessary.
     * 
     * @param table the table
     * @param entry the entry
     */
    public void add(Table table, JettyLaunchConfigEntry entry)
    {
        entries.add(entry);

        entry.updateItem(table, listener, entries.size() - 1, false);
    }

    /**
     * Returns the entry at the specified index
     * 
     * @param index the index
     * @return the entry
     */
    public JettyLaunchConfigEntry get(int index)
    {
        return entries.get(index);
    }

    /**
     * Removes the entry at the specified index
     * 
     * @param table the table
     * @param index the index
     */
    public void remove(Table table, int index)
    {
        JettyLaunchConfigEntry entry = entries.remove(index);

        entry.deleteItem(table);

        if (index < entries.size())
        {
            entries.get(index).updateItem(table, listener, index, true);
        }
    }

    /**
     * Exchanges the entry at the specified index with the next one
     * 
     * @param table the table
     * @param index the index
     */
    public void exchange(Table table, int index)
    {
        JettyLaunchConfigEntry entryA = entries.remove(index);
        JettyLaunchConfigEntry entryB = entries.remove(index);

        entryA.deleteItem(table);
        entryB.deleteItem(table);

        entries.add(index, entryA);
        entries.add(index, entryB);

        entryB.createItem(table, listener, index);
        entryA.createItem(table, listener, index + 1);
    }

    /**
     * Fills and updates all entries (if necessary)
     * 
     * @param configuration the configuration
     * @param table the table
     * @param configs the {@link JettyConfig}
     * @return true if updated
     * @throws CoreException on occasion
     */
    public boolean update(ILaunchConfiguration configuration, Table table, List<JettyConfig> configs)
        throws CoreException
    {
        if (configHash != configuration.hashCode())
        {
            clear(table);
            configHash = configuration.hashCode();
        }

        boolean updated = false;

        // run through all entries and update the state of the entry
        int index = 0;
        for (JettyConfig config : configs)
        {
            if (index >= entries.size())
            {
                JettyLaunchConfigEntry entry = new JettyLaunchConfigEntry(config);

                entries.add(entry);

                entry.updateItem(table, listener, index, false);
                updated |= true;
            }
            else
            {
                JettyLaunchConfigEntry entry = entries.get(index);

                entry.setPath(config.getPath());
                entry.setType(config.getType());
                entry.setActive(config.isActive());

                updated |= entry.updateItem(table, listener, index, false);
            }

            index += 1;
        }

        // delete the rest
        while (index < entries.size())
        {
            entries.remove(index).deleteItem(table);
            updated |= true;
        }

        return updated;
    }

    /**
     * Clears the table
     * 
     * @param table the table
     */
    public void clear(Table table)
    {
        for (JettyLaunchConfigEntry entry : entries)
        {
            entry.deleteItem(table);
        }

        entries.clear();
    }

    /**
     * Returns all {@link JettyConfig}s stored in the table.
     * 
     * @return the configs
     */
    public List<JettyConfig> getConfigs()
    {
        List<JettyConfig> results = new ArrayList<JettyConfig>();

        for (JettyLaunchConfigEntry entry : entries)
        {
            results.add(entry.getJettyConfig());
        }

        return results;
    }

}
