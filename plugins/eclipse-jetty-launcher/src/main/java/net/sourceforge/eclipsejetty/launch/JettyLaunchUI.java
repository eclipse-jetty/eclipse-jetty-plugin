package net.sourceforge.eclipsejetty.launch;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

public class JettyLaunchUI
{

    protected static Label createLabel(final Composite composite, final String text, final int widthHint,
        int horizontalSpan, int verticalSpan)
    {
        Label label = new Label(composite, SWT.NONE);

        GridData gridData =
            new GridData((widthHint < 0) ? SWT.FILL : SWT.LEFT, (verticalSpan <= 1) ? SWT.CENTER : SWT.TOP,
                widthHint < 0, false, horizontalSpan, verticalSpan);

        if (widthHint >= 0)
        {
            gridData.widthHint = widthHint;
        }

        label.setLayoutData(gridData);
        label.setText(text);

        return label;
    }

    protected static Button createButton(final Composite composite, int style, final String text, final int widthHint,
        int horizontalSpan, int verticalSpan, SelectionListener... selectionListeners)
    {
        return createButton(composite, style, null, text, widthHint, horizontalSpan, verticalSpan, selectionListeners);
    }

    protected static Button createButton(final Composite composite, int style, final Image image, final int widthHint,
        int horizontalSpan, int verticalSpan, SelectionListener... selectionListeners)
    {
        return createButton(composite, style, image, null, widthHint, horizontalSpan, verticalSpan, selectionListeners);
    }

    protected static Button createButton(final Composite composite, int style, Image image, final String text,
        final int widthHint, int horizontalSpan, int verticalSpan, SelectionListener... selectionListeners)
    {
        Button button = new Button(composite, style);

        GridData gridData =
            new GridData((widthHint < 0) ? SWT.FILL : SWT.LEFT, (verticalSpan <= 1) ? SWT.CENTER : SWT.TOP,
                widthHint < 0, false, horizontalSpan, verticalSpan);

        if (widthHint >= 0)
        {
            gridData.widthHint = widthHint;
        }

        button.setLayoutData(gridData);

        if (text != null)
        {
            button.setText(text);
        }

        if (image != null)
        {
            button.setImage(image);
        }

        if (selectionListeners != null)
        {
            for (SelectionListener selectionListener : selectionListeners)
            {
                button.addSelectionListener(selectionListener);
            }
        }

        return button;
    }

    protected static Text createText(final Composite composite, int style, final int widthHint, int heightHint,
        int horizontalSpan, int verticalSpan, ModifyListener... modifyListeners)
    {
        Text text = new Text(composite, style);

        GridData gridData =
            new GridData((widthHint < 0) ? SWT.FILL : SWT.LEFT, (verticalSpan <= 1) ? SWT.CENTER : SWT.TOP,
                widthHint < 0, false, horizontalSpan, verticalSpan);

        if (widthHint >= 0)
        {
            gridData.widthHint = widthHint;
        }

        if (heightHint >= 0)
        {
            gridData.heightHint = heightHint;
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

    protected static Table createTable(Composite composite, int style, int widthHint, int heightHint,
        int horizontalSpan, int verticalSpan, String... titles)
    {
        Table table = new Table(composite, style);
        table.setLinesVisible(false);
        table.setHeaderVisible(true);

        GridData gridData =
            new GridData((widthHint < 0) ? SWT.FILL : SWT.LEFT, SWT.TOP, widthHint < 0, true, horizontalSpan,
                verticalSpan);

        if (widthHint >= 0)
        {
            gridData.widthHint = widthHint;
        }

        if (heightHint >= 0)
        {
            gridData.minimumHeight = heightHint;
        }

        table.setLayoutData(gridData);

        for (String title : titles)
        {
            TableColumn column = new TableColumn(table, SWT.NONE);
            column.setText(title);
        }

        return table;
    }

}
