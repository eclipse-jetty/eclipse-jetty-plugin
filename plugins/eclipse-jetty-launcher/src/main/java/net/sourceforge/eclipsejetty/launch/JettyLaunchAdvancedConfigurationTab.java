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

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;

import net.sourceforge.eclipsejetty.JettyPlugin;
import net.sourceforge.eclipsejetty.JettyPluginConstants;
import net.sourceforge.eclipsejetty.JettyPluginUtils;
import net.sourceforge.eclipsejetty.jetty.JettyVersion;
import net.sourceforge.eclipsejetty.util.RegularMatcher;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.IStringVariable;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.StringVariableSelectionDialog;
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
import org.eclipse.swt.widgets.Text;

/**
 * UI
 * 
 * @author Christian K&ouml;berl
 * @author Manfred Hantschel
 */
public class JettyLaunchAdvancedConfigurationTab extends AbstractJettyLaunchConfigurationTab
{
    private Button embeddedButton;
    private Button externButton;
    private Text pathText;
    private Button pathVariablesButton;
    private Button pathBrowseButton;

    private Button jspSupportButton;
    private Button showLauncherInfoButon;

    private Button mavenIncludeCompile;
    private Button mavenIncludeProvided;
    private Button mavenIncludeRuntime;
    private Button mavenIncludeTest;
    private Button mavenIncludeSystem;
    private Text excludedLibrariesText;

    private final ModifyDialogListener modifyDialogListener;

    public JettyLaunchAdvancedConfigurationTab()
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

        createLabel(jettyGroup, "JSP Support:", 128, 1, 1);
        jspSupportButton = createButton(jettyGroup, SWT.CHECK, "Enabled", -1, 3, 1, modifyDialogListener);

        createLabel(jettyGroup, "Detailed Server Info:", 128, 1, 1);
        showLauncherInfoButon = createButton(jettyGroup, SWT.CHECK, "Show", -1, 3, 1, modifyDialogListener);

        final Group dependencyGroup = new Group(tabComposite, SWT.NONE);

        dependencyGroup.setLayout(new GridLayout(3, false));
        dependencyGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        dependencyGroup.setText("Libraries and Dependencies:");

        createLabel(dependencyGroup, "Included Maven Dependencies:", 224, 1, 3);
        mavenIncludeCompile = createButton(dependencyGroup, SWT.CHECK, "Compile Scope", -1, 1, 1, modifyDialogListener);
        mavenIncludeProvided =
            createButton(dependencyGroup, SWT.CHECK, "Provided Scope", -1, 1, 1, modifyDialogListener);
        mavenIncludeRuntime = createButton(dependencyGroup, SWT.CHECK, "Runtime Scope", -1, 1, 1, modifyDialogListener);
        mavenIncludeSystem = createButton(dependencyGroup, SWT.CHECK, "System Scope", -1, 1, 1, modifyDialogListener);
        createLabel(dependencyGroup, "", -1, 1, 1);
        mavenIncludeTest = createButton(dependencyGroup, SWT.CHECK, "Test Scope", -1, 1, 1, modifyDialogListener);

        createLabel(dependencyGroup, "Excluded Libaries and Directories:", 224, 1, 2);
        excludedLibrariesText =
            createText(dependencyGroup, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL, -1, 50, 2, 2, modifyDialogListener);
        excludedLibrariesText
            .setToolTipText("Comma or line separated list of libraries and directories to exclude from the classpath. The entries are regular expressions.");

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
            //JettyVersion version = JettyPluginConstants.getVersion(configuration);

            embeddedButton.setSelection(JettyPluginConstants.isEmbedded(configuration));
            externButton.setSelection(!JettyPluginConstants.isEmbedded(configuration));
            pathText.setText(JettyPluginConstants.getPath(configuration));

            jspSupportButton.setSelection(JettyPluginConstants.isJspSupport(configuration));
            showLauncherInfoButon.setSelection(JettyPluginConstants.isShowLauncherInfo(configuration));

            mavenIncludeCompile.setSelection(!JettyPluginConstants.isScopeCompileExcluded(configuration));
            mavenIncludeProvided.setSelection(!JettyPluginConstants.isScopeProvidedExcluded(configuration));
            mavenIncludeRuntime.setSelection(!JettyPluginConstants.isScopeRuntimeExcluded(configuration));
            mavenIncludeSystem.setSelection(!JettyPluginConstants.isScopeSystemExcluded(configuration));
            mavenIncludeTest.setSelection(!JettyPluginConstants.isScopeTestExcluded(configuration));
            excludedLibrariesText.setText(JettyPluginConstants.getExcludedLibs(configuration));

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
        
        JettyVersion jettyVersion =
            JettyPluginUtils.detectJettyVersion(embedded, JettyPluginUtils.resolveVariables(jettyPath));

        JettyPluginConstants.setMainTypeName(configuration, jettyVersion);
        JettyPluginConstants.setVersion(configuration, jettyVersion);
        JettyPluginConstants.setJspSupport(configuration, jspSupportButton.getSelection());
        JettyPluginConstants.setShowLauncherInfo(configuration, showLauncherInfoButon.getSelection());

        JettyPluginConstants.setScopeCompileExcluded(configuration, !mavenIncludeCompile.getSelection());
        JettyPluginConstants.setScopeProvidedExcluded(configuration, !mavenIncludeProvided.getSelection());
        JettyPluginConstants.setScopeRuntimeExcluded(configuration, !mavenIncludeRuntime.getSelection());
        JettyPluginConstants.setScopeSystemExcluded(configuration, !mavenIncludeSystem.getSelection());
        JettyPluginConstants.setScopeTestExcluded(configuration, !mavenIncludeTest.getSelection());

        JettyPluginConstants.setExcludedLibs(configuration, excludedLibrariesText.getText());
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

        try
        {
            JettyPluginUtils.extractPatterns(new ArrayList<RegularMatcher>(), excludedLibrariesText.getText());
        }
        catch (final IllegalArgumentException e)
        {
            setErrorMessage("Failed to parse Excluded Libraries. " + e.getMessage());
            return false;
        }

        setDirty(true);

        return true;
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
