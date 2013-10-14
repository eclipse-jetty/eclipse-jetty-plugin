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
package net.sourceforge.eclipsejetty.launch.shortcut;

import static net.sourceforge.eclipsejetty.launch.util.JettyLaunchUI.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.eclipsejetty.JettyPlugin;
import net.sourceforge.eclipsejetty.JettyPluginUtils;
import net.sourceforge.eclipsejetty.Messages;
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
import org.eclipse.debug.ui.ILaunchShortcut2;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.FileEditorInput;

/**
 * The "Run As..." and "Debug As..." shortcut. Basically, works on any single resource. Tries to locate the web
 * application folder within the project and creates a launch configuration.
 * 
 * @author Manfred Hantschel
 */
public class JettyLaunchShortcut implements ILaunchShortcut2
{

    private static final String LAUNCH_CONFIGURATION_TYPE = "net.sourceforge.eclipsejetty.launchConfigurationType"; //$NON-NLS-1$

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.debug.ui.ILaunchShortcut#launch(org.eclipse.jface.viewers.ISelection, java.lang.String)
     */
    public void launch(ISelection selection, String mode)
    {
        IResource resource = getResource(selection);

        if (resource == null)
        {
            return;
        }

        launch(resource, mode);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.debug.ui.ILaunchShortcut#launch(org.eclipse.ui.IEditorPart, java.lang.String)
     */
    public void launch(IEditorPart editorPart, String mode)
    {
        IResource resource = getResource(editorPart);

        if (resource == null)
        {
            return;
        }

        launch(resource, mode);
    }

    /**
     * Launches the Jetty by using an existing launch configuration or by creating a new one. Prefers to find the
     * WEB-INF/web.xml at or beneath the specified resource. If not WEB-INF/web.xml was not found there, it tries to
     * locate it within the project. If still no resource was found, it asks the user to specify one.
     * 
     * @param resource any resource of a project or the project itself
     * @param mode the launch mode (RUN or DEBUG)
     */
    protected void launch(IResource resource, String mode)
    {
        launch(resource.getProject(), resource, mode);
    }

    /**
     * Launches the Jetty by using an existing launch configuration or by creating a new one. Prefers to find the
     * WEB-INF/web.xml at or beneath the specified resource. If not WEB-INF/web.xml was not found there, it tries to
     * locate it within the project. If still no resource was found, it asks the user to specify one.
     * 
     * @param project the project
     * @param resource the resource
     * @param mode the launch mode (RUN or DEBUG)
     */
    protected void launch(final IProject project, IResource resource, String mode)
    {
        File webAppPath;

        resource = findWebXMLResource(resource);

        if (resource == null)
        {
            resource = findWebXMLResource(project);
        }

        if (resource == null)
        {
            Display.getCurrent().syncExec(new Runnable()
            {
                public void run()
                {
                    MessageDialog.openError(Display.getCurrent().getActiveShell(),
                        Messages.shortcut_webAppDirNotFoundTitle,
                        String.format(Messages.shortcut_webAppDirNotFoundMessage, project.getName()));
                }
            });

            String path =
                chooseWorkspaceDirectory(Display.getCurrent().getActiveShell(),
                    JettyPluginUtils.getProject(project.getName()), Messages.shortcut_webAppSelectTitle,
                    Messages.shortcut_webAppSelectMessage, null);

            if (path == null)
            {
                return;
            }

            webAppPath = JettyPluginUtils.resolveFolder(project, path);
        }
        else
        {
            IPath webAppResource = resource.getFullPath().removeLastSegments(2);

            webAppPath = JettyPluginUtils.resolveFolder(project, webAppResource.toString());
        }

        if (webAppPath == null)
        {
            JettyPlugin.warning(Messages.shortcut_webAppNotFound);
            return;
        }

        webAppPath = webAppPath.getAbsoluteFile();

        ILaunchConfiguration[] existingLaunchConfigurations = getLaunchConfigurations(project, webAppPath);

        if ((existingLaunchConfigurations != null) && (existingLaunchConfigurations.length > 0))
        {
            DebugUITools.launch(existingLaunchConfigurations[0], mode);
            return;
        }

        ILaunchConfigurationWorkingCopy createdLaunchConfiguration = createLaunchConfiguration(project, webAppPath);

        if (createdLaunchConfiguration != null)
        {
            DebugUITools.launch(createdLaunchConfiguration, mode);
            return;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.debug.ui.ILaunchShortcut2#getLaunchConfigurations(org.eclipse.jface.viewers.ISelection)
     */
    public ILaunchConfiguration[] getLaunchConfigurations(ISelection selection)
    {
        IResource resource = getResource(selection);

        if (resource == null)
        {
            return null;
        }

        return getLaunchConfigurations(resource);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.debug.ui.ILaunchShortcut2#getLaunchConfigurations(org.eclipse.ui.IEditorPart)
     */
    public ILaunchConfiguration[] getLaunchConfigurations(IEditorPart editorpart)
    {
        IResource resource = getResource(editorpart);

        if (resource == null)
        {
            return null;
        }

        return getLaunchConfigurations(resource);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.debug.ui.ILaunchShortcut2#getLaunchableResource(org.eclipse.jface.viewers.ISelection)
     */
    public IResource getLaunchableResource(ISelection selection)
    {
        return getResource(selection);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.debug.ui.ILaunchShortcut2#getLaunchableResource(org.eclipse.ui.IEditorPart)
     */
    public IResource getLaunchableResource(IEditorPart editorPart)
    {
        return getResource(editorPart);
    }

    /**
     * Tries to grab the resource of the selection.
     * 
     * @param selection the selection
     * @return the resource, null if failed to grab
     */
    protected IResource getResource(ISelection selection)
    {
        if (selection instanceof TreeSelection)
        {
            Object element = ((TreeSelection) selection).getFirstElement();

            if (element instanceof IResource)
            {
                return (IResource) element;
            }

            if (element instanceof IJavaElement)
            {
                return ((IJavaElement) element).getResource();
            }

            JettyPlugin.warning(String.format(Messages.shortcut_unsupportedLaunchSelectionElement, element.getClass()));

            return null;
        }

        JettyPlugin.warning(String.format(Messages.shortcut_unsupportedLaunchSelection, selection.getClass()));

        return null;
    }

    /**
     * Tries to grab the resource from the editor.
     * 
     * @param editorPart the editor part
     * @return the resource, null if failed to grab
     */
    protected IResource getResource(IEditorPart editorPart)
    {
        FileEditorInput fileEditorInput =
            (FileEditorInput) editorPart.getEditorInput().getAdapter(FileEditorInput.class);

        if (fileEditorInput == null)
        {
            JettyPlugin.warning(Messages.shortcut_invalidEditorInput);

            return null;
        }

        if (fileEditorInput.getFile() == null)
        {
            JettyPlugin.warning(Messages.shortcut_invalidEditorInputFile);

            return null;
        }

        return fileEditorInput.getFile();
    }

    /**
     * Searches for existing launch configurations. Prefers to find the WEB-INF/web.xml at or beneath the specified
     * resource. If not WEB-INF/web.xml was not found there, it tries to locate it within the project.
     * 
     * @param resource any resource of a project or the project itself
     * @return an array of existing launch configuration, empty if none was found, null if unable to create one (e.g. no
     *         WEB-INF/web.xml was found).
     */
    protected ILaunchConfiguration[] getLaunchConfigurations(IResource resource)
    {
        return getLaunchConfigurations(resource.getProject(), resource);
    }

    /**
     * Searches for existing launch configurations. Prefers to find the WEB-INF/web.xml at or beneath the specified
     * resource. If not WEB-INF/web.xml was not found there, it tries to locate it within the project.
     * 
     * @param resource the WEB-INF/web.xml file. If null, tries to locate the file first beneath the resource, then
     *            within the project.
     * @return an array of existing launch configuration, empty if none was found, null if unable to create one (e.g. no
     *         WEB-INF/web.xml was found).
     */
    protected ILaunchConfiguration[] getLaunchConfigurations(final IProject project, IResource resource)
    {
        File webAppPath;

        resource = findWebXMLResource(resource);

        if (resource == null)
        {
            resource = findWebXMLResource(project);
        }

        if (resource == null)
        {
            return null;
        }

        IPath webAppResource = resource.getFullPath().removeLastSegments(2);

        webAppPath = JettyPluginUtils.resolveFolder(project, webAppResource.toString());

        if (webAppPath == null)
        {
            return null;
        }

        webAppPath = webAppPath.getAbsoluteFile();

        return getLaunchConfigurations(project, webAppPath);
    }

    /**
     * Returns all existing launch configurations with the specified project and the specified web application path.
     * 
     * @param project the project, must no be null
     * @param webAppPath the web application path, must no be null
     * @return all existing launch configurations, never null
     */
    protected ILaunchConfiguration[] getLaunchConfigurations(IProject project, File webAppPath)
    {
        List<ILaunchConfiguration> results = new ArrayList<ILaunchConfiguration>();
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

                    results.add(configuration);
                }
            }
        }
        catch (CoreException e)
        {
            // ignore
        }

        return results.toArray(new ILaunchConfiguration[results.size()]);
    }

    /**
     * Creates a new launch configuration.
     * 
     * @param project the project, must not be null
     * @param webAppPath the web application path, must not be null
     * @return the launch configuration, null if there was an error
     */
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
            JettyPlugin.error(Messages.shortcut_createFailed, e);
        }

        return null;
    }

    /**
     * Tries to locate the web.xml file within the specified resource. Searches sub-folders if the resource points to a
     * folder.
     * 
     * @param resource the resource, may be null
     * @return the web.xml as resource, null if not found
     */
    protected IResource findWebXMLResource(IResource resource)
    {
        try
        {
            return JettyLaunchUtils.findWebXML(resource);
        }
        catch (CoreException e)
        {
            // ignore
        }

        return null;
    }

    /**
     * Tries to locate the web.xml file within the specified project.
     * 
     * @param project the project, may be null
     * @return the web.xml as resource, null if not found
     */
    protected IResource findWebXMLResource(IProject project)
    {
        try
        {
            return JettyLaunchUtils.findWebXML(project);
        }
        catch (CoreException e)
        {
            // ignore
        }

        return null;
    }

}
