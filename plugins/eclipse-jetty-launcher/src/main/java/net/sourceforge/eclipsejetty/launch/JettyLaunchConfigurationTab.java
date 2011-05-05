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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
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

public class JettyLaunchConfigurationTab extends JavaLaunchTab
{
	private Text txtProject;
	private Text txtContext;
	private Text txtPort;
	private Text txtWebappDirectory;
	private Text txtJettyPath;
	private Button btnJetty5;
	private Button btnJetty6;
	private Button btnJetty7;
	private Button btnEnableJsp;
	private Button btnBrowseWebappDirectory;
	private ModifyDialogListener modifyDialogListener;

	public JettyLaunchConfigurationTab()
	{
		modifyDialogListener = new ModifyDialogListener();
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	public void createControl(Composite parent)
	{
		Composite tabComposite = new Composite(parent, SWT.NONE);
		tabComposite.setLayout(new GridLayout(1, false));
		
		Group grpProject = new Group(tabComposite, SWT.NONE);
		grpProject.setLayout(new GridLayout(2, false));
		grpProject.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpProject.setText("Project");
		
		txtProject = new Text(grpProject, SWT.BORDER);
		txtProject.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtProject.addModifyListener(modifyDialogListener);
		
		Button btnBrowseProject = new Button(grpProject, SWT.NONE);
		btnBrowseProject.setText("&Browse...");
		btnBrowseProject.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				chooseJavaProject();
			}
		});
		
		Group grpWebApplication = new Group(tabComposite, SWT.NONE);
		grpWebApplication.setLayout(new FormLayout());
		grpWebApplication.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpWebApplication.setText("Web Application");
		
		Label lblContext = new Label(grpWebApplication, SWT.NONE);
		FormData fd_lblContext = new FormData();
		fd_lblContext.top = new FormAttachment(0, 10);
		fd_lblContext.left = new FormAttachment(0, 10);
		lblContext.setLayoutData(fd_lblContext);
		lblContext.setText("Context");
		
		txtContext = new Text(grpWebApplication, SWT.BORDER);
		FormData fd_txtContext = new FormData();
		fd_txtContext.top = new FormAttachment(lblContext, -3, SWT.TOP);
		fd_txtContext.left = new FormAttachment(lblContext, 3, SWT.RIGHT);
		txtContext.setLayoutData(fd_txtContext);
		txtContext.addModifyListener(modifyDialogListener);
		
		Label lblPort = new Label(grpWebApplication, SWT.NONE);
		fd_txtContext.right = new FormAttachment(lblPort, -3);
		FormData fd_lblPort = new FormData();
		fd_lblPort.top = new FormAttachment(lblContext, 0, SWT.TOP);
		lblPort.setLayoutData(fd_lblPort);
		lblPort.setText("Port");
		
		txtPort = new Text(grpWebApplication, SWT.BORDER);
		fd_lblPort.right = new FormAttachment(txtPort, -6);
		FormData fd_txtPort = new FormData();
		fd_txtPort.top = new FormAttachment(lblContext, -3, SWT.TOP);
		fd_txtPort.right = new FormAttachment(100, -10);
		fd_txtPort.width = 30;
		txtPort.setLayoutData(fd_txtPort);
		txtPort.addModifyListener(modifyDialogListener);

		Label lblWebappDirectory = new Label(grpWebApplication, SWT.NONE);
		FormData fd_lblWebappDirectory = new FormData();
		fd_lblWebappDirectory.top = new FormAttachment(lblContext, 12);
		fd_lblWebappDirectory.left = new FormAttachment(0, 10);
		lblWebappDirectory.setLayoutData(fd_lblWebappDirectory);
		lblWebappDirectory.setText("Webapp Directory");
		
		txtWebappDirectory = new Text(grpWebApplication, SWT.BORDER);
		FormData fd_txtWebappDirectory = new FormData();
		fd_txtWebappDirectory.top = new FormAttachment(lblWebappDirectory, -3, SWT.TOP);
		fd_txtWebappDirectory.left = new FormAttachment(lblWebappDirectory, 5);
		txtWebappDirectory.setLayoutData(fd_txtWebappDirectory);
		txtWebappDirectory.addModifyListener(modifyDialogListener);

		btnBrowseWebappDirectory = new Button(grpWebApplication, SWT.NONE);
		fd_txtWebappDirectory.right = new FormAttachment(btnBrowseWebappDirectory, -6);
		FormData fd_btnBrowseWebappDirectory = new FormData();
		fd_btnBrowseWebappDirectory.top = new FormAttachment(txtPort, 6);
		fd_btnBrowseWebappDirectory.right = new FormAttachment(txtPort, 0, SWT.RIGHT);
		btnBrowseWebappDirectory.setLayoutData(fd_btnBrowseWebappDirectory);
		btnBrowseWebappDirectory.setText("Browse...");
		btnBrowseWebappDirectory.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				chooseWebappDir();
			}
		});

		Group grpJetty = new Group(tabComposite, SWT.NONE);
		grpJetty.setLayout(new FormLayout());
		grpJetty.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpJetty.setText("Jetty");
		
		Label lblPath = new Label(grpJetty, SWT.NONE);
		FormData fd_lblPath = new FormData();
		fd_lblPath.top = new FormAttachment(0, 10);
		fd_lblPath.left = new FormAttachment(0, 10);
		lblPath.setLayoutData(fd_lblPath);
		lblPath.setText("Path");
		
		txtJettyPath = new Text(grpJetty, SWT.BORDER);
		FormData fd_txtPath = new FormData();
		fd_txtPath.bottom = new FormAttachment(lblPath, 0, SWT.BOTTOM);
		fd_txtPath.left = new FormAttachment(lblPath, 6);
		txtJettyPath.setLayoutData(fd_txtPath);
		
		Button btnBrowsePath = new Button(grpJetty, SWT.NONE);
		fd_txtPath.right = new FormAttachment(btnBrowsePath, -6);
		FormData fd_btnBrowsePath = new FormData();
		fd_btnBrowsePath.bottom = new FormAttachment(lblPath, 0, SWT.BOTTOM);
		fd_btnBrowsePath.right = new FormAttachment(100, -10);
		btnBrowsePath.setLayoutData(fd_btnBrowsePath);
		btnBrowsePath.setText("Browse...");
		btnBrowsePath.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				chooseJettyPath();
			}
		});

		Label lblVersion = new Label(grpJetty, SWT.NONE);
		FormData fd_lblVersion = new FormData();
		fd_lblVersion.left = new FormAttachment(lblPath, 0, SWT.LEFT);
		fd_lblVersion.top = new FormAttachment(lblPath, 12);
		lblVersion.setLayoutData(fd_lblVersion);
		lblVersion.setText("Version");

		btnJetty5 = new Button(grpJetty, SWT.RADIO);
		FormData fd_btnJettyx = new FormData();
		fd_btnJettyx.bottom = new FormAttachment(lblVersion, 0, SWT.BOTTOM);
		fd_btnJettyx.left = new FormAttachment(lblVersion, 11);
		btnJetty5.setLayoutData(fd_btnJettyx);
		btnJetty5.setText("Jetty 5.x");
		btnJetty5.addSelectionListener(modifyDialogListener);

		btnJetty6 = new Button(grpJetty, SWT.RADIO);
		FormData fd_btnJettyx_1 = new FormData();
		fd_btnJettyx_1.bottom = new FormAttachment(lblVersion, 0, SWT.BOTTOM);
		fd_btnJettyx_1.left = new FormAttachment(btnJetty5, 6);
		btnJetty6.setLayoutData(fd_btnJettyx_1);
		btnJetty6.setText("Jetty 6.x");
		btnJetty6.addSelectionListener(modifyDialogListener);

		btnJetty7 = new Button(grpJetty, SWT.RADIO);
		FormData fd_btnJettyx_2 = new FormData();
		fd_btnJettyx_2.bottom = new FormAttachment(lblVersion, 0, SWT.BOTTOM);
		fd_btnJettyx_2.left = new FormAttachment(btnJetty6, 6);
		btnJetty7.setLayoutData(fd_btnJettyx_2);
		btnJetty7.setText("Jetty 7.x");
		btnJetty7.addSelectionListener(modifyDialogListener);

		btnEnableJsp = new Button(grpJetty, SWT.CHECK);
		FormData fd_btnEnableJsp = new FormData();
		fd_btnEnableJsp.top = new FormAttachment(lblVersion, -1, SWT.TOP);
		fd_btnEnableJsp.left = new FormAttachment(btnJetty7, 6);
		btnEnableJsp.setLayoutData(fd_btnEnableJsp);
		btnEnableJsp.setText("Enable JSP");
		btnEnableJsp.addSelectionListener(modifyDialogListener);

		setControl(tabComposite);
	}

	@Override
	public String getName()
	{
		return "Jetty";
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
		try
		{
			txtProject.setText(configuration.getAttribute(ATTR_PROJECT_NAME, ""));
			txtContext.setText(configuration.getAttribute(JettyPluginConstants.ATTR_CONTEXT, ""));
			txtWebappDirectory.setText(configuration.getAttribute(JettyPluginConstants.ATTR_WEBAPPDIR, ""));
			txtPort.setText(configuration.getAttribute(JettyPluginConstants.ATTR_PORT, ""));
			txtJettyPath.setText(configuration.getAttribute(JettyPluginConstants.ATTR_JETTY_PATH, ""));
			String jettyVersion = configuration.getAttribute(JettyPluginConstants.ATTR_JETTY_VERSION, "6");
			btnJetty7.setSelection("7".equals(jettyVersion));
			btnJetty6.setSelection("6".equals(jettyVersion));
			btnJetty5.setSelection("5".equals(jettyVersion));
			btnEnableJsp.setSelection(Boolean.valueOf(configuration.getAttribute(JettyPluginConstants.ATTR_JSP_ENABLED, JettyPluginConstants.ATTR_JSP_ENABLED_DEFAULT)));
		}
		catch (CoreException e)
		{
			JettyPlugin.logError(e);
		}
	}

	@Override
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
			JettyPluginConstants.DEFAULT_BOOTSTRAP_CLASS_NAME);

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

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(ATTR_PROJECT_NAME, txtProject.getText());
		configuration.setAttribute(JettyPluginConstants.ATTR_CONTEXT, txtContext.getText());
		configuration.setAttribute(JettyPluginConstants.ATTR_WEBAPPDIR, txtWebappDirectory.getText());
		configuration.setAttribute(JettyPluginConstants.ATTR_PORT, txtPort.getText());
		configuration.setAttribute(JettyPluginConstants.ATTR_JETTY_PATH, txtJettyPath.getText());

		configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH_PROVIDER,
			JettyPluginConstants.CLASSPATH_PROVIDER_JETTY);

		final String jettyVersion = getJettyVersion();
		configuration.setAttribute(JettyPluginConstants.ATTR_JETTY_VERSION, jettyVersion);
		if("7".equals(jettyVersion))
		{
			configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
					JettyPluginConstants.JETTY7_BOOTSTRAP_CLASS_NAME);
		}
		else
		{
			configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
				JettyPluginConstants.DEFAULT_BOOTSTRAP_CLASS_NAME);
		}

		configuration.setAttribute(JettyPluginConstants.ATTR_JSP_ENABLED, String.valueOf(btnEnableJsp.getSelection()));

		// save the Jetty path in preferences
		JettyPlugin.getDefault().getPluginPreferences().setValue(JettyPluginConstants.ATTR_JETTY_PATH, txtJettyPath.getText());
		JettyPlugin.getDefault().savePluginPreferences();
	}

	private String getJettyVersion()
	{
		if(btnJetty7.getSelection())
			return "7";
		else if(btnJetty5.getSelection())
			return "5";
		else
			return "6";
	}

	public boolean isValid(ILaunchConfiguration config)
	{
		setErrorMessage(null);
		setMessage(null);

		String projectName = txtProject.getText().trim();
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
					btnBrowseWebappDirectory.setEnabled(false);
					return false;
				}
				if (!project.isOpen())
				{
					setErrorMessage(MessageFormat.format("Project {0} is closed", projectName));
					btnBrowseWebappDirectory.setEnabled(false);
					return false;
				}
			}
			else
			{
				setErrorMessage(MessageFormat.format("Illegal project name: {0}", status.getMessage()));
				btnBrowseWebappDirectory.setEnabled(false);
				return false;
			}
			btnBrowseWebappDirectory.setEnabled(true);
		}
		else
		{
			setErrorMessage("No project selected");
			return false;
		}

		String directory = txtWebappDirectory.getText().trim();
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

		String jettyPath = txtJettyPath.getText().trim();
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

		if (btnJetty5.getSelection())
		{
			btnEnableJsp.setSelection(false);
			btnEnableJsp.setEnabled(false);
		}
		else
		{
			btnEnableJsp.setEnabled(true);
		}

		setDirty(true);
		return true;
	}

	private void chooseJavaProject()
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
		String projectName = txtProject.getText().trim();
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
			txtProject.setText(projectName);
		}
	}

	private void chooseWebappDir()
	{
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(txtProject.getText());
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
			txtWebappDirectory.setText(containerName);
		}
	}

	private void chooseJettyPath()
	{
		DirectoryDialog dialog = new DirectoryDialog(getShell(), SWT.OPEN);
		dialog.setText("Select Jetty Install Directory");
		String jettyPath = dialog.open();
		if(jettyPath != null)
			txtJettyPath.setText(jettyPath);
	}

	private final class ModifyDialogListener implements ModifyListener,
			SelectionListener
	{
		public void modifyText(ModifyEvent e)
		{
			updateLaunchConfigurationDialog();
		}

		public void widgetDefaultSelected(SelectionEvent arg0)
		{

		}

		public void widgetSelected(SelectionEvent arg0)
		{
			updateLaunchConfigurationDialog();
		}
	}
}
