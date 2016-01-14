package org.phoenix.jmeter.core;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class LoadUserPathClass {
    private static final String CLASSPATH_SEPARATOR = File.pathSeparator;
    private static final String OS_NAME = System.getProperty("os.name");// $NON-NLS-1$
    private static final String OS_NAME_LC = OS_NAME.toLowerCase(java.util.Locale.ENGLISH);
    private static final DynamicClassLoader loader;
    private static final String jmDir;

    static {
        final List<URL> jars = new LinkedList<URL>();

        jmDir = System.getProperty("user.dir");
        boolean usesUNC = OS_NAME_LC.startsWith("windows");// $NON-NLS-1$

        StringBuilder classpath = new StringBuilder();
        File[] libDirs = new File[] { 
        		new File(jmDir + File.separator + "lib"),
        };
        for (File libDir : libDirs) {
            File[] libJars = libDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".jar");
                }
            });
            if (libJars == null) {
                new Throwable("Could not access " + libDir).printStackTrace();
                continue;
            }
            Arrays.sort(libJars); 
            for (File libJar : libJars) {
                try {
                    String s = libJar.getPath();

                    if (usesUNC) {
                        if (s.startsWith("\\\\") && !s.startsWith("\\\\\\")) {
                            s = "\\\\" + s;
                        } else if (s.startsWith("//") && !s.startsWith("///")) {
                            s = "//" + s;
                        }
                    }

                    jars.add(new File(s).toURI().toURL());
                    classpath.append(CLASSPATH_SEPARATOR);
                    classpath.append(s);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }

        loader = AccessController.doPrivileged(
                new java.security.PrivilegedAction<DynamicClassLoader>() {
                    @Override
                    public DynamicClassLoader run() {
                        return new DynamicClassLoader(jars.toArray(new URL[jars.size()]));
                    }
                }
        );
    }

    private static File[] listJars(File dir) {
        if (dir.isDirectory()) {
            return dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File f, String name) {
                    if (name.endsWith(".jar")) {
                        File jar = new File(f, name);
                        return jar.isFile() && jar.canRead();
                    }
                    return false;
                }
            });
        }
        return new File[0];
    }

    public static void addURL(String path) {
        File furl = new File(path);
        try {
            loader.addURL(furl.toURI().toURL()); 
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        File[] jars = listJars(furl);
        for (File jar : jars) {
            try {
                loader.addURL(jar.toURI().toURL()); 
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void addURL(URL url) {
        loader.addURL(url);
    }

    public static void addPath(String path) throws MalformedURLException {
        File file = new File(path);
        if (file.isDirectory() && !path.endsWith("/")) {
            file = new File(path + "/");
        }
        loader.addURL(file.toURI().toURL()); 
        StringBuilder sb = new StringBuilder();
        sb.append(CLASSPATH_SEPARATOR);
        sb.append(path);
        File[] jars = listJars(file);
        for (File jar : jars) {
            try {
                loader.addURL(jar.toURI().toURL()); // See Java bug 4496398
                sb.append(CLASSPATH_SEPARATOR);
                sb.append(jar.getPath());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

    }
}
