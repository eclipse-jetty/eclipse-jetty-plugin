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

import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.*;

import java.io.File;
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
import org.eclipse.core.variables.IStringVariable;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.StringVariableSelectionDialog;
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
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
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
    private Button webAppButton;
    private Text contextText;
    private Text pathText;
    private Text portText;

    public JettyLaunchConfigurationTab()
    {
        modifyDialogListener = new ModifyDialogListener();
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

        projectText = createText(projectGroup, -1, 1, modifyDialogListener);
        createButton(projectGroup, SWT.NONE, "Browse...", 96, new SelectionAdapter()
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

        createLabel(applicationGroup, "WebApp Directory:", 128, 1);
        webAppText = createText(applicationGroup, -1, 1, modifyDialogListener);
        webAppButton = createButton(applicationGroup, SWT.NONE, "Browse...", 96, new SelectionAdapter()
        {
            @Override
            public void widgetSelected(final SelectionEvent e)
            {
                chooseWebappDir();
            }
        });

        createLabel(applicationGroup, "Context Path:", 128, 1);
        contextText = createText(applicationGroup, -1, 1, modifyDialogListener);
        createLabel(applicationGroup, "", 0, 1);

        final Group jettyGroup = new Group(tabComposite, SWT.NONE);
        jettyGroup.setLayout(new GridLayout(4, false));
        jettyGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        jettyGroup.setText("Jetty:");

        createLabel(jettyGroup, "Jetty Home:", 128, 1);
        pathText = createText(jettyGroup, -1, 3, modifyDialogListener);

        createLabel(jettyGroup, "", -1, 2);
        createButton(jettyGroup, SWT.NONE, "Variables...", 96, new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                chooseJettyPathVariable();
            }
        });
        createButton(jettyGroup, SWT.NONE, "Browse...", 96, new SelectionAdapter()
        {
            @Override
            public void widgetSelected(final SelectionEvent e)
            {
                chooseJettyPath();
            }
        });

        createLabel(jettyGroup, "HTTP Port:", 128, 1);
        portText = createText(jettyGroup, 64, 3, modifyDialogListener);

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
            pathText.setText(JettyPluginConstants.getPath(configuration));
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
        }
        catch (CoreException e)
        {
            JettyPlugin.logError(e);
        }

        try
        {
            JettyPluginConstants.setContext(configuration, JettyPluginConstants.getContext(configuration));
        }
        catch (CoreException e)
        {
            JettyPlugin.logError(e);
        }

        try
        {
            JettyPluginConstants.setPath(configuration, JettyPluginConstants.getPath(configuration));
        }
        catch (CoreException e)
        {
            JettyPlugin.logError(e);
        }
        
        try
        {
            JettyPluginConstants.setPort(configuration, JettyPluginConstants.getPort(configuration));
        }
        catch (CoreException e)
        {
            JettyPlugin.logError(e);
        }
    }

    public void performApply(final ILaunchConfigurationWorkingCopy configuration)
    {
        JettyPluginConstants.setProject(configuration, projectText.getText().trim());
        JettyPluginConstants.setContext(configuration, contextText.getText().trim());
        JettyPluginConstants.setWebAppDir(configuration, webAppText.getText().trim());
        JettyPluginConstants.setPort(configuration, portText.getText().trim());

        String jettyPath = pathText.getText().trim();

        JettyPluginConstants.setPath(configuration, jettyPath);
        JettyPluginConstants.setClasspathProvider(configuration, JettyPluginConstants.CLASSPATH_PROVIDER_JETTY);
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

        String jettyPath = JettyPluginUtils.resolveVariables(pathText.getText()).trim();
        if (jettyPath.length() > 0)
        {
            File f = new File(jettyPath);
            if (!f.exists() || !f.isDirectory())
            {
                setErrorMessage(MessageFormat.format("The path {0} is not a valid directory.", jettyPath));
                return false;
            }
        }
        else
        {
            setErrorMessage("Jetty path is not set");
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

        setDirty(true);

        return true;
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
            new ContainerSelectionDialog(getShell(), project, false, "Select Web Application Directory");

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

    protected void chooseJettyPathVariable()
    {
        StringVariableSelectionDialog dialog = new StringVariableSelectionDialog(getShell());

        if (Window.OK == dialog.open())
        {
            Object[] results = dialog.getResult();

            for (int i = results.length - 1; i >= 0; i -= 1)
            {
                String placeholder = "${" + ((IStringVariable) results[i]).getName() + "}";
                int position = pathText.getCaretPosition();
                String text = pathText.getText();

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

                pathText.setText(text);
            }
        }
    }

    protected void chooseJettyPath()
    {
        String jettyPath = JettyPluginUtils.resolveVariables(pathText.getText());
        DirectoryDialog dialog = new DirectoryDialog(getShell(), SWT.OPEN);

        dialog.setText("Select Jetty Home Directory");
        dialog
            .setMessage("Choose the installation directory of your Jetty. Currenty, the versions 5 to 8 are supported.");
        dialog.setFilterPath(jettyPath);

        jettyPath = dialog.open();

        if (jettyPath != null)
        {
            pathText.setText(jettyPath);
        }
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
