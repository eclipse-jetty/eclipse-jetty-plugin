package net.sourceforge.eclipsejetty.jetty;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import net.sourceforge.eclipsejetty.util.DOMBuilder;

public abstract class AbstractBuilder
{

    protected static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public AbstractBuilder()
    {
        super();
    }

    public DOMBuilder build(boolean warning)
    {
        DOMBuilder builder = new DOMBuilder();

        if (warning)
        {
            StringBuilder comment = new StringBuilder();

            comment.append(LINE_SEPARATOR);
            comment.append("This is a temporary file.");
            comment.append(LINE_SEPARATOR);
            comment.append("It was created automatically by the Eclipse Jetty Plugin.");
            comment.append(LINE_SEPARATOR);
            comment.append("There is no need, nor sense to edit this file!");
            comment.append(LINE_SEPARATOR);

            builder.comment(comment);
        }

        buildBody(builder);

        return builder;
    }

    protected abstract void buildBody(DOMBuilder builder);

    public void write(File file, boolean formatted) throws IOException
    {
        FileOutputStream out = new FileOutputStream(file);

        try
        {
            build(formatted).write(out, formatted);
        }
        finally
        {
            out.close();
        }
    }

}
