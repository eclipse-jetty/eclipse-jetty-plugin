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
package net.sourceforge.eclipsejetty.launch.util;

import java.io.File;

import net.sourceforge.eclipsejetty.JettyPluginUtils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.variables.IStringVariable;
import org.eclipse.debug.ui.StringVariableSelectionDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class JettyLaunchUI
{

    /**
     * Creates a label
     * 
     * @param composite the composite
     * @param text the text of the label
     * @param widthHint the width, <0 to fill up the space
     * @param horizontalSpan the horizontal span
     * @param verticalSpan the vertical span
     * @return the label
     */
    public static Label createLabel(final Composite composite, final String text, final int widthHint,
        int horizontalSpan, int verticalSpan)
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

    /**
     * Creates an hint.
     * 
     * @param composite the composite
     * @param text the text of the hint
     * @param widthHint the width, <0 to fill up the space
     * @param horizontalSpan the horizontal span
     * @param verticalSpan the vertical span
     * @return the label
     */
    public static Label createHint(final Composite composite, final String text, final int widthHint,
        int horizontalSpan, int verticalSpan)
    {
        Label label = createLabel(composite, text, widthHint, horizontalSpan, verticalSpan);

        label.setAlignment(SWT.RIGHT);

        FontData[] fontData = label.getFont().getFontData();

        for (FontData element : fontData)
        {
            element.setStyle(SWT.ITALIC);
        }

        final Font italicFont = new Font(composite.getDisplay(), fontData);

        label.setFont(italicFont);

        label.addDisposeListener(new DisposeListener()
        {
            public void widgetDisposed(DisposeEvent e)
            {
                italicFont.dispose();
            }
        });

        return label;
    }

    /**
     * Create a button.
     * 
     * @param composite the composite
     * @param style the style
     * @param text the text of the button
     * @param toolTip the tool tip of the button
     * @param widthHint the width, <0 to fill up the space
     * @param horizontalSpan the horizontal span
     * @param verticalSpan the vertical span
     * @param selectionListeners listeners to be notified on action
     * @return the button
     */
    public static Button createButton(final Composite composite, int style, final String text, String toolTip,
        final int widthHint, int horizontalSpan, int verticalSpan, SelectionListener... selectionListeners)
    {
        return createButton(composite, style, null, text, toolTip, widthHint, horizontalSpan, verticalSpan,
            selectionListeners);
    }

    /**
     * Create a button.
     * 
     * @param composite the composite
     * @param style the style
     * @param image the image
     * @param toolTip the tool tip of the button
     * @param widthHint the width, <0 to fill up the space
     * @param horizontalSpan the horizontal span
     * @param verticalSpan the vertical span
     * @param selectionListeners listeners to be notified on action
     * @return the button
     */
    public static Button createButton(final Composite composite, int style, final Image image, String toolTip,
        final int widthHint, int horizontalSpan, int verticalSpan, SelectionListener... selectionListeners)
    {
        return createButton(composite, style, image, null, toolTip, widthHint, horizontalSpan, verticalSpan,
            selectionListeners);
    }

    /**
     * Create a button.
     * 
     * @param composite the composite
     * @param style the style
     * @param image the image
     * @param text the text of the button
     * @param toolTip the tool tip of the button
     * @param widthHint the width, <0 to fill up the space
     * @param horizontalSpan the horizontal span
     * @param verticalSpan the vertical span
     * @param selectionListeners listeners to be notified on action
     * @return the button
     */
    public static Button createButton(final Composite composite, int style, Image image, final String text,
        String toolTip, final int widthHint, int horizontalSpan, int verticalSpan,
        SelectionListener... selectionListeners)
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

        if (text != null)
        {
            button.setText(text);
        }

        if (toolTip != null)
        {
            button.setToolTipText(toolTip);
        }

        if (image != null)
        {
            button.setImage(image);
        }

        if (selectionListeners != null)
        {
            for (SelectionListener selectionListener : selectionListeners)
            {
                button.addSelectionListener(selectionListener);
            }
        }

        return button;
    }

    /**
     * Creates a text component
     * 
     * @param composite the composite
     * @param style the style
     * @param toolTip the tool tip
     * @param widthHint the width, <0 to fill up the space
     * @param heightHint the height, <0 to ignore
     * @param horizontalSpan the horizontal span
     * @param verticalSpan the vertical span
     * @param selectionListeners listeners to be notified on action
     * @return the component
     */
    public static Text createText(final Composite composite, int style, String toolTip, final int widthHint,
        int heightHint, int horizontalSpan, int verticalSpan, ModifyListener... modifyListeners)
    {
        Text text = new Text(composite, style);

        GridData gridData =
            new GridData((widthHint < 0) ? SWT.FILL : SWT.LEFT, (verticalSpan <= 1) ? SWT.CENTER : SWT.TOP,
                widthHint < 0, false, horizontalSpan, verticalSpan);

        if (toolTip != null)
        {
            text.setToolTipText(toolTip);
        }

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

    /**
     * Creates a spinner
     * 
     * @param composite the composite
     * @param style the style
     * @param toolTip the tool tip
     * @param widthHint the width, <0 to fill up the space
     * @param heightHint the height, <0 to ignore
     * @param horizontalSpan the horizontal span
     * @param verticalSpan the vertical span
     * @param selectionListeners listeners to be notified on action
     * @return the component
     */
    public static Spinner createSpinner(final Composite composite, int style, String toolTip, final int widthHint,
        int heightHint, int horizontalSpan, int verticalSpan, ModifyListener... modifyListeners)
    {
        Spinner spinner = new Spinner(composite, style);

        GridData gridData =
            new GridData((widthHint < 0) ? SWT.FILL : SWT.LEFT, (verticalSpan <= 1) ? SWT.CENTER : SWT.TOP,
                widthHint < 0, false, horizontalSpan, verticalSpan);

        if (toolTip != null)
        {
            spinner.setToolTipText(toolTip);
        }

        if (widthHint >= 0)
        {
            gridData.widthHint = widthHint;
        }

        if (heightHint >= 0)
        {
            gridData.heightHint = heightHint;
        }

        spinner.setLayoutData(gridData);

        if (modifyListeners != null)
        {
            for (ModifyListener modifyListener : modifyListeners)
            {
                spinner.addModifyListener(modifyListener);
            }
        }

        return spinner;
    }

    /**
     * Creates a composite without label and without border (slight margin)
     * 
     * @param composite the composite
     * @param style the type
     * @param columns the number of columns
     * @param widthHint the width, <0 to fill up the space
     * @param grabVerticalSpace true to grab all vertical space
     * @param horizontalSpan the horizontal span
     * @param verticalSpan the vertical span
     * @return the composite
     */
    public static Composite createTopComposite(final Composite composite, int style, int columns, final int widthHint,
        boolean grabVerticalSpace, int horizontalSpan, int verticalSpan)
    {
        GridLayout layout = new GridLayout(columns, false);

        layout.marginHeight = 8;
        layout.marginWidth = 8;

        return createComposite(composite, style, widthHint, grabVerticalSpace, horizontalSpan, verticalSpan, layout);
    }

    /**
     * Create a composite, usually used for button bars (no margin)
     * 
     * @param composite the composite
     * @param style the type
     * @param columns the number of columns
     * @param widthHint the width, <0 to fill up the space
     * @param grabVerticalSpace true to grab all vertical space
     * @param horizontalSpan the horizontal span
     * @param verticalSpan the vertical span
     * @return the composite
     */
    public static Composite createComposite(final Composite composite, int style, int columns, final int widthHint,
        boolean grabVerticalSpace, int horizontalSpan, int verticalSpan)
    {
        GridLayout layout = new GridLayout(columns, false);

        layout.marginHeight = 0;
        layout.marginWidth = 0;

        return createComposite(composite, style, widthHint, grabVerticalSpace, horizontalSpan, verticalSpan, layout);
    }

    /**
     * Create a composite, using the specified layout
     * 
     * @param composite the composite
     * @param style the type
     * @param widthHint the width, <0 to fill up the space
     * @param grabVerticalSpace true to grab all vertical space
     * @param horizontalSpan the horizontal span
     * @param verticalSpan the vertical span
     * @param layout the layout
     * @return the composite
     */
    private static Composite createComposite(final Composite composite, int style, final int widthHint,
        boolean grabVerticalSpace, int horizontalSpan, int verticalSpan, GridLayout layout)
    {
        Composite result = new Composite(composite, style);

        result.setLayout(layout);

        GridData gridData =
            new GridData((widthHint < 0) ? SWT.FILL : SWT.LEFT, (verticalSpan <= 1) ? SWT.CENTER : SWT.TOP,
                widthHint < 0, grabVerticalSpace, horizontalSpan, verticalSpan);

        if (widthHint >= 0)
        {
            gridData.widthHint = widthHint;
        }

        result.setLayoutData(gridData);

        return result;
    }

    /**
     * Creates a table
     * 
     * @param composite the composite
     * @param style the type
     * @param widthHint the width, <0 to fill up the space
     * @param heightHint the minimum height, <0 to ignore
     * @param horizontalSpan the horizontal span
     * @param verticalSpan the vertical span
     * @param titles the column titles
     * @return the table
     */
    public static Table createTable(Composite composite, int style, int widthHint, int heightHint, int horizontalSpan,
        int verticalSpan, String... titles)
    {
        Table table = new Table(composite, style);
        table.setLinesVisible(false);
        table.setHeaderVisible(true);

        GridData gridData =
            new GridData((widthHint < 0) ? SWT.FILL : SWT.LEFT, SWT.TOP, widthHint < 0, true, horizontalSpan,
                verticalSpan);

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

    /**
     * Shows a dialog to select a file from the workspace
     * 
     * @param shell the shell
     * @param project the project
     * @param title the title
     * @param message the message
     * @param path the initial path
     * @return the result, null if canceled
     */
    public static String chooseWorkspaceFile(Shell shell, IProject project, String title, String message, String path)
    {
        path = JettyPluginUtils.resolveVariables(path);

        ElementTreeSelectionDialog dialog =
            new ElementTreeSelectionDialog(shell, new WorkbenchLabelProvider(), new BaseWorkbenchContentProvider());

        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());

        if ((path != null) && (path.length() > 0))
        {
            dialog.setInitialSelection(path);
        }

        dialog.setAllowMultiple(false);

        dialog.open();

        Object[] results = dialog.getResult();

        if ((results != null) && (results.length > 0) && (results[0] instanceof IFile))
        {
            IFile file = (IFile) results[0];

            return JettyPluginUtils.toRelativePath(project, file.getFullPath().toString());
        }

        return null;
    }

    /**
     * Shows a dialog to select a folder from the workspace
     * 
     * @param project the project
     * @param shell the shell
     * @param title the title
     * @param message the message
     * @param path the initial path
     * @return the result, null if canceled
     */
    public static String chooseWorkspaceDirectory(Shell shell, IProject project, String title, String message,
        String path)
    {
        path = JettyPluginUtils.resolveVariables(path);

        ContainerSelectionDialog dialog = new ContainerSelectionDialog(shell, project, false, message);

        dialog.setTitle(title);

        if (project != null)
        {
            dialog.setInitialSelections(new Object[]{path});
        }

        dialog.showClosedProjects(false);
        dialog.open();

        Object[] results = dialog.getResult();

        if ((results != null) && (results.length > 0) && (results[0] instanceof IPath))
        {
            IPath folder = (IPath) results[0];

            return JettyPluginUtils.toRelativePath(project, folder.toString());
        }

        return null;
    }

    /**
     * Shows a dialog to select a folder from the file system
     * 
     * @param shell the shell
     * @param text the title
     * @param message the message
     * @param path the inital path
     * @return the folder, null if canceled
     */
    public static String chooseExternalDirectory(Shell shell, String text, String message, String path)
    {
        path = JettyPluginUtils.resolveVariables(path);

        DirectoryDialog dialog = new DirectoryDialog(shell, SWT.OPEN);

        dialog.setText(text);
        dialog.setMessage(message);
        dialog.setFilterPath(path);

        return dialog.open();
    }

    /**
     * Shows a dialog to select a file from the file system
     * 
     * @param shell the shell
     * @param path the initial path
     * @param text the title
     * @param filter an array of filter options
     * @return the selected file, null if canceled
     */
    public static String chooseExternalFile(Shell shell, String path, String text, String... filter)
    {
        path = JettyPluginUtils.resolveVariables(path);

        FileDialog dialog = new FileDialog(shell, SWT.OPEN);

        dialog.setText(text);

        if (path != null)
        {
            File file = new File(path);

            dialog.setFileName(file.getName());
            dialog.setFilterPath(file.getParent());
        }

        dialog.setFilterExtensions(filter);

        return dialog.open();
    }

    /**
     * Shows a dialog to select or define a variable
     * 
     * @param shell the shell
     * @param textComponent the text component to be filled
     */
    public static void chooseVariable(Shell shell, Text textComponent)
    {
        StringVariableSelectionDialog dialog = new StringVariableSelectionDialog(shell);

        if (Window.OK == dialog.open())
        {
            Object[] results = dialog.getResult();

            for (int i = results.length - 1; i >= 0; i -= 1)
            {
                String placeholder = "${" + ((IStringVariable) results[i]).getName() + "}"; //$NON-NLS-1$ //$NON-NLS-2$
                int position = textComponent.getCaretPosition();
                String text = textComponent.getText();

                if (position <= 0)
                {
                    text = placeholder + text;
                }
                else if (position >= text.length())
                {
                    text = text + placeholder;
                }
                else
                {
                    text = text.substring(0, position) + placeholder + text.substring(position);
                }

                textComponent.setText(text);
            }
        }
    }

}
