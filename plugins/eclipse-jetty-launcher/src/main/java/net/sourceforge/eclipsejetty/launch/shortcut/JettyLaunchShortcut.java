package net.sourceforge.eclipsejetty.launch.shortcut;

import static net.sourceforge.eclipsejetty.launch.util.JettyLaunchUI.*;

import java.io.File;

import net.sourceforge.eclipsejetty.JettyPlugin;
import net.sourceforge.eclipsejetty.JettyPluginUtils;
import net.sourceforge.eclipsejetty.launch.util.JettyLaunchConfigurationAdapter;
import net.sourceforge.eclipsejetty.launch.util.JettyLaunchUtils;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.FileEditorInput;

public class JettyLaunchShortcut implements ILaunchShortcut
{

    private static final String LAUNCH_CONFIGURATION_TYPE = "net.sourceforge.eclipsejetty.launchConfigurationType";

    public void launch(ISelection selection, String mode)
    {
        if (selection instanceof TreeSelection)
        {
            Object element = ((TreeSelection) selection).getFirstElement();

            if (element instanceof IResource)
            {
                launch((IResource) element, mode);
            }
            else if (element instanceof IJavaElement)
            {
                launch(((IJavaElement) element).getResource(), mode);
            }
            else
            {
                JettyPlugin.warning("Unsupported launch selection first element: " + element.getClass());
                return;
            }
        }
        else
        {
            JettyPlugin.warning("Unsupported launch selection: " + selection.getClass());
            return;
        }
    }

    public void launch(IEditorPart editor, String mode)
    {
        FileEditorInput fileEditorInput = (FileEditorInput) editor.getEditorInput().getAdapter(FileEditorInput.class);

        if (fileEditorInput == null)
        {
            JettyPlugin.warning("Cannot determine editor input");
            return;
        }

        if (fileEditorInput.getFile() == null)
        {
            JettyPlugin.warning("Cannot determine file of editor input");
            return;
        }

        launch(fileEditorInput.getFile(), mode);
    }

    protected void launch(IResource resource, String mode)
    {
        IResource webXMLResource = null;

        try
        {
            webXMLResource = JettyLaunchUtils.findWebXML(resource);
        }
        catch (CoreException e)
        {
            // ignore
        }

        launch(resource.getProject(), webXMLResource, mode);
    }

    protected void launch(final IProject project, IResource webXMLResource, String mode)
    {
        File webAppPath;
        
        if (webXMLResource == null)
        {
            try
            {
                webXMLResource = JettyLaunchUtils.findWebXML(webXMLResource);
            }
            catch (CoreException e)
            {
                // ignore
            }
        }

        if (webXMLResource == null)
        {
            try
            {
                webXMLResource = JettyLaunchUtils.findWebXML(project);
            }
            catch (CoreException e)
            {
                // ignore
            }
        }

        if (webXMLResource == null)
        {
            Display.getCurrent().syncExec(new Runnable()
            {
                public void run()
                {
                    MessageDialog.openError(Display.getCurrent().getActiveShell(), "WebApp Directory not found",
                        "Could not to find the file \"WEB-INF/web.xml\" in project " + project.getName()
                            + ".\n\nPlease locate the WebApp Directory manually.");
                }
            });

            String path =
                chooseWorkspaceDirectory(
                    Display.getCurrent().getActiveShell(),
                    JettyPluginUtils.getProject(project.getName()),
                    "WebApp Folder",
                    "Select your web application root folder. That't the one,\nthat contains the WEB-INF directory with the web.xml.",
                    null);

            if (path == null)
            {
                return;
            }

            webAppPath = JettyPluginUtils.resolveFolder(project, path);
        }
        else {
            IPath webAppResource = webXMLResource.getFullPath().removeLastSegments(2);
            
            webAppPath = JettyPluginUtils.resolveFolder(project, webAppResource.toString());
        }

        if (webAppPath == null)
        {
            JettyPlugin.warning("Could not find WebApp path");
            return;
        }

        webAppPath = webAppPath.getAbsoluteFile();

        ILaunchConfiguration existingLaunchConfiguration = findLaunchConfiguration(project, webAppPath);

        if (existingLaunchConfiguration != null)
        {
            DebugUITools.launch(existingLaunchConfiguration, mode);
            return;
        }

        ILaunchConfigurationWorkingCopy createdLaunchConfiguration = createLaunchConfiguration(project, webAppPath);

        if (createdLaunchConfiguration != null)
        {
            DebugUITools.launch(createdLaunchConfiguration, mode);
            return;
        }
    }

    protected ILaunchConfigurationWorkingCopy createLaunchConfiguration(IProject project, File webAppPath)
    {
        try
        {
            ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
            ILaunchConfigurationType launchConfigurationType =
                launchManager.getLaunchConfigurationType(LAUNCH_CONFIGURATION_TYPE);

            String name = JettyLaunchUtils.generateLaunchConfigurationName(project);
            ILaunchConfigurationWorkingCopy configuration = launchConfigurationType.newInstance(null, name);
            JettyLaunchConfigurationAdapter adapter = JettyLaunchConfigurationAdapter.getInstance(configuration);

            adapter.initialize(project, webAppPath);

            configuration.setMappedResources(new IResource[]{project});
            configuration.doSave();

            return configuration;
        }
        catch (CoreException e)
        {
            JettyPlugin.error("Failed to create and launch configuration", e);
        }

        return null;
    }

    protected ILaunchConfiguration findLaunchConfiguration(IProject project, File webAppPath)
    {
        String projectName = project.getName();

        try
        {
            ILaunchConfiguration[] configurations =
                DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations();

            for (ILaunchConfiguration configuration : configurations)
            {
                ILaunchConfigurationType type = configuration.getType();
                JettyLaunchConfigurationAdapter adapter = JettyLaunchConfigurationAdapter.getInstance(configuration);

                if (LAUNCH_CONFIGURATION_TYPE.equals(type.getIdentifier()))
                {
                    if (!projectName.equals(adapter.getProjectName()))
                    {
                        continue;
                    }

                    File currentWebAppPath = adapter.getWebAppPath();

                    if (currentWebAppPath == null)
                    {
                        continue;
                    }

                    currentWebAppPath = currentWebAppPath.getAbsoluteFile();

                    if (!webAppPath.equals(currentWebAppPath))
                    {
                        continue;
                    }

                    return configuration;
                }
            }
        }
        catch (CoreException e)
        {
            // ignore
        }

        return null;
    }

}
