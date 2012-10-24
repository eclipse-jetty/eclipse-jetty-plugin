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

import static net.sourceforge.eclipsejetty.launch.JettyLaunchUI.*;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.*;

import java.io.File;
import java.text.MessageFormat;
import java.util.List;

import net.sourceforge.eclipsejetty.JettyPlugin;
import net.sourceforge.eclipsejetty.JettyPluginConstants;
import net.sourceforge.eclipsejetty.jetty.JettyConfig;
import net.sourceforge.eclipsejetty.jetty.JettyConfigScope;
import net.sourceforge.eclipsejetty.jetty.JettyConfigType;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * UI
 * 
 * @author Christian K&ouml;berl
 * @author Manfred Hantschel
 */
public class JettyLaunchConfigurationTab extends AbstractJettyLaunchConfigurationTab
{
    private final ModifyDialogListener modifyDialogListener;
    private final JettyLaunchConfigEntryList configEntryList;

    private Text projectText;
    private Text webAppText;
    private Button webAppButton;
    private Text contextText;
    private Text portText;
    private Table configTable;
    private boolean configTableFormatted = false;
    private Button editConfigButton;
    private Button removeConfigButton;
    private Button moveUpConfigButton;
    private Button moveDownConfigButton;

    public JettyLaunchConfigurationTab()
    {
        modifyDialogListener = new ModifyDialogListener();
        configEntryList = new JettyLaunchConfigEntryList(modifyDialogListener);
    }

    /**
     * @wbp.parser.entryPoint
     */
    public void createControl(final Composite parent)
    {
        final Composite tabComposite = new Composite(parent, SWT.NONE);
        tabComposite.setLayout(new GridLayout(1, false));

        final Group projectGroup = new Group(tabComposite, SWT.NONE);
        projectGroup.setLayout(new GridLayout(2, false));
        projectGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        projectGroup.setText("Project:");

        projectText = createText(projectGroup, SWT.BORDER, -1, -1, 1, 1, modifyDialogListener);
        createButton(projectGroup, SWT.NONE, "Browse...", 128, 1, 1, new SelectionAdapter()
        {
            @Override
            public void widgetSelected(final SelectionEvent e)
            {
                chooseJavaProject();
            }
        });

        final Group applicationGroup = new Group(tabComposite, SWT.NONE);
        applicationGroup.setLayout(new GridLayout(3, false));
        applicationGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        applicationGroup.setText("Web Application:");

        createLabel(applicationGroup, "WebApp Directory:", 128, 1, 1);
        webAppText = createText(applicationGroup, SWT.BORDER, -1, -1, 1, 1, modifyDialogListener);
        webAppButton = createButton(applicationGroup, SWT.NONE, "Browse...", 128, 1, 1, new SelectionAdapter()
        {
            @Override
            public void widgetSelected(final SelectionEvent e)
            {
                chooseWebappDir();
            }
        });

        createLabel(applicationGroup, "Context Path:", 128, 1, 1);
        contextText = createText(applicationGroup, SWT.BORDER, -1, -1, 1, 1, modifyDialogListener);
        createLabel(applicationGroup, "", 0, 1, 1);

        createLabel(applicationGroup, "HTTP Port:", 128, 1, 1);
        portText = createText(applicationGroup, SWT.BORDER, 64, -1, 1, 1, modifyDialogListener);
        createLabel(applicationGroup, "", 0, 1, 1);

        final Group configGroup = new Group(tabComposite, SWT.NONE);
        configGroup.setLayout(new GridLayout(4, false));
        configGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        configGroup.setText("Jetty Context Configuration:");

        configTable =
            createTable(configGroup, SWT.BORDER | SWT.FULL_SELECTION, -1, 85, 3, 4, "Include", "Jetty Context File", "Scope");
        configTable.addSelectionListener(new SelectionAdapter()
        {

            @Override
            public void widgetSelected(SelectionEvent e)
            {
                updateButtonState();
            }

        });
        editConfigButton = createButton(configGroup, SWT.NONE, "Edit...", 128, 1, 1, new SelectionAdapter()
        {
            @Override
            public void widgetSelected(final SelectionEvent e)
            {
                int index = configTable.getSelectionIndex();

                if (index > 0)
                {
                    String path = null;
                    JettyLaunchConfigEntry entry = configEntryList.get(index);

                    switch (entry.getType())
                    {
                        case PATH:
                            path = chooseConfigFromFileSystem(entry.getPath());
                            break;

                        case WORKSPACE:
                            path = chooseConfig(entry.getPath());
                            break;

                        default:
                            break;
                    }

                    if (path != null)
                    {
                        entry.setPath(path);
                        updateLaunchConfigurationDialog();
                    }
                }
            }
        });
        moveUpConfigButton = createButton(configGroup, SWT.NONE, "Up", 128, 1, 1, new SelectionAdapter()
        {
            @Override
            public void widgetSelected(final SelectionEvent e)
            {
                int index = configTable.getSelectionIndex();

                if (index > 0)
                {
                    configEntryList.exchange(configTable, index - 1);
                    configTable.setSelection(index - 1);
                    updateLaunchConfigurationDialog();
                }
            }
        });
        moveDownConfigButton = createButton(configGroup, SWT.NONE, "Down", 128, 1, 2, new SelectionAdapter()
        {
            @Override
            public void widgetSelected(final SelectionEvent e)
            {
                int index = configTable.getSelectionIndex();

                if ((index >= 0) && (index < (configTable.getItemCount() - 1)))
                {
                    configEntryList.exchange(configTable, index);
                    configTable.setSelection(index + 1);
                    updateLaunchConfigurationDialog();
                }
            }
        });
        createLabel(configGroup, "", -1, 1, 1);
        createButton(configGroup, SWT.NONE, "Add...", 128, 1, 1, new SelectionAdapter()
        {
            @Override
            public void widgetSelected(final SelectionEvent e)
            {
                String path = chooseConfig(null);

                if (path != null)
                {
                    JettyConfigScope scope =
                        JettyConfig.determineScope(JettyConfig.getFile(ResourcesPlugin.getWorkspace(),
                            JettyConfigType.WORKSPACE, path));

                    configEntryList.add(configTable, new JettyLaunchConfigEntry(new JettyConfig(path,
                        JettyConfigType.WORKSPACE, scope, true)));
                    updateLaunchConfigurationDialog();
                }
            }
        });
        createButton(configGroup, SWT.NONE, "Add External...", 128, 1, 1, new SelectionAdapter()
        {
            @Override
            public void widgetSelected(final SelectionEvent e)
            {
                String path = chooseConfigFromFileSystem(null);

                if (path != null)
                {
                    JettyConfigScope scope =
                        JettyConfig.determineScope(JettyConfig.getFile(ResourcesPlugin.getWorkspace(),
                            JettyConfigType.PATH, path));

                    configEntryList.add(configTable, new JettyLaunchConfigEntry(new JettyConfig(path,
                        JettyConfigType.PATH, scope, true)));
                    updateLaunchConfigurationDialog();
                }
            }
        });
        removeConfigButton = createButton(configGroup, SWT.NONE, "Remove", 128, 1, 1, new SelectionAdapter()
        {
            @Override
            public void widgetSelected(final SelectionEvent e)
            {
                int index = configTable.getSelectionIndex();

                if (index >= 0)
                {
                    JettyLaunchConfigEntry entry = configEntryList.get(index);

                    if ((entry.getType() == JettyConfigType.PATH) || (entry.getType() == JettyConfigType.WORKSPACE))
                    {
                        configEntryList.remove(configTable, index);
                        updateLaunchConfigurationDialog();
                    }
                }
            }
        });

        setControl(tabComposite);
    }

    public String getName()
    {
        return "WebApp";
    }

    @Override
    public Image getImage()
    {
        return JettyPlugin.getJettyIcon();
    }

    @Override
    public String getMessage()
    {
        return "Create a configuration to launch a web application with Jetty.";
    }

    @Override
    public void initializeFrom(final ILaunchConfiguration configuration)
    {
        try
        {
            projectText.setText(JettyPluginConstants.getProject(configuration));
            webAppText.setText(JettyPluginConstants.getWebAppDir(configuration));
            contextText.setText(JettyPluginConstants.getContext(configuration));
            portText.setText(JettyPluginConstants.getPort(configuration));

            updateTable(configuration, true);
            updateButtonState();
        }
        catch (final CoreException e)
        {
            JettyPlugin.logError(e);
        }
    }

    public void setDefaults(final ILaunchConfigurationWorkingCopy configuration)
    {
        final IJavaElement javaElement = getContext();
        if (javaElement != null)
        {
            initializeJavaProject(javaElement, configuration);
        }
        else
        {
            configuration.setAttribute(ATTR_PROJECT_NAME, "");
        }

        JettyPluginConstants.setClasspathProvider(configuration, JettyPluginConstants.CLASSPATH_PROVIDER_JETTY);

        // get the name for this launch configuration
        String launchConfigName = "";
        try
        {
            // try to base the launch config name on the current project
            launchConfigName = configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "");
        }
        catch (final CoreException e)
        {
            // ignore
        }
        if ((launchConfigName == null) || (launchConfigName.length() == 0))
        {
            // if no project name was found, base on a default name
            launchConfigName = "Jetty Webapp";
        }

        // generate an unique name (e.g. myproject(2))
        launchConfigName = getLaunchConfigurationDialog().generateName(launchConfigName);
        configuration.rename(launchConfigName); // and rename the config

        try
        {
            JettyPluginConstants.setWebAppDir(configuration, JettyPluginConstants.getWebAppDir(configuration));
            JettyPluginConstants.setContext(configuration, JettyPluginConstants.getContext(configuration));
            JettyPluginConstants.setPort(configuration, JettyPluginConstants.getPort(configuration));
            JettyPluginConstants.setConfigs(configuration, JettyPluginConstants.getConfigs(configuration));
        }
        catch (CoreException e)
        {
            JettyPlugin.logError(e);
        }
    }

    public void performApply(final ILaunchConfigurationWorkingCopy configuration)
    {
        try
        {
            JettyPluginConstants.setProject(configuration, projectText.getText().trim());
            JettyPluginConstants.setContext(configuration, contextText.getText().trim());
            JettyPluginConstants.setWebAppDir(configuration, webAppText.getText().trim());
            JettyPluginConstants.setPort(configuration, portText.getText().trim());
            JettyPluginConstants.setConfigs(configuration, configEntryList.getConfigs());
        }
        catch (CoreException e)
        {
            JettyPlugin.logError(e);
        }

        JettyPluginConstants.setClasspathProvider(configuration, JettyPluginConstants.CLASSPATH_PROVIDER_JETTY);

        updateTable(configuration, false);
        updateButtonState();
    }

    @Override
    public boolean isValid(final ILaunchConfiguration config)
    {
        setErrorMessage(null);
        setMessage(null);

        String projectName = projectText.getText().trim();
        IProject project = null;
        if (projectName.length() > 0)
        {
            IWorkspace workspace = ResourcesPlugin.getWorkspace();
            IStatus status = workspace.validateName(projectName, IResource.PROJECT);
            if (status.isOK())
            {
                project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
                if (!project.exists())
                {
                    setErrorMessage(MessageFormat.format("Project {0} does not exist", projectName));
                    webAppButton.setEnabled(false);
                    return false;
                }
                if (!project.isOpen())
                {
                    setErrorMessage(MessageFormat.format("Project {0} is closed", projectName));
                    webAppButton.setEnabled(false);
                    return false;
                }
            }
            else
            {
                setErrorMessage(MessageFormat.format("Illegal project name: {0}", status.getMessage()));
                webAppButton.setEnabled(false);
                return false;
            }
            webAppButton.setEnabled(true);
        }
        else
        {
            setErrorMessage("No project selected");
            return false;
        }

        String directory = webAppText.getText().trim();
        if (directory.length() > 0)
        {
            IFolder folder = project.getFolder(directory);
            if (!folder.exists())
            {
                setErrorMessage(MessageFormat.format("Folder {0} does not exist in project {1}", directory,
                    project.getName()));
                return false;
            }
            IFile file = project.getFile(new Path(directory + "/WEB-INF/web.xml"));
            if (!file.exists())
            {
                setErrorMessage(MessageFormat.format(
                    "Directoy {0} does not contain WEB-INF/web.xml; it is not a valid web application directory",
                    directory));
                return false;
            }
        }
        else
        {
            setErrorMessage("Web application directory is not set");
            return false;
        }

        String jettyPort = portText.getText().trim();
        if (jettyPort.length() > 0)
        {
            try
            {
                int port = Integer.parseInt(jettyPort);

                if ((port <= 0) || (port >= 65536))
                {
                    setErrorMessage(MessageFormat.format("The port {0} must be a number between 0 and 65536.",
                        jettyPort));
                }
            }
            catch (NumberFormatException e)
            {
                setErrorMessage(MessageFormat.format("The port {0} must be a number between 0 and 65536.", jettyPort));
            }
        }
        else
        {
            setErrorMessage("Jetty port is not set");
            return false;
        }

        List<JettyConfig> contexts = configEntryList.getConfigs();

        for (JettyConfig context : contexts)
        {
            if (!context.isValid(ResourcesPlugin.getWorkspace()))
            {
                setErrorMessage(MessageFormat.format("The Jetty context file {0} does not exist.", context.getPath()));
            }
        }

        setDirty(true);

        return true;
    }

    private void updateTable(final ILaunchConfiguration configuration, boolean updateType)
    {
        try
        {
            List<JettyConfig> contexts = JettyPluginConstants.getConfigs(configuration);

            if (configEntryList.update(configuration, configTable, contexts))
            {
                if (!configTableFormatted)
                {
                    for (int i = 0; i < configTable.getColumnCount(); i += 1)
                    {
                        configTable.getColumn(i).pack();
                    }
                }

                if (configTable.getItemCount() > 0)
                {
                    configTableFormatted = true;
                }
            }
        }
        catch (CoreException e)
        {
            JettyPlugin.logError(e);
        }
    }

    protected void chooseJavaProject()
    {
        final ILabelProvider labelProvider = new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_DEFAULT);
        final ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), labelProvider);
        dialog.setTitle("Project Selection");
        dialog.setMessage("Select a project to constrain your search.");
        try
        {
            dialog.setElements(JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()).getJavaProjects());
        }
        catch (final JavaModelException jme)
        {
            JettyPlugin.logError(jme);
        }

        IJavaProject javaProject = null;
        String projectName = projectText.getText().trim();
        if (projectName.length() > 0)
        {
            final IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
            javaProject = JavaCore.create(workspaceRoot).getJavaProject(projectName);
        }
        if (javaProject != null)
        {
            dialog.setInitialSelections(new Object[]{javaProject});
        }
        if (dialog.open() == Window.OK)
        {
            final IJavaProject selectedProject = (IJavaProject) dialog.getFirstResult();
            projectName = selectedProject.getElementName();
            projectText.setText(projectName);
        }
    }

    protected void chooseWebappDir()
    {
        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectText.getText());
        ContainerSelectionDialog dialog =
            new ContainerSelectionDialog(getShell(), project, false,
                "Select a directory to act as Web Application Root:");

        dialog.setTitle("Folder Selection");

        if (project != null)
        {
            IPath path = project.getFullPath();
            dialog.setInitialSelections(new Object[]{path});
        }

        dialog.showClosedProjects(false);
        dialog.open();

        Object[] results = dialog.getResult();

        if ((results != null) && (results.length > 0) && (results[0] instanceof IPath))
        {
            IPath path = (IPath) results[0];

            String projectSegment = path.segment(0);

            if (!projectSegment.equalsIgnoreCase(projectText.getText()))
            {
                projectText.setText(projectSegment);
            }

            path = path.removeFirstSegments(1);

            String containerName = path.makeRelative().toString();
            webAppText.setText(containerName);
        }
    }

    protected String chooseConfig(String path)
    {
        ElementTreeSelectionDialog dialog =
            new ElementTreeSelectionDialog(getShell(), new WorkbenchLabelProvider(), new BaseWorkbenchContentProvider());

        dialog.setTitle("Resource Selection");
        dialog.setMessage("Select a resource as Jetty Context file:");
        dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());

        if (path != null)
        {
            dialog.setInitialSelection(path);
        }

        dialog.open();

        Object[] results = dialog.getResult();

        if ((results != null) && (results.length > 0) && (results[0] instanceof IFile))
        {
            IFile file = (IFile) results[0];
            return file.getFullPath().toString();
        }

        return null;
    }

    protected String chooseConfigFromFileSystem(String path)
    {
        FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);

        dialog.setText("Select Jetty Context File");

        if (path != null)
        {
            File file = new File(path);

            dialog.setFileName(file.getName());
            dialog.setFilterPath(file.getParent());
        }

        dialog.setFilterExtensions(new String[]{"*.xml", "*.*"});

        return dialog.open();
    }

    public void updateButtonState()
    {
        int index = configTable.getSelectionIndex();
        JettyLaunchConfigEntry entry = (index >= 0) ? configEntryList.get(index) : null;
        JettyConfigType type = (entry != null) ? entry.getType() : null;

        editConfigButton.setEnabled((type == JettyConfigType.PATH) || (type == JettyConfigType.WORKSPACE));
        moveUpConfigButton.setEnabled(index > 0);
        moveDownConfigButton.setEnabled((index >= 0) && (index < (configTable.getItemCount() - 1)));
        removeConfigButton.setEnabled((type == JettyConfigType.PATH) || (type == JettyConfigType.WORKSPACE));
    }

    public final class ModifyDialogListener implements ModifyListener, SelectionListener
    {
        @SuppressWarnings("synthetic-access")
        public void modifyText(final ModifyEvent e)
        {
            updateLaunchConfigurationDialog();
        }

        public void widgetDefaultSelected(final SelectionEvent arg0)
        {
            // intentionally left blank
        }

        @SuppressWarnings("synthetic-access")
        public void widgetSelected(final SelectionEvent arg0)
        {
            updateLaunchConfigurationDialog();
        }
    }
}
