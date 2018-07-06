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
package net.sourceforge.eclipsejetty;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.PatternSyntaxException;

import net.sourceforge.eclipsejetty.util.Dependency;
import net.sourceforge.eclipsejetty.util.RegularMatcher;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;

/**
 * Some utilities
 * 
 * @author Christian K&ouml;berl
 * @author Manfred Hantschel
 */
public class JettyPluginUtils
{

    public static final String EMPTY = "";

    /**
     * A collator set to primary strength, which means 'a', 'A' and '&auml;' is the same
     */
    public static final Collator DICTIONARY_COLLATOR;

    public static final Comparator<String> DICTIONARY_COMPARATOR = new Comparator<String>()
    {

        public int compare(String left, String right)
        {
            return dictionaryCompare(left, right);
        }

    };

    static
    {
        DICTIONARY_COLLATOR = Collator.getInstance();

        DICTIONARY_COLLATOR.setStrength(Collator.PRIMARY);
        DICTIONARY_COLLATOR.setDecomposition(Collator.CANONICAL_DECOMPOSITION);
    }

    public static boolean equals(final Object obj0, final Object obj1)
    {
        return ((obj0 == null) && (obj1 == null)) || ((obj0 != null) && (obj0.equals(obj1)));
    }

    /**
     * Compares the two objects. If one of the objects is null, it will always be greater than the other object. If both
     * objects are null, they are equal.
     * 
     * @param <TYPE> the type of the object
     * @param left the first object
     * @param right the second object
     * @return the result of the compare function
     */
    public static <TYPE extends Comparable<TYPE>> int compare(final TYPE left, final TYPE right)
    {
        if (left == null)
        {
            if (right != null)
            {
                return 1;
            }
        }
        else
        {
            if (right != null)
            {
                return left.compareTo(right);
            }

            return -1;
        }

        return 0;
    }

    /**
     * Compares the two objects. If one of the objects is null, it will always be greater than the other object. If both
     * objects are null, they are equal. Uses the comparator to compare the objects.
     * 
     * @param <TYPE> the type of the object
     * @param comparator the comparator to be used
     * @param left the first object
     * @param right the second object
     * @return the result of the compare function
     */
    public static <TYPE> int compare(final Comparator<TYPE> comparator, final TYPE left, final TYPE right)
    {
        if (left == null)
        {
            if (right != null)
            {
                return 1;
            }
        }
        else
        {
            if (right != null)
            {
                return comparator.compare(left, right);
            }

            return -1;
        }

        return 0;
    }

    /**
     * Compares the strings using a dictionary collator. If one of the objects is null, it will always be greater than
     * the other object. If both objects are null, they are equal.
     * 
     * @param left the first string
     * @param right the second string
     * @return the result of the compare function
     */
    public static int dictionaryCompare(final String left, final String right)
    {
        return compare(DICTIONARY_COLLATOR, left, right);
    }

    public static List<RegularMatcher> extractPatterns(final List<RegularMatcher> list, final Collection<String> text)
        throws IllegalArgumentException
    {
        for (final String entry : text)
        {
            if (entry.trim().length() > 0)
            {
                try
                {
                    list.add(new RegularMatcher(entry.trim()));
                }
                catch (final PatternSyntaxException e)
                {
                    throw new IllegalArgumentException(
                        String.format("Invalid pattern: %s (%s)", entry, e.getMessage()), e);
                }
            }
        }

        return list;
    }

    public static String link(String[] values)
    {
        StringBuilder result = new StringBuilder();

        if (values != null)
        {
            for (int i = 0; i < values.length; i += 1)
            {
                if (i > 0)
                {
                    // result.append(File.pathSeparator); // it seems, Jetty was built for Windows
                    result.append(";");
                }

                result.append(values[i]);
            }
        }

        return result.toString();
    }

    public static String toCommaSeparatedString(Collection<String> values)
    {
        if (values == null)
        {
            return null;
        }

        List<String> list = new ArrayList<String>(values);

        Collections.sort(list, DICTIONARY_COMPARATOR);

        StringBuilder result = new StringBuilder();

        for (String value : values)
        {
            if (result.length() > 0)
            {
                result.append(", ");
            }

            result.append(value);
        }

        return result.toString();
    }

    public static Collection<String> fromCommaSeparatedString(String value)
    {
        if (value == null)
        {
            return null;
        }

        value = value.trim();

        if (value.length() <= 0)
        {
            return Collections.<String> emptySet();
        }

        Collection<String> result = new LinkedHashSet<String>();
        String[] values = value.split("[,\\n\\r]");

        for (String current : values)
        {
            result.add(current.trim());
        }

        return result;
    }

    public static String resolveVariables(String s)
    {
        if (s == null)
        {
            return null;
        }

        try
        {
            s = VariablesPlugin.getDefault().getStringVariableManager().performStringSubstitution(s);
        }
        catch (CoreException e)
        {
            // ignore
        }

        return s;
    }

    public static String[] toLocationArrayFromScoped(Collection<Dependency> classpathEntries)
    {
        return toLocationArrayFromScoped(classpathEntries.toArray(new Dependency[classpathEntries.size()]));
    }

    public static String[] toLocationArray(Collection<IRuntimeClasspathEntry> classpathEntries)
    {
        return toLocationArrayFromScoped(classpathEntries.toArray(new Dependency[classpathEntries.size()]));
    }

    public static String[] toLocationArrayFromScoped(Dependency... classpathEntries)
    {
        Collection<String> results = toLocationCollectionFromScoped(classpathEntries);

        return results.toArray(new String[results.size()]);
    }

    public static String[] toLocationArray(IRuntimeClasspathEntry... classpathEntries)
    {
        Collection<String> results = toLocationCollection(classpathEntries);

        return results.toArray(new String[results.size()]);
    }

    public static Collection<String> toLocationCollectionFromScoped(Collection<Dependency> classpathEntries)
    {
        return toLocationCollectionFromScoped(classpathEntries.toArray(new Dependency[classpathEntries.size()]));
    }

    public static Collection<String> toLocationCollection(Collection<IRuntimeClasspathEntry> classpathEntries)
    {
        return toLocationCollection(classpathEntries.toArray(new IRuntimeClasspathEntry[classpathEntries.size()]));
    }

    public static Collection<String> toLocationCollectionFromScoped(Dependency... classpathEntries)
    {
        Set<String> results = new LinkedHashSet<String>();

        for (Dependency entry : classpathEntries)
        {
            String location = toLocation(entry);

            if (location != null)
            {
                results.add(location);
            }
        }

        return results;
    }

    public static Collection<String> toLocationCollection(IRuntimeClasspathEntry... classpathEntries)
    {
        Set<String> results = new LinkedHashSet<String>();

        for (IRuntimeClasspathEntry entry : classpathEntries)
        {
            String location = toLocation(entry);

            if (location != null)
            {
                results.add(location);
            }
        }

        return results;
    }

    public static String toLocation(Dependency entry)
    {
        return toLocation(entry.getRuntimeClasspathEntry());
    }

    public static String toLocation(IRuntimeClasspathEntry entry)
    {
        String location = entry.getLocation();

        if (location == null)
        {
            return null;
        }

        return location.replace('\\', '/');
    }

    public static String prepend(String s, String prefix)
    {
        if (!s.startsWith(prefix))
        {
            s = prefix + s;
        }

        return s;
    }

    public static String getName(String location)
    {
        int index = location.lastIndexOf('/');

        if (index < 0)
        {
            return location;
        }

        return location.substring(index + 1);
    };

    public static String getPath(String location)
    {
        int index = location.lastIndexOf('/');

        if (index < 0)
        {
            return JettyPluginUtils.EMPTY;
        }

        return location.substring(0, index);
    }

    public static void copy(InputStream in, OutputStream out) throws IOException
    {
        int length = 0;
        byte[] buffer = new byte[4096];

        while ((length = in.read(buffer)) >= 0)
        {
            out.write(buffer, 0, length);
        }
    }

    public static File getNonRandomTempFile(String prefix, String name, String suffix)
    {
        return new File(System.getProperty("java.io.tmpdir"), prefix + fixFilename(name) + suffix);
    }

    public static String fixFilename(String filename)
    {
        return filename.replaceAll("[\\W]", "_");
    }

    /**
     * Tries to resolve the file: first as absolute (external) file, second as workspace (internal) file, third as
     * project file. Returns null if the file does not exist. The name may contain variables.
     * 
     * @param project the project, may be null
     * @param name the name of the file
     * @return the file
     */
    public static File resolveFile(IProject project, String name)
    {
        if (name == null)
        {
            return null;
        }

        name = JettyPluginUtils.resolveVariables(name);

        File file = new File(name);

        if ((file.isAbsolute()) && (file.exists()))
        {
            return file;
        }

        try
        {
            IFile resource = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(name));

            if (resource.exists())
            {
                IPath location = resource.getLocation();

                if (location != null)
                {
                    return location.toFile().getAbsoluteFile();
                }
            }
        }
        catch (IllegalArgumentException e)
        {
            // ignore
        }

        if (project == null)
        {
            return null;
        }

        try
        {
            IFile resource = project.getFile(name);

            if (resource.exists())
            {
                IPath location = resource.getLocation();

                if (location != null)
                {
                    return location.toFile().getAbsoluteFile();
                }
            }
        }
        catch (IllegalArgumentException e)
        {
            // ignore
        }

        return null;
    }

    /**
     * Tries to resolve the folder: first as absolute (external) file, second as workspace (internal) file, third as
     * project file. Returns null if the file does not exist. The name may contain variables.
     * 
     * @param project the project, may be null
     * @param name the name of the file
     * @return the file
     */
    public static File resolveFolder(IProject project, String name)
    {
        if (name == null)
        {
            return null;
        }

        name = JettyPluginUtils.resolveVariables(name);

        File file = new File(name);

        if ((file.isAbsolute()) && (file.exists() && (file.isDirectory())))
        {
            return file;
        }

        try
        {
            IFolder resource = ResourcesPlugin.getWorkspace().getRoot().getFolder(new Path(name));

            if (resource.exists())
            {
                IPath location = resource.getLocation();

                if (location != null)
                {
                    return location.toFile().getAbsoluteFile();
                }
            }
        }
        catch (IllegalArgumentException e)
        {
            // ignore
        }

        if (project == null)
        {
            return null;
        }

        try
        {
            IFolder resource = project.getFolder(name);

            if (resource.exists())
            {
                IPath location = resource.getLocation();

                if (location != null)
                {
                    return location.toFile().getAbsoluteFile();
                }
            }
        }
        catch (IllegalArgumentException e)
        {
            // ignore
        }

        return null;
    }

    /**
     * Converts the path to a project relative path, if it points to a location within the project
     * 
     * @param project the project
     * @param path the path
     * @return the corrected path
     */
    public static String toRelativePath(IProject project, String path)
    {
        if (project == null)
        {
            return path;
        }

        if ((path == null) || (path.length() == 0))
        {
            return path;
        }

        IPath resource = new Path(path);

        if (resource.isAbsolute())
        {
            IPath location = project.getLocation();

            if (location != null)
            {
                return resource.makeRelativeTo(location).toString();
            }
        }

        return resource.makeRelativeTo(project.getFullPath()).toString();
    }

    public static IProject getProject(String projectName)
    {
        if ((projectName == null) || (projectName.length() == 0))
        {
            return null;
        }

        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);

        if (!project.exists())
        {
            return null;
        }

        return project;
    }

}
