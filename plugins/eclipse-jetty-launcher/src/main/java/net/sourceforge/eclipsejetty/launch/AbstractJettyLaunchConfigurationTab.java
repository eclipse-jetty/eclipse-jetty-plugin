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

import org.eclipse.jdt.debug.ui.launchConfigurations.JavaLaunchTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

/**
 * UI
 * 
 * @author Christian K&ouml;berl
 * @author Manfred Hantschel
 */
public abstract class AbstractJettyLaunchConfigurationTab extends JavaLaunchTab
{

    protected Label createLabel(final Composite composite, final String text, final int widthHint, int horizontalSpan,
        int verticalSpan)
    {
        Label label = new Label(composite, SWT.NONE);

        GridData gridData =
            new GridData((widthHint < 0) ? SWT.FILL : SWT.LEFT, (verticalSpan <= 1) ? SWT.CENTER : SWT.TOP,
                widthHint < 0, false, horizontalSpan, verticalSpan);

        if (widthHint >= 0)
        {
            gridData.widthHint = widthHint;
        }

        label.setLayoutData(gridData);
        label.setText(text);

        return label;
    }

    protected Button createButton(final Composite composite, int style, final String text, final int widthHint,
        int horizontalSpan, int verticalSpan, SelectionListener... selectionListeners)
    {
        Button button = new Button(composite, style);

        GridData gridData =
            new GridData((widthHint < 0) ? SWT.FILL : SWT.LEFT, (verticalSpan <= 1) ? SWT.CENTER : SWT.TOP,
                widthHint < 0, false, horizontalSpan, verticalSpan);

        if (widthHint >= 0)
        {
            gridData.widthHint = widthHint;
        }

        button.setLayoutData(gridData);
        button.setText(text);

        if (selectionListeners != null)
        {
            for (SelectionListener selectionListener : selectionListeners)
            {
                button.addSelectionListener(selectionListener);
            }
        }

        return button;
    }

    protected Text createText(final Composite composite, int style, final int widthHint, int heightHint,
        int horizontalSpan, int verticalSpan, ModifyListener... modifyListeners)
    {
        Text text = new Text(composite, style);

        GridData gridData =
            new GridData((widthHint < 0) ? SWT.FILL : SWT.LEFT, (verticalSpan <= 1) ? SWT.CENTER : SWT.TOP,
                widthHint < 0, false, horizontalSpan, verticalSpan);

        if (widthHint >= 0)
        {
            gridData.widthHint = widthHint;
        }

        if (heightHint >= 0)
        {
            gridData.heightHint = heightHint;
        }

        text.setLayoutData(gridData);

        if (modifyListeners != null)
        {
            for (ModifyListener modifyListener : modifyListeners)
            {
                text.addModifyListener(modifyListener);
            }
        }

        return text;
    }
    
    protected Table createTable(Composite composite, int style, int widthHint, int heightHint, int horizontalSpan, int verticalSpan, String... titles) {
        Table table = new Table(composite, SWT.BORDER | SWT.HIDE_SELECTION);
        table.setLinesVisible(false);
        table.setHeaderVisible(true);

        GridData gridData = new GridData((widthHint < 0) ? SWT.FILL : SWT.LEFT, SWT.TOP, widthHint < 0, true, horizontalSpan, verticalSpan);
        
        if (widthHint >= 0)
        {
            gridData.widthHint = widthHint;
        }

        if (heightHint >= 0)
        {
            gridData.minimumHeight = heightHint;
        }

        table.setLayoutData(gridData);

        for (String title : titles)
        {
            TableColumn column = new TableColumn(table, SWT.NONE);
            column.setText(title);
        }
        
        return table;
    }
}
