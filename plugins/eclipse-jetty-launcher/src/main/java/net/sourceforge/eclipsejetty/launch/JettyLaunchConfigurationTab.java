package net.sourceforge.eclipsejetty.launch;

import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.*;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.regex.Pattern;

import net.sourceforge.eclipsejetty.JettyLaunchConfigurationClassPathProvider;
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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
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
	private Text projectText;
	private Text webAppText;
	private Button webAppButton;
	private Text contextText;
	private Text portText;
	private Text pathText;
	private Button jettyAutoButton;
	private Button jetty5Button;
	private Button jetty6Button;
	private Button jetty7Button;
	private Button jspDisabledButton;
	private Button jsp20Button;
	private Button jsp21Button;
	private Text excludedLibrariesText;
	private final ModifyDialogListener modifyDialogListener;

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

		projectGroup.setLayout(new GridLayout(3, false));
		projectGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		projectGroup.setText("Project");

		projectText = new Text(projectGroup, SWT.BORDER);

		projectText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		projectText.addModifyListener(modifyDialogListener);

		final Button projectButton = new Button(projectGroup, SWT.NONE);

		projectButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		projectButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				chooseJavaProject();
			}
		});
		projectButton.setText("Browse...");

		final Group applicationGroup = new Group(tabComposite, SWT.NONE);

		applicationGroup.setLayout(new GridLayout(3, false));
		applicationGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		applicationGroup.setText("Web Application");

		createLabel(applicationGroup, "WebApp Directory", 128);

		webAppText = new Text(applicationGroup, SWT.BORDER);

		webAppText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		webAppText.addModifyListener(modifyDialogListener);

		webAppButton = new Button(applicationGroup, SWT.NONE);

		webAppButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		webAppButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				chooseWebappDir();
			}
		});
		webAppButton.setText("Browse...");

		createLabel(applicationGroup, "Context Path", 128);

		contextText = new Text(applicationGroup, SWT.BORDER);

		contextText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		contextText.addModifyListener(modifyDialogListener);

		createLabel(applicationGroup, "", 0);

		createLabel(applicationGroup, "HTTP Port", 128);

		portText = new Text(applicationGroup, SWT.BORDER);

		final GridData portTextGridData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		portTextGridData.widthHint = 64;
		portText.setLayoutData(portTextGridData);
		portText.addModifyListener(modifyDialogListener);

		final Group jettyGroup = new Group(tabComposite, SWT.NONE);

		jettyGroup.setLayout(new GridLayout(3, false));
		jettyGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		jettyGroup.setText("Jetty");

		createLabel(jettyGroup, "Home", 128);

		pathText = new Text(jettyGroup, SWT.BORDER);

		pathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		pathText.addModifyListener(modifyDialogListener);

		final Button pathButton = new Button(jettyGroup, SWT.NONE);

		pathButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		pathButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				chooseJettyPath();
			}
		});
		pathButton.setText("Browse...");

		createLabel(jettyGroup, "Version", 128);

		final Group versionGroup = new Group(jettyGroup, SWT.SHADOW_NONE);

		versionGroup.setLayout(new RowLayout());
		versionGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

		jettyAutoButton = new Button(versionGroup, SWT.RADIO);

		jettyAutoButton.setText("Auto Detect");
		jettyAutoButton.addSelectionListener(modifyDialogListener);

		jetty5Button = new Button(versionGroup, SWT.RADIO);

		jetty5Button.setText("Jetty 5.x");
		jetty5Button.addSelectionListener(modifyDialogListener);

		jetty6Button = new Button(versionGroup, SWT.RADIO);

		jetty6Button.setText("Jetty 6.x");
		jetty6Button.addSelectionListener(modifyDialogListener);

		jetty7Button = new Button(versionGroup, SWT.RADIO);

		jetty7Button.setText("Jetty 7.x");
		jetty7Button.addSelectionListener(modifyDialogListener);

		createLabel(jettyGroup, "", 0);

		createLabel(jettyGroup, "JSP Support", 128);

		final Group jspGroup = new Group(jettyGroup, SWT.SHADOW_NONE);

		jspGroup.setLayout(new RowLayout());
		jspGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

		jspDisabledButton = new Button(jspGroup, SWT.RADIO);

		jspDisabledButton.setText("Disabled");
		jspDisabledButton.addSelectionListener(modifyDialogListener);

		jsp20Button = new Button(jspGroup, SWT.RADIO);

		jsp20Button.setText("JSP 2.0");
		jsp20Button.addSelectionListener(modifyDialogListener);

		jsp21Button = new Button(jspGroup, SWT.RADIO);

		jsp21Button.setText("JSP 2.1");
		jsp21Button.addSelectionListener(modifyDialogListener);

		createLabel(jettyGroup, "", 0);

		createLabel(jettyGroup, "Excluded Libraries", 128);

		excludedLibrariesText = new Text(jettyGroup, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);

		final GridData excludedLibrariesGridData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		excludedLibrariesGridData.heightHint = 50;
		excludedLibrariesText.setLayoutData(excludedLibrariesGridData);
		excludedLibrariesText.addModifyListener(modifyDialogListener);
		excludedLibrariesText
			.setToolTipText("Comma or line separated list of libraries to exclude from the classpath. The entries are regular expressions.");

		setControl(tabComposite);
	}

	private Label createLabel(final Composite composite, final String text, final int widthHint)
	{
		final Label label = new Label(composite, SWT.NONE);

		final GridData projectLabelGridData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		projectLabelGridData.widthHint = widthHint;
		label.setLayoutData(projectLabelGridData);
		label.setText(text);

		return label;
	}

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
	public void initializeFrom(final ILaunchConfiguration configuration)
	{
		try
		{
			projectText.setText(configuration.getAttribute(ATTR_PROJECT_NAME, ""));
			webAppText.setText(configuration.getAttribute(JettyPluginConstants.ATTR_WEBAPPDIR, ""));
			contextText.setText(configuration.getAttribute(JettyPluginConstants.ATTR_CONTEXT, ""));
			portText.setText(configuration.getAttribute(JettyPluginConstants.ATTR_PORT, ""));
			pathText.setText(configuration.getAttribute(JettyPluginConstants.ATTR_JETTY_PATH, ""));

			final String jettyVersion = configuration.getAttribute(JettyPluginConstants.ATTR_JETTY_VERSION, "auto");

			jettyAutoButton.setSelection("auto".equals(jettyVersion));
			jetty5Button.setSelection("5".equals(jettyVersion));
			jetty6Button.setSelection("6".equals(jettyVersion));
			jetty7Button.setSelection("7".equals(jettyVersion));

			final String jspSupport =
				configuration.getAttribute(JettyPluginConstants.ATTR_JSP_ENABLED,
					JettyPluginConstants.ATTR_JSP_ENABLED_DEFAULT);

			jspDisabledButton.setSelection("false".equals(jspSupport));
			jsp20Button.setSelection("2.0".equals(jspSupport));
			jsp21Button.setSelection("2.1".equals(jspSupport) || "true".equals(jspSupport));

			excludedLibrariesText.setText(configuration.getAttribute(JettyPluginConstants.ATTR_LAUNCHER_EXCLUDED_LIBS,
				JettyPluginConstants.DEFAULT_LAUNCHER_EXCLUDED_LIBS));
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

		configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
			JettyPluginConstants.DEFAULT_BOOTSTRAP_CLASS_NAME);

		// set the class path provider so that Jetty and the bootstrap jar are
		// added to the run time class path. Value has to be the same as the one
		// defined for the extension point
		configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH_PROVIDER,
			JettyPluginConstants.CLASSPATH_PROVIDER_JETTY);

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

		configuration.setAttribute(JettyPluginConstants.ATTR_CONTEXT, "/");
		configuration.setAttribute(JettyPluginConstants.ATTR_WEBAPPDIR, "src/main/webapp");
		configuration.setAttribute(JettyPluginConstants.ATTR_PORT, "8080");
		configuration.setAttribute(JettyPluginConstants.ATTR_JETTY_PATH, JettyPlugin.getDefault()
			.getPluginPreferences().getString(JettyPluginConstants.ATTR_JETTY_PATH));
	}

	public void performApply(final ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(ATTR_PROJECT_NAME, projectText.getText());
		configuration.setAttribute(JettyPluginConstants.ATTR_CONTEXT, contextText.getText());
		configuration.setAttribute(JettyPluginConstants.ATTR_WEBAPPDIR, webAppText.getText());
		configuration.setAttribute(JettyPluginConstants.ATTR_PORT, portText.getText());
		configuration.setAttribute(JettyPluginConstants.ATTR_LAUNCHER_EXCLUDED_LIBS, excludedLibrariesText.getText());
		configuration.setAttribute(JettyPluginConstants.ATTR_JETTY_PATH, pathText.getText());

		configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH_PROVIDER,
			JettyPluginConstants.CLASSPATH_PROVIDER_JETTY);

		final String jettyVersion = getJettyVersion();
		configuration.setAttribute(JettyPluginConstants.ATTR_JETTY_VERSION, jettyVersion);
		if ("7".equals(jettyVersion))
		{
			configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
				JettyPluginConstants.JETTY7_BOOTSTRAP_CLASS_NAME);
		}
		else
		{
			configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
				JettyPluginConstants.DEFAULT_BOOTSTRAP_CLASS_NAME);
		}

		configuration.setAttribute(JettyPluginConstants.ATTR_JSP_ENABLED, getJSPEnabled());

		// save the Jetty path in preferences
		JettyPlugin.getDefault().getPluginPreferences()
			.setValue(JettyPluginConstants.ATTR_JETTY_PATH, pathText.getText());
		JettyPlugin.getDefault().savePluginPreferences();
	}

	private String getJettyVersion()
	{
		if (jetty5Button.getSelection())
		{
			return "5";
		}
		else if (jetty6Button.getSelection())
		{
			return "6";
		}
		else if (jetty7Button.getSelection())
		{
			return "7";
		}
		else
		{
			return "auto";
		}
	}

	private String getJSPEnabled()
	{
		if (jspDisabledButton.getSelection())
		{
			return "false";
		}
		else if (jsp20Button.getSelection())
		{
			return "2.0";
		}
		else
		{
			return "true";
		}
	}

	@Override
	public boolean isValid(final ILaunchConfiguration config)
	{
		setErrorMessage(null);
		setMessage(null);

		final String projectName = projectText.getText().trim();
		IProject project = null;
		if (projectName.length() > 0)
		{
			final IWorkspace workspace = ResourcesPlugin.getWorkspace();
			final IStatus status = workspace.validateName(projectName, IResource.PROJECT);
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

		final String directory = webAppText.getText().trim();
		if (!"".equals(directory.trim()))
		{
			final IFolder folder = project.getFolder(directory);
			if (!folder.exists())
			{
				setErrorMessage(MessageFormat.format("Folder {0} does not exist in project {1}", directory,
					project.getName()));
				return false;
			}
			final IFile file = project.getFile(new Path(directory + "/WEB-INF/web.xml"));
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

		final String jettyPath = pathText.getText().trim();
		if (!"".equals(jettyPath))
		{
			final File f = new File(jettyPath);
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

		if (jetty5Button.getSelection())
		{
			jspDisabledButton.setEnabled(true);
			jsp20Button.setEnabled(false);
			jsp21Button.setEnabled(false);

			jspDisabledButton.setSelection(true);
			jsp20Button.setSelection(false);
			jsp21Button.setSelection(false);
		}
		else if (jetty7Button.getSelection())
		{
			jspDisabledButton.setEnabled(true);
			jsp20Button.setEnabled(false);
			jsp21Button.setEnabled(true);
			
			if (jsp20Button.getSelection())
			{
				jsp20Button.setSelection(false);
				jsp21Button.setSelection(true);
			}
		}
		else
		{
			jspDisabledButton.setEnabled(true);
			jsp20Button.setEnabled(true);
			jsp21Button.setEnabled(true);
		}

		try
		{
			JettyLaunchConfigurationClassPathProvider.detectJettyVersion(pathText.getText(), getJettyVersion());
		}
		catch (final IllegalArgumentException e)
		{
			setErrorMessage(e.getMessage());
			return false;
		}

		try
		{
			JettyLaunchConfigurationClassPathProvider.extractPatterns(new ArrayList<Pattern>(),
				excludedLibrariesText.getText());
		}
		catch (final IllegalArgumentException e)
		{
			setErrorMessage("Failed to parse Excluded Libraries. " + e.getMessage());
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
		final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectText.getText());
		final ContainerSelectionDialog dialog =
			new ContainerSelectionDialog(getShell(), project, false, "Select Web Application Directory");
		dialog.setTitle("Folder Selection");
		if (project != null)
		{
			final IPath path = project.getFullPath();
			dialog.setInitialSelections(new Object[]{path});
		}
		dialog.showClosedProjects(false);
		dialog.open();
		final Object[] results = dialog.getResult();
		if ((results != null) && (results.length > 0) && (results[0] instanceof IPath))
		{
			IPath path = (IPath) results[0];
			path = path.removeFirstSegments(1);
			final String containerName = path.makeRelative().toString();
			webAppText.setText(containerName);
		}
	}

	protected void chooseJettyPath()
	{
		final DirectoryDialog dialog = new DirectoryDialog(getShell(), SWT.OPEN);
		dialog.setText("Select Jetty Install Directory");
		final String jettyPath = dialog.open();
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
