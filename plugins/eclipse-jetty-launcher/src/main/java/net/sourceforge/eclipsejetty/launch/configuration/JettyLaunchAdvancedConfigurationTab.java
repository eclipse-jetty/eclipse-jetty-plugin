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
import net.sourceforge.eclipsejetty.Messages;
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
        jettyGroup.setText(Messages.advConfigTab_jettyGroupTitle);

        embeddedButton =
            createButton(jettyGroup, SWT.RADIO, Messages.advConfigTab_embeddedButton,
                Messages.advConfigTab_embeddedButtonTip, -1, 4, 1, modifyDialogListener);
        externButton =
            createButton(jettyGroup, SWT.RADIO, Messages.advConfigTab_externButton,
                Messages.advConfigTab_externButtonTip, 128, 1, 1, modifyDialogListener);
        pathText =
            createText(jettyGroup, SWT.BORDER, Messages.advConfigTab_pathTextTip, -1, -1, 3, 1, modifyDialogListener);

        createLabel(jettyGroup, JettyPluginUtils.BLANK, -1, 2, 1);
        pathVariablesButton =
            createButton(jettyGroup, SWT.NONE, Messages.advConfigTab_pathVariablesButton,
                Messages.advConfigTab_pathVariablesButtonTip, 96, 1, 1, new SelectionAdapter()
                {
                    @Override
                    public void widgetSelected(SelectionEvent e)
                    {
                        chooseVariable(getShell(), pathText);
                    }
                });
        pathBrowseButton =
            createButton(jettyGroup, SWT.NONE, Messages.advConfigTab_externalButton,
                Messages.advConfigTab_externalButtonTip, 96, 1, 1, new SelectionAdapter()
                {
                    @Override
                    public void widgetSelected(final SelectionEvent e)
                    {
                        String path =
                            chooseExternalDirectory(getShell(), Messages.advConfigTab_externalTitle,
                                Messages.advConfigTab_externalMessage, pathText.getText());

                        if (path != null)
                        {
                            pathText.setText(path);
                        }
                    }
                });

        final Group jettyFeatureGroup = new Group(tabComposite, SWT.NONE);
        jettyFeatureGroup.setLayout(new GridLayout(1, false));
        jettyFeatureGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        jettyFeatureGroup.setText(Messages.advConfigTab_jettyFeaturesGroupTitle);

        jspSupportButton =
            createButton(jettyFeatureGroup, SWT.CHECK, Messages.advConfigTab_jspSupportButton,
                Messages.advConfigTab_jspSupportButtonTip, -1, 1, 1, modifyDialogListener);
        jndiSupportButton =
            createButton(jettyFeatureGroup, SWT.CHECK, Messages.advConfigTab_jndiSupportButton,
                Messages.advConfigTab_jndiSupportButtonTip, -1, 1, 1, modifyDialogListener);
        jmxSupportButton =
            createButton(jettyFeatureGroup, SWT.CHECK, Messages.advConfigTab_jmxSupportButton,
                Messages.advConfigTab_jmxSupportButtonTip, -1, 1, 1, modifyDialogListener);

        final Group pluginFeatureGroup = new Group(tabComposite, SWT.NONE);
        pluginFeatureGroup.setLayout(new GridLayout(1, false));
        pluginFeatureGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        pluginFeatureGroup.setText(Messages.advConfigTab_pluginFeaturesGroupTitle);

        showLauncherInfoButton =
            createButton(pluginFeatureGroup, SWT.CHECK, Messages.advConfigTab_showLauncherInfoButton,
                Messages.advConfigTab_showLauncherInfoButtonTip, 224, 1, 1, modifyDialogListener);
        consoleEnabledButton =
            createButton(pluginFeatureGroup, SWT.CHECK, Messages.advConfigTab_consoleEnabledButton,
                Messages.advConfigTab_consoleEnabledButtonTip, 224, 1, 1, modifyDialogListener);

        final Group configGroup = new Group(tabComposite, SWT.NONE);
        configGroup.setLayout(new GridLayout(2, false));
        configGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        configGroup.setText(Messages.advConfigTab_jettyConfigurationGroupTitle);

        threadPoolLimitEnabledButton =
            createButton(configGroup, SWT.CHECK, Messages.advConfigTab_threadPoolLimitEnabledButton,
                Messages.advConfigTab_threadPoolLimitEnabledButtonTip, 224, 1, 1, modifyDialogListener);
        threadPoolLimitCountSpinner =
            createSpinner(configGroup, SWT.BORDER, Messages.advConfigTab_threadPoolLimitCountSpinnerTip, 32, -1, 1, 1,
                modifyDialogListener);
        threadPoolLimitCountSpinner.setMinimum(8);
        threadPoolLimitCountSpinner.setMaximum(128);
        threadPoolLimitCountSpinner.setIncrement(1);
        threadPoolLimitCountSpinner.setPageIncrement(8);

        acceptorLimitEnabledButton =
            createButton(configGroup, SWT.CHECK, Messages.advConfigTab_acceptorLimitEnabledButton,
                Messages.advConfigTab_acceptorLimitEnabledButtonTip, 224, 1, 1, modifyDialogListener);
        acceptorLimitCountSpinner =
            createSpinner(configGroup, SWT.BORDER, Messages.advConfigTab_acceptorLimitCountSpinnerTip, 32, -1, 1, 1,
                modifyDialogListener);
        acceptorLimitCountSpinner.setMinimum(2);
        acceptorLimitCountSpinner.setMaximum(64);
        acceptorLimitCountSpinner.setIncrement(1);
        acceptorLimitCountSpinner.setPageIncrement(8);

        ajpSupportButton =
            createButton(configGroup, SWT.CHECK, Messages.advConfigTab_ajpSupportButton,
                Messages.advConfigTab_ajpSupportButtonTip, 224, 1, 1, modifyDialogListener);
        // TODO enable when implemented
        ajpSupportButton.setEnabled(false);
        ajpPortSpinner =
            createSpinner(configGroup, SWT.BORDER, Messages.advConfigTab_ajpPortSpinnerTip, 32, -1, 1, 1,
                modifyDialogListener);
        ajpPortSpinner.setMinimum(0);
        ajpPortSpinner.setMaximum(65535);
        ajpPortSpinner.setIncrement(1);
        ajpPortSpinner.setPageIncrement(1000);
        // TODO enable when implemented
        ajpPortSpinner.setEnabled(false);

        customWebDefaultsEnabledButton =
            createButton(configGroup, SWT.CHECK, Messages.advConfigTab_customWebDefaultsEnabledButton,
                Messages.advConfigTab_customWebDefaultsEnabledButtonTip, 224, 1, 1, modifyDialogListener);
        customWebDefaultsResourceText =
            createText(configGroup, SWT.BORDER, Messages.advConfigTab_customWebDefaultsResourceTextTip, -1, -1, 1, 1,
                modifyDialogListener);

        Composite customWebDefaultsButtons = createComposite(configGroup, SWT.NONE, 4, -1, false, 2, 1);
        createLabel(customWebDefaultsButtons, JettyPluginUtils.BLANK, -1, 1, 1);
        customWebDefaultsWorkspaceButton =
            createButton(customWebDefaultsButtons, SWT.NONE, Messages.advConfigTab_customWebDefaultsWorkspaceButton,
                Messages.advConfigTab_customWebDefaultsWorkspaceButtonTip, 96, 1, 1, new SelectionAdapter()
                {
                    @Override
                    public void widgetSelected(final SelectionEvent e)
                    {
                        JettyLaunchConfigurationAdapter adapter =
                            JettyLaunchConfigurationAdapter.getInstance(getCurrentLaunchConfiguration());

                        String path =
                            chooseWorkspaceFile(adapter.getProject(), getShell(),
                                Messages.advConfigTab_customWebDefaultsWorkspaceTitle,
                                Messages.advConfigTab_customWebDefaultsWorkspaceMessage,
                                customWebDefaultsResourceText.getText());

                        if (path != null)
                        {
                            customWebDefaultsResourceText.setText(path);
                        }
                    }
                });
        customWebDefaultsFileSystemButton =
            createButton(customWebDefaultsButtons, SWT.NONE, Messages.advConfigTab_customWebDefaultsFileSystemButton,
                Messages.advConfigTab_customWebDefaultsFileSystemButtonTip, 96, 1, 1, new SelectionAdapter()
                {
                    @Override
                    public void widgetSelected(final SelectionEvent e)
                    {
                        String path =
                            chooseExternalFile(getShell(), customWebDefaultsResourceText.getText(),
                                Messages.advConfigTab_customWebDefaultsFileSystemTitle, "*.xml", "*.*"); //$NON-NLS-1$//$NON-NLS-2$ 

                        if (path != null)
                        {
                            customWebDefaultsResourceText.setText(path);
                        }
                    }
                });
        customWebDefaultsVariablesButton =
            createButton(customWebDefaultsButtons, SWT.NONE, Messages.advConfigTab_customWebDefaultsVariablesButton,
                Messages.advConfigTab_customWebDefaultsVariablesButtonTip, 96, 1, 1, new SelectionAdapter()
                {
                    @Override
                    public void widgetSelected(SelectionEvent e)
                    {
                        chooseVariable(getShell(), customWebDefaultsResourceText);
                    }
                });

        serverCacheDisabledButton =
            createButton(configGroup, SWT.CHECK, Messages.advConfigTab_serverCacheDisabledButton,
                Messages.advConfigTab_serverCacheDisabledButtonTip, 224, 2, 1, modifyDialogListener);

        clientCacheDisabledButton =
            createButton(configGroup, SWT.CHECK, Messages.advConfigTab_clientCacheDisabledButton,
                Messages.advConfigTab_clientCacheDisabledButtonTip, 224, 2, 1, modifyDialogListener);

        Group contextGroup = new Group(tabComposite, SWT.NONE);
        contextGroup.setLayout(new GridLayout(6, false));
        contextGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        contextGroup.setText(Messages.advConfigTab_contextGroupTitle);

        configTable =
            createTable(contextGroup, SWT.BORDER | SWT.FULL_SELECTION, -1, 64, 5, 3,
                Messages.advConfigTab_contextTableInclude, Messages.advConfigTab_contextTableFile);
        configTable.addSelectionListener(new SelectionAdapter()
        {

            @Override
            public void widgetSelected(SelectionEvent e)
            {
                updateConfigButtonState();
            }

        });

        moveUpConfigButton =
            createButton(contextGroup, SWT.NONE, Messages.advConfigTab_contextUpButton,
                Messages.advConfigTab_contextUpButtonTip, 128, 1, 1, new SelectionAdapter()
                {
                    @Override
                    public void widgetSelected(SelectionEvent e)
                    {
                        moveUpConfig();
                    }
                });
        moveDownConfigButton =
            createButton(contextGroup, SWT.NONE, Messages.advConfigTab_contextDownButton,
                Messages.advConfigTab_contextDownButtonTip, 128, 1, 2, new SelectionAdapter()
                {
                    @Override
                    public void widgetSelected(SelectionEvent e)
                    {
                        moveDownConfig();
                    }
                });

        openConfigButton =
            createButton(contextGroup, SWT.NONE, Messages.advConfigTab_contextOpenButton,
                Messages.advConfigTab_contextOpenButtonTip, 128, 1, 1, new SelectionAdapter()
                {

                    @Override
                    public void widgetSelected(SelectionEvent e)
                    {
                        openConfig();
                    }

                });

        createLabel(contextGroup, JettyPluginUtils.BLANK, -1, 1, 1);
        createButton(contextGroup, SWT.NONE, Messages.advConfigTab_contextAddButton,
            Messages.advConfigTab_contextAddButtonTip, 128, 1, 1, new SelectionAdapter()
            {
                @Override
                public void widgetSelected(SelectionEvent e)
                {
                    addConig();
                }
            });
        createButton(contextGroup, SWT.NONE, Messages.advConfigTab_contextAddExternalButton,
            Messages.advConfigTab_contextAddExternalButtonTip, 128, 1, 1, new SelectionAdapter()
            {
                @Override
                public void widgetSelected(SelectionEvent e)
                {
                    addExternalConfig();
                }
            });

        editConfigButton =
            createButton(contextGroup, SWT.NONE, Messages.advConfigTab_contextEditButton,
                Messages.advConfigTab_contextEditButtonTip, 128, 1, 1, new SelectionAdapter()
                {
                    @Override
                    public void widgetSelected(SelectionEvent e)
                    {
                        editConfig();
                    }
                });

        removeConfigButton =
            createButton(contextGroup, SWT.NONE, Messages.advConfigTab_contextRemoveButton,
                Messages.advConfigTab_contextRemoveButtonTip, 128, 1, 1, new SelectionAdapter()
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
        return Messages.advConfigTab_title;
    }

    @Override
    public Image getImage()
    {
        return JettyPlugin.getJettyAdvancedIcon();
    }

    @Override
    public String getMessage()
    {
        return Messages.advConfigTab_message;
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
            JettyPlugin.error(Messages.advConfigTab_initializeFailed, e);
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

            adapter.setConfigs(configEntryList.getConfigs());

            adapter.setClasspathProvider(JettyLaunchConfigurationAdapter.CLASSPATH_PROVIDER_JETTY);

            updateTable(configuration, false);
            updateConfigButtonState();
        }
        catch (CoreException e)
        {
            JettyPlugin.error(Messages.advConfigTab_performApplyFailed, e);
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
                    setErrorMessage(String.format(Messages.advConfigTab_pathInvalid, jettyPath));
                    return false;
                }
            }
            else
            {
                setErrorMessage(Messages.advConfigTab_pathMissing);
                return false;
            }

            try
            {
                JettyPluginUtils.detectJettyVersion(embedded, jettyPath);
            }
            catch (final IllegalArgumentException e)
            {
                setErrorMessage(String.format(Messages.advConfigTab_versionDetectionFailed, jettyPath));
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
                    setErrorMessage(String
                        .format(Messages.advConfigTab_customWebDefaultsInvalid, customWebDefaultsPath));

                    return false;
                }
            }
            else
            {
                setErrorMessage(String.format(Messages.advConfigTab_customWebDefaultsMissing));
            }
        }

        List<JettyConfig> contexts = configEntryList.getConfigs();

        for (JettyConfig context : contexts)
        {
            if ((context.isActive()) && (!context.isValid(ResourcesPlugin.getWorkspace())))
            {
                setErrorMessage(String.format(Messages.advConfigTab_contextInvalid, context.getPath()));
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
            JettyPlugin.error(Messages.advConfigTab_updateTableFailed, e);
        }
    }

    protected String chooseConfig(String path)
    {
        JettyLaunchConfigurationAdapter adapter =
            JettyLaunchConfigurationAdapter.getInstance(getCurrentLaunchConfiguration());

        return chooseWorkspaceFile(adapter.getProject(), getShell(), Messages.advConfigTab_contextAddTitle,
            Messages.advConfigTab_contextAddMessage, path);
    }

    protected String chooseConfigFromFileSystem(String path)
    {
        return chooseExternalFile(getShell(), path, Messages.advConfigTab_contextAddExternalTitle, "*.xml", "*.*"); //$NON-NLS-1$//$NON-NLS-2$ 
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
                                .getDelegates(new HashSet<String>(Arrays.asList("run"))); //$NON-NLS-1$

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
                        JettyPlugin.error(Messages.advConfigTab_contextCreateFailed, ex);
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
                    JettyPlugin.error(Messages.advConfigTab_contextOpenFailed, ex);
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
