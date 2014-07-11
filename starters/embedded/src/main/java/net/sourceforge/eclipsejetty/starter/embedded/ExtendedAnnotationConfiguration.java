package net.sourceforge.eclipsejetty.starter.embedded;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.LinkedHashSet;

import org.eclipse.jetty.annotations.AbstractDiscoverableAnnotationHandler;
import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.annotations.AnnotationDecorator;
import org.eclipse.jetty.annotations.AnnotationParser;
import org.eclipse.jetty.annotations.AnnotationParser.DiscoverableAnnotationHandler;
import org.eclipse.jetty.annotations.ClassNameResolver;
import org.eclipse.jetty.annotations.WebFilterAnnotationHandler;
import org.eclipse.jetty.annotations.WebListenerAnnotationHandler;
import org.eclipse.jetty.annotations.WebServletAnnotationHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * This class works around a bug in Jetty 8 {@link http://bugs.eclipse.org/bugs/show_bug.cgi?id=404176}, which does not
 * scan external directories for annotations. The solution is based on
 * 
 * <pre>
 * {@link http://stackoverflow.com/questions/13222071/spring-3-1-webapplicationinitializer-embedded-jetty-8-annotationconfiguration}.
 * </pre>
 * 
 * @author Manfred Hantschel
 */
public class ExtendedAnnotationConfiguration extends AnnotationConfiguration
{

    public ExtendedAnnotationConfiguration()
    {
        super();
    }

    @Override
    public void configure(WebAppContext context) throws Exception
    {
        boolean metadataComplete = context.getMetaData().isMetaDataComplete();
        
        context.addDecorator(new AnnotationDecorator(context));

        //Even if metadata is complete, we still need to scan for ServletContainerInitializers - if there are any
        AnnotationParser parser = null;
        
        if (!metadataComplete)
        {
            //If metadata isn't complete, if this is a servlet 3 webapp or isConfigDiscovered is true, we need to search for annotations
            if ((context.getServletContext().getEffectiveMajorVersion() >= 3) || context.isConfigurationDiscovered())
            {
                _discoverableAnnotationHandlers.add(new WebServletAnnotationHandler(context));
                _discoverableAnnotationHandlers.add(new WebFilterAnnotationHandler(context));
                _discoverableAnnotationHandlers.add(new WebListenerAnnotationHandler(context));
            }
        }

        //Regardless of metadata, if there are any ServletContainerInitializers with @HandlesTypes, then we need to scan all the
        //classes so we can call their onStartup() methods correctly
        createServletContainerInitializerAnnotationHandlers(context, getNonExcludedInitializers(context));

        if (!_discoverableAnnotationHandlers.isEmpty() || (_classInheritanceHandler != null)
            || !_containerInitializerAnnotationHandlers.isEmpty())
        {
            parser = createAnnotationParser();

            parse(context, parser);

            for (DiscoverableAnnotationHandler h : _discoverableAnnotationHandlers)
            {
                context.getMetaData().addDiscoveredAnnotations(
                    ((AbstractDiscoverableAnnotationHandler) h).getAnnotationList());
            }
        }
    }

    private void parse(final WebAppContext context, AnnotationParser parser) throws Exception
    {
        Collection<Resource> _resources = collectResources(new LinkedHashSet<Resource>(), context.getClassLoader());

        for (Resource _resource : _resources)
        {
            if (_resource == null)
            {
                return;
            }

            parser.clearHandlers();
            
            for (DiscoverableAnnotationHandler h : _discoverableAnnotationHandlers)
            {
                if (h instanceof AbstractDiscoverableAnnotationHandler)
                {
                    ((AbstractDiscoverableAnnotationHandler) h).setResource(null); //
                }
            }
            
            parser.registerHandlers(_discoverableAnnotationHandlers);
            parser.registerHandler(_classInheritanceHandler);
            parser.registerHandlers(_containerInitializerAnnotationHandlers);

            parser.parse(_resource, new ClassNameResolver()
            {
                @Override
                public boolean isExcluded(String name)
                {
                    if (context.isSystemClass(name))
                    {
                        return true;
                    }
                    if (context.isServerClass(name))
                    {
                        return false;
                    }
                    
                    return false;
                }

                @Override
                public boolean shouldOverride(String name)
                {
                    //looking at webapp classpath, found already-parsed class of same name - did it come from system or duplicate in webapp?
                    if (context.isParentLoaderPriority())
                    {
                        return false;
                    }
                    
                    return true;
                }
            });
        }
    }

    private Collection<Resource> collectResources(Collection<Resource> resources, ClassLoader classLoader)
        throws IOException
    {
        if (classLoader instanceof URLClassLoader)
        {
            URL[] urls = ((URLClassLoader) classLoader).getURLs();

            for (URL url : urls)
            {
                resources.add(Resource.newResource(url));
            }
        }

        return resources;
    }

}
