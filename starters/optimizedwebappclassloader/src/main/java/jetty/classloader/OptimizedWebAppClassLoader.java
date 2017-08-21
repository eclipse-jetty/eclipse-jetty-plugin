package jetty.classloader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipOutputStream;

import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.webapp.WebAppClassLoader;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * Optimized class loader for Jetty <br>
 * Useful in case you have a lot of open (exploded) projects on your classpath
 * and keep wondering why your server start (class-loading) is so slow. <br>
 * It turns out, that class loading is much faster with *.jar on the classpath
 * as then the classloader can easily evaluate if a class is present in a *.jar
 * or not. When dealing with exploded target/classes folders it turned out that
 * every class being loaded is check for existance in every directory which
 * takes forever. <br>
 * Therefore this class loader does the following:
 * <ul>
 * <li>Override addClassPath() from
 * org.eclipse.jetty.webapp.WebAppClassLoader</li>
 * <li>If a class path entry is in fact a directory -> create a JAR-file for it
 * and append the JAR-file not the directory to the classpath</li>
 * <li>Exclude some special directories - for example the web assembly must be
 * exploded</li>
 * <li>Moreover tapestry web-modules need to be kept exploded otherwise
 * tapestry's live reloading does not work.</li>
 * </ul>
 * This class used a highly optimized version of String.replace which is more
 * than 4-times faster, resulting in 10sec JAR-file generation instead of ~60.
 * (for CrossNG SWP) <br>
 * With this optimized class loader the server starts as fast as when only the
 * web assembly project is opened. Independent of the amount of open projects in
 * your eclipse workspace. <br>
 * Tests resulted in 8min5sec with the default class loader and 3min48sec with
 * the optimized version. >50% improvement. <br>
 * Debugging works, Tapestry live reloading only with excluded web-directories.
 * (so that they are kept exploded and not packaged.)
 * 
 * @author Robert Schmelzer (ext.rschmelzer)
 * @author Stefan Starke (ext.stas)
 */
public class OptimizedWebAppClassLoader extends WebAppClassLoader {
    private static final Logger LOG = Log.getLogger(OptimizedWebAppClassLoader.class);

    // Directory path to store and reference the package JAR-files to
    private static String TEMP_JAR_DIR = System.getProperty("java.io.tmpdir") + "jetty9JarCache" + File.separatorChar;
    // Comma separated list of resources (regex) that are not package but left
    // exploded
    // Always add assembly. Add ".*assembly.*,.*web.*" do enable tapestry live
    // reloading.
    private static String EXCLUDED_RESOURCES = ".*assembly.*";

    public OptimizedWebAppClassLoader(WebAppContext context, String exclusionPattern) throws IOException {
        super(context);
        EXCLUDED_RESOURCES = exclusionPattern;
        LOG.debug("constructor OptimizedWebAppClassLoader(context)");
        if (context.getExtraClasspath() != null)
            addClassPathInternal(context.getExtraClasspath());
    }

    // Called by super constructor, implemented empty so that nothing happens
    // We want to call addClassPath manually once the exclusion pattern is set
    // See -> addClassPathInternal()
    @Override
    public void addClassPath(String classPath) throws IOException {
    }

    public void addClassPathInternal(String classPath) throws IOException {
        LOG.debug(String.format("addClassPath: {}", classPath));

        if (classPath == null)
            return;

        // Correct path to include the context name
        TEMP_JAR_DIR = TEMP_JAR_DIR
                + ((ContextHandler) super.getContext()).getContextPath().replace("/", "").replace("\\", "")
                + File.separatorChar;
        LOG.warn(
                "Start melting open projects into strong java archives (Exclusion Pattern: '{}'. Temporary archive storage: {})",
                EXCLUDED_RESOURCES, TEMP_JAR_DIR);

        long start = new Date().getTime();
        int jarCnt = 0;
        int excludedJarCnt = 0;

        // Create temp jar file directory. If it already exists, clear it
        if (Files.exists(Paths.get(TEMP_JAR_DIR))) {
            for (File file : Paths.get(TEMP_JAR_DIR).toFile().listFiles())
                if (!file.isDirectory())
                    file.delete();
        } else {
            Files.createDirectories(Paths.get(TEMP_JAR_DIR));
        }

        String[] excludedResources = EXCLUDED_RESOURCES.split(",");

        StringTokenizer tokenizer = new StringTokenizer(classPath, ",;");
        while (tokenizer.hasMoreTokens()) {
            Resource resource = getContext().newResource(tokenizer.nextToken().trim());

            // Add the resource
            if (resource.isDirectory() && resource instanceof ResourceCollection)
                addClassPath(resource);
            else {
                // Resolve file path if possible
                File file = resource.getFile();
                if (file != null && !resource.isDirectory()) {
                    URL url = resource.getURI().toURL();
                    addURL(url);
                    LOG.debug("Left {} untouched as it is not a directory.", file.getPath());
                } else if (resource.isDirectory()) {
                    jarCnt++;
                    boolean doNotPackage = false;
                    for (String excludedResource : excludedResources) {
                        if (resource.getName().toLowerCase().matches(excludedResource)) {
                            doNotPackage = true;
                            LOG.debug("Excluded {} from packaging as it matches regex {}.", resource.getName(),
                                    excludedResource);
                        }
                    }
                    if (doNotPackage) {
                        addURL(resource.getURI().toURL());
                        excludedJarCnt++;
                    } else {
                        Path tmpJar = Files.createFile(Paths.get(TEMP_JAR_DIR + UUID.randomUUID() + ".jar"));
                        // Manifest m = new Manifest();
                        JarOutputStream zs = new JarOutputStream(Files.newOutputStream(tmpJar));
                        createJAR(resource.getFile().getAbsolutePath(), zs);
                        zs.close();
                        addURL(tmpJar.toUri().toURL());
                        LOG.debug("Packaged {} to {}.", resource.getName(), tmpJar.toAbsolutePath().toString());
                    }
                } else {
                    LOG.debug("Check file exists and is not nested jar: " + resource);
                    throw new IllegalArgumentException(
                            "File not resolvable or incompatible with URLClassloader: " + resource);
                }
            }

        }
        long timeInMs = new Date().getTime() - start;
        LOG.warn(
                "Finished melting open projects into strong java archives within {} seconds. (Found {} open projects, Created {} JARs, Excluded {} JARs)",
                getTimeString(timeInMs), jarCnt, jarCnt - excludedJarCnt, excludedJarCnt);
    }

    /** return time in format 23.456 */
    private String getTimeString(long millis) {
        int seconds = (int) ((millis / 1000) % 60);
        int milliseconds = (int) (millis % 1000);
        return String.format("%02d.%03d", seconds, milliseconds);
    }

    private void createJAR(String sourceDirPath, ZipOutputStream zs) throws IOException {
        Path pp = Paths.get(sourceDirPath);
        Files.walk(pp).forEach(path -> {
            // Do not use String.replace. It is four times slower (45secs
            // slower) than this implementation.
            // You do not believe it? Try it. ;)
            String name = replace(pp.relativize(path).toString(), "\\", "/");
            if (Files.isDirectory(path)) {
                name = name + "/";
            }
            JarEntry zipEntry = new JarEntry(name);
            try {
                zs.putNextEntry(zipEntry);
                if (!Files.isDirectory(path)) {
                    zs.write(Files.readAllBytes(path));
                }
                zs.closeEntry();
            } catch (Exception e) {
                System.err.println(e);
            }
        });
    }

    /**
     * Super hyper fast String.replace - thx to stackoverflow <br>
     * https://stackoverflow.com/questions/16228992/commons-lang-stringutils-replace-performance-vs-string-replace
     * 
     * @param source
     *            .
     * @param os
     *            .
     * @param ns
     *            .
     * @return .
     */
    private String replace(String source, String os, String ns) {
        if (source == null) {
            return null;
        }
        int i = 0;
        if ((i = source.indexOf(os, i)) >= 0) {
            char[] sourceArray = source.toCharArray();
            char[] nsArray = ns.toCharArray();
            int oLength = os.length();
            StringBuilder buf = new StringBuilder(sourceArray.length);
            buf.append(sourceArray, 0, i).append(nsArray);
            i += oLength;
            int j = i;
            // Replace all remaining instances of oldString with newString.
            while ((i = source.indexOf(os, i)) > 0) {
                buf.append(sourceArray, j, i - j).append(nsArray);
                i += oLength;
                j = i;
            }
            buf.append(sourceArray, j, sourceArray.length - j);
            source = buf.toString();
            buf.setLength(0);
        }
        return source;
    }

}
