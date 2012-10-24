package net.sourceforge.eclipsejetty.launch;

import net.sourceforge.eclipsejetty.JettyPluginUtils;
import net.sourceforge.eclipsejetty.jetty.JettyConfig;
import net.sourceforge.eclipsejetty.jetty.JettyConfigScope;
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
    private JettyConfigScope scope;
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
        scope = config.getScope();
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

    public JettyConfigScope getScope()
    {
        return scope;
    }

    public void setScope(JettyConfigScope scope)
    {
        if (!JettyPluginUtils.equals(this.scope, scope))
        {
            this.scope = scope;
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
                    //                    button.setSelection(!button.getSelection());
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

                item.setText(1, "Eclipse Jetty Launcher Context");
                item.setForeground(1, color);

                item.setText(2, scope.getDescription());
                item.setForeground(2, color);
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

                item.setText(2, scope.getDescription());
                item.setForeground(2, color);
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
            //            int indexOf = table.indexOf(item);

            item.dispose();
            button.dispose();

            //            if (indexOf > 0)
            //            {
            //                table.clearAll();
            //            }

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
        return new JettyConfig(path, type, scope, active);
    }

}
