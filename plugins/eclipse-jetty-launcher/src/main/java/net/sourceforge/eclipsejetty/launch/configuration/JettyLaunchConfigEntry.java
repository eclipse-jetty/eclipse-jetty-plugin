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

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        if (!JettyPluginUtils.equals(this.path, path))
        {
            this.path = path;
            needsUpdate = true;
        }
    }

    public JettyConfigType getType()
    {
        return type;
    }

    public void setType(JettyConfigType type)
    {
        if (!JettyPluginUtils.equals(this.type, type))
        {
            this.type = type;
            needsUpdate = true;
        }
    }

    public boolean isActive()
    {
        return active;
    }

    public void setActive(boolean active)
    {
        if (this.active != active)
        {
            this.active = active;
            needsUpdate = true;
        }
    }

    public TableItem getItem()
    {
        return item;
    }

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

    public void setItem(TableItem item)
    {
        this.item = item;
    }

    public JettyConfig getJettyConfig()
    {
        return new JettyConfig(path, type, active);
    }

}
