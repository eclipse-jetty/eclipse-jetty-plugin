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

import net.sourceforge.eclipsejetty.JettyPluginUtils;

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
 * Holds one dependency entry of the table in the advance configuration tab.
 * 
 * @author Manfred Hantschel
 */
public class JettyLaunchDependencyEntry implements Comparable<JettyLaunchDependencyEntry>
{

    public enum Type
    {
        DEFAULT,
        ALWAYS_INCLUDED,
        ALWAYS_EXCLUDED;
    }

    private final String path;
    private final String name;

    private Type type;
    private boolean included;
    private boolean obsolete;
    private String scope;
    private boolean needsUpdate;
    private TableItem item;
    private Button button;

    public JettyLaunchDependencyEntry(String path, String name, Type type)
    {
        super();

        this.path = path;
        this.name = name;
        this.type = type;
    }

    public boolean isIncluded()
    {
        return included;
    }

    public void setIncluded(boolean included)
    {
        if (this.included != included)
        {
            this.included = included;
            needsUpdate = true;
        }
    }

    public String getPath()
    {
        return path;
    }

    public String getName()
    {
        return name;
    }

    public Type getType()
    {
        return type;
    }

    public void setType(Type type)
    {
        if (this.type != type)
        {
            this.type = type;
            needsUpdate = true;
        }
    }

    public boolean isObsolete()
    {
        return obsolete;
    }

    public void setObsolete(boolean obsolete)
    {
        if (this.obsolete != obsolete)
        {
            this.obsolete = obsolete;
            needsUpdate = true;
        }
    }

    public String getScope()
    {
        return scope;
    }

    public void setScope(String scope)
    {
        if (!JettyPluginUtils.equals(this.scope, scope))
        {
            this.scope = scope;
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
                if (button.getSelection())
                {
                    if (!button.getGrayed())
                    {
                        button.setGrayed(true);
                        type = Type.DEFAULT;
                        fillItem(item);
                    }
                    else
                    {
                        type = Type.ALWAYS_EXCLUDED;
                        fillItem(item);
                    }
                }
                else
                {
                    if (button.getGrayed())
                    {
                        button.setGrayed(false);
                        button.setSelection(true);
                        type = Type.ALWAYS_INCLUDED;
                        fillItem(item);
                    }
                    else
                    {
                        type = Type.ALWAYS_EXCLUDED;
                        fillItem(item);
                    }
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
        switch (type)
        {
            case DEFAULT:
                button.setGrayed(true);
                button.setSelection(true);
                break;

            case ALWAYS_EXCLUDED:
                button.setGrayed(false);
                button.setSelection(false);
                break;

            case ALWAYS_INCLUDED:
                button.setGrayed(false);
                button.setSelection(true);
                break;
        }

        Color color;

        if (((included) || (type == Type.ALWAYS_INCLUDED)) && (type != Type.ALWAYS_EXCLUDED))
        {
            color = item.getDisplay().getSystemColor(SWT.COLOR_BLACK);
        }
        else
        {
            color = item.getDisplay().getSystemColor(SWT.COLOR_GRAY);
        }

        item.setText(1, name);
        item.setForeground(1, color);
        item.setText(2, scope);
        item.setForeground(2, color);
        item.setText(3, path);
        item.setForeground(3, color);
        
        needsUpdate = false;
    }

    public boolean updateItem(Table table, SelectionListener listener, int index)
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

        if (needsUpdate)
        {
            fillItem(item);
            return true;
        }
        
        return false;
    }

    public void deleteItem(Table table)
    {
        if (item != null) {
            int indexOf = table.indexOf(item);
            
            item.dispose();
            button.dispose();
            
            if (indexOf > 0) {
                table.clearAll();
            }
            
            item = null;
            button = null;
        }
    }
    
    public void setItem(TableItem item)
    {
        this.item = item;
    }

    public String createMatcher()
    {
        // TODO this method whole can be heavily enhanced - e.g. it does not look at the path. If you exclude one test-classes entry, all are excluded
        // meanwhile this one-liner should do it
        
        return ".*/" + name.replaceAll("([\\\\*+\\[\\](){}\\$.?\\^|])", "\\\\$1");
    }

    public int compareTo(JettyLaunchDependencyEntry entry)
    {
        int result = JettyPluginUtils.dictionaryCompare(name, entry.name);

        if (result != 0)
        {
            return result;
        }

        return JettyPluginUtils.dictionaryCompare(path, entry.path);
    }

}
