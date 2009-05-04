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

import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME;

import java.io.File;
import java.text.MessageFormat;

import net.sourceforge.eclipsejetty.JettyPlugin;
import net.sourceforge.eclipsejetty.JettyPluginConstants;

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
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaLaunchTab;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;


/**
 * Launch tab for the RunJettyRun plugin.
 * 
 * @author hillenius
 */
public class JettyTab extends JavaLaunchTab
{
	private final class ModifyDialogListener implements ModifyListener,
			SelectionListener {
		public void modifyText(ModifyEvent e) {
			updateLaunchConfigurationDialog();
		}

		public void widgetDefaultSelected(SelectionEvent arg0) {

		}

		public void widgetSelected(SelectionEvent arg0) {
			updateLaunchConfigurationDialog();
		}
	}

	private static abstract class ButtonListener implements SelectionListener
	{
		public void widgetDefaultSelected(SelectionEvent e)
		{
		}
	}

	private Text fProjText;

	private Text fContextText;

	private Text fWebAppDirText;

	private Text fPortText;

	private Text fJettyPathText;

	private Button fWebappDirButton;

	private Button jetty5Button;

	private Button jetty6Button;

	private Button jetty7Button;

	private ModifyDialogListener modifyDialogListener;

	private Button nojspButton;

	private Button jsp20Button;

	private Button jsp21Button;

	/**
	 * Construct.
	 */
	public JettyTab()
	{
		modifyDialogListener = new ModifyDialogListener();
	}

	public void createControl(Composite parent)
	{

		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(1, false));
		comp.setFont(parent.getFont());
		GridData gd = new GridData(1);
		gd.horizontalSpan = GridData.FILL_BOTH;
		comp.setLayoutData(gd);
		((GridLayout) comp.getLayout()).verticalSpacing = 0;
		createProjectEditor(comp);
		createVerticalSpacer(comp, 1);
		createJettyOptionsEditor(comp);
		createJettySelection(comp);
		createVerticalSpacer(comp, 1);
		setControl(comp);
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(),
		// IJavaDebugHelpContextIds.LAUNCH_CONFIGURATION_DIALOG_MAIN_TAB);
	}

	/**
	 * Creates the widgets for specifying a main type.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	private void createProjectEditor(Composite parent)
	{
		Font font = parent.getFont();
		Group group = new Group(parent, SWT.NONE);
		group.setText("Project");
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		group.setLayoutData(gd);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		group.setLayout(layout);
		group.setFont(font);
		fProjText = new Text(group, SWT.SINGLE | SWT.BORDER);
		gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		fProjText.setLayoutData(gd);
		fProjText.setFont(font);
		fProjText.addModifyListener(modifyDialogListener);
		Button fProjButton = createPushButton(group, "&Browse...", null);
		fProjButton.addSelectionListener(new ButtonListener()
		{
			public void widgetSelected(SelectionEvent e)
			{
				handleProjectButtonSelected();
			}
		});
	}

	/**
	 * Creates the widgets for specifying the directory, context and port for the web application.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	private void createJettyOptionsEditor(Composite parent)
	{
		Font font = parent.getFont();
		Group group = new Group(parent, SWT.NONE);
		group.setText("Web Application");
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		group.setLayoutData(gd);
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		group.setLayout(layout);
		group.setFont(font);

		new Label(group, SWT.LEFT).setText("Context");
		fContextText = new Text(group, SWT.SINGLE | SWT.BORDER);
		fContextText.addModifyListener(modifyDialogListener);
		gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		fContextText.setLayoutData(gd);
		fContextText.setFont(font);

		new Label(group, SWT.LEFT).setText("Port");
		fPortText = new Text(group, SWT.SINGLE | SWT.BORDER);
		fPortText.addModifyListener(modifyDialogListener);
		gd = new GridData();
		fPortText.setLayoutData(gd);
		fPortText.setFont(font);

		new Label(group, SWT.LEFT).setText("WebApp Directory");
		fWebAppDirText = new Text(group, SWT.SINGLE | SWT.BORDER);
		fWebAppDirText.addModifyListener(modifyDialogListener);
		gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalSpan = 2;
		fWebAppDirText.setLayoutData(gd);
		fWebAppDirText.setFont(font);
		fWebappDirButton = createPushButton(group, "&Browse...", null);
		fWebappDirButton.addSelectionListener(new ButtonListener()
		{
			public void widgetSelected(SelectionEvent e)
			{
				chooseWebappDir();
			}
		});
		fWebappDirButton.setEnabled(false);
	}

	private void createJettySelection(Composite parent)
	{
		Group group = new Group(parent, SWT.NONE);
		group.setText("Jetty");
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		group.setLayoutData(gd);
		GridLayout layout = new GridLayout();
		layout.numColumns = 5;
		group.setLayout(layout);
		group.setFont(parent.getFont());

		// Jetty path
		new Label(group, SWT.LEFT).setText("Path");
		fJettyPathText = new Text(group, SWT.SINGLE | SWT.BORDER);
		fJettyPathText.addModifyListener(modifyDialogListener);
		gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalSpan = 3;
		fJettyPathText.setLayoutData(gd);
		Button chooseJettyPathButton = createPushButton(group, "&Browse...", null);
		chooseJettyPathButton.addSelectionListener(new ButtonListener()
		{
			public void widgetSelected(SelectionEvent e)
			{
				chooseJettyPath();
			}
		});

		new Label(group, SWT.LEFT).setText("Version");
		Composite jettyVersionGroup = new Composite(group, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 3;
		jettyVersionGroup.setLayout(layout);
		gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalSpan = 4;
		jettyVersionGroup.setLayoutData(gd);
		jetty5Button = new Button(jettyVersionGroup, SWT.RADIO);
		jetty5Button.setText("Jetty 5.x");
		jetty5Button.setEnabled(false);
		jetty5Button.addSelectionListener(modifyDialogListener);
		jetty6Button = new Button(jettyVersionGroup, SWT.RADIO);
		jetty6Button.setText("Jetty 6.x");
		jetty6Button.addSelectionListener(modifyDialogListener);
		jetty7Button = new Button(jettyVersionGroup, SWT.RADIO);
		jetty7Button.setText("Jetty 7.x");
		jetty7Button.addSelectionListener(modifyDialogListener);

		new Label(group, SWT.LEFT).setText("JSP");
		Composite jspVersionComposite = new Composite(group, SWT.NONE);
		jspVersionComposite.setLayout(layout);
		jspVersionComposite.setLayoutData(gd);
		nojspButton = new Button(jspVersionComposite, SWT.RADIO);
		nojspButton.setText("disabled");
		nojspButton.setSelection(true);
		nojspButton.addSelectionListener(modifyDialogListener);
		jsp20Button = new Button(jspVersionComposite, SWT.RADIO);
		jsp20Button.setText("JSP 2.0");
		jsp20Button.addSelectionListener(modifyDialogListener);
		jsp21Button = new Button(jspVersionComposite, SWT.RADIO);
		jsp21Button.setText("JSP 2.1");
		jsp21Button.addSelectionListener(modifyDialogListener);
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

	public String getName()
	{
		return "Jetty";
	}

	public void initializeFrom(ILaunchConfiguration configuration)
	{
		try
		{
			fProjText.setText(configuration.getAttribute(ATTR_PROJECT_NAME, ""));
			fContextText.setText(configuration.getAttribute(JettyPluginConstants.ATTR_CONTEXT, ""));
			fWebAppDirText.setText(configuration.getAttribute(JettyPluginConstants.ATTR_WEBAPPDIR, ""));
			fPortText.setText(configuration.getAttribute(JettyPluginConstants.ATTR_PORT, ""));
			fJettyPathText.setText(configuration.getAttribute(JettyPluginConstants.ATTR_JETTY_PATH, ""));
			String jettyVersion = configuration.getAttribute(JettyPluginConstants.ATTR_JETTY_VERSION, "6");
			jetty7Button.setSelection("7".equals(jettyVersion));
			jetty6Button.setSelection("6".equals(jettyVersion));
			jetty5Button.setSelection("5".equals(jettyVersion));
			String jspVersion = configuration.getAttribute(JettyPluginConstants.ATTR_JSP_VERSION, JettyPluginConstants.ATTR_JSP_VERSION_NO);
			nojspButton.setSelection(JettyPluginConstants.ATTR_JSP_VERSION_NO.equals(jspVersion));
			jsp20Button.setSelection(JettyPluginConstants.ATTR_JSP_VERSION_20.equals(jspVersion));
			jsp21Button.setSelection(JettyPluginConstants.ATTR_JSP_VERSION_21.equals(jspVersion));
		}
		catch (CoreException e)
		{
			JettyPlugin.logError(e);
		}
	}

	public boolean isValid(ILaunchConfiguration config)
	{
		setErrorMessage(null);
		setMessage(null);

		String projectName = fProjText.getText().trim();
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
					fWebappDirButton.setEnabled(false);
					return false;
				}
				if (!project.isOpen())
				{
					setErrorMessage(MessageFormat.format("Project {0} is closed", projectName));
					fWebappDirButton.setEnabled(false);
					return false;
				}
			}
			else
			{
				setErrorMessage(MessageFormat.format("Illegal project name: {0}", status.getMessage()));
				fWebappDirButton.setEnabled(false);
				return false;
			}
			fWebappDirButton.setEnabled(true);
		}
		else
		{
			setErrorMessage("No project selected");
			return false;
		}

		String directory = fWebAppDirText.getText().trim();
		if (!"".equals(directory.trim()))
		{
			IFolder folder = project.getFolder(directory);
			if (!folder.exists())
			{
				setErrorMessage(MessageFormat.format("Folder {0} does not exist in project {1}", directory, project
					.getName()));
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

		String jettyPath = fJettyPathText.getText().trim();
		if(!"".equals(jettyPath))
		{
			File f = new File(jettyPath);
			if(!f.exists() || !f.isDirectory())
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

		setDirty(true);
		return true;
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(ATTR_PROJECT_NAME, fProjText.getText());
		configuration.setAttribute(JettyPluginConstants.ATTR_CONTEXT, fContextText.getText());
		configuration.setAttribute(JettyPluginConstants.ATTR_WEBAPPDIR, fWebAppDirText.getText());
		configuration.setAttribute(JettyPluginConstants.ATTR_PORT, fPortText.getText());
		configuration.setAttribute(JettyPluginConstants.ATTR_JETTY_PATH, fJettyPathText.getText());
		configuration.setAttribute(JettyPluginConstants.ATTR_JETTY_VERSION, getJettyVersion());
		configuration.setAttribute(JettyPluginConstants.ATTR_JSP_VERSION, getJspVersion());
		configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH_PROVIDER,
			JettyPluginConstants.CLASSPATH_PROVIDER_JETTY);

		// save the Jetty path in preferences
		JettyPlugin.getDefault().getPluginPreferences().setValue(JettyPluginConstants.ATTR_JETTY_PATH, fJettyPathText.getText());
		JettyPlugin.getDefault().savePluginPreferences();
	}

	private String getJettyVersion()
	{
		if(jetty7Button.getSelection())
			return "7";
		else if(jetty5Button.getSelection())
			return "5";
		else
			return "6";
	}

	private String getJspVersion()
	{
		if(jsp20Button.getSelection())
			return JettyPluginConstants.ATTR_JSP_VERSION_20;
		else if(jsp21Button.getSelection())
			return JettyPluginConstants.ATTR_JSP_VERSION_21;
		else
			return JettyPluginConstants.ATTR_JSP_VERSION_NO;
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

		configuration.setAttribute(
			IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
			JettyPluginConstants.BOOTSTRAP_CLASS_NAME);

		// set the class path provider so that Jetty and the bootstrap jar are
		// added to the run time class path. Value has to be the same as the one
		// defined for the extension point
		configuration.setAttribute(
			IJavaLaunchConfigurationConstants.ATTR_CLASSPATH_PROVIDER,
			JettyPluginConstants.CLASSPATH_PROVIDER_JETTY);

		// get the name for this launch configuration
		String launchConfigName = "";
		try
		{
			// try to base the launch config name on the current project
			launchConfigName = configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "");
		}
		catch (CoreException e)
		{
			// ignore
		}
		if (launchConfigName == null || launchConfigName.length() == 0)
		{
			// if no project name was found, base on a default name
			launchConfigName = "Jetty Webapp";
		}
		// generate an unique name (e.g. myproject(2))
		launchConfigName = getLaunchConfigurationDialog().generateName(launchConfigName);
		configuration.rename(launchConfigName); // and rename the config

		configuration.setAttribute(JettyPluginConstants.ATTR_CONTEXT, "/");
		configuration.setAttribute(JettyPluginConstants.ATTR_WEBAPPDIR, "src/main/webapp");
		configuration.setAttribute(JettyPluginConstants.ATTR_PORT, "8080");
		configuration.setAttribute(JettyPluginConstants.ATTR_JETTY_PATH, JettyPlugin.getDefault().getPluginPreferences().getString(JettyPluginConstants.ATTR_JETTY_PATH));
	}

	private IJavaProject chooseJavaProject()
	{
		ILabelProvider labelProvider = new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_DEFAULT);
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), labelProvider);
		dialog.setTitle("Project Selection");
		dialog.setMessage("Select a project to constrain your search.");
		try
		{
			dialog.setElements(JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()).getJavaProjects());
		}
		catch (JavaModelException jme)
		{
			JettyPlugin.logError(jme);
		}

		IJavaProject javaProject = null;
		String projectName = fProjText.getText().trim();
		if (projectName.length() > 0)
		{
			javaProject = JavaCore.create(getWorkspaceRoot()).getJavaProject(projectName);
		}
		if (javaProject != null)
		{
			dialog.setInitialSelections(new Object[]{javaProject});
		}
		if (dialog.open() == Window.OK)
		{
			return (IJavaProject) dialog.getFirstResult();
		}
		return null;
	}

	private void chooseWebappDir()
	{
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(fProjText.getText());
		ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(), project, false,
			"Select Web Application Directory");
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
			path = path.removeFirstSegments(1);
			String containerName = path.makeRelative().toString();
			fWebAppDirText.setText(containerName);
		}
	}

	private void chooseJettyPath()
	{
		DirectoryDialog dialog = new DirectoryDialog(getShell(), SWT.OPEN);
		dialog.setText("Select Jetty Install Directory");
		String jettyPath = dialog.open();
		if(jettyPath != null)
			fJettyPathText.setText(jettyPath);
	}

	private IWorkspaceRoot getWorkspaceRoot()
	{
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	private void handleProjectButtonSelected()
	{
		IJavaProject project = chooseJavaProject();
		if (project == null)
		{
			return;
		}
		String projectName = project.getElementName();
		fProjText.setText(projectName);
	}
}
