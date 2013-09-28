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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import net.sourceforge.eclipsejetty.JettyPlugin;
import net.sourceforge.eclipsejetty.JettyPluginConstants;
import net.sourceforge.eclipsejetty.JettyPluginM2EUtils;
import net.sourceforge.eclipsejetty.util.Dependency;

import org.eclipse.core.runtime.CoreException;
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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;

/**
 * UI
 * 
 * @author Christian K&ouml;berl
 * @author Manfred Hantschel
 */
public class JettyLaunchDependencyConfigurationTab extends AbstractJettyLaunchConfigurationTab
{
    private final JettyLaunchDependencyEntryList dependencyEntryList;
    private final ModifyDialogListener modifyDialogListener;

    private Composite tabComposite;

    private Label m2eLabel;
    private Button mavenIncludeCompile;
    private Button mavenIncludeProvided;
    private Button mavenIncludeRuntime;
    private Button mavenIncludeTest;
    private Button mavenIncludeSystem;
    private Button mavenIncludeImport;
    private Button mavenIncludeNone;
    private Table dependencyTable;
    private boolean dependencyTableFormatted = false;

    public JettyLaunchDependencyConfigurationTab()
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

        Composite mavenGroup = createComposite(tabComposite, SWT.NONE, 3, -1, false, 1, 1);
        createLabel(mavenGroup, "Include Maven Dependencies:", 224, 1, 1);
        mavenIncludeCompile = createButton(mavenGroup, SWT.CHECK, "Compile Scope", 224, 1, 1, modifyDialogListener);
        mavenIncludeProvided = createButton(mavenGroup, SWT.CHECK, "Provided Scope", -1, 1, 1, modifyDialogListener);

        m2eLabel = createLabel(mavenGroup, "", 224, 1, 2);
        mavenIncludeRuntime = createButton(mavenGroup, SWT.CHECK, "Runtime Scope", 224, 1, 1, modifyDialogListener);
        mavenIncludeSystem = createButton(mavenGroup, SWT.CHECK, "System Scope", -1, 1, 1, modifyDialogListener);

        mavenIncludeTest = createButton(mavenGroup, SWT.CHECK, "Test Scope", 224, 1, 1, modifyDialogListener);
        mavenIncludeImport = createButton(mavenGroup, SWT.CHECK, "Import Scope", -1, 1, 1, modifyDialogListener);

        Composite otherGroup = createComposite(tabComposite, SWT.NONE, 3, -1, false, 1, 1);

        createLabel(otherGroup, "Include Other Dependencies:", 224, 1, 1);
        mavenIncludeNone = createButton(otherGroup, SWT.CHECK, "Without Scope", 224, 2, 1, modifyDialogListener);

        Composite tableGroup = createComposite(tabComposite, SWT.NONE, 3, -1, true, 1, 1);

        dependencyTable =
            createTable(tableGroup, SWT.BORDER | SWT.HIDE_SELECTION, -1, 200, 3, 1, "Include", "Name", "Global",
                "Scope", "Path");

        createButton(tableGroup, SWT.NONE, "Reset Dependency Overrides", 196, 3, 1, new SelectionAdapter()
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
        return "Dependencies";
    }

    @Override
    public Image getImage()
    {
        return JettyPlugin.getJettyDependencyIcon();
    }

    @Override
    public String getMessage()
    {
        return "Configure dependencies of Jetty.";
    }

    @Override
    public void initializeFrom(final ILaunchConfiguration configuration)
    {
        super.initializeFrom(configuration);

        try
        {
            mavenIncludeCompile.setSelection(!JettyPluginConstants.isScopeCompileExcluded(configuration));
            mavenIncludeProvided.setSelection(!JettyPluginConstants.isScopeProvidedExcluded(configuration));
            mavenIncludeRuntime.setSelection(!JettyPluginConstants.isScopeRuntimeExcluded(configuration));
            mavenIncludeSystem.setSelection(!JettyPluginConstants.isScopeSystemExcluded(configuration));
            mavenIncludeTest.setSelection(!JettyPluginConstants.isScopeTestExcluded(configuration));
            mavenIncludeImport.setSelection(!JettyPluginConstants.isScopeImportExcluded(configuration));
            mavenIncludeNone.setSelection(!JettyPluginConstants.isScopeNoneExcluded(configuration));

            updateTable(configuration, true);
        }
        catch (final CoreException e)
        {
            JettyPlugin.error("Failed to initialize dependency tab", e);
        }
    }

    public void setDefaults(final ILaunchConfigurationWorkingCopy configuration)
    {
        try
        {
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
            JettyPluginConstants.setScopeImportExcluded(configuration,
                JettyPluginConstants.isScopeImportExcluded(configuration));
            JettyPluginConstants.setScopeNoneExcluded(configuration,
                JettyPluginConstants.isScopeNoneExcluded(configuration));
            JettyPluginConstants.setShowLauncherInfo(configuration,
                JettyPluginConstants.isShowLauncherInfo(configuration));

            JettyPluginConstants.setExcludedGenericIds(configuration,
                JettyPluginConstants.getExcludedGenericIds(configuration));
            JettyPluginConstants.setIncludedGenericIds(configuration,
                JettyPluginConstants.getIncludedGenericIds(configuration));
            JettyPluginConstants.setGlobalGenericIds(configuration,
                JettyPluginConstants.getGlobalGenericIds(configuration));

            deprecatedSetDefaults(configuration);
        }
        catch (CoreException e)
        {
            JettyPlugin.error("Failed to set defaults in dependency tab", e);
        }
    }

    @SuppressWarnings("deprecation")
    private void deprecatedSetDefaults(final ILaunchConfigurationWorkingCopy configuration) throws CoreException
    {
        JettyPluginConstants.setExcludedLibs(configuration, JettyPluginConstants.getExcludedLibs(configuration));
        JettyPluginConstants.setIncludedLibs(configuration, JettyPluginConstants.getIncludedLibs(configuration));
        JettyPluginConstants.setGlobalLibs(configuration, JettyPluginConstants.getGlobalLibs(configuration));
    }

    public void performApply(final ILaunchConfigurationWorkingCopy configuration)
    {
        JettyPluginConstants.updateConfigVersion(configuration);

        JettyPluginConstants.setScopeCompileExcluded(configuration, !mavenIncludeCompile.getSelection());
        JettyPluginConstants.setScopeProvidedExcluded(configuration, !mavenIncludeProvided.getSelection());
        JettyPluginConstants.setScopeRuntimeExcluded(configuration, !mavenIncludeRuntime.getSelection());
        JettyPluginConstants.setScopeSystemExcluded(configuration, !mavenIncludeSystem.getSelection());
        JettyPluginConstants.setScopeTestExcluded(configuration, !mavenIncludeTest.getSelection());
        JettyPluginConstants.setScopeImportExcluded(configuration, !mavenIncludeImport.getSelection());
        JettyPluginConstants.setScopeNoneExcluded(configuration, !mavenIncludeNone.getSelection());

        JettyPluginConstants.setExcludedGenericIds(configuration, dependencyEntryList.createExcludedGenericIds());
        JettyPluginConstants.setIncludedGenericIds(configuration, dependencyEntryList.createIncludedGenericIds());
        JettyPluginConstants.setGlobalGenericIds(configuration, dependencyEntryList.createGlobalGenericIds());

        deprecatedPerformApply(configuration);

        updateTable(configuration, false);
    }

    @SuppressWarnings("deprecation")
    private void deprecatedPerformApply(final ILaunchConfigurationWorkingCopy configuration)
    {
        JettyPluginConstants.setExcludedLibs(configuration, dependencyEntryList.createExcludedLibs());
        JettyPluginConstants.setIncludedLibs(configuration, dependencyEntryList.createIncludedLibs());
        JettyPluginConstants.setGlobalLibs(configuration, dependencyEntryList.createGlobalLibs());
    }

    @Override
    public boolean isValid(final ILaunchConfiguration configuration)
    {
        setErrorMessage(null);
        setMessage(null);

        boolean scopeable = false;

        m2eLabel.setText("");

        if (JettyPluginM2EUtils.isM2EAvailable())
        {
            try
            {
                scopeable = JettyPluginM2EUtils.getMavenProjectFacade(configuration) != null;
            }
            catch (CoreException e)
            {
                // ignore
            }

            if (!scopeable)
            {
                m2eLabel.setText("(no m2e nature, no scope info)");
            }
        }
        else
        {
            m2eLabel.setText("(m2e not available, no scope info)");
        }

        mavenIncludeCompile.setEnabled(scopeable);
        mavenIncludeProvided.setEnabled(scopeable);
        mavenIncludeRuntime.setEnabled(scopeable);
        mavenIncludeTest.setEnabled(scopeable);
        mavenIncludeSystem.setEnabled(scopeable);
        mavenIncludeImport.setEnabled(scopeable);
        mavenIncludeNone.setEnabled(true);

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

                Collection<Dependency> originalClasspathEntries = delegate.getOriginalClasspathEntries(configuration);
                Collection<Dependency> webappClasspathEntries =
                    delegate.getWebappClasspathEntries(configuration, originalClasspathEntries);
                Collection<Dependency> globalWebappClasspathEntries =
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
            JettyPlugin.error("Failed to update table in advanced configuration tab", e);
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
