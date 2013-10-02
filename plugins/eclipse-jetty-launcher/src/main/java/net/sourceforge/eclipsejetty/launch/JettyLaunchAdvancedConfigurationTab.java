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

import java.io.File;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import net.sourceforge.eclipsejetty.JettyPlugin;
import net.sourceforge.eclipsejetty.JettyPluginConstants;
import net.sourceforge.eclipsejetty.JettyPluginUtils;
import net.sourceforge.eclipsejetty.jetty.JettyConfig;
import net.sourceforge.eclipsejetty.jetty.JettyConfigType;
import net.sourceforge.eclipsejetty.jetty.JettyVersion;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchDelegate;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;

/**
 * UI
 * 
 * @author Christian K&ouml;berl
 * @author Manfred Hantschel
 */
public class JettyLaunchAdvancedConfigurationTab extends AbstractJettyLaunchConfigurationTab
{
    private final ModifyDialogListener modifyDialogListener;
    private final JettyLaunchConfigEntryList configEntryList;

    private Composite tabComposite;
    private Button embeddedButton;
    private Button externButton;
    private Text pathText;
    private Button pathVariablesButton;
    private Button pathBrowseButton;

    private Button jspSupportButton;
    private Button jmxSupportButton;
    private Button jndiSupportButton;
    private Button ajpSupportButton;
    private Spinner ajpPortSpinner;

    private Button threadPoolLimitEnabledButton;
    private Spinner threadPoolLimitCountSpinner;
    private Button acceptorLimitEnabledButton;
    private Spinner acceptorLimitCountSpinner;
    private Button customWebDefaultsEnabledButton;
    private Text customWebDefaultsResourceText;
    private Button customWebDefaultsWorkspaceButton;
    private Button customWebDefaultsFileSystemButton;
    private Button customWebDefaultsVariablesButton;

    private Table configTable;
    private boolean configTableFormatted = false;
    private Button openConfigButton;
    private Button editConfigButton;
    private Button removeConfigButton;
    private Button moveUpConfigButton;
    private Button moveDownConfigButton;

    private Button showLauncherInfoButton;
    private Button consoleEnabledButton;

    public JettyLaunchAdvancedConfigurationTab()
    {
        modifyDialogListener = new ModifyDialogListener();
        configEntryList = new JettyLaunchConfigEntryList(modifyDialogListener);
    }

    /**
     * @wbp.parser.entryPoint
     */
    public void createControl(final Composite parent)
    {
        tabComposite = new Composite(parent, SWT.NONE);
        tabComposite.setLayout(new GridLayout(2, true));

        final Group jettyGroup = new Group(tabComposite, SWT.NONE);
        jettyGroup.setLayout(new GridLayout(4, false));
        jettyGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        jettyGroup.setText("Jetty:");

        embeddedButton =
            createButton(jettyGroup, SWT.RADIO, "Use Jetty provided by launcher plugin (Jetty 8.x)", -1, 4, 1,
                modifyDialogListener);
        externButton = createButton(jettyGroup, SWT.RADIO, "Use Jetty at path:", 128, 1, 1, modifyDialogListener);
        pathText = createText(jettyGroup, SWT.BORDER, -1, -1, 3, 1, modifyDialogListener);

        createLabel(jettyGroup, "", -1, 2, 1);
        pathVariablesButton = createButton(jettyGroup, SWT.NONE, "Variables...", 96, 1, 1, new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                chooseVariable(getShell(), pathText);
            }
        });
        pathBrowseButton = createButton(jettyGroup, SWT.NONE, "External...", 96, 1, 1, new SelectionAdapter()
        {
            @Override
            public void widgetSelected(final SelectionEvent e)
            {
                String path =
                    chooseExternalDirectory(
                        getShell(),
                        "Select Jetty Home Directory",
                        "Choose the installation directory of your Jetty. Currenty, the versions 6 to 9 are supported.",
                        pathText.getText());

                if (path != null)
                {
                    pathText.setText(path);
                }
            }
        });

        final Group jettyFeatureGroup = new Group(tabComposite, SWT.NONE);
        jettyFeatureGroup.setLayout(new GridLayout(1, false));
        jettyFeatureGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        jettyFeatureGroup.setText("Jetty Features:");

        jspSupportButton =
            createButton(jettyFeatureGroup, SWT.CHECK, "Enable JSP Support", -1, 1, 1, modifyDialogListener);
        jndiSupportButton =
            createButton(jettyFeatureGroup, SWT.CHECK, "Enable JNDI Support", -1, 1, 1, modifyDialogListener);
        jmxSupportButton =
            createButton(jettyFeatureGroup, SWT.CHECK, "Enable JMX Support", -1, 1, 1, modifyDialogListener);

        final Group pluginFeatureGroup = new Group(tabComposite, SWT.NONE);
        pluginFeatureGroup.setLayout(new GridLayout(1, false));
        pluginFeatureGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        pluginFeatureGroup.setText("Plugin Features:");

        showLauncherInfoButton =
            createButton(pluginFeatureGroup, SWT.CHECK, "Enable Jetty Launch Info", 224, 1, 1, modifyDialogListener);
        consoleEnabledButton =
            createButton(pluginFeatureGroup, SWT.CHECK, "Enable Jetty Plugin Console", 224, 1, 1, modifyDialogListener);

        final Group configGroup = new Group(tabComposite, SWT.NONE);
        configGroup.setLayout(new GridLayout(2, false));
        configGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        configGroup.setText("Jetty Configuration:");

        threadPoolLimitEnabledButton =
            createButton(configGroup, SWT.CHECK, "Limit Size of Thread Pool:", 224, 1, 1, modifyDialogListener);
        threadPoolLimitCountSpinner = createSpinner(configGroup, SWT.BORDER, 32, -1, 1, 1, modifyDialogListener);
        threadPoolLimitCountSpinner.setMinimum(8);
        threadPoolLimitCountSpinner.setMaximum(128);
        threadPoolLimitCountSpinner.setIncrement(1);
        threadPoolLimitCountSpinner.setPageIncrement(8);

        acceptorLimitEnabledButton =
            createButton(configGroup, SWT.CHECK, "Limit Number of Acceptors:", 224, 1, 1, modifyDialogListener);
        acceptorLimitCountSpinner = createSpinner(configGroup, SWT.BORDER, 32, -1, 1, 1, modifyDialogListener);
        acceptorLimitCountSpinner.setMinimum(2);
        acceptorLimitCountSpinner.setMaximum(64);
        acceptorLimitCountSpinner.setIncrement(1);
        acceptorLimitCountSpinner.setPageIncrement(8);

        ajpSupportButton =
            createButton(configGroup, SWT.CHECK, "Enable AJP Connector on Port:", 224, 1, 1, modifyDialogListener);
        // TODO enable when implemented
        ajpSupportButton.setEnabled(false);
        ajpPortSpinner = createSpinner(configGroup, SWT.BORDER, 32, -1, 1, 1, modifyDialogListener);
        ajpPortSpinner.setMinimum(0);
        ajpPortSpinner.setMaximum(65535);
        ajpPortSpinner.setIncrement(1);
        ajpPortSpinner.setPageIncrement(1000);
        // TODO enable when implemented
        ajpPortSpinner.setEnabled(false);

        customWebDefaultsEnabledButton =
            createButton(configGroup, SWT.CHECK, "Custom Web Defaults XML:", 224, 1, 1, modifyDialogListener);
        customWebDefaultsResourceText = createText(configGroup, SWT.BORDER, -1, -1, 1, 1, modifyDialogListener);

        Composite customWebDefaultsButtons = createComposite(configGroup, SWT.NONE, 4, -1, false, 2, 1);
        createLabel(customWebDefaultsButtons, "", -1, 1, 1);
        customWebDefaultsWorkspaceButton =
            createButton(customWebDefaultsButtons, SWT.NONE, "Workspace...", 96, 1, 1, new SelectionAdapter()
            {
                @Override
                public void widgetSelected(final SelectionEvent e)
                {
                    String path =
                        chooseWorkspaceFile(JettyPluginConstants.getProject(getCurrentLaunchConfiguration()),
                            getShell(), "Resource Selection", "Select a resource as Jetty Context file:",
                            customWebDefaultsResourceText.getText());

                    if (path != null)
                    {
                        customWebDefaultsResourceText.setText(path);
                    }
                }
            });
        customWebDefaultsFileSystemButton =
            createButton(customWebDefaultsButtons, SWT.NONE, "File System...", 96, 1, 1, new SelectionAdapter()
            {
                @Override
                public void widgetSelected(final SelectionEvent e)
                {
                    String path =
                        chooseExternalFile(getShell(), customWebDefaultsResourceText.getText(),
                            "Select Custom Web Defaults XML", "*.xml", "*.*");

                    if (path != null)
                    {
                        customWebDefaultsResourceText.setText(path);
                    }
                }
            });
        customWebDefaultsVariablesButton =
            createButton(customWebDefaultsButtons, SWT.NONE, "Variables...", 96, 1, 1, new SelectionAdapter()
            {
                @Override
                public void widgetSelected(SelectionEvent e)
                {
                    chooseVariable(getShell(), customWebDefaultsResourceText);
                }
            });

        Group contextGroup = new Group(tabComposite, SWT.NONE);
        contextGroup.setLayout(new GridLayout(6, false));
        contextGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        contextGroup.setText("Jetty Context Configuration:");

        configTable =
            createTable(contextGroup, SWT.BORDER | SWT.FULL_SELECTION, -1, 64, 5, 3, "Include", "Jetty Context File");
        configTable.addSelectionListener(new SelectionAdapter()
        {

            @Override
            public void widgetSelected(SelectionEvent e)
            {
                updateConfigButtonState();
            }

        });

        moveUpConfigButton = createButton(contextGroup, SWT.NONE, "Up", 128, 1, 1, new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                moveUpConfig();
            }
        });
        moveDownConfigButton = createButton(contextGroup, SWT.NONE, "Down", 128, 1, 2, new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                moveDownConfig();
            }
        });

        openConfigButton = createButton(contextGroup, SWT.NONE, "Open...", 128, 1, 1, new SelectionAdapter()
        {

            @Override
            public void widgetSelected(SelectionEvent e)
            {
                openConfig();
            }

        });

        createLabel(contextGroup, "", -1, 1, 1);
        createButton(contextGroup, SWT.NONE, "Add...", 128, 1, 1, new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                addConig();
            }
        });
        createButton(contextGroup, SWT.NONE, "Add External...", 128, 1, 1, new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                addExternalConfig();
            }
        });

        editConfigButton = createButton(contextGroup, SWT.NONE, "Edit...", 128, 1, 1, new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                editConfig();
            }
        });

        removeConfigButton = createButton(contextGroup, SWT.NONE, "Remove", 128, 1, 1, new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                removeConfig();
            }
        });

        setControl(tabComposite);
    }

    public String getName()
    {
        return "Options";
    }

    @Override
    public Image getImage()
    {
        return JettyPlugin.getJettyAdvancedIcon();
    }

    @Override
    public String getMessage()
    {
        return "Configure advanced settings of Jetty.";
    }

    @Override
    public void initializeFrom(final ILaunchConfiguration configuration)
    {
        super.initializeFrom(configuration);

        try
        {
            embeddedButton.setSelection(JettyPluginConstants.isEmbedded(configuration));
            externButton.setSelection(!JettyPluginConstants.isEmbedded(configuration));
            pathText.setText(JettyPluginConstants.getPathString(configuration));

            jspSupportButton.setSelection(JettyPluginConstants.isJspSupport(configuration));
            jmxSupportButton.setSelection(JettyPluginConstants.isJmxSupport(configuration));
            jndiSupportButton.setSelection(JettyPluginConstants.isJndiSupport(configuration));
            ajpSupportButton.setSelection(JettyPluginConstants.isAjpSupport(configuration));

            threadPoolLimitEnabledButton.setSelection(JettyPluginConstants.isThreadPoolLimitEnabled(configuration));
            threadPoolLimitCountSpinner.setSelection(JettyPluginConstants.getThreadPoolLimitCount(configuration));
            acceptorLimitEnabledButton.setSelection(JettyPluginConstants.isAcceptorLimitEnabled(configuration));
            acceptorLimitCountSpinner.setSelection(JettyPluginConstants.getAcceptorLimitCount(configuration));

            customWebDefaultsEnabledButton.setSelection(JettyPluginConstants.isCustomWebDefaultsEnabled(configuration));
            customWebDefaultsResourceText.setText(JettyPluginConstants.getCustomWebDefaultsResource(configuration));

            updateTable(configuration, true);
            updateConfigButtonState();

            showLauncherInfoButton.setSelection(JettyPluginConstants.isShowLauncherInfo(configuration));
            consoleEnabledButton.setSelection(JettyPluginConstants.isConsoleEnabled(configuration));
        }
        catch (final CoreException e)
        {
            JettyPlugin.error("Failed to initialize advanced configuration tab", e);
        }
    }

    public void setDefaults(final ILaunchConfigurationWorkingCopy configuration)
    {
        // intentionally left blank
    }

    public void performApply(final ILaunchConfigurationWorkingCopy configuration)
    {
        JettyPluginConstants.updateConfigVersion(configuration);

        boolean embedded = embeddedButton.getSelection();

        JettyPluginConstants.setEmbedded(configuration, embedded);

        String jettyPath = pathText.getText().trim();

        JettyPluginConstants.setPathString(configuration, jettyPath);

        try
        {
            JettyVersion jettyVersion =
                JettyPluginUtils.detectJettyVersion(embedded, JettyPluginUtils.resolveVariables(jettyPath));

            JettyPluginConstants.setMainTypeName(configuration, jettyVersion);
            JettyPluginConstants.setVersion(configuration, jettyVersion);
        }
        catch (IllegalArgumentException e)
        {
            // failed to detect
        }

        JettyPluginConstants.setJspSupport(configuration, jspSupportButton.getSelection());
        JettyPluginConstants.setJmxSupport(configuration, jmxSupportButton.getSelection());
        JettyPluginConstants.setJndiSupport(configuration, jndiSupportButton.getSelection());
        JettyPluginConstants.setAjpSupport(configuration, ajpSupportButton.getSelection());

        JettyPluginConstants.setThreadPoolLimitEnabled(configuration, threadPoolLimitEnabledButton.getSelection());
        JettyPluginConstants.setThreadPoolLimitCount(configuration, threadPoolLimitCountSpinner.getSelection());
        JettyPluginConstants.setAcceptorLimitEnabled(configuration, acceptorLimitEnabledButton.getSelection());
        JettyPluginConstants.setAcceptorLimitCount(configuration, acceptorLimitCountSpinner.getSelection());

        JettyPluginConstants.setShowLauncherInfo(configuration, showLauncherInfoButton.getSelection());
        JettyPluginConstants.setConsoleEnabled(configuration, consoleEnabledButton.getSelection());

        JettyPluginConstants.setCustomWebDefaultsEnabled(configuration, customWebDefaultsEnabledButton.getSelection());
        JettyPluginConstants.setCustomWebDefaultsResource(configuration, customWebDefaultsResourceText.getText());

        try
        {
            JettyPluginConstants.setConfigs(configuration, configEntryList.getConfigs());
        }
        catch (CoreException e)
        {
            JettyPlugin.error("Failed to perform apply in advanced configuration tab", e);
        }

        JettyPluginConstants.setClasspathProvider(configuration, JettyPluginConstants.CLASSPATH_PROVIDER_JETTY);

        updateTable(configuration, false);
        updateConfigButtonState();
    }

    @Override
    public boolean isValid(final ILaunchConfiguration configuration)
    {
        setErrorMessage(null);
        setMessage(null);

        boolean embedded = embeddedButton.getSelection();

        pathText.setEnabled(!embedded);
        pathVariablesButton.setEnabled(!embedded);
        pathBrowseButton.setEnabled(!embedded);

        boolean threadPoolLimitEnabled = threadPoolLimitEnabledButton.getSelection();

        threadPoolLimitCountSpinner.setEnabled(threadPoolLimitEnabled);

        boolean acceptorLimitEnabled = acceptorLimitEnabledButton.getSelection();

        acceptorLimitCountSpinner.setEnabled(acceptorLimitEnabled);

        if (acceptorLimitEnabled)
        {
            int minimum = Math.max(8, acceptorLimitCountSpinner.getSelection() * 2);

            threadPoolLimitCountSpinner.setMinimum(minimum);

            if (threadPoolLimitCountSpinner.getSelection() < minimum)
            {
                threadPoolLimitCountSpinner.setSelection(minimum);
            }
        }

        boolean customWebDefaultsEnabled = customWebDefaultsEnabledButton.getSelection();

        customWebDefaultsResourceText.setEnabled(customWebDefaultsEnabled);
        customWebDefaultsVariablesButton.setEnabled(customWebDefaultsEnabled);
        customWebDefaultsFileSystemButton.setEnabled(customWebDefaultsEnabled);
        customWebDefaultsWorkspaceButton.setEnabled(customWebDefaultsEnabled);

        if (!embedded)
        {
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

            try
            {
                JettyPluginUtils.detectJettyVersion(embedded, jettyPath);
            }
            catch (final IllegalArgumentException e)
            {
                setErrorMessage("Failed to find and detect Jetty version at path \"" + jettyPath + "\"");
                return false;
            }
        }

        if (customWebDefaultsEnabled)
        {
            String customWebDefaultsPath =
                JettyPluginUtils.resolveVariables(customWebDefaultsResourceText.getText()).trim();

            if (customWebDefaultsPath.length() > 0)
            {
                File file =
                    JettyPluginUtils.resolveFile(JettyPluginConstants.getProject(configuration), customWebDefaultsPath);

                if ((file == null) || (!file.exists()))
                {
                    setErrorMessage(String.format("The custom web defaults XML %s does not exist.",
                        customWebDefaultsPath));

                    return false;
                }
            }
            else
            {
                setErrorMessage(String.format("The custom web defaults XML is missing."));
            }
        }

        List<JettyConfig> contexts = configEntryList.getConfigs();

        for (JettyConfig context : contexts)
        {
            if ((context.isActive()) && (!context.isValid(ResourcesPlugin.getWorkspace())))
            {
                setErrorMessage(MessageFormat.format("The Jetty context file {0} does not exist.", context.getPath()));
            }
        }

        setDirty(true);

        return true;
    }

    private void updateTable(ILaunchConfiguration configuration, boolean updateType)
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
            JettyPlugin.error("Failed to update table in configuration tab", e);
        }
    }

    protected String chooseConfig(String path)
    {
        return chooseWorkspaceFile(JettyPluginConstants.getProject(getCurrentLaunchConfiguration()), getShell(),
            "Resource Selection", "Select a resource as Jetty Context file:", path);
    }

    protected String chooseConfigFromFileSystem(String path)
    {
        return chooseExternalFile(getShell(), path, "Select Jetty Context File", "*.xml", "*.*");
    }

    public void updateConfigButtonState()
    {
        int index = configTable.getSelectionIndex();
        JettyLaunchConfigEntry entry = (index >= 0) ? configEntryList.get(index) : null;
        JettyConfigType type = (entry != null) ? entry.getType() : null;

        openConfigButton.setEnabled(configTable.getSelectionIndex() >= 0);
        editConfigButton.setEnabled((type == JettyConfigType.PATH) || (type == JettyConfigType.WORKSPACE));
        moveUpConfigButton.setEnabled(index > 0);
        moveDownConfigButton.setEnabled((index >= 0) && (index < (configTable.getItemCount() - 1)));
        removeConfigButton.setEnabled((type == JettyConfigType.PATH) || (type == JettyConfigType.WORKSPACE));
    }

    private void moveUpConfig()
    {
        int index = configTable.getSelectionIndex();

        if (index > 0)
        {
            configEntryList.exchange(configTable, index - 1);
            configTable.setSelection(index - 1);
            updateLaunchConfigurationDialog();
        }
    }

    private void moveDownConfig()
    {
        int index = configTable.getSelectionIndex();

        if ((index >= 0) && (index < (configTable.getItemCount() - 1)))
        {
            configEntryList.exchange(configTable, index);
            configTable.setSelection(index + 1);
            updateLaunchConfigurationDialog();
        }
    }

    private void addConig()
    {
        String path = chooseConfig(null);

        if (path != null)
        {
            configEntryList.add(configTable, new JettyLaunchConfigEntry(new JettyConfig(path,
                JettyConfigType.WORKSPACE, true)));
            updateLaunchConfigurationDialog();
        }
    }

    private void addExternalConfig()
    {
        String path = chooseConfigFromFileSystem(null);

        if (path != null)
        {
            configEntryList.add(configTable, new JettyLaunchConfigEntry(new JettyConfig(path, JettyConfigType.PATH,
                true)));
            updateLaunchConfigurationDialog();
        }
    }

    private void openConfig()
    {
        int index = configTable.getSelectionIndex();

        if (index >= 0)
        {
            JettyLaunchConfigEntry entry = configEntryList.get(index);
            IWorkbench workbench = PlatformUI.getWorkbench();
            IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
            IWorkbenchPage page = window.getActivePage();
            IEditorInput input = null;
            IEditorDescriptor descriptor = null;

            switch (entry.getType())
            {
                case DEFAULT:
                    try
                    {
                        ILaunchConfiguration configuration = getCurrentLaunchConfiguration();
                        ILaunchDelegate[] delegates =
                            configuration.getType().getDelegates(new HashSet<String>(Arrays.asList("run")));

                        if (delegates.length == 1)
                        {
                            JettyLaunchConfigurationDelegate delegate =
                                (JettyLaunchConfigurationDelegate) delegates[0].getDelegate();

                            File file = delegate.createJettyConfigurationFile(configuration, true);
                            descriptor = workbench.getEditorRegistry().getDefaultEditor(file.getName());
                            input = new FileStoreEditorInput(EFS.getLocalFileSystem().fromLocalFile(file));
                        }
                    }
                    catch (CoreException ex)
                    {
                        JettyPlugin.error("Failed to create default context file", ex);
                    }

                    break;

                case PATH:
                {
                    File file = new File(entry.getPath());
                    descriptor = workbench.getEditorRegistry().getDefaultEditor(file.getName());
                    input = new FileStoreEditorInput(EFS.getLocalFileSystem().fromLocalFile(file));
                }
                    break;

                case WORKSPACE:
                {
                    IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(entry.getPath()));
                    descriptor = workbench.getEditorRegistry().getDefaultEditor(file.getName());
                    input = new FileEditorInput(file);
                }
                    break;
            }

            if (descriptor != null)
            {
                try
                {
                    IDE.openEditor(page, input, descriptor.getId());
                }
                catch (PartInitException ex)
                {
                    JettyPlugin.error("Failed to open", ex);
                }
            }
        }
    }

    private void editConfig()
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

    private void removeConfig()
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
