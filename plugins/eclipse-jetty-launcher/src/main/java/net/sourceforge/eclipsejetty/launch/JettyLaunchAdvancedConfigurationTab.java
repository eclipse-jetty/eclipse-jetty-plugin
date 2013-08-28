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
import java.util.Collection;
import java.util.HashSet;

import net.sourceforge.eclipsejetty.JettyPlugin;
import net.sourceforge.eclipsejetty.JettyPluginConstants;
import net.sourceforge.eclipsejetty.JettyPluginUtils;
import net.sourceforge.eclipsejetty.jetty.JettyVersion;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.IStringVariable;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchDelegate;
import org.eclipse.debug.ui.StringVariableSelectionDialog;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

/**
 * UI
 * 
 * @author Christian K&ouml;berl
 * @author Manfred Hantschel
 */
public class JettyLaunchAdvancedConfigurationTab extends AbstractJettyLaunchConfigurationTab
{
    private final JettyLaunchDependencyEntryList dependencyEntryList;
    private final ModifyDialogListener modifyDialogListener;

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
    private Text ajpPortText;
    private Button showLauncherInfoButon;

    private Button mavenIncludeCompile;
    private Button mavenIncludeProvided;
    private Button mavenIncludeRuntime;
    private Button mavenIncludeTest;
    private Button mavenIncludeSystem;
    private Table dependencyTable;
    private boolean dependencyTableFormatted = false;

    public JettyLaunchAdvancedConfigurationTab()
    {
        modifyDialogListener = new ModifyDialogListener();
        dependencyEntryList = new JettyLaunchDependencyEntryList(modifyDialogListener);
    }

    /**
     * @wbp.parser.entryPoint
     */
    public void createControl(final Composite parent)
    {
        tabComposite = new Composite(parent, SWT.NONE);
        tabComposite.setLayout(new GridLayout(1, false));

        final Group jettyGroup = new Group(tabComposite, SWT.NONE);
        jettyGroup.setLayout(new GridLayout(4, false));
        jettyGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
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
                chooseJettyPathVariable();
            }
        });
        pathBrowseButton = createButton(jettyGroup, SWT.NONE, "Browse...", 96, 1, 1, new SelectionAdapter()
        {
            @Override
            public void widgetSelected(final SelectionEvent e)
            {
                chooseJettyPath();
            }
        });

        final Group jettyFeatureGroup = new Group(tabComposite, SWT.NONE);
        jettyFeatureGroup.setLayout(new GridLayout(4, false));
        jettyFeatureGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        jettyFeatureGroup.setText("Jetty Features:");

        jspSupportButton =
            createButton(jettyFeatureGroup, SWT.CHECK, "Enable JSP Support", 224, 1, 1, modifyDialogListener);
        ajpSupportButton =
            createButton(jettyFeatureGroup, SWT.CHECK, "Enable AJP Connector", 224, 1, 1, modifyDialogListener);
        
        // TODO enable when implemented
        ajpSupportButton.setEnabled(false);

        createLabel(jettyFeatureGroup, "AJP Port:", 48, 1, 1);
        ajpPortText = createText(jettyFeatureGroup, SWT.BORDER, 32, -1, 1, 1, modifyDialogListener);
        
        // TODO enable when implemented
        ajpPortText.setEnabled(false);

        jndiSupportButton =
            createButton(jettyFeatureGroup, SWT.CHECK, "Enable JNDI Support", 224, 1, 1,
                modifyDialogListener);
        showLauncherInfoButon =
            createButton(jettyFeatureGroup, SWT.CHECK, "Show Detailed Server Info", -1, 3, 1, modifyDialogListener);

        jmxSupportButton =
            createButton(jettyFeatureGroup, SWT.CHECK, "Enable JMX Support", 224, 1, 1, modifyDialogListener);
        
        // TODO enable when implemented
        jmxSupportButton.setEnabled(false);

        final Group dependencyGroup = new Group(tabComposite, SWT.NONE);
        dependencyGroup.setLayout(new GridLayout(3, false));
        dependencyGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
        dependencyGroup.setText("Libraries and Dependencies:");

        createLabel(dependencyGroup, "Included Maven Dependencies:", 224, 1, 3);
        mavenIncludeCompile =
            createButton(dependencyGroup, SWT.CHECK, "Compile Scope", 224, 1, 1, modifyDialogListener);
        mavenIncludeProvided =
            createButton(dependencyGroup, SWT.CHECK, "Provided Scope", -1, 1, 1, modifyDialogListener);

        mavenIncludeRuntime =
            createButton(dependencyGroup, SWT.CHECK, "Runtime Scope", 224, 1, 1, modifyDialogListener);
        mavenIncludeSystem = createButton(dependencyGroup, SWT.CHECK, "System Scope", -1, 1, 1, modifyDialogListener);

        createLabel(dependencyGroup, "", 224, 1, 1);
        mavenIncludeTest = createButton(dependencyGroup, SWT.CHECK, "Test Scope", -1, 1, 1, modifyDialogListener);

        dependencyTable =
            createTable(dependencyGroup, SWT.BORDER | SWT.HIDE_SELECTION, -1, 200, 3, 1, "Include", "Name", "Global",
                "Scope", "Path");

        createButton(dependencyGroup, SWT.NONE, "Reset Dependency Overrides", 196, 3, 1, new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                dependencyEntryList.reset();
                updateLaunchConfigurationDialog();
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
        try
        {
            embeddedButton.setSelection(JettyPluginConstants.isEmbedded(configuration));
            externButton.setSelection(!JettyPluginConstants.isEmbedded(configuration));
            pathText.setText(JettyPluginConstants.getPath(configuration));

            jspSupportButton.setSelection(JettyPluginConstants.isJspSupport(configuration));
            jmxSupportButton.setSelection(JettyPluginConstants.isJmxSupport(configuration));
            jndiSupportButton.setSelection(JettyPluginConstants.isJndiSupport(configuration));
            ajpSupportButton.setSelection(JettyPluginConstants.isAjpSupport(configuration));
            showLauncherInfoButon.setSelection(JettyPluginConstants.isShowLauncherInfo(configuration));

            mavenIncludeCompile.setSelection(!JettyPluginConstants.isScopeCompileExcluded(configuration));
            mavenIncludeProvided.setSelection(!JettyPluginConstants.isScopeProvidedExcluded(configuration));
            mavenIncludeRuntime.setSelection(!JettyPluginConstants.isScopeRuntimeExcluded(configuration));
            mavenIncludeSystem.setSelection(!JettyPluginConstants.isScopeSystemExcluded(configuration));
            mavenIncludeTest.setSelection(!JettyPluginConstants.isScopeTestExcluded(configuration));

            updateTable(configuration, true);
        }
        catch (final CoreException e)
        {
            JettyPlugin.logError(e);
        }
    }

    public void setDefaults(final ILaunchConfigurationWorkingCopy configuration)
    {
        try
        {
            JettyPluginConstants.setEmbedded(configuration, JettyPluginConstants.isEmbedded(configuration));
            JettyPluginConstants.setPath(configuration, JettyPluginConstants.getPath(configuration));

            JettyPluginConstants.setJspSupport(configuration, JettyPluginConstants.isJspSupport(configuration));
            JettyPluginConstants.setJmxSupport(configuration, JettyPluginConstants.isJmxSupport(configuration));
            JettyPluginConstants.setJndiSupport(configuration, JettyPluginConstants.isJndiSupport(configuration));
            JettyPluginConstants.setAjpSupport(configuration, JettyPluginConstants.isAjpSupport(configuration));
            JettyPluginConstants.setScopeCompileExcluded(configuration,
                JettyPluginConstants.isScopeCompileExcluded(configuration));
            JettyPluginConstants.setScopeProvidedExcluded(configuration,
                JettyPluginConstants.isScopeProvidedExcluded(configuration));
            JettyPluginConstants.setScopeRuntimeExcluded(configuration,
                JettyPluginConstants.isScopeRuntimeExcluded(configuration));
            JettyPluginConstants.setScopeSystemExcluded(configuration,
                JettyPluginConstants.isScopeSystemExcluded(configuration));
            JettyPluginConstants.setScopeTestExcluded(configuration,
                JettyPluginConstants.isScopeTestExcluded(configuration));
            JettyPluginConstants.setShowLauncherInfo(configuration,
                JettyPluginConstants.isShowLauncherInfo(configuration));

            JettyPluginConstants.setExcludedLibs(configuration, JettyPluginConstants.getExcludedLibs(configuration));
            JettyPluginConstants.setIncludedLibs(configuration, JettyPluginConstants.getIncludedLibs(configuration));
            JettyPluginConstants.setGlobalLibs(configuration, JettyPluginConstants.getGlobalLibs(configuration));
        }
        catch (CoreException e)
        {
            JettyPlugin.logError(e);
        }
    }

    public void performApply(final ILaunchConfigurationWorkingCopy configuration)
    {
        boolean embedded = embeddedButton.getSelection();

        JettyPluginConstants.setEmbedded(configuration, embedded);

        String jettyPath = pathText.getText().trim();

        JettyPluginConstants.setPath(configuration, jettyPath);

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
        JettyPluginConstants.setShowLauncherInfo(configuration, showLauncherInfoButon.getSelection());

        JettyPluginConstants.setScopeCompileExcluded(configuration, !mavenIncludeCompile.getSelection());
        JettyPluginConstants.setScopeProvidedExcluded(configuration, !mavenIncludeProvided.getSelection());
        JettyPluginConstants.setScopeRuntimeExcluded(configuration, !mavenIncludeRuntime.getSelection());
        JettyPluginConstants.setScopeSystemExcluded(configuration, !mavenIncludeSystem.getSelection());
        JettyPluginConstants.setScopeTestExcluded(configuration, !mavenIncludeTest.getSelection());

        JettyPluginConstants.setExcludedLibs(configuration, dependencyEntryList.createExcludedLibs());
        JettyPluginConstants.setIncludedLibs(configuration, dependencyEntryList.createIncludedLibs());
        JettyPluginConstants.setGlobalLibs(configuration, dependencyEntryList.createGlobalLibs());

        updateTable(configuration, false);
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

        setDirty(true);

        return true;
    }

    private void updateTable(final ILaunchConfiguration configuration, boolean updateType)
    {
        try
        {
            ILaunchDelegate[] delegates =
                configuration.getType().getDelegates(new HashSet<String>(Arrays.asList("run")));

            if (delegates.length == 1)
            {
                JettyLaunchConfigurationDelegate delegate =
                    (JettyLaunchConfigurationDelegate) delegates[0].getDelegate();

                Collection<IRuntimeClasspathEntry> originalClasspathEntries =
                    delegate.getOriginalClasspathEntries(configuration);
                Collection<IRuntimeClasspathEntry> webappClasspathEntries =
                    delegate.getWebappClasspathEntries(configuration, originalClasspathEntries);
                Collection<IRuntimeClasspathEntry> globalWebappClasspathEntries =
                    delegate.getGlobalWebappClasspathEntries(configuration, webappClasspathEntries);

                if (dependencyEntryList.update(configuration, dependencyTable, originalClasspathEntries,
                    webappClasspathEntries, globalWebappClasspathEntries, updateType))
                {
                    if (!dependencyTableFormatted)
                    {
                        for (int i = 0; i < dependencyTable.getColumnCount(); i += 1)
                        {
                            dependencyTable.getColumn(i).pack();
                        }
                    }

                    if (dependencyTable.getItemCount() > 0)
                    {
                        dependencyTableFormatted = true;
                    }
                }
            }
        }
        catch (CoreException e)
        {
            JettyPlugin.logError(e);
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
