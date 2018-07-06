package net.sourceforge.eclipsejetty.launch.util;

import net.sourceforge.eclipsejetty.JettyPlugin;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;

public class WebAppPathLabelProvider extends BaseLabelProvider implements ILabelProvider
{

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
     */
    public Image getImage(Object element)
    {
        return JettyPlugin.getIcon(JettyPlugin.FOLDER_ICON);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
     */
    public String getText(Object element)
    {
        return String.valueOf(element);
    }

}
