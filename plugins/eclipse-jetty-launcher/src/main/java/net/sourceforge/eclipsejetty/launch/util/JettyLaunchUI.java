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
import java.util.List;

import net.sourceforge.eclipsejetty.JettyPluginUtils;
import net.sourceforge.eclipsejetty.Messages;
import net.sourceforge.eclipsejetty.util.Result;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.variables.IStringVariable;
import org.eclipse.debug.ui.StringVariableSelectionDialog;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class JettyLaunchUI
{

    /**
     * Creates a label
     * 
     * @param parent the parent composite
     * @param text the text of the label
     * @param widthHint the width, <0 to fill up the space
     * @param horizontalAlignment the horizontal alignment of the text
     * @param horizontalSpan the horizontal span
     * @param verticalSpan the vertical span
     * @return the label
     */
    public static Label createLabel(Composite parent, String text, int widthHint, int horizontalAlignment,
        int horizontalSpan, int verticalSpan)
    {
        Label label = new Label(parent, SWT.NONE);

        GridData gridData =
            new GridData(SWT.FILL, (verticalSpan <= 1) ? SWT.CENTER : SWT.TOP, widthHint < 0, false, horizontalSpan,
                verticalSpan);

        if (widthHint >= 0)
        {
            gridData.widthHint = widthHint;
        }

        label.setLayoutData(gridData);
        label.setText(text);
        label.setAlignment(horizontalAlignment);

        return label;
    }

    /**
     * Creates a label with an image
     * 
     * @param parent the parent composite
     * @param image the image
     * @param widthHint the width, <0 to fill up the space
     * @param horizontalAlignment the horizontal alignment
     * @param verticalAlignment the vertical alignment
     * @param horizontalSpan the horizontal span
     * @param verticalSpan the vertical span
     * @return the label with an image
     */
    public static Label createImage(Composite parent, Image image, int widthHint, int horizontalAlignment,
        int verticalAlignment, int horizontalSpan, int verticalSpan)
    {
        Label label = new Label(parent, SWT.NONE);

        GridData gridData =
            new GridData(horizontalAlignment, verticalAlignment, false, false, horizontalSpan, verticalSpan);

        if (widthHint >= 0)
        {
            gridData.widthHint = widthHint;
        }

        label.setAlignment(horizontalAlignment);
        label.setLayoutData(gridData);
        label.setImage(image);

        return label;
    }

    /**
     * Creates an hint.
     * 
     * @param parent the parent composite
     * @param text the text of the hint
     * @param widthHint the width, <0 to fill up the space
     * @param horizontalSpan the horizontal span
     * @param verticalSpan the vertical span
     * @return the label
     */
    public static Label createHint(Composite parent, String text, int widthHint, int horizontalSpan, int verticalSpan)
    {
        Label label = createLabel(parent, text, widthHint, SWT.LEFT, horizontalSpan, verticalSpan);

        label.setAlignment(SWT.RIGHT);

        setItalicFont(parent.getDisplay(), label);

        return label;
    }

    /**
     * Creates a link
     * 
     * @param parent the parent composite
     * @param style the type
     * @param text the text
     * @param horizontalAlignment the horizontal alignment of the text
     * @param horizontalSpan the horizontal span
     * @param verticalSpan the vertical span
     * @param listener the listener
     * @return the link
     */
    public static Link createLink(Composite parent, int style, String text, int horizontalAlignment,
        int horizontalSpan, int verticalSpan, Listener listener)
    {
        Link link = new Link(parent, style);

        GridData gridData = new GridData(horizontalAlignment, SWT.CENTER, false, false, horizontalSpan, verticalSpan);

        link.setLayoutData(gridData);

        if (text != null)
        {
            link.setText(text);
        }

        if (listener != null)
        {
            link.addListener(SWT.Selection, listener);
        }

        setItalicFont(parent.getDisplay(), link);

        return link;
    }

    /**
     * Create a button.
     * 
     * @param parent the parent composite
     * @param style the style
     * @param text the text of the button
     * @param toolTip the tool tip of the button
     * @param widthHint the width, <0 to fill up the space
     * @param horizontalSpan the horizontal span
     * @param verticalSpan the vertical span
     * @param selectionListeners listeners to be notified on action
     * @return the button
     */
    public static Button createButton(Composite parent, int style, String text, String toolTip, int widthHint,
        int horizontalSpan, int verticalSpan, SelectionListener... selectionListeners)
    {
        return createButton(parent, style, null, text, toolTip, widthHint, horizontalSpan, verticalSpan,
            selectionListeners);
    }

    /**
     * Create a button.
     * 
     * @param parent the parent composite
     * @param style the style
     * @param image the image
     * @param toolTip the tool tip of the button
     * @param widthHint the width, <0 to fill up the space
     * @param horizontalSpan the horizontal span
     * @param verticalSpan the vertical span
     * @param selectionListeners listeners to be notified on action
     * @return the button
     */
    public static Button createButton(Composite parent, int style, Image image, String toolTip, int widthHint,
        int horizontalSpan, int verticalSpan, SelectionListener... selectionListeners)
    {
        return createButton(parent, style, image, null, toolTip, widthHint, horizontalSpan, verticalSpan,
            selectionListeners);
    }

    /**
     * Create a button.
     * 
     * @param parent the parent composite
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
    public static Button createButton(Composite parent, int style, Image image, String text, String toolTip,
        int widthHint, int horizontalSpan, int verticalSpan, SelectionListener... selectionListeners)
    {
        Button button = new Button(parent, style);

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
     * @param parent the parent composite
     * @param style the style
     * @param toolTip the tool tip
     * @param widthHint the width, <0 to fill up the space
     * @param heightHint the height, <0 to ignore
     * @param horizontalSpan the horizontal span
     * @param verticalSpan the vertical span
     * @param selectionListeners listeners to be notified on action
     * @return the component
     */
    public static Text createText(Composite parent, int style, String toolTip, int widthHint, int heightHint,
        int horizontalSpan, int verticalSpan, ModifyListener... modifyListeners)
    {
        Text text = new Text(parent, style);

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
     * @param parent the parent composite
     * @param style the style
     * @param toolTip the tool tip
     * @param widthHint the width, <0 to fill up the space
     * @param heightHint the height, <0 to ignore
     * @param horizontalSpan the horizontal span
     * @param verticalSpan the vertical span
     * @param selectionListeners listeners to be notified on action
     * @return the component
     */
    public static Spinner createSpinner(Composite parent, int style, String toolTip, int widthHint, int heightHint,
        int horizontalSpan, int verticalSpan, ModifyListener... modifyListeners)
    {
        Spinner spinner = new Spinner(parent, style);

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
     * Creates a titled group
     * 
     * @param parent the parent composite
     * @param title the title
     * @param columns the number of columns
     * @param widthHint the width, <0 to fill up the space
     * @param grabVerticalSpace true to grab all vertical space
     * @param horizontalSpan the horizontal span
     * @param verticalSpan the vertical span
     * @return the group
     */
    public static Group createGroup(Composite parent, String title, int columns, int widthHint,
        boolean grabVerticalSpace, int horizontalSpan, int verticalSpan)
    {
        Group group = new Group(parent, SWT.NONE);

        group.setLayout(new GridLayout(columns, false));

        GridData gridData =
            new GridData((widthHint < 0) ? SWT.FILL : SWT.LEFT, (verticalSpan <= 1) ? SWT.FILL : SWT.TOP,
                widthHint < 0, grabVerticalSpace, horizontalSpan, verticalSpan);

        if (widthHint >= 0)
        {
            gridData.widthHint = widthHint;
        }

        group.setLayoutData(gridData);
        group.setText(title);

        return group;
    }

    /**
     * Creates the main tab composite
     * 
     * @param parent the parent
     * @param columns the number of columns
     * @param equalWidth true, to make columns equals in width
     * @return the composite;
     */
    public static Composite createTabComposite(Composite parent, int columns, boolean equalWidth)
    {
        Composite composite = new Composite(parent, SWT.NONE);

        composite.setLayout(new GridLayout(columns, equalWidth));

        return composite;
    }

    /**
     * Creates a composite without label and without border (slight margin)
     * 
     * @param parent the parent composite
     * @param style the type
     * @param columns the number of columns
     * @param widthHint the width, <0 to fill up the space
     * @param grabVerticalSpace true to grab all vertical space
     * @param horizontalSpan the horizontal span
     * @param verticalSpan the vertical span
     * @return the composite
     */
    public static Composite createTopComposite(Composite parent, int style, int columns, int widthHint,
        boolean grabVerticalSpace, int horizontalSpan, int verticalSpan)
    {
        GridLayout layout = new GridLayout(columns, false);

        layout.marginHeight = 8;
        layout.marginWidth = 8;

        return createComposite(parent, style, widthHint, grabVerticalSpace, horizontalSpan, verticalSpan, layout);
    }

    /**
     * Create a composite, usually used for button bars (no margin)
     * 
     * @param parent the parent composite
     * @param style the type
     * @param columns the number of columns
     * @param widthHint the width, <0 to fill up the space
     * @param grabVerticalSpace true to grab all vertical space
     * @param horizontalSpan the horizontal span
     * @param verticalSpan the vertical span
     * @return the composite
     */
    public static Composite createComposite(Composite parent, int style, int columns, int widthHint,
        boolean grabVerticalSpace, int horizontalSpan, int verticalSpan)
    {
        GridLayout layout = new GridLayout(columns, false);

        layout.marginHeight = 0;
        layout.marginWidth = 0;

        return createComposite(parent, style, widthHint, grabVerticalSpace, horizontalSpan, verticalSpan, layout);
    }

    /**
     * Create a composite, using the specified layout
     * 
     * @param parent the parent composite
     * @param style the type
     * @param widthHint the width, <0 to fill up the space
     * @param grabVerticalSpace true to grab all vertical space
     * @param horizontalSpan the horizontal span
     * @param verticalSpan the vertical span
     * @param layout the layout
     * @return the composite
     */
    private static Composite createComposite(Composite parent, int style, int widthHint, boolean grabVerticalSpace,
        int horizontalSpan, int verticalSpan, GridLayout layout)
    {
        Composite composite = new Composite(parent, style);

        composite.setLayout(layout);

        GridData gridData =
            new GridData((widthHint < 0) ? SWT.FILL : SWT.LEFT, (verticalSpan <= 1) ? SWT.FILL : SWT.TOP,
                widthHint < 0, grabVerticalSpace, horizontalSpan, verticalSpan);

        if (widthHint >= 0)
        {
            gridData.widthHint = widthHint;
        }

        composite.setLayoutData(gridData);

        return composite;
    }

    /**
     * Creates a table
     * 
     * @param parent the parent composite
     * @param style the type
     * @param widthHint the width, <0 to fill up the space
     * @param heightHint the minimum height, <0 to ignore
     * @param horizontalSpan the horizontal span
     * @param verticalSpan the vertical span
     * @param titles the column titles
     * @return the table
     */
    public static Table createTable(Composite parent, int style, int widthHint, int heightHint, int horizontalSpan,
        int verticalSpan, String... titles)
    {
        Table table = new Table(parent, style);
        table.setLinesVisible(false);
        table.setHeaderVisible(true);

        GridData gridData = new GridData(SWT.FILL, SWT.FILL, widthHint < 0, true, horizontalSpan, verticalSpan);

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

    /**
     * Searches for the web app directory.
     * 
     * @param shell the shell
     * @param project the project
     * @param path the current path, may be null
     * @return the web app directory
     * @throws CoreException on occasion
     */
    public static String chooseWebAppDir(final Shell shell, final IProject project, final String path)
        throws CoreException
    {
        final List<IPath> paths = JettyLaunchUtils.findWebappDirs(project, Integer.MAX_VALUE);

        if (paths.size() == 0)
        {
            Display.getCurrent().syncExec(new Runnable()
            {
                public void run()
                {
                    MessageDialog.openError(Display.getCurrent().getActiveShell(),
                        Messages.configTab_webAppScanFailedTitle,
                        String.format(Messages.configTab_webAppScanFailedMessage, project.getName()));
                }
            });

            return chooseWorkspaceDirectory(shell, project, Messages.configTab_webAppBrowseTitle,
                Messages.configTab_webAppBrowseMessage, path);
        }
        else if (paths.size() > 1)
        {
            return chooseWebAppDir(shell, project, paths, path);
        }

        return JettyPluginUtils.toRelativePath(project, paths.get(0).toString());
    }

    /**
     * Choose one webApp directory from a list of directories. Show a user selection on cancel
     * 
     * @param shell the shell
     * @param project the project
     * @param paths the paths
     * @param path the path for the file selection
     * @return the selected directory, null if none was selected
     * @throws CoreException on occasion
     */
    public static String chooseWebAppDir(final Shell shell, final IProject project, final List<IPath> paths,
        final String path) throws CoreException
    {
        final Result<String> result = new Result<String>();

        Display.getCurrent().syncExec(new Runnable()
        {
            public void run()
            {
                ElementListSelectionDialog dialog =
                    new ElementListSelectionDialog(Display.getCurrent().getActiveShell(), new WebAppPathLabelProvider());
                String[] elements = JettyLaunchUtils.toStringArray(paths);

                for (int i = 0; i < elements.length; i += 1)
                {
                    elements[i] = JettyPluginUtils.toRelativePath(project, elements[i]);
                }
                dialog.setElements(elements);
                dialog.setTitle("Choose WebApp Directory");
                dialog
                    .setMessage("There are multiple folders, that may act as Web Application directory.\nPlease choose one:");
                dialog.setMultipleSelection(false);

                if (dialog.open() != Window.OK)
                {
                    result.setResult(chooseWorkspaceDirectory(shell, project, Messages.configTab_webAppBrowseTitle,
                        Messages.configTab_webAppBrowseMessage, path));

                    return;
                }

                result.setResult((String) dialog.getResult()[0]);
            }
        });

        return result.getResult();
    }

    private static void setItalicFont(Display display, Control control)
    {
        FontData[] fontData = control.getFont().getFontData();

        for (FontData element : fontData)
        {
            element.setStyle(SWT.ITALIC);
        }

        final Font italicFont = new Font(display, fontData);

        control.setFont(italicFont);

        control.addDisposeListener(new DisposeListener()
        {
            public void widgetDisposed(DisposeEvent e)
            {
                italicFont.dispose();
            }
        });
    }

}
