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

import net.sourceforge.eclipsejetty.JettyPlugin;
import net.sourceforge.eclipsejetty.JettyPluginUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
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

    public enum Kind
    {
        OTHER(JettyPlugin.DEPENDENCY_OTHER, JettyPlugin.DEPENDENCY_OTHER_DEACTIVATED),
        JAR(JettyPlugin.DEPENDENCY_JAR, JettyPlugin.DEPENDENCY_JAR_DEACTIVATED),
        PROJECT(JettyPlugin.DEPENDENCY_PROJECT, JettyPlugin.DEPENDENCY_PROJECT_DEACTIVATED);

        private final String id;
        private final String deactivatedId;

        private Kind(String id, String deactivatedId)
        {
            this.id = id;
            this.deactivatedId = deactivatedId;
        }

        public Image getIcon()
        {
            return JettyPlugin.getIcon(id);
        }

        public Image getDeactivatedIcon()
        {
            return JettyPlugin.getIcon(deactivatedId);
        }
    }

    private final String genericId;
    private final String path;
    private final String name;
    private final Kind kind;

    private Type type;
    private boolean included;
    private boolean defaultIncluded;
    private boolean obsolete;
    private boolean global;
    private String scope;
    private boolean needsUpdate;
    private TableItem item;
    private Button includeButton;
    private Button globalButton;

    public JettyLaunchDependencyEntry(String genericId, String path, String name, Kind kind, Type type)
    {
        super();

        this.genericId = genericId;
        this.path = path;
        this.name = name;
        this.kind = kind;
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

    public boolean isDefaultIncluded()
    {
        return defaultIncluded;
    }

    public void setDefaultIncluded(boolean defaultIncluded)
    {
        if (this.defaultIncluded != defaultIncluded)
        {
            this.defaultIncluded = defaultIncluded;
            needsUpdate = true;
        }
    }

    public String getGenericId()
    {
        return genericId;
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
        this.obsolete = obsolete;
    }

    public boolean isGlobal()
    {
        return global;
    }

    public void setGlobal(boolean global)
    {
        if (this.global != global)
        {
            this.global = global;
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
        TableEditor includeEditor = new TableEditor(table);

        includeButton = new Button(table, SWT.CHECK);
        includeButton.addListener(SWT.Selection, new Listener()
        {
            public void handleEvent(Event e)
            {
                if (includeButton.getSelection())
                {
                    if (!includeButton.getGrayed())
                    {
                        includeButton.setGrayed(true);
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
                    if (includeButton.getGrayed())
                    {
                        includeButton.setGrayed(false);
                        includeButton.setSelection(true);
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
        includeButton.addSelectionListener(listener);
        includeButton.pack();

        includeEditor.minimumWidth = includeButton.getSize().x;
        includeEditor.horizontalAlignment = SWT.CENTER;
        includeEditor.setEditor(includeButton, item, 0);

        TableEditor globalEditor = new TableEditor(table);

        globalButton = new Button(table, SWT.CHECK);
        globalButton.addListener(SWT.Selection, new Listener()
        {
            public void handleEvent(Event e)
            {
                global = globalButton.getSelection();
                fillItem(item);
            }
        });
        globalButton.addSelectionListener(listener);
        globalButton.pack();

        globalEditor.minimumWidth = globalButton.getSize().x;
        globalEditor.horizontalAlignment = SWT.CENTER;
        globalEditor.setEditor(globalButton, item, 2);

        fillItem(item);
        setItem(item);
    }

    private void fillItem(TableItem item)
    {
        switch (type)
        {
            case DEFAULT:
                includeButton.setGrayed(true);
                includeButton.setSelection(true);
                break;

            case ALWAYS_EXCLUDED:
                includeButton.setGrayed(false);
                includeButton.setSelection(false);
                break;

            case ALWAYS_INCLUDED:
                includeButton.setGrayed(false);
                includeButton.setSelection(true);
                break;
        }

        Color color;

        if (((included) || (type == Type.ALWAYS_INCLUDED)) && (type != Type.ALWAYS_EXCLUDED))
        {
            color = item.getDisplay().getSystemColor(SWT.COLOR_BLACK);
            globalButton.setEnabled(true);
            item.setImage(0, kind.getIcon());
        }
        else
        {
            color = item.getDisplay().getSystemColor(SWT.COLOR_GRAY);
            globalButton.setEnabled(false);
            item.setImage(0, kind.getDeactivatedIcon());
        }

        item.setText(1, name);
        item.setForeground(1, color);

        globalButton.setSelection(global);

        item.setText(3, scope);
        item.setForeground(3, color);
        item.setText(4, path);
        item.setForeground(4, color);

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
        if (item != null)
        {
            int indexOf = table.indexOf(item);

            item.dispose();
            includeButton.dispose();
            globalButton.dispose();

            if (indexOf > 0)
            {
                table.clearAll();
            }

            item = null;
            includeButton = null;
            globalButton = null;
        }
    }

    public void setItem(TableItem item)
    {
        this.item = item;
    }

    /**
     * @deprecated replaced by a generic id mechanism to solve the problem with the "classes" and "test-classes"
     */
    @Deprecated
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
