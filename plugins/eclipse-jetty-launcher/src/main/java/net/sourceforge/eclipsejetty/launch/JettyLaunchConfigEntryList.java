package net.sourceforge.eclipsejetty.launch;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.eclipsejetty.jetty.JettyConfig;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Table;

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

    public void add(Table table, JettyLaunchConfigEntry entry)
    {
        entries.add(entry);

        entry.updateItem(table, listener, entries.size() - 1, false);
    }
    
    public JettyLaunchConfigEntry get(int index)
    {
        return entries.get(index);
    }

    public void remove(Table table, int index)
    {
        JettyLaunchConfigEntry entry = entries.remove(index);

        entry.deleteItem(table);

        if (index < entries.size())
        {
            entries.get(index).updateItem(table, listener, index, true);
        }
    }

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

    public void clear(Table table)
    {
        for (JettyLaunchConfigEntry entry : entries)
        {
            entry.deleteItem(table);
        }

        entries.clear();
    }

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
