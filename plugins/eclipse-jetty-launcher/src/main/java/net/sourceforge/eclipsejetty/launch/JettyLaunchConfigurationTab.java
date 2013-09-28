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

import java.text.MessageFormat;

import net.sourceforge.eclipsejetty.JettyPlugin;
import net.sourceforge.eclipsejetty.JettyPluginConstants;
import net.sourceforge.eclipsejetty.JettyPluginUtils;

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
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

/**
 * UI
 * 
 * @author Christian K&ouml;berl
 * @author Manfred Hantschel
 */
public class JettyLaunchConfigurationTab extends AbstractJettyLaunchConfigurationTab
{
    private final ModifyDialogListener modifyDialogListener;

    private Text projectText;
    private Text webAppText;
    private Button webAppBrowseButton;
    private Button webAppScanButton;
    private Text contextText;
    private Spinner portSpinner;
    private Spinner httpsPortSpinner;
    private Button httpsEnabledButton;

    public JettyLaunchConfigurationTab()
    {
        modifyDialogListener = new ModifyDialogListener();
    }

    /**
     * @wbp.parser.entryPoint
     */
    public void createControl(Composite parent)
    {
        Composite tabComposite = new Composite(parent, SWT.NONE);
        tabComposite.setLayout(new GridLayout(2, false));

        Label label = new Label(tabComposite, SWT.NONE);
        label.setImage(JettyPlugin.getJettyPluginLogo());

        Group projectGroup = new Group(tabComposite, SWT.NONE);
        projectGroup.setLayout(new GridLayout(2, false));
        projectGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        projectGroup.setText("Project:");

        projectText = createText(projectGroup, SWT.BORDER, -1, -1, 1, 1, modifyDialogListener);
        createButton(projectGroup, SWT.NONE, "Browse...", 128, 1, 1, new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                chooseJavaProject();
            }
        });

        Composite applicationGroup = createComposite(tabComposite, SWT.NONE, 4, -1, false, 2, 1);

        createLabel(applicationGroup, "WebApp Directory:", 128, 1, 1);
        webAppText = createText(applicationGroup, SWT.BORDER, -1, -1, 1, 1, modifyDialogListener);
        webAppScanButton = createButton(applicationGroup, SWT.NONE, "Scan...", 128, 1, 1, new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                scanWebappDir();
            }
        });
        webAppBrowseButton = createButton(applicationGroup, SWT.NONE, "Browse...", 128, 1, 1, new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                chooseWebappDir();
            }
        });

        Composite serverGroup = createComposite(tabComposite, SWT.NONE, 7, -1, false, 2, 1);

        createLabel(serverGroup, "Context Path:", 128, 1, 1);
        contextText = createText(serverGroup, SWT.BORDER, -1, -1, 6, 1, modifyDialogListener);

        createLabel(serverGroup, "HTTP / HTTPs Port:", 128, 1, 1);
        portSpinner = createSpinner(serverGroup, SWT.BORDER, 32, -1, 1, 1, modifyDialogListener);
        portSpinner.setMinimum(0);
        portSpinner.setMaximum(65535);
        portSpinner.setIncrement(1);
        portSpinner.setPageIncrement(1000);
        createLabel(serverGroup, "/", 16, 1, 1).setAlignment(SWT.CENTER);
        httpsPortSpinner = createSpinner(serverGroup, SWT.BORDER, 32, -1, 1, 1, modifyDialogListener);
        httpsPortSpinner.setMinimum(0);
        httpsPortSpinner.setMaximum(65535);
        httpsPortSpinner.setIncrement(1);
        httpsPortSpinner.setPageIncrement(1000);
        httpsEnabledButton = createButton(serverGroup, SWT.CHECK, "Enable HTTPs", -1, 3, 1, modifyDialogListener);

        Composite helpGroup = new Composite(tabComposite, SWT.NONE);
        helpGroup.setLayout(new GridLayout(1, false));
        helpGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        Link link = new Link(helpGroup, SWT.NONE);
        link.setText("Visit the <a>Eclipse Jetty Plugin homepage</a> at SourceForge.");
        link.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 2, 1));
        link.addListener(SWT.Selection, new Listener()
        {
            public void handleEvent(Event event)
            {
                Program.launch("http://eclipse-jetty.sourceforge.net/");
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
    public void initializeFrom(ILaunchConfiguration configuration)
    {
        super.initializeFrom(configuration);

        try
        {
            projectText.setText(JettyPluginConstants.getProject(configuration));
            webAppText.setText(JettyPluginConstants.getWebAppDir(configuration));
            contextText.setText(JettyPluginConstants.getContext(configuration));
            portSpinner.setSelection(JettyPluginConstants.getPort(configuration));
            httpsPortSpinner.setSelection(JettyPluginConstants.getHttpsPort(configuration));
            httpsEnabledButton.setSelection(JettyPluginConstants.isHttpsEnabled(configuration));
        }
        catch (CoreException e)
        {
            JettyPlugin.error("Failed to initialize form in configuration tab", e);
        }
    }

    public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
    {
        IJavaElement javaElement = getContext();
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
        String projectName = "";

        try
        {
            projectName = configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "");
        }
        catch (CoreException e)
        {
            // ignore
        }

        String launchConfigName = projectName;

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
            String webAppDir = "src/main/webapp";
            
            if ((projectName != null) && (projectName.length() > 0)) {
                IResource resource = findWebappDir(ResourcesPlugin.getWorkspace().getRoot().getProject(projectName));
    
                if (resource != null)
                {
                    webAppDir = getWebappText(resource);
                }
            }

            JettyPluginConstants.setWebAppDir(configuration, webAppDir);
            JettyPluginConstants.setContext(configuration, JettyPluginConstants.getContext(configuration));
            JettyPluginConstants.setPort(configuration, JettyPluginConstants.getPort(configuration));
            JettyPluginConstants.setConfigs(configuration, JettyPluginConstants.getConfigs(configuration));
        }
        catch (CoreException e)
        {
            JettyPlugin.error("Failed to set defaults in configuration tab", e);
        }
    }

    public void performApply(ILaunchConfigurationWorkingCopy configuration)
    {
        JettyPluginConstants.setProject(configuration, projectText.getText().trim());
        JettyPluginConstants.setContext(configuration, contextText.getText().trim());
        JettyPluginConstants.setWebAppDir(configuration, webAppText.getText().trim());
        JettyPluginConstants.setPort(configuration, portSpinner.getSelection());
        JettyPluginConstants.setHttpsPort(configuration, httpsPortSpinner.getSelection());
        JettyPluginConstants.setHttpsEnabled(configuration, httpsEnabledButton.getSelection());

        JettyPluginConstants.setClasspathProvider(configuration, JettyPluginConstants.CLASSPATH_PROVIDER_JETTY);
    }

    @Override
    public boolean isValid(ILaunchConfiguration config)
    {
        setErrorMessage(null);
        setMessage(null);

        httpsPortSpinner.setEnabled(httpsEnabledButton.getSelection());

        String projectName = projectText.getText().trim();
        IProject project = null;
        if (projectName.length() > 0)
        {
            IWorkspace workspace = ResourcesPlugin.getWorkspace();
            IStatus status = workspace.validateName(projectName, IResource.PROJECT);
            if (status.isOK())
            {
                project = workspace.getRoot().getProject(projectName);
                if (!project.exists())
                {
                    setErrorMessage(MessageFormat.format("Project {0} does not exist", projectName));
                    webAppBrowseButton.setEnabled(false);
                    webAppScanButton.setEnabled(false);
                    return false;
                }
                if (!project.isOpen())
                {
                    setErrorMessage(MessageFormat.format("Project {0} is closed", projectName));
                    webAppBrowseButton.setEnabled(false);
                    webAppScanButton.setEnabled(false);
                    return false;
                }
            }
            else
            {
                setErrorMessage(MessageFormat.format("Illegal project name: {0}", status.getMessage()));
                webAppBrowseButton.setEnabled(false);
                webAppScanButton.setEnabled(false);
                return false;
            }
            webAppBrowseButton.setEnabled(true);
            webAppScanButton.setEnabled(true);
        }
        else
        {
            setErrorMessage("No project selected");
            webAppBrowseButton.setEnabled(false);
            webAppScanButton.setEnabled(false);
            return false;
        }

        IFile file;
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
            file = project.getFile(new Path(directory + "/WEB-INF/web.xml"));
        }
        else
        {
            file = project.getFile(new Path("/WEB-INF/web.xml"));
        }

        if (!file.exists())
        {
            setErrorMessage(MessageFormat
                .format("Directoy {0} does not contain WEB-INF/web.xml; it is not a valid web application directory",
                    directory));
            return false;
        }

        setDirty(true);

        return true;
    }

    protected void chooseJavaProject()
    {
        ILabelProvider labelProvider = new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_DEFAULT);
        ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), labelProvider);
        dialog.setTitle("Project Selection");
        dialog.setMessage("Select a project to constrain your search.");
        try
        {
            dialog.setElements(JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()).getJavaProjects());
        }
        catch (JavaModelException e)
        {
            JettyPlugin.error("Failed to detect Java projects", e);
        }

        IJavaProject javaProject = null;
        String projectName = projectText.getText().trim();
        if (projectName.length() > 0)
        {
            IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
            javaProject = JavaCore.create(workspaceRoot).getJavaProject(projectName);
        }
        if (javaProject != null)
        {
            dialog.setInitialSelections(new Object[]{javaProject});
        }
        if (dialog.open() == Window.OK)
        {
            IJavaProject selectedProject = (IJavaProject) dialog.getFirstResult();
            projectName = selectedProject.getElementName();
            projectText.setText(projectName);
        }
    }

    protected void scanWebappDir()
    {
        IResource resource = findWebappDir(ResourcesPlugin.getWorkspace().getRoot().getProject(projectText.getText()));

        if (resource == null)
        {
            Display.getCurrent().syncExec(new Runnable()
            {
                public void run()
                {
                    MessageDialog.openError(Display.getCurrent().getActiveShell(), "WebApp Directory not found",
                        "Could not to find the folder \"WEB-INF\" in project " + projectText.getText()
                            + ".\n\nPlease locate the WebApp Directory manually.");
                }
            });

            return;
        }

        String containerName = getWebappText(resource);

        webAppText.setText(containerName);
    }

    protected String getWebappText(IResource resource)
    {
        IPath path = resource.getFullPath().removeLastSegments(2);

        path = path.removeFirstSegments(1);

        String containerName = path.makeRelative().toString();

        return containerName;
    }

    protected IResource findWebappDir(IProject project)
    {
        IResource resource = null;

        try
        {
            resource = JettyPluginUtils.findResource(project, "WEB-INF", "web.xml");
        }
        catch (CoreException e)
        {
            JettyPlugin.warning("Failed to scan project", e);
        }

        return resource;
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

    public class ModifyDialogListener implements ModifyListener, SelectionListener
    {
        @SuppressWarnings("synthetic-access")
        public void modifyText(ModifyEvent e)
        {
            updateLaunchConfigurationDialog();
        }

        public void widgetDefaultSelected(SelectionEvent arg0)
        {
            // intentionally left blank
        }

        @SuppressWarnings("synthetic-access")
        public void widgetSelected(SelectionEvent arg0)
        {
            updateLaunchConfigurationDialog();
        }
    }
}
