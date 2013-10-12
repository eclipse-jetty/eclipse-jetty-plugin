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

import static net.sourceforge.eclipsejetty.launch.util.JettyLaunchUI.*;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.*;

import java.text.MessageFormat;

import net.sourceforge.eclipsejetty.JettyPlugin;
import net.sourceforge.eclipsejetty.JettyPluginUtils;
import net.sourceforge.eclipsejetty.launch.util.JettyLaunchConfigurationAdapter;
import net.sourceforge.eclipsejetty.launch.util.JettyLaunchUtils;

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

        Composite applicationGroup = createTopComposite(tabComposite, SWT.NONE, 4, -1, false, 2, 1);

        createLabel(applicationGroup, "WebApp Folder:", 128, 1, 1);
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

        Composite serverGroup = createTopComposite(tabComposite, SWT.NONE, 7, -1, false, 2, 1);

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
            JettyLaunchConfigurationAdapter adapter = JettyLaunchConfigurationAdapter.getInstance(configuration);

            projectText.setText(adapter.getProjectName());
            webAppText.setText(adapter.getWebAppString());
            contextText.setText(adapter.getContext());
            portSpinner.setSelection(adapter.getPort());
            httpsPortSpinner.setSelection(adapter.getHttpsPort());
            httpsEnabledButton.setSelection(adapter.isHttpsEnabled());
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

        String projectName = "";

        try
        {
            projectName = configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "");
        }
        catch (CoreException e)
        {
            // ignore
        }

        JettyLaunchConfigurationAdapter adapter = JettyLaunchConfigurationAdapter.getInstance(configuration);

        try
        {
            adapter.initialize(JettyPluginUtils.getProject(projectName), null);
        }
        catch (CoreException e)
        {
            JettyPlugin.error("Failed to initialize project", e);
        }

        //        // get the name for this launch configuration
        //        String projectName = "";
        //
        //        try
        //        {
        //            projectName = configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "");
        //        }
        //        catch (CoreException e)
        //        {
        //            // ignore
        //        }
        //
        //        String launchConfigName = projectName;
        //
        //        if ((launchConfigName == null) || (launchConfigName.length() == 0))
        //        {
        //            // if no project name was found, base on a default name
        //            launchConfigName = "Jetty Webapp";
        //        }

        // generate an unique name (e.g. myproject(2))
        //        launchConfigName = getLaunchConfigurationDialog().generateName(launchConfigName);
        //        configuration.rename(launchConfigName); // and rename the config

        //        String webAppDir = "src/main/webapp";
        //
        //        if ((projectName != null) && (projectName.length() > 0))
        //        {
        //            IProject project = JettyPluginUtils.getProject(projectName);
        //
        //            if (project != null)
        //            {
        //                IPath path = JettyLaunchUtils.findWebappDir(project);
        //
        //                if (path != null)
        //                {
        //                    webAppDir = JettyPluginUtils.toRelativePath(project, path.toString());
        //                }
        //            }
        //        }
        //
        //        JettyPluginConstants.setWebAppString(configuration, webAppDir);
    }

    public void performApply(ILaunchConfigurationWorkingCopy configuration)
    {
        JettyLaunchConfigurationAdapter adapter = JettyLaunchConfigurationAdapter.getInstance(configuration);

        try
        {
            adapter.setProjectName(projectText.getText().trim());
            adapter.setContext(contextText.getText().trim());
            adapter.setWebAppString(webAppText.getText().trim());
            adapter.setPort(portSpinner.getSelection());
            adapter.setHttpsPort(httpsPortSpinner.getSelection());
            adapter.setHttpsEnabled(httpsEnabledButton.getSelection());
            adapter.setClasspathProvider(JettyLaunchConfigurationAdapter.CLASSPATH_PROVIDER_JETTY);
        }
        catch (CoreException e)
        {
            JettyPlugin.error("Failed to update configuration", e);
        }
    }

    @Override
    public boolean isValid(ILaunchConfiguration config)
    {
        setErrorMessage(null);
        setMessage(null);

        httpsPortSpinner.setEnabled(httpsEnabledButton.getSelection());

        String projectName = projectText.getText().trim();
        IProject project = JettyPluginUtils.getProject(projectName);

        webAppScanButton.setEnabled(project != null);
        webAppBrowseButton.setEnabled(project != null);

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
        IProject project = JettyPluginUtils.getProject(projectText.getText());
        IPath path = JettyLaunchUtils.findWebappDir(project);

        if (path == null)
        {
            Display.getCurrent().syncExec(new Runnable()
            {
                public void run()
                {
                    MessageDialog.openError(Display.getCurrent().getActiveShell(), "WebApp Directory not found",
                        "Could not to find the file \"WEB-INF/web.xml\" in project " + projectText.getText()
                            + ".\n\nPlease locate the WebApp Directory manually.");
                }
            });

            chooseWebappDir();

            return;
        }

        String containerName = JettyPluginUtils.toRelativePath(project, path.toString());

        webAppText.setText(containerName);
    }

    protected void chooseWebappDir()
    {
        String path =
            chooseWorkspaceDirectory(
                getShell(),
                JettyPluginUtils.getProject(projectText.getText()),
                "WebApp Folder",
                "Select your web application root folder. That't the one,\nthat contains the WEB-INF directory with the web.xml.",
                webAppText.getText());

        if (path != null)
        {
            webAppText.setText(path);
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
