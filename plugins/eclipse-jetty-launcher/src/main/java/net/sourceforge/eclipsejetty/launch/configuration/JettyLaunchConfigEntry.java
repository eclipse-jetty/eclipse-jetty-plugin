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

import net.sourceforge.eclipsejetty.JettyPluginUtils;
import net.sourceforge.eclipsejetty.Messages;
import net.sourceforge.eclipsejetty.jetty.JettyConfig;
import net.sourceforge.eclipsejetty.jetty.JettyConfigType;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * One Jetty configuration XML file entry of the table.
 * 
 * @author Manfred Hantschel
 */
public class JettyLaunchConfigEntry
{

    private String path;
    private JettyConfigType type;
    private boolean active;

    private boolean needsUpdate;
    private TableItem item;
    private Button button;

    public JettyLaunchConfigEntry()
    {
        super();
    }

    public JettyLaunchConfigEntry(JettyConfig config)
    {
        super();

        path = config.getPath();
        type = config.getType();
        active = config.isActive();

        needsUpdate = true;
    }

    /**
     * Returns the path to the config file
     * 
     * @return the path
     */
    public String getPath()
    {
        return path;
    }

    /**
     * Sets the path to the config file. Sets the {@link #needsUpdate} flag, if the value had changed.
     * 
     * @param path the path
     */
    public void setPath(String path)
    {
        if (!JettyPluginUtils.equals(this.path, path))
        {
            this.path = path;
            needsUpdate = true;
        }
    }

    /**
     * Returns the type of the config entry
     * 
     * @return the type
     */
    public JettyConfigType getType()
    {
        return type;
    }

    /**
     * Sets the type of the config entry. Sets the {@link #needsUpdate} flag, if the value had changed.
     * 
     * @param type the type
     */
    public void setType(JettyConfigType type)
    {
        if (!JettyPluginUtils.equals(this.type, type))
        {
            this.type = type;
            needsUpdate = true;
        }
    }

    /**
     * Returns true if the entry is active
     * 
     * @return true if active
     */
    public boolean isActive()
    {
        return active;
    }

    /**
     * Sets the active flag of the entry. Sets the {@link #needsUpdate} flag, if the value had changed.
     * 
     * @param active true to activate
     */
    public void setActive(boolean active)
    {
        if (this.active != active)
        {
            this.active = active;
            needsUpdate = true;
        }
    }

    /**
     * Returns the table item, but does not create it.
     * 
     * @return the table item
     */
    public TableItem getItem()
    {
        return item;
    }

    /**
     * Creates the table item.
     * 
     * @param table the table
     * @param listener the listener, triggered when selected
     * @param index the index of the item
     */
    public void createItem(Table table, SelectionListener listener, int index)
    {
        final TableItem item = new TableItem(table, SWT.NONE, index);
        TableEditor editor = new TableEditor(table);

        button = new Button(table, SWT.CHECK);
        button.addListener(SWT.Selection, new Listener()
        {
            public void handleEvent(Event e)
            {
                if (type != JettyConfigType.DEFAULT)
                {
                    active = button.getSelection();
                    fillItem(item);
                }
            }
        });
        button.addSelectionListener(listener);
        button.pack();

        editor.minimumWidth = button.getSize().x;
        editor.horizontalAlignment = SWT.CENTER;
        editor.setEditor(button, item, 0);

        fillItem(item);
        setItem(item);
    }

    /**
     * Fills the table item with the data, resets the {@link #needsUpdate} flag.
     * 
     * @param item the item
     */
    private void fillItem(TableItem item)
    {
        Color color;

        switch (type)
        {
            case DEFAULT:
                button.setSelection(true);
                button.setEnabled(false);
                color = item.getDisplay().getSystemColor(SWT.COLOR_BLACK);

                item.setText(1, Messages.configEntry_default);
                item.setForeground(1, color);
                break;

            default:
                if (active)
                {
                    button.setSelection(true);
                    color = item.getDisplay().getSystemColor(SWT.COLOR_BLACK);
                }
                else
                {
                    button.setSelection(false);
                    color = item.getDisplay().getSystemColor(SWT.COLOR_GRAY);
                }

                item.setText(1, path);
                item.setForeground(1, color);
                break;
        }

        needsUpdate = false;
    }

    /**
     * Updates the item. Creates it, if necessary. Updates the data, if necessary. Changes the index, if necessary.
     * 
     * @param table the table
     * @param listener the listener for changes
     * @param index the index
     * @param force true to force update
     * @return true if updated
     */
    public boolean updateItem(Table table, SelectionListener listener, int index, boolean force)
    {
        if (item == null)
        {
            createItem(table, listener, index);
            return true;
        }

        if (table.indexOf(item) != index)
        {
            deleteItem(table);
            createItem(table, listener, index);
            return true;
        }

        if ((needsUpdate) || (force))
        {
            fillItem(item);
            return true;
        }

        return false;
    }

    /**
     * Deletes the item from the table
     * 
     * @param table the table
     */
    public void deleteItem(Table table)
    {
        if (item != null)
        {
            item.dispose();
            button.dispose();

            item = null;
            button = null;
        }
    }

    /**
     * Sets the item
     * 
     * @param item the item
     */
    public void setItem(TableItem item)
    {
        this.item = item;
    }

    /**
     * Create a {@link JettyConfig} from the entry
     * 
     * @return the {@link JettyConfig}
     */
    public JettyConfig getJettyConfig()
    {
        return new JettyConfig(path, type, active);
    }

}
