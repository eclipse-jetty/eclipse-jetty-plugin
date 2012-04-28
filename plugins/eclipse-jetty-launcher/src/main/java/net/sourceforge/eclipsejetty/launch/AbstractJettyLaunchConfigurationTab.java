package net.sourceforge.eclipsejetty.launch;

import org.eclipse.jdt.debug.ui.launchConfigurations.JavaLaunchTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * UI
 * 
 * @author Christian K&ouml;berl
 * @author Manfred Hantschel
 */
public abstract class AbstractJettyLaunchConfigurationTab extends JavaLaunchTab
{

    protected Label createLabel(final Composite composite, final String text, final int widthHint, int horizontalSpan)
    {
        Label label = new Label(composite, SWT.NONE);

        GridData gridData =
            new GridData((widthHint < 0) ? SWT.FILL : SWT.LEFT, SWT.CENTER, widthHint < 0, false, horizontalSpan, 1);

        if (widthHint >= 0)
        {
            gridData.widthHint = widthHint;
        }

        label.setLayoutData(gridData);
        label.setText(text);

        return label;
    }

    protected Button createButton(final Composite composite, int style, final String text, final int widthHint,
        SelectionListener... selectionListeners)
    {
        Button button = new Button(composite, style);

        GridData gridData = new GridData((widthHint < 0) ? SWT.FILL : SWT.LEFT, SWT.CENTER, widthHint < 0, false, 1, 1);

        if (widthHint >= 0)
        {
            gridData.widthHint = widthHint;
        }

        button.setLayoutData(gridData);
        button.setText(text);

        if (selectionListeners != null)
        {
            for (SelectionListener selectionListener : selectionListeners)
            {
                button.addSelectionListener(selectionListener);
            }
        }

        return button;
    }

    protected Text createText(final Composite composite, final int widthHint, int horizontalSpan,
        ModifyListener... modifyListeners)
    {
        Text text = new Text(composite, SWT.BORDER);

        GridData gridData =
            new GridData((widthHint < 0) ? SWT.FILL : SWT.LEFT, SWT.CENTER, widthHint < 0, false, horizontalSpan, 1);

        if (widthHint >= 0)
        {
            gridData.widthHint = widthHint;
        }

        text.setLayoutData(gridData);

        if (modifyListeners != null)
        {
            for (ModifyListener modifyListener : modifyListeners)
            {
                text.addModifyListener(modifyListener);
            }
        }

        return text;
    }
}
