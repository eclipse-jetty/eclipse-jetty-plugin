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

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import net.sourceforge.eclipsejetty.JettyPlugin;
import net.sourceforge.eclipsejetty.JettyPluginUtils;
import net.sourceforge.eclipsejetty.jetty.JettyConfig;
import net.sourceforge.eclipsejetty.jetty.JettyConfigType;
import net.sourceforge.eclipsejetty.jetty.JettyVersion;
import net.sourceforge.eclipsejetty.launch.util.JettyLaunchConfigurationAdapter;
import net.sourceforge.eclipsejetty.launch.util.JettyLaunchConfigurationDelegate;

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
    
    private Button serverCacheDisabledButton;
    private Button clientCacheDisabledButton;

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
            createButton(jettyGroup, SWT.RADIO, "Use Jetty provided by launcher plugin (Jetty 8.x)",
                "The Eclipse Jetty Plugin comes with a tested default Jetty. Use it!", -1, 4, 1, modifyDialogListener);
        externButton =
            createButton(jettyGroup, SWT.RADIO, "Use Jetty at path:",
                "Specify a path containing a default Jetty installation.", 128, 1, 1, modifyDialogListener);
        pathText =
            createText(jettyGroup, SWT.BORDER, "The path to your Jetty installation folder.", -1, -1, 3, 1,
                modifyDialogListener);

        createLabel(jettyGroup, JettyPluginUtils.BLANK, -1, 2, 1);
        pathVariablesButton =
            createButton(jettyGroup, SWT.NONE, "Variables...", "Add variables to the path.", 96, 1, 1,
                new SelectionAdapter()
                {
                    @Override
                    public void widgetSelected(SelectionEvent e)
                    {
                        chooseVariable(getShell(), pathText);
                    }
                });
        pathBrowseButton =
            createButton(jettyGroup, SWT.NONE, "External...", "Search for a Jetty installation in your file system.",
                96, 1, 1, new SelectionAdapter()
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
            createButton(jettyFeatureGroup, SWT.CHECK, "Enable JSP Support", "Enable support and compilation of JSPs.",
                -1, 1, 1, modifyDialogListener);
        jndiSupportButton =
            createButton(jettyFeatureGroup, SWT.CHECK, "Enable JNDI Support",
                "Enable JNDI support. You will need to specify a context file, too.", -1, 1, 1, modifyDialogListener);
        jmxSupportButton =
            createButton(jettyFeatureGroup, SWT.CHECK, "Enable JMX Support",
                "Add JMX beans and enable JMX support for the JVM.", -1, 1, 1, modifyDialogListener);

        final Group pluginFeatureGroup = new Group(tabComposite, SWT.NONE);
        pluginFeatureGroup.setLayout(new GridLayout(1, false));
        pluginFeatureGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        pluginFeatureGroup.setText("Plugin Features:");

        showLauncherInfoButton =
            createButton(pluginFeatureGroup, SWT.CHECK, "Enable Jetty Launch Info",
                "Show the most important configuration options and the classpath before starting Jetty.", 224, 1, 1,
                modifyDialogListener);
        consoleEnabledButton =
            createButton(pluginFeatureGroup, SWT.CHECK, "Enable Jetty Plugin Console",
                "Enable the powerful console. Type \"help\" in your Eclipse console.", 224, 1, 1, modifyDialogListener);

        final Group configGroup = new Group(tabComposite, SWT.NONE);
        configGroup.setLayout(new GridLayout(2, false));
        configGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        configGroup.setText("Jetty Configuration:");

        threadPoolLimitEnabledButton =
            createButton(configGroup, SWT.CHECK, "Limit Size of Thread Pool:",
                "Limit the size of Jetty's thread pool. Be aware that this includes waiting connections.", 224, 1, 1,
                modifyDialogListener);
        threadPoolLimitCountSpinner =
            createSpinner(configGroup, SWT.BORDER,
                "The size of Jetty's thread pool. Be aware that this includes waiting connections.", 32, -1, 1, 1,
                modifyDialogListener);
        threadPoolLimitCountSpinner.setMinimum(8);
        threadPoolLimitCountSpinner.setMaximum(128);
        threadPoolLimitCountSpinner.setIncrement(1);
        threadPoolLimitCountSpinner.setPageIncrement(8);

        acceptorLimitEnabledButton =
            createButton(configGroup, SWT.CHECK, "Limit Number of Acceptors:",
                "Limit the number of Jetty's acceptors, that simultaniously handle requests.", 224, 1, 1,
                modifyDialogListener);
        acceptorLimitCountSpinner =
            createSpinner(configGroup, SWT.BORDER,
                "The number of Jetty's acceptors, that simultaniously handle requests.", 32, -1, 1, 1,
                modifyDialogListener);
        acceptorLimitCountSpinner.setMinimum(2);
        acceptorLimitCountSpinner.setMaximum(64);
        acceptorLimitCountSpinner.setIncrement(1);
        acceptorLimitCountSpinner.setPageIncrement(8);

        ajpSupportButton =
            createButton(configGroup, SWT.CHECK, "Enable AJP Connector on Port:",
                "Enable an AJP connector on the specified port for your HTTP server.", 224, 1, 1, modifyDialogListener);
        // TODO enable when implemented
        ajpSupportButton.setEnabled(false);
        ajpPortSpinner =
            createSpinner(configGroup, SWT.BORDER, "The port for the AJP connector.", 32, -1, 1, 1,
                modifyDialogListener);
        ajpPortSpinner.setMinimum(0);
        ajpPortSpinner.setMaximum(65535);
        ajpPortSpinner.setIncrement(1);
        ajpPortSpinner.setPageIncrement(1000);
        // TODO enable when implemented
        ajpPortSpinner.setEnabled(false);

        customWebDefaultsEnabledButton =
            createButton(configGroup, SWT.CHECK, "Custom Web Defaults XML:",
                "Define a web.xml that is executed before the web.xml of the web application.", 224, 1, 1,
                modifyDialogListener);
        customWebDefaultsResourceText =
            createText(configGroup, SWT.BORDER,
                "The path to an web.xml that is executed before the web.xml of the web application.", -1, -1, 1, 1,
                modifyDialogListener);

        Composite customWebDefaultsButtons = createComposite(configGroup, SWT.NONE, 4, -1, false, 2, 1);
        createLabel(customWebDefaultsButtons, JettyPluginUtils.BLANK, -1, 1, 1);
        customWebDefaultsWorkspaceButton =
            createButton(customWebDefaultsButtons, SWT.NONE, "Workspace...",
                "Search the workspace for a custom web defaults XML file.", 96, 1, 1, new SelectionAdapter()
                {
                    @Override
                    public void widgetSelected(final SelectionEvent e)
                    {
                        JettyLaunchConfigurationAdapter adapter =
                            JettyLaunchConfigurationAdapter.getInstance(getCurrentLaunchConfiguration());

                        String path =
                            chooseWorkspaceFile(adapter.getProject(), getShell(), "Resource Selection",
                                "Select a resource as Jetty Context file:", customWebDefaultsResourceText.getText());

                        if (path != null)
                        {
                            customWebDefaultsResourceText.setText(path);
                        }
                    }
                });
        customWebDefaultsFileSystemButton =
            createButton(customWebDefaultsButtons, SWT.NONE, "File System...",
                "Search the file system for a custom web defaults XML file.", 96, 1, 1, new SelectionAdapter()
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
            createButton(customWebDefaultsButtons, SWT.NONE, "Variables...",
                "Add variables to the path of the custom web defaults XML file.", 96, 1, 1, new SelectionAdapter()
                {
                    @Override
                    public void widgetSelected(SelectionEvent e)
                    {
                        chooseVariable(getShell(), customWebDefaultsResourceText);
                    }
                });

        serverCacheDisabledButton =
            createButton(configGroup, SWT.CHECK, "Disable Server Cache",
                "Disables Jetty's server cache for static resources.", 224, 2, 1, modifyDialogListener);

        clientCacheDisabledButton =
            createButton(configGroup, SWT.CHECK, "Disable Client Cache",
                "If disabled, Jetty sends a \"Cache-Control: max-age=0\" for each servlet request.", 224, 2, 1, modifyDialogListener);

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

        moveUpConfigButton =
            createButton(contextGroup, SWT.NONE, "Up", "Move the selected configuration up.", 128, 1, 1,
                new SelectionAdapter()
                {
                    @Override
                    public void widgetSelected(SelectionEvent e)
                    {
                        moveUpConfig();
                    }
                });
        moveDownConfigButton =
            createButton(contextGroup, SWT.NONE, "Down", "Move the selected configuration down.", 128, 1, 2,
                new SelectionAdapter()
                {
                    @Override
                    public void widgetSelected(SelectionEvent e)
                    {
                        moveDownConfig();
                    }
                });

        openConfigButton =
            createButton(contextGroup, SWT.NONE, "Open...", "Open the selected configuration in an editor.", 128, 1, 1,
                new SelectionAdapter()
                {

                    @Override
                    public void widgetSelected(SelectionEvent e)
                    {
                        openConfig();
                    }

                });

        createLabel(contextGroup, JettyPluginUtils.BLANK, -1, 1, 1);
        createButton(contextGroup, SWT.NONE, "Add...", "Add a Jetty configuration XML file from the workspace.", 128,
            1, 1, new SelectionAdapter()
            {
                @Override
                public void widgetSelected(SelectionEvent e)
                {
                    addConig();
                }
            });
        createButton(contextGroup, SWT.NONE, "Add External...",
            "Add a Jetty configuration XML file from the file system.", 128, 1, 1, new SelectionAdapter()
            {
                @Override
                public void widgetSelected(SelectionEvent e)
                {
                    addExternalConfig();
                }
            });

        editConfigButton =
            createButton(contextGroup, SWT.NONE, "Edit...", "Edit the path of the Jetty configuration XML file.", 128,
                1, 1, new SelectionAdapter()
                {
                    @Override
                    public void widgetSelected(SelectionEvent e)
                    {
                        editConfig();
                    }
                });

        removeConfigButton =
            createButton(contextGroup, SWT.NONE, "Remove", "Remove the Jetty configuration XML file.", 128, 1, 1,
                new SelectionAdapter()
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
            JettyLaunchConfigurationAdapter adapter = JettyLaunchConfigurationAdapter.getInstance(configuration);

            embeddedButton.setSelection(adapter.isEmbedded());
            externButton.setSelection(!adapter.isEmbedded());
            pathText.setText(adapter.getPathString());

            jspSupportButton.setSelection(adapter.isJspSupport());
            jmxSupportButton.setSelection(adapter.isJmxSupport());
            jndiSupportButton.setSelection(adapter.isJndiSupport());
            ajpSupportButton.setSelection(adapter.isAjpSupport());

            threadPoolLimitEnabledButton.setSelection(adapter.isThreadPoolLimitEnabled());
            threadPoolLimitCountSpinner.setSelection(adapter.getThreadPoolLimitCount());
            acceptorLimitEnabledButton.setSelection(adapter.isAcceptorLimitEnabled());
            acceptorLimitCountSpinner.setSelection(adapter.getAcceptorLimitCount());

            customWebDefaultsEnabledButton.setSelection(adapter.isCustomWebDefaultsEnabled());
            customWebDefaultsResourceText.setText(adapter.getCustomWebDefaultsResource());

            serverCacheDisabledButton.setSelection(!adapter.isServerCacheEnabled());
            clientCacheDisabledButton.setSelection(!adapter.isClientCacheEnabled());

            updateTable(configuration, true);
            updateConfigButtonState();

            showLauncherInfoButton.setSelection(adapter.isShowLauncherInfo());
            consoleEnabledButton.setSelection(adapter.isConsoleEnabled());
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
        JettyLaunchConfigurationAdapter adapter = JettyLaunchConfigurationAdapter.getInstance(configuration);

        try
        {
            adapter.updateConfigVersion();

            boolean embedded = embeddedButton.getSelection();

            adapter.setEmbedded(embedded);

            String jettyPath = pathText.getText().trim();

            adapter.setPathString(jettyPath);

            try
            {
                JettyVersion jettyVersion =
                    JettyPluginUtils.detectJettyVersion(embedded, JettyPluginUtils.resolveVariables(jettyPath));

                adapter.setMainTypeName(jettyVersion);
                adapter.setVersion(jettyVersion);
            }
            catch (IllegalArgumentException e)
            {
                // failed to detect
            }

            adapter.setJspSupport(jspSupportButton.getSelection());
            adapter.setJmxSupport(jmxSupportButton.getSelection());
            adapter.setJndiSupport(jndiSupportButton.getSelection());
            adapter.setAjpSupport(ajpSupportButton.getSelection());

            adapter.setThreadPoolLimitEnabled(threadPoolLimitEnabledButton.getSelection());
            adapter.setThreadPoolLimitCount(threadPoolLimitCountSpinner.getSelection());
            adapter.setAcceptorLimitEnabled(acceptorLimitEnabledButton.getSelection());
            adapter.setAcceptorLimitCount(acceptorLimitCountSpinner.getSelection());

            adapter.setShowLauncherInfo(showLauncherInfoButton.getSelection());
            adapter.setConsoleEnabled(consoleEnabledButton.getSelection());

            adapter.setCustomWebDefaultsEnabled(customWebDefaultsEnabledButton.getSelection());
            adapter.setCustomWebDefaultsResource(customWebDefaultsResourceText.getText());

            adapter.setServerCacheEnabled(!serverCacheDisabledButton.getSelection());
            adapter.setClientCacheEnabled(!clientCacheDisabledButton.getSelection());
            
            try
            {
                adapter.setConfigs(configEntryList.getConfigs());
            }
            catch (CoreException e)
            {
                JettyPlugin.error("Failed to perform apply in advanced configuration tab", e);
            }

            adapter.setClasspathProvider(JettyLaunchConfigurationAdapter.CLASSPATH_PROVIDER_JETTY);

            updateTable(configuration, false);
            updateConfigButtonState();
        }
        catch (CoreException e)
        {
            JettyPlugin.error("Failed to update configuration", e);
        }
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
        serverCacheDisabledButton.setEnabled(!customWebDefaultsEnabled);
        clientCacheDisabledButton.setEnabled(!customWebDefaultsEnabled);

        if (!embedded)
        {
            String jettyPath = JettyPluginUtils.resolveVariables(pathText.getText()).trim();

            if (jettyPath.length() > 0)
            {
                File f = new File(jettyPath);
                if (!f.exists() || !f.isDirectory())
                {
                    setErrorMessage(String.format("The path %s is not a valid directory.", jettyPath));
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
                setErrorMessage(String.format("Failed to find and detect Jetty version at path \"%s\"", jettyPath));
                return false;
            }
        }

        if (customWebDefaultsEnabled)
        {
            String customWebDefaultsPath =
                JettyPluginUtils.resolveVariables(customWebDefaultsResourceText.getText()).trim();

            if (customWebDefaultsPath.length() > 0)
            {
                JettyLaunchConfigurationAdapter adapter = JettyLaunchConfigurationAdapter.getInstance(configuration);

                File file = JettyPluginUtils.resolveFile(adapter.getProject(), customWebDefaultsPath);

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
                setErrorMessage(String.format("The Jetty context file %s does not exist.", context.getPath()));
            }
        }

        setDirty(true);

        return true;
    }

    private void updateTable(ILaunchConfiguration configuration, boolean updateType)
    {
        try
        {
            JettyLaunchConfigurationAdapter adapter = JettyLaunchConfigurationAdapter.getInstance(configuration);
            List<JettyConfig> contexts = adapter.getConfigs();

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
        JettyLaunchConfigurationAdapter adapter =
            JettyLaunchConfigurationAdapter.getInstance(getCurrentLaunchConfiguration());

        return chooseWorkspaceFile(adapter.getProject(), getShell(), "Resource Selection",
            "Select a resource as Jetty Context file:", path);
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
                        JettyLaunchConfigurationAdapter adapter =
                            JettyLaunchConfigurationAdapter.getInstance(getCurrentLaunchConfiguration());
                        ILaunchDelegate[] delegates =
                            adapter.getConfiguration().getType()
                                .getDelegates(new HashSet<String>(Arrays.asList("run")));

                        if (delegates.length == 1)
                        {
                            JettyLaunchConfigurationDelegate delegate =
                                (JettyLaunchConfigurationDelegate) delegates[0].getDelegate();

                            File file = delegate.createJettyConfigurationFile(adapter, true);
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
