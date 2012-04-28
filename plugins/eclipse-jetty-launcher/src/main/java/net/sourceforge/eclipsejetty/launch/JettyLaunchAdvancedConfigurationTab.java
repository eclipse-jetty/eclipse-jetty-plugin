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

import java.util.ArrayList;

import net.sourceforge.eclipsejetty.JettyPlugin;
import net.sourceforge.eclipsejetty.JettyPluginConstants;
import net.sourceforge.eclipsejetty.JettyPluginUtils;
import net.sourceforge.eclipsejetty.jetty.JettyVersion;
import net.sourceforge.eclipsejetty.jetty.JspSupport;
import net.sourceforge.eclipsejetty.util.RegularMatcher;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
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
    private static final String DETECTED_TEXT = " (detected)";

    private Button jettyAutoButton;
    private Button jetty5Button;
    private Button jetty6Button;
    private Button jetty7Button;
    private Button jetty8Button;

    private Button jspEnabledButton;
    private Button jsp20Button;
    private Button jsp21Button;
    private Button jsp22Button;

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

        final Group jettyVersionGroup = new Group(tabComposite, SWT.NONE);
        jettyVersionGroup.setLayout(new GridLayout(2, false));
        jettyVersionGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        jettyVersionGroup.setText("Jetty Version:");

        jettyAutoButton =
            createButton(jettyVersionGroup, SWT.CHECK, "Autodetect Jetty Version", 224, modifyDialogListener);
        jetty5Button = createButton(jettyVersionGroup, SWT.RADIO, "Jetty 5.x", -1, modifyDialogListener);
        createLabel(jettyVersionGroup, "", 224, 1);
        jetty6Button = createButton(jettyVersionGroup, SWT.RADIO, "Jetty 6.x", -1, modifyDialogListener);
        createLabel(jettyVersionGroup, "", 224, 1);
        jetty7Button = createButton(jettyVersionGroup, SWT.RADIO, "Jetty 7.x", -1, modifyDialogListener);
        createLabel(jettyVersionGroup, "", 224, 1);
        jetty8Button = createButton(jettyVersionGroup, SWT.RADIO, "Jetty 8.x", -1, modifyDialogListener);

        final Group jspGroup = new Group(tabComposite, SWT.NONE);

        jspGroup.setLayout(new GridLayout(2, false));
        jspGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        jspGroup.setText("JSP Support:");

        jspEnabledButton = createButton(jspGroup, SWT.CHECK, "JSP Support Enabled", 224, modifyDialogListener);
        jsp20Button = createButton(jspGroup, SWT.RADIO, "JSP 2.0 (Jetty 6)", -1, modifyDialogListener);
        createLabel(jspGroup, "", 224, 1);
        jsp21Button = createButton(jspGroup, SWT.RADIO, "JSP 2.1 (Jetty 6 and 7)", -1, modifyDialogListener);
        createLabel(jspGroup, "", 224, 1);
        jsp22Button = createButton(jspGroup, SWT.RADIO, "JSP 2.2 (Jetty 8)", -1, modifyDialogListener);

        final Group mavenGroup = new Group(tabComposite, SWT.NONE);

        mavenGroup.setLayout(new GridLayout(2, false));
        mavenGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        mavenGroup.setText("Maven:");

        createLabel(mavenGroup, "Included Dependencies by Scope:", 224, 1);
        mavenIncludeCompile = createButton(mavenGroup, SWT.CHECK, "Compile Scope", -1, modifyDialogListener);
        createLabel(mavenGroup, "", 224, 1);
        mavenIncludeProvided = createButton(mavenGroup, SWT.CHECK, "Provided Scope", -1, modifyDialogListener);
        createLabel(mavenGroup, "", 224, 1);
        mavenIncludeRuntime = createButton(mavenGroup, SWT.CHECK, "Runtime Scope", -1, modifyDialogListener);
        createLabel(mavenGroup, "", 224, 1);
        mavenIncludeTest = createButton(mavenGroup, SWT.CHECK, "Test Scope", -1, modifyDialogListener);
        createLabel(mavenGroup, "", 224, 1);
        mavenIncludeSystem = createButton(mavenGroup, SWT.CHECK, "System Scope", -1, modifyDialogListener);

        final Group fooGroup = new Group(tabComposite, SWT.NONE);

        fooGroup.setLayout(new GridLayout(2, false));
        fooGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        fooGroup.setText("Foobar:");

        createLabel(fooGroup, "Excluded Libraries", 128, 1);

        excludedLibrariesText = new Text(fooGroup, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);

        final GridData excludedLibrariesGridData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        excludedLibrariesGridData.heightHint = 50;
        excludedLibrariesText.setLayoutData(excludedLibrariesGridData);
        excludedLibrariesText.addModifyListener(modifyDialogListener);
        excludedLibrariesText
            .setToolTipText("Comma or line separated list of libraries to exclude from the classpath. The entries are regular expressions.");

        setControl(tabComposite);
    }

    public String getName()
    {
        return "Config";
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
            JettyVersion version = JettyPluginConstants.getVersion(configuration);

            jettyAutoButton.setSelection(version == JettyVersion.JETTY_AUTO_DETECT);
            jetty5Button.setSelection(version == JettyVersion.JETTY_5);
            jetty6Button.setSelection(version == JettyVersion.JETTY_6);
            jetty7Button.setSelection(version == JettyVersion.JETTY_7);
            jetty7Button.setSelection(version == JettyVersion.JETTY_8);

            JspSupport jspSupport = JettyPluginConstants.getJspSupport(configuration);

            jspEnabledButton.setSelection(jspSupport != JspSupport.JSP_DISABLED);
            jsp20Button.setSelection(jspSupport == JspSupport.JSP_2_0);
            jsp21Button.setSelection(jspSupport == JspSupport.JSP_2_1);
            jsp22Button.setSelection(jspSupport == JspSupport.JSP_2_2);

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
            JettyPluginConstants.setVersion(configuration, JettyPluginConstants.getVersion(configuration));
        }
        catch (CoreException e)
        {
            JettyPlugin.logError(e);
        }

        try
        {
            JettyPluginConstants.setJspSupport(configuration, JettyPluginConstants.getJspSupport(configuration));
        }
        catch (CoreException e)
        {
            JettyPlugin.logError(e);
        }

        try
        {
            JettyPluginConstants.setScopeCompileExcluded(configuration,
                JettyPluginConstants.isScopeCompileExcluded(configuration));
        }
        catch (CoreException e)
        {
            JettyPlugin.logError(e);
        }

        try
        {
            JettyPluginConstants.setScopeProvidedExcluded(configuration,
                JettyPluginConstants.isScopeProvidedExcluded(configuration));
        }
        catch (CoreException e)
        {
            JettyPlugin.logError(e);
        }

        try
        {
            JettyPluginConstants.setScopeRuntimeExcluded(configuration,
                JettyPluginConstants.isScopeRuntimeExcluded(configuration));
        }
        catch (CoreException e)
        {
            JettyPlugin.logError(e);
        }

        try
        {
            JettyPluginConstants.setScopeSystemExcluded(configuration,
                JettyPluginConstants.isScopeSystemExcluded(configuration));
        }
        catch (CoreException e)
        {
            JettyPlugin.logError(e);
        }

        try
        {
            JettyPluginConstants.setScopeTestExcluded(configuration,
                JettyPluginConstants.isScopeTestExcluded(configuration));
        }
        catch (CoreException e)
        {
            JettyPlugin.logError(e);
        }
    }

    public void performApply(final ILaunchConfigurationWorkingCopy configuration)
    {

        String jettyPath = "";
        try
        {
            jettyPath = JettyPluginUtils.resolveVariables(JettyPluginConstants.getPath(configuration));
        }
        catch (CoreException e)
        {
            JettyPlugin.logError(e);
        }

        JettyVersion jettyVersion = getJettyVersion();

        JettyPluginConstants.setVersion(configuration, jettyVersion);

        try
        {
            jettyVersion = JettyPluginUtils.detectJettyVersion(jettyPath, jettyVersion);
        }
        catch (IllegalArgumentException e)
        {
            // ignore
        }

        JettyPluginConstants.setMainTypeName(configuration, jettyVersion);
        JettyPluginConstants.setJspSupport(configuration, getJspEnabled());

        JettyPluginConstants.setScopeCompileExcluded(configuration, !mavenIncludeCompile.getSelection());
        JettyPluginConstants.setScopeProvidedExcluded(configuration, !mavenIncludeProvided.getSelection());
        JettyPluginConstants.setScopeRuntimeExcluded(configuration, !mavenIncludeRuntime.getSelection());
        JettyPluginConstants.setScopeSystemExcluded(configuration, !mavenIncludeSystem.getSelection());
        JettyPluginConstants.setScopeTestExcluded(configuration, !mavenIncludeTest.getSelection());

        JettyPluginConstants.setExcludedLibs(configuration, excludedLibrariesText.getText());
    }

    private JettyVersion getJettyVersion()
    {
        if (jettyAutoButton.getSelection())
        {
            return JettyVersion.JETTY_AUTO_DETECT;
        }
        else if (jetty5Button.getSelection())
        {
            return JettyVersion.JETTY_5;
        }
        else if (jetty6Button.getSelection())
        {
            return JettyVersion.JETTY_6;
        }
        else if (jetty7Button.getSelection())
        {
            return JettyVersion.JETTY_7;
        }
        else if (jetty8Button.getSelection())
        {
            return JettyVersion.JETTY_8;
        }

        return JettyVersion.JETTY_AUTO_DETECT;
    }

    private JspSupport getJspEnabled()
    {
        if (!jspEnabledButton.getSelection())
        {
            return JspSupport.JSP_DISABLED;
        }
        else if (jsp20Button.getSelection())
        {
            return JspSupport.JSP_2_0;
        }
        else if (jsp21Button.getSelection())
        {
            return JspSupport.JSP_2_1;
        }
        else if (jsp22Button.getSelection())
        {
            return JspSupport.JSP_2_2;
        }

        return JspSupport.JSP_DISABLED;
    }

    @Override
    public boolean isValid(final ILaunchConfiguration configuration)
    {
        setErrorMessage(null);
        setMessage(null);

        String jettyPath = "";
        try
        {
            jettyPath = JettyPluginUtils.resolveVariables(JettyPluginConstants.getPath(configuration));
        }
        catch (CoreException e)
        {
            JettyPlugin.logError(e);
        }

        try
        {
            JettyVersion detectedVersion =
                JettyPluginUtils.detectJettyVersion(jettyPath, JettyVersion.JETTY_AUTO_DETECT);

            updateJettyVersionButtonText(jetty5Button, detectedVersion == JettyVersion.JETTY_5);
            updateJettyVersionButtonText(jetty6Button, detectedVersion == JettyVersion.JETTY_6);
            updateJettyVersionButtonText(jetty7Button, detectedVersion == JettyVersion.JETTY_7);
            updateJettyVersionButtonText(jetty8Button, detectedVersion == JettyVersion.JETTY_8);
        }
        catch (final IllegalArgumentException e)
        {
            updateJettyVersionButtonText(jetty5Button, false);
            updateJettyVersionButtonText(jetty6Button, false);
            updateJettyVersionButtonText(jetty7Button, false);
            updateJettyVersionButtonText(jetty8Button, false);
        }

        if (jettyAutoButton.getSelection())
        {
            jetty5Button.setEnabled(false);
            jetty6Button.setEnabled(false);
            jetty7Button.setEnabled(false);
            jetty8Button.setEnabled(false);
        }
        else
        {
            jetty5Button.setEnabled(true);
            jetty6Button.setEnabled(true);
            jetty7Button.setEnabled(true);
            jetty8Button.setEnabled(true);
        }

        JettyVersion version;

        try
        {
            version = JettyPluginUtils.detectJettyVersion(jettyPath, getJettyVersion());
        }
        catch (final IllegalArgumentException e)
        {
            setErrorMessage("Failed to find and detect Jetty version at path \"" + jettyPath + "\"");
            return false;
        }

        jspEnabledButton.setEnabled(version.isJspSupported());

        if ((!version.isJspSupported()) && (jspEnabledButton.getSelection()))
        {
            jspEnabledButton.setSelection(false);
        }

        jsp20Button.setEnabled(jspEnabledButton.getSelection() && version.containsJspSupport(JspSupport.JSP_2_0));
        jsp21Button.setEnabled(jspEnabledButton.getSelection() && version.containsJspSupport(JspSupport.JSP_2_1));
        jsp22Button.setEnabled(jspEnabledButton.getSelection() && version.containsJspSupport(JspSupport.JSP_2_2));

        if ((!jettyAutoButton.getSelection()) && (!jetty5Button.getSelection()) && (!jetty6Button.getSelection())
            && (!jetty7Button.getSelection()) && (!jetty8Button.getSelection()))
        {
            setErrorMessage("You must select a Jetty version if auto detection is turned off.");
            return false;
        }

        if ((jspEnabledButton.getSelection()) && (!jsp20Button.getSelection()) && (!jsp21Button.getSelection())
            && (!jsp22Button.getSelection()))
        {
            setErrorMessage("You must select a JSP version if JSP support is enabled.");
            return false;
        }
        
        if ((jspEnabledButton.getSelection()) && (jsp20Button.getSelection()) && (!version.containsJspSupport(JspSupport.JSP_2_0))) {
            setErrorMessage("JSP 2.0 is not supported by your Jetty.");
            return false;
        }

        if ((jspEnabledButton.getSelection()) && (jsp21Button.getSelection()) && (!version.containsJspSupport(JspSupport.JSP_2_1))) {
            setErrorMessage("JSP 2.1 is not supported by your Jetty.");
            return false;
        }

        if ((jspEnabledButton.getSelection()) && (jsp22Button.getSelection()) && (!version.containsJspSupport(JspSupport.JSP_2_2))) {
            setErrorMessage("JSP 2.2 is not supported by your Jetty.");
            return false;
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

    private static void updateJettyVersionButtonText(Button button, boolean detected)
    {
        boolean endsWithDetected = button.getText().endsWith(DETECTED_TEXT);

        if ((!detected) && (endsWithDetected))
        {
            button.setText(button.getText().substring(0, button.getText().length() - DETECTED_TEXT.length()));
        }
        else if ((detected) && (!endsWithDetected))
        {
            button.setText(button.getText() + DETECTED_TEXT);
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
