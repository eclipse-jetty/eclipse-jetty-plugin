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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import net.sourceforge.eclipsejetty.JettyPlugin;
import net.sourceforge.eclipsejetty.JettyPluginM2EUtils;
import net.sourceforge.eclipsejetty.JettyPluginUtils;
import net.sourceforge.eclipsejetty.Messages;
import net.sourceforge.eclipsejetty.launch.util.JettyLaunchConfigurationAdapter;
import net.sourceforge.eclipsejetty.launch.util.JettyLaunchConfigurationDelegate;
import net.sourceforge.eclipsejetty.util.Dependency;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchDelegate;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

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

    private Label m2eLabel;
    private Button mavenIncludeCompile;
    private Button mavenIncludeProvided;
    private Button mavenIncludeRuntime;
    private Button mavenIncludeTest;
    private Button mavenIncludeSystem;
    private Button mavenIncludeImport;
    private Button mavenIncludeNone;
    private Text dependencyFilterText;
    private Table dependencyTable;
    private boolean dependencyTableFormatted = false;
    private String defaultFilterText;

    public JettyLaunchDependencyConfigurationTab()
    {
        modifyDialogListener = new ModifyDialogListener();
        dependencyEntryList = new JettyLaunchDependencyEntryList(modifyDialogListener);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(org.eclipse.swt.widgets.Composite)
     * @wbp.parser.entryPoint
     */
    public void createControl(final Composite parent)
    {
        Composite tabComposite = createTabComposite(parent, 2, false);

        createMavenGroup(tabComposite);
        createOtherGroup(tabComposite);
        createTableGroup(tabComposite);

        setControl(tabComposite);
    }

    private void createMavenGroup(Composite tabComposite)
    {
        Composite mavenGroup = createTopComposite(tabComposite, SWT.NONE, 3, -1, false, 1, 1);

        createLabel(mavenGroup, Messages.depConfigTab_mavenGroupTitle, 224, SWT.LEFT, 1, 1);
        mavenIncludeCompile =
            createButton(mavenGroup, SWT.CHECK, Messages.depConfigTab_mavenIncludeCompileButton,
                Messages.depConfigTab_mavenIncludeCompileButtonTip, 224, 1, 1, modifyDialogListener);
        mavenIncludeProvided =
            createButton(mavenGroup, SWT.CHECK, Messages.depConfigTab_mavenIncludeProvidedButton,
                Messages.depConfigTab_mavenIncludeProvidedButtonTip, -1, 1, 1, modifyDialogListener);

        m2eLabel = createLabel(mavenGroup, JettyPluginUtils.EMPTY, 224, SWT.LEFT, 1, 2);
        mavenIncludeRuntime =
            createButton(mavenGroup, SWT.CHECK, Messages.depConfigTab_mavenIncludeRuntimeButton,
                Messages.depConfigTab_mavenIncludeRuntimeButtonTip, 224, 1, 1, modifyDialogListener);
        mavenIncludeSystem =
            createButton(mavenGroup, SWT.CHECK, Messages.depConfigTab_mavenIncludeSystemButton,
                Messages.depConfigTab_mavenIncludeSystemButtonTip, -1, 1, 1, modifyDialogListener);

        mavenIncludeTest =
            createButton(mavenGroup, SWT.CHECK, Messages.depConfigTab_mavenIncludeTestButton,
                Messages.depConfigTab_mavenIncludeTestButtonTip, 224, 1, 1, modifyDialogListener);
        mavenIncludeImport =
            createButton(mavenGroup, SWT.CHECK, Messages.depConfigTab_mavenIncludeImportButton,
                Messages.depConfigTab_mavenIncludeImportButtonTip, -1, 1, 1, modifyDialogListener);

        createImage(tabComposite, JettyPlugin.getIcon(JettyPlugin.JETTY_PLUGIN_DEPENDENCY_LOGO), 96, SWT.CENTER,
            SWT.TOP, 1, 1);
    }

    private void createOtherGroup(Composite tabComposite)
    {
        Composite otherGroup = createTopComposite(tabComposite, SWT.NONE, 3, -1, false, 2, 1);

        createLabel(otherGroup, Messages.depConfigTab_otherGroupTitle, 224, SWT.LEFT, 1, 1);
        mavenIncludeNone =
            createButton(otherGroup, SWT.CHECK, Messages.depConfigTab_mavenIncludeNoneButton,
                Messages.depConfigTab_mavenIncludeNoneButtonTip, 224, 2, 1, modifyDialogListener);
    }

    private void createTableGroup(Composite tabComposite)
    {
        Composite tableGroup = createTopComposite(tabComposite, SWT.NONE, 3, -1, true, 2, 1);

        defaultFilterText = Messages.depConfigTab_dependencyFilterDefault;

        dependencyFilterText =
            createText(tableGroup, SWT.BORDER, Messages.depConfigTab_dependencyFilterTip, -1, -1, 3, 1,
                new ModifyListener()
            {
                public void modifyText(ModifyEvent e)
                {
                    final ILaunchConfiguration configuration = getCurrentLaunchConfiguration();

                    if (configuration != null)
                    {
                        updateTable(JettyLaunchConfigurationAdapter.getInstance(configuration), true);
                        dependencyTable.getDisplay().syncExec(new Runnable()
                        {
                            public void run()
                            {
                                updateTable(JettyLaunchConfigurationAdapter.getInstance(configuration), true);
                            }
                        });
                    }
                }
            });

        final Color defaultFilterColor = dependencyFilterText.getForeground();

        dependencyFilterText.addFocusListener(new FocusListener()
        {
            public void focusLost(FocusEvent e)
            {
                if (dependencyFilterText.getText().trim().length() == 0)
                {
                    dependencyFilterText.setText(defaultFilterText);
                    dependencyFilterText.selectAll();
                    dependencyFilterText.setForeground(dependencyFilterText.getDisplay().getSystemColor(
                        SWT.COLOR_DARK_GRAY));

                    ILaunchConfiguration configuration = getCurrentLaunchConfiguration();

                    if (configuration != null)
                    {
                        updateTable(JettyLaunchConfigurationAdapter.getInstance(configuration), true);
                    }
                }
            }

            public void focusGained(FocusEvent e)
            {
                if (defaultFilterText.equalsIgnoreCase(dependencyFilterText.getText().trim()))
                {
                    dependencyFilterText.setText(JettyPluginUtils.EMPTY);
                    dependencyFilterText.setForeground(defaultFilterColor);

                    ILaunchConfiguration configuration = getCurrentLaunchConfiguration();

                    if (configuration != null)
                    {
                        updateTable(JettyLaunchConfigurationAdapter.getInstance(configuration), true);
                    }
                }
            }
        });
        dependencyFilterText.setText(defaultFilterText);
        dependencyFilterText.selectAll();
        dependencyFilterText.setForeground(dependencyFilterText.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));

        dependencyTable =
            createTable(tableGroup, SWT.BORDER | SWT.HIDE_SELECTION, 320, 128, 3, 1,
                Messages.depConfigTab_dependencyTableInclude, Messages.depConfigTab_dependencyTableName,
                Messages.depConfigTab_dependencyTableGlobal, Messages.depConfigTab_dependencyTableScope,
                Messages.depConfigTab_dependencyTablePath);

        Composite buttonComposite = createComposite(tableGroup, SWT.NONE, 4, -1, false, 3, 1);

        createButton(buttonComposite, SWT.NONE, Messages.depConfigTab_dependencyTableResetButton,
            Messages.depConfigTab_dependencyTableResetButtonTip, 196, 1, 1, new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                dependencyEntryList.reset();
                updateLaunchConfigurationDialog();
            }
        });

        createLabel(buttonComposite, JettyPluginUtils.EMPTY, -1, SWT.LEFT, 1, 1);
        createImage(buttonComposite, JettyPlugin.getJettyIcon(), 16, SWT.CENTER, SWT.CENTER, 1, 1);
        createLink(buttonComposite, SWT.NONE, Messages.depConfigTab_homepageLink, SWT.RIGHT, 1, 1, new Listener()
        {
            public void handleEvent(Event event)
            {
                Program.launch("http://eclipse-jetty.github.io/"); //$NON-NLS-1$
            }
        });

    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getName()
     */
    public String getName()
    {
        return Messages.depConfigTab_title;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.debug.ui.AbstractLaunchConfigurationTab#getImage()
     */
    @Override
    public Image getImage()
    {
        return JettyPlugin.getJettyDependencyIcon();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.debug.ui.AbstractLaunchConfigurationTab#getMessage()
     */
    @Override
    public String getMessage()
    {
        return Messages.depConfigTab_message;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.jdt.debug.ui.launchConfigurations.JavaLaunchTab#initializeFrom(org.eclipse.debug.core.ILaunchConfiguration)
     */
    @Override
    public void initializeFrom(final ILaunchConfiguration configuration)
    {
        super.initializeFrom(configuration);

        try
        {
            JettyLaunchConfigurationAdapter adapter = JettyLaunchConfigurationAdapter.getInstance(configuration);

            mavenIncludeCompile.setSelection(!adapter.isScopeCompileExcluded());
            mavenIncludeProvided.setSelection(!adapter.isScopeProvidedExcluded());
            mavenIncludeRuntime.setSelection(!adapter.isScopeRuntimeExcluded());
            mavenIncludeSystem.setSelection(!adapter.isScopeSystemExcluded());
            mavenIncludeTest.setSelection(!adapter.isScopeTestExcluded());
            mavenIncludeImport.setSelection(!adapter.isScopeImportExcluded());
            mavenIncludeNone.setSelection(!adapter.isScopeNoneExcluded());

            updateTable(adapter, true);
        }
        catch (final CoreException e)
        {
            JettyPlugin.error(Messages.depConfigTab_initializeFailed, e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
     */
    public void setDefaults(final ILaunchConfigurationWorkingCopy configuration)
    {
        // intentionally left blank
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
     */
    public void performApply(final ILaunchConfigurationWorkingCopy configuration)
    {
        JettyLaunchConfigurationAdapter adapter = JettyLaunchConfigurationAdapter.getInstance(configuration);

        try
        {
            adapter.updateConfigVersion();

            adapter.setScopeCompileExcluded(!mavenIncludeCompile.getSelection());
            adapter.setScopeProvidedExcluded(!mavenIncludeProvided.getSelection());
            adapter.setScopeRuntimeExcluded(!mavenIncludeRuntime.getSelection());
            adapter.setScopeSystemExcluded(!mavenIncludeSystem.getSelection());
            adapter.setScopeTestExcluded(!mavenIncludeTest.getSelection());
            adapter.setScopeImportExcluded(!mavenIncludeImport.getSelection());
            adapter.setScopeNoneExcluded(!mavenIncludeNone.getSelection());

            adapter.setExcludedGenericIds(dependencyEntryList.createExcludedGenericIds());
            adapter.setIncludedGenericIds(dependencyEntryList.createIncludedGenericIds());
            adapter.setGlobalGenericIds(dependencyEntryList.createGlobalGenericIds());

            deprecatedPerformApply(adapter);

            updateTable(adapter, false);
        }
        catch (CoreException e)
        {
            JettyPlugin.error(Messages.depConfigTab_performApplyFailed, e);
        }
    }

    @SuppressWarnings("deprecation")
    private void deprecatedPerformApply(JettyLaunchConfigurationAdapter adapter) throws CoreException
    {

        adapter.setExcludedLibs(dependencyEntryList.createExcludedLibs());
        adapter.setIncludedLibs(dependencyEntryList.createIncludedLibs());
        adapter.setGlobalLibs(dependencyEntryList.createGlobalLibs());
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.debug.ui.AbstractLaunchConfigurationTab#isValid(org.eclipse.debug.core.ILaunchConfiguration)
     */
    @Override
    public boolean isValid(final ILaunchConfiguration configuration)
    {
        setErrorMessage(null);
        setMessage(null);

        boolean scopeable = false;

        m2eLabel.setText(JettyPluginUtils.EMPTY);

        if (JettyPluginM2EUtils.isM2EAvailable())
        {
            try
            {
                scopeable =
                    JettyPluginM2EUtils.getMavenProjectFacade(JettyLaunchConfigurationAdapter
                        .getInstance(configuration)) != null;
            }
            catch (CoreException e)
            {
                // ignore
            }

            if (!scopeable)
            {
                m2eLabel.setText(Messages.depConfigTab_noM2ENature);
            }
        }
        else
        {
            m2eLabel.setText(Messages.depConfigTab_m2eNotAvailable);
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

    /**
     * Updates the dependency table
     *
     * @param adapter the configuration adapter
     * @param updateType true to update the types of the entries
     */
    private void updateTable(final JettyLaunchConfigurationAdapter adapter, final boolean updateType)
    {
        dependencyTable.getDisplay().asyncExec(new Runnable()
        {
            public void run()
            {
                try
                {
                    ILaunchDelegate[] delegates =
                        adapter.getConfiguration().getType().getDelegates(new HashSet<String>(Arrays.asList("run"))); //$NON-NLS-1$

                    if (delegates.length == 1)
                    {
                        JettyLaunchConfigurationDelegate delegate =
                            (JettyLaunchConfigurationDelegate) delegates[0].getDelegate();

                        Collection<Dependency> originalClasspathEntries = delegate.getOriginalClasspathEntries(adapter);
                        Collection<Dependency> webappClasspathEntries =
                            delegate.getWebappClasspathEntries(adapter, originalClasspathEntries);
                        Collection<Dependency> globalWebappClasspathEntries =
                            delegate.getGlobalWebappClasspathEntries(adapter, webappClasspathEntries);

                        if (dependencyEntryList.update(adapter, dependencyTable, originalClasspathEntries,
                            webappClasspathEntries, globalWebappClasspathEntries, updateType,
                            getDependencyTableFilterPattern()))
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
                    JettyPlugin.error(Messages.depConfigTab_dependencyTableUpdateFailed, e);
                }
            }
        });
    }

    protected String getDependencyTableFilterPattern()
    {
        String text = dependencyFilterText.getText().trim();

        if (defaultFilterText.equalsIgnoreCase(text))
        {
            return "*"; //$NON-NLS-1$
        }

        return "*" + text + "*"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    public final class ModifyDialogListener implements ModifyListener, SelectionListener
    {
        /**
         * {@inheritDoc}
         *
         * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
         */
        @SuppressWarnings("synthetic-access")
        public void modifyText(final ModifyEvent e)
        {
            updateLaunchConfigurationDialog();
        }

        /**
         * {@inheritDoc}
         *
         * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
         */
        public void widgetDefaultSelected(final SelectionEvent arg0)
        {
            // intentionally left blank
        }

        /**
         * {@inheritDoc}
         *
         * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
         */
        @SuppressWarnings("synthetic-access")
        public void widgetSelected(final SelectionEvent arg0)
        {
            updateLaunchConfigurationDialog();
        }
    }
}
