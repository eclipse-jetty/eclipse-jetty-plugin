package net.sourceforge.eclipsejetty.launch.util;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

/**
 * Tests the project of a specified {@link IResource} to contains an WEB-INF/web.xml file
 * 
 * @author thred
 */
public class JettyLaunchPossiblePropertiesTester extends PropertyTester
{

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object, java.lang.String, java.lang.Object[],
     *      java.lang.Object)
     */
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
    {
        if (!(receiver instanceof IResource))
        {
            return false;
        }

        IProject project = ((IResource) receiver).getProject();

        if (project == null)
        {
            return false;
        }

        if (!project.exists())
        {
            return false;
        }

        if (!project.isOpen())
        {
            return false;
        }

        try
        {
            return JettyLaunchUtils.findWebXML(project) != null;
        }
        catch (CoreException e)
        {
            return false;
        }
    }

}
