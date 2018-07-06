package net.sourceforge.eclipsejetty.jetty;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import net.sourceforge.eclipsejetty.JettyPlugin;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;

public class JettyVersion
{

    private static class CacheKey
    {
        private final String jettyPath;
        private final boolean embedded;

        public CacheKey(String jettyPath, boolean embedded)
        {
            super();

            this.jettyPath = jettyPath;
            this.embedded = embedded;
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;

            result = (prime * result) + (embedded ? 1231 : 1237);
            result = (prime * result) + ((jettyPath == null) ? 0 : jettyPath.hashCode());

            return result;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
            {
                return true;
            }

            if (obj == null)
            {
                return false;
            }

            if (getClass() != obj.getClass())
            {
                return false;
            }

            CacheKey other = (CacheKey) obj;

            if (embedded != other.embedded)
            {
                return false;
            }

            if (jettyPath == null)
            {
                if (other.jettyPath != null)
                {
                    return false;
                }
            }
            else if (!jettyPath.equals(other.jettyPath))
            {
                return false;
            }

            return true;
        }
    }

    private static final long MAX_TIME_TO_LIVE = 60 * 1000;

    private static final Map<CacheKey, JettyVersion> CACHE = new HashMap<CacheKey, JettyVersion>();

    public static JettyVersion detect(String jettyPath, boolean embedded)
    {
        CacheKey cacheKey = new CacheKey(jettyPath, embedded);
        JettyVersion version = CACHE.get(cacheKey);

        if ((version != null) && ((System.currentTimeMillis() - version.timestamp) < MAX_TIME_TO_LIVE))
        {
            return version;
        }

        if (embedded)
        {
            try
            {
                URL url = FileLocator.find(JettyPlugin.getDefault().getBundle(),
                    Path.fromOSString("lib/jetty/jetty-server.jar"), null);

                if (url == null)
                {
                    throw new IllegalArgumentException(
                        "Could not detect Jetty version of embedded Jetty (did not find jetty-server.jar)");
                }

                version = new JettyVersion(detect(new File(FileLocator.toFileURL(url).getFile())), true);
            }
            catch (IOException e)
            {
                throw new IllegalArgumentException("Could not detect Jetty version of embedded Jetty", e);
            }
        }
        else
        {
            final File jettyLibDir = new File(jettyPath, "lib");

            if (!jettyLibDir.exists() || !jettyLibDir.isDirectory())
            {
                throw new IllegalArgumentException("Could not find Jetty libs");
            }

            for (File file : jettyLibDir.listFiles())
            {
                if (!file.isFile())
                {
                    continue;
                }

                String name = file.getName();

                if ((name.startsWith("jetty-")) && (name.endsWith(".jar")))
                {
                    String value = detect(file);

                    if (value != null)
                    {
                        version = new JettyVersion(value, false);

                        break;
                    }
                }
            }
        }

        if (version == null)
        {
            throw new IllegalArgumentException("Failed to detect Jetty version");
        }

        if (!version.isValid())
        {
            throw new IllegalArgumentException("Invalid Jetty version detected");
        }

        CACHE.put(cacheKey, version);

        return version;
    }

    protected static String detect(File file)
    {
        if ((!file.exists()) || (!file.canRead()))
        {
            return null;
        }

        try
        {
            JarFile jarFile = new JarFile(file);

            try
            {
                Manifest manifest = jarFile.getManifest();

                if (manifest == null)
                {
                    return null;
                }

                Attributes attributes = manifest.getMainAttributes();

                if ((attributes == null) || (attributes.size() == 0))
                {
                    return null;
                }

                return attributes.getValue("Implementation-Version");
            }
            finally
            {
                jarFile.close();
            }
        }
        catch (IOException e)
        {
            // failed to read jar file
            return null;
        }
    }

    private final long timestamp;
    private final String version;
    private final Integer majorVersion;
    private final Integer minorVersion;
    private final Integer microVersion;
    private final JettyVersionType type;

    protected JettyVersion(String version, boolean embedded)
    {
        super();

        timestamp = System.currentTimeMillis();

        this.version = version;

        majorVersion = extractMajorVersion(version);
        minorVersion = extractMinorVersion(version);
        microVersion = extractMicroVersion(version);
        type = extractType(majorVersion, minorVersion, embedded);
    }

    public String getVersion()
    {
        return version;
    }

    public Integer getMajorVersion()
    {
        return majorVersion;
    }

    public Integer getMinorVersion()
    {
        return minorVersion;
    }

    public Integer getMicroVersion()
    {
        return microVersion;
    }

    public JettyVersionType getType()
    {
        return type;
    }

    public boolean isValid()
    {
        return type != null;
    }

    @Override
    public String toString()
    {
        return String.format("%s (%s, major=%s, minor=%s, micro=%s)", version, type, majorVersion, minorVersion,
            microVersion);
    }

    private static Integer extractMajorVersion(String version)
    {
        return extractVersion(version, 0);
    }

    private static Integer extractMinorVersion(String version)
    {
        return extractVersion(version, 1);
    }

    private static Integer extractMicroVersion(String version)
    {
        return extractVersion(version, 2);
    }

    private static Integer extractVersion(String version, int index)
    {
        if (version == null)
        {
            return null;
        }

        String[] split = version.split("\\.");

        if (split.length <= index)
        {
            return null;
        }

        try
        {
            return Integer.parseInt(split[index]);
        }
        catch (NumberFormatException e)
        {
            return null;
        }
    }

    private static JettyVersionType extractType(Integer majorVersion, Integer minorVersion, boolean embedded)
    {
        if (embedded)
        {
            return JettyVersionType.JETTY_EMBEDDED;
        }

        if (majorVersion == null)
        {
            return null;
        }

        switch (majorVersion.intValue())
        {
            case 6:
                return JettyVersionType.JETTY_6;

            case 7:
                return JettyVersionType.JETTY_7;

            case 8:
                return JettyVersionType.JETTY_8;

            case 9:
            {
                if (minorVersion.intValue() <= 2)
                {
                    return JettyVersionType.JETTY_9;
                }
                else
                {
                    return JettyVersionType.JETTY_9_3;
                }
            }
            default:
                return null;
        }
    }

}
