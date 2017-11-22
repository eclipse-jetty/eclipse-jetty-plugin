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
import net.sourceforge.eclipsejetty.JettyPlugin;
import net.sourceforge.eclipsejetty.JettyPluginUtils;
import net.sourceforge.eclipsejetty.Messages;
import net.sourceforge.eclipsejetty.launch.util.JettyLaunchConfigurationAdapter;
import net.sourceforge.eclipsejetty.launch.util.JettyLaunchUI;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
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
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
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
    private Link httpLink;
    private Link httpsLink;

    public JettyLaunchConfigurationTab()
    {
        modifyDialogListener = new ModifyDialogListener();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(org.eclipse.swt.widgets.Composite)
     * @wbp.parser.entryPoint
     */
    public void createControl(Composite parent)
    {
        Composite tabComposite = createTabComposite(parent, 2, false);

        createProjectGroup(tabComposite);
        createImage(tabComposite, JettyPlugin.getJettyPluginLogo(), 96, SWT.CENTER, SWT.TOP, 1, 3);
        createApplicationGroup(tabComposite);
        createServerGroup(tabComposite);
        createLinkGroup(tabComposite);
        createHelpGroup(tabComposite);

        setControl(tabComposite);
    }

    private void createProjectGroup(Composite tabComposite)
    {
        Composite projectGroup = createTopComposite(tabComposite, SWT.NONE, 2, -1, false, 1, 1);

        createLabel(projectGroup, Messages.configTab_projectGroupTitle, 128, SWT.RIGHT, 1, 1);
        projectText =
            createText(projectGroup, SWT.BORDER, Messages.configTab_projectTextTip, -1, -1, 1, 1, modifyDialogListener);

        Composite buttonComposite = createComposite(projectGroup, SWT.NONE, 2, -1, false, 2, 1);

        createLabel(buttonComposite, JettyPluginUtils.EMPTY, -1, SWT.LEFT, 1, 1);
        createButton(buttonComposite, SWT.NONE, Messages.configTab_projectBrowseButton,
            Messages.configTab_projectBrowseButtonTip, 128, 1, 1, new SelectionAdapter()
            {
                @Override
                public void widgetSelected(SelectionEvent e)
                {
                    chooseJavaProject();
                }
            });
    }

    private void createApplicationGroup(Composite tabComposite)
    {
        Composite applicationGroup = createTopComposite(tabComposite, SWT.NONE, 2, -1, false, 1, 1);

        createLabel(applicationGroup, Messages.configTab_webAppLabel, 128, SWT.RIGHT, 1, 1);
        webAppText =
            createText(applicationGroup, SWT.BORDER, Messages.configTab_webAppTextTip, -1, -1, 1, 1,
                modifyDialogListener);

        Composite buttonComposite = createComposite(applicationGroup, SWT.NONE, 3, -1, false, 2, 1);

        createLabel(buttonComposite, JettyPluginUtils.EMPTY, -1, SWT.RIGHT, 1, 1);
        webAppScanButton =
            createButton(buttonComposite, SWT.NONE, Messages.configTab_webAppScanButton,
                Messages.configTab_webAppScanButtonTip, 128, 1, 1, new SelectionAdapter()
                {
                    @Override
                    public void widgetSelected(SelectionEvent e)
                    {
                        scanWebappDir();
                    }
                });
        webAppBrowseButton =
            createButton(buttonComposite, SWT.NONE, Messages.configTab_webAppBrowseButton,
                Messages.configTab_webAppBrowseButtonTip, 128, 1, 1, new SelectionAdapter()
                {
                    @Override
                    public void widgetSelected(SelectionEvent e)
                    {
                        chooseWebappDir();
                    }
                });
    }

    private void createServerGroup(Composite tabComposite)
    {
        Composite serverGroup = createTopComposite(tabComposite, SWT.NONE, 7, -1, false, 1, 1);

        createLabel(serverGroup, Messages.configTab_contextPathLabel, 128, SWT.RIGHT, 1, 1);
        contextText =
            createText(serverGroup, SWT.BORDER, Messages.configTab_contextPathTextTip, -1, -1, 6, 1,
                modifyDialogListener);

        createLabel(serverGroup, Messages.configTab_portLabel, 128, SWT.RIGHT, 1, 1);
        portSpinner =
            createSpinner(serverGroup, SWT.BORDER, Messages.configTab_portSpinnerTip, 32, -1, 1, 1,
                modifyDialogListener);
        portSpinner.setMinimum(0);
        portSpinner.setMaximum(65535);
        portSpinner.setIncrement(1);
        portSpinner.setPageIncrement(1000);
        createLabel(serverGroup, "/", 16, SWT.CENTER, 1, 1); //$NON-NLS-1$
        httpsPortSpinner =
            createSpinner(serverGroup, SWT.BORDER, Messages.configTab_httpProtSpinnerTip, 32, -1, 1, 1,
                modifyDialogListener);
        httpsPortSpinner.setMinimum(0);
        httpsPortSpinner.setMaximum(65535);
        httpsPortSpinner.setIncrement(1);
        httpsPortSpinner.setPageIncrement(1000);
        httpsEnabledButton =
            createButton(serverGroup, SWT.CHECK, Messages.configTab_httpsEnableButton,
                Messages.configTab_httpsEnableButtonTip, -1, 3, 1, modifyDialogListener);
    }

    private void createLinkGroup(Composite tabComposite)
    {
        Composite linkGroup = createTopComposite(tabComposite, SWT.NONE, 2, -1, true, 2, 1);

        createLabel(linkGroup, JettyPluginUtils.EMPTY, 128, SWT.RIGHT, 1, 2);

        httpLink = createLink(linkGroup, SWT.NONE, JettyPluginUtils.EMPTY, SWT.LEFT, -1, -1, new Listener()
        {
            public void handleEvent(Event event)
            {
                launchHTTP();
            }
        });

        httpsLink = createLink(linkGroup, SWT.NONE, JettyPluginUtils.EMPTY, SWT.LEFT, -1, -1, new Listener()
        {
            public void handleEvent(Event event)
            {
                launchHTTPs();
            }
        });
    }

    private void createHelpGroup(Composite tabComposite)
    {
        Composite helpGroup = createTopComposite(tabComposite, SWT.NONE, 3, -1, false, 2, 1);

        createLabel(helpGroup, JettyPluginUtils.EMPTY, -1, SWT.LEFT, 1, 1);
        createImage(helpGroup, JettyPlugin.getJettyIcon(), 16, SWT.CENTER, SWT.BOTTOM, 1, 1);
        createLink(helpGroup, SWT.NONE, Messages.configTab_homepageLink, SWT.RIGHT, 1, 1, new Listener()
        {
            public void handleEvent(Event event)
            {
                Program.launch("http://eclipse-jetty.github.io/"); //$NON-NLS-1$
            }
        });
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getName()
     */
    public String getName()
    {
        return Messages.configTab_name;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.debug.ui.AbstractLaunchConfigurationTab#getImage()
     */
    @Override
    public Image getImage()
    {
        return JettyPlugin.getJettyIcon();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.debug.ui.AbstractLaunchConfigurationTab#getMessage()
     */
    @Override
    public String getMessage()
    {
        return Messages.configTab_message;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jdt.debug.ui.launchConfigurations.JavaLaunchTab#initializeFrom(org.eclipse.debug.core.ILaunchConfiguration)
     */
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
            JettyPlugin.error(Messages.configTab_initializeFailed, e);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
     */
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
    {
        IJavaElement javaElement = getContext();

        if (javaElement != null)
        {
            initializeJavaProject(javaElement, configuration);
        }
        else
        {
            configuration.setAttribute(ATTR_PROJECT_NAME, JettyPluginUtils.EMPTY);
        }

        String projectName = JettyPluginUtils.EMPTY;

        try
        {
            projectName =
                configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, JettyPluginUtils.EMPTY);
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
            JettyPlugin.error(Messages.configTab_defaultsFailed, e);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
     */
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
            JettyPlugin.error(Messages.configTab_performApplyFailed, e);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.debug.ui.AbstractLaunchConfigurationTab#isValid(org.eclipse.debug.core.ILaunchConfiguration)
     */
    @Override
    public boolean isValid(ILaunchConfiguration config)
    {
        setErrorMessage(null);
        setMessage(null);

        httpLink.setText(JettyPluginUtils.EMPTY);
        httpsLink.setText(JettyPluginUtils.EMPTY);

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
                    setErrorMessage(String.format(Messages.configTab_projectNotExisting, projectName));
                    webAppBrowseButton.setEnabled(false);
                    webAppScanButton.setEnabled(false);
                    return false;
                }
                if (!project.isOpen())
                {
                    setErrorMessage(String.format(Messages.configTab_projectClosed, projectName));
                    webAppBrowseButton.setEnabled(false);
                    webAppScanButton.setEnabled(false);
                    return false;
                }
            }
            else
            {
                setErrorMessage(String.format(Messages.configTab_projectNameInvalid, status.getMessage()));
                webAppBrowseButton.setEnabled(false);
                webAppScanButton.setEnabled(false);
                return false;
            }
            webAppBrowseButton.setEnabled(true);
            webAppScanButton.setEnabled(true);
        }
        else
        {
            setErrorMessage(Messages.configTab_noProjectSelected);
            webAppBrowseButton.setEnabled(false);
            webAppScanButton.setEnabled(false);
            return false;
        }

        String directory = webAppText.getText().trim();
        
        if (directory.length() > 0)
        {
            IFolder folder = project.getFolder(directory);

            if (!folder.exists())
            {
                setErrorMessage(String.format(Messages.configTab_webAppNotExisting, directory, project.getName()));
                return false;
            }
        }

        String httpURL = getHTTPURL();

        if (httpURL != null)
        {
            String text = String.format(Messages.configTab_serverHTTPLink, httpURL);

            if (!text.equals(httpLink.getText()))
            {
                httpLink.setText(text);
            }
        }

        String httpsURL = getHTTPsURL();

        if (httpsURL != null)
        {
            String text = String.format(Messages.configTab_serverHTTPsLink, httpsURL);

            if (!text.equals(httpsLink.getText()))
            {
                httpsLink.setText(text);
            }
        }

        setDirty(true);

        return true;
    }

    /**
     * Selects a Java project
     */
    protected void chooseJavaProject()
    {
        ILabelProvider labelProvider = new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_DEFAULT);
        ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), labelProvider);

        dialog.setTitle(Messages.configTab_projectBrowseTitle);
        dialog.setMessage(Messages.configTab_projectBrowseMessage);

        try
        {
            dialog.setElements(JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()).getJavaProjects());
        }
        catch (JavaModelException e)
        {
            JettyPlugin.error(Messages.configTab_projectBrowseFailed, e);
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

    /**
     * Searches the project for the webApp directory. Shows an error and a selection dialog if not found.
     */
    protected void scanWebappDir()
    {
        IProject project = JettyPluginUtils.getProject(projectText.getText());

        try
        {
            String webAppDir =
                JettyLaunchUI.chooseWebAppDir(Display.getCurrent().getActiveShell(), project, webAppText.getText());

            if (webAppDir != null)
            {
                webAppText.setText(webAppDir);
            }
        }
        catch (CoreException e)
        {
            JettyPlugin.error("Failed to scan for WebApp directory", e);
        }
    }

    /**
     * Shows a dialog for selecting the webApp directory.
     */
    protected void chooseWebappDir()
    {
        String path =
            chooseWorkspaceDirectory(getShell(), JettyPluginUtils.getProject(projectText.getText()),
                Messages.configTab_webAppBrowseTitle, Messages.configTab_webAppBrowseMessage, webAppText.getText());

        if (path != null)
        {
            webAppText.setText(path);
        }
    }

    protected String getHTTPURL()
    {
        return String.format("http://localhost:%s%s", portSpinner.getSelection(), //$NON-NLS-1$
            JettyPluginUtils.prepend(contextText.getText().trim(), "/")); //$NON-NLS-1$
    }

    protected String getHTTPsURL()
    {
        if (!httpsEnabledButton.getSelection())
        {
            return null;
        }

        return String.format("https://localhost:%s%s", httpsPortSpinner.getSelection(), //$NON-NLS-1$
            JettyPluginUtils.prepend(contextText.getText().trim(), "/")); //$NON-NLS-1$
    }

    protected void launchHTTP()
    {
        Program.launch(getHTTPURL());
    }

    protected void launchHTTPs()
    {
        String httpsURL = getHTTPsURL();

        if (httpsURL != null)
        {
            Program.launch(httpsURL);
        }
    }

    public class ModifyDialogListener implements ModifyListener, SelectionListener
    {
        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
         */
        @SuppressWarnings("synthetic-access")
        public void modifyText(ModifyEvent e)
        {
            updateLaunchConfigurationDialog();
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
         */
        public void widgetDefaultSelected(SelectionEvent arg0)
        {
            // intentionally left blank
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
         */
        @SuppressWarnings("synthetic-access")
        public void widgetSelected(SelectionEvent arg0)
        {
            updateLaunchConfigurationDialog();
        }
    }
}
