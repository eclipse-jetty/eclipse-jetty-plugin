package net.sourceforge.eclipsejetty.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

/**
 * @author thred
 */
public abstract class FilenameMatcher implements FilenameFilter
{

    /**
     * Matches all entries.
     * 
     * @return the matcher
     */
    public static FilenameMatcher all()
    {
        return new FilenameMatcher()
        {

            public boolean accept(File dir, String name)
            {
                return true;
            }

            @Override
            public String toString()
            {
                return "all";
            }

        };
    }

    /**
     * All matchers must match
     * 
     * @param matchers the matchers
     * @return the matcher
     */
    public static FilenameMatcher and(final FilenameMatcher... matchers)
    {
        if ((matchers == null) || (matchers.length == 0))
        {
            return all();
        }

        if (matchers.length == 1)
        {
            return matchers[0];
        }

        return new FilenameMatcher()
        {

            public boolean accept(File dir, String name)
            {
                for (FilenameMatcher matcher : matchers)
                {
                    if (!matcher.accept(dir, name))
                    {
                        return false;
                    }
                }

                return true;
            }

            @Override
            public String toString()
            {
                return "and" + Arrays.toString(matchers);
            }

        };
    }

    /**
     * At least one matcher must match
     * 
     * @param matchers the matchers
     * @return the matcher
     */
    public static FilenameMatcher or(final FilenameMatcher... matchers)
    {
        if ((matchers == null) || (matchers.length == 0))
        {
            return all();
        }

        if (matchers.length == 1)
        {
            return matchers[0];
        }

        return new FilenameMatcher()
        {

            public boolean accept(File dir, String name)
            {
                for (FilenameMatcher matcher : matchers)
                {
                    if (matcher.accept(dir, name))
                    {
                        return true;
                    }
                }

                return false;
            }

            @Override
            public String toString()
            {
                return "or" + Arrays.toString(matchers);
            }

        };
    }

    /**
     * The matcher must not match
     * 
     * @param matcher the matcher
     * @return the matcher
     */
    public static FilenameMatcher not(final FilenameMatcher matcher)
    {
        return new FilenameMatcher()
        {

            public boolean accept(File dir, String name)
            {
                return !matcher.accept(dir, name);
            }

            @Override
            public String toString()
            {
                return "not[" + matcher + "]";
            }

        };
    }

    public static FilenameMatcher ofType(final String s)
    {
        return new FilenameMatcher()
        {

            public boolean accept(File dir, String name)
            {
                return name.endsWith("." + s);
            }

            @Override
            public String toString()
            {
                return "ofType(" + s + ")";
            }
        };
    }

    public static FilenameMatcher named(String regex)
    {
        final RegularMatcher matcher = new RegularMatcher(regex);

        return new FilenameMatcher()
        {

            public boolean accept(File dir, String name)
            {
                return matcher.matches(name);
            }

            @Override
            public String toString()
            {
                return "withName(" + matcher + ")";
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract String toString();

}
