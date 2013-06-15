package nl.ivonet.classloader.trial;

import sun.misc.Resource;
import sun.misc.URLClassPath;
import sun.net.www.ParseUtil;
import sun.security.util.SecurityConstants;

import java.io.Closeable;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.SocketPermission;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandlerFactory;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.security.SecureClassLoader;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * This class loader is used to load classes and resources from a search path of URLs referring to both JAR files and
 * directories. Any URL that ends with a '/' is assumed to refer to a directory. Otherwise, the URL is assumed to refer
 * to a JAR file which will be opened as needed.
 * <p/>
 * The AccessControlContext of the thread that created the instance of URLClassLoader will be used when subsequently
 * loading classes and resources.
 * <p/>
 * The classes that are loaded are by default granted permission only to access the URLs specified when the
 * URLClassLoader was created.
 *
 * @author David Connelly
 * @since 1.2
 */
public class IvoNetClassLoader2 extends SecureClassLoader implements Closeable {
    /* The search path for classes and resources */
    private final URLClassPath urlClassPath;
    /* The context to be used when loading classes and resources */
    private final AccessControlContext accessControlContext;
    private final WeakHashMap<Closeable, Void>
            closeables = new WeakHashMap<>();

    /**
     * Constructs a new URLClassLoader for the given URLs. The URLs will be searched in the order specified for classes
     * and resources after first searching in the specified parent class loader. Any URL that ends with a '/' is assumed
     * to refer to a directory. Otherwise, the URL is assumed to refer to a JAR file which will be downloaded and opened
     * as needed.
     * <p/>
     * <p>If there is a security manager, this method first calls the security manager's {@code checkCreateClassLoader}
     * method to ensure creation of a class loader is allowed.
     *
     * @param urls   the URLs from which to load classes and resources
     * @param parent the parent class loader for delegation
     * @throws SecurityException if a security manager exists and its {@code checkCreateClassLoader} method doesn't
     *                           allow creation of a class loader.
     * @see SecurityManager#checkCreateClassLoader
     */
    public IvoNetClassLoader2(final URL[] urls, final ClassLoader parent) {
        super(parent);
        // this is to make the stack depth consistent with 1.1
        final SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkCreateClassLoader();
        }
        urlClassPath = new URLClassPath(urls);
        this.accessControlContext = AccessController.getContext();
    }

    IvoNetClassLoader2(final URL[] urls, final ClassLoader parent,
                       final AccessControlContext context) {
        super(parent);
        // this is to make the stack depth consistent with 1.1
        final SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkCreateClassLoader();
        }
        urlClassPath = new URLClassPath(urls);
        this.accessControlContext = context;
    }

    /**
     * Constructs a new URLClassLoader for the specified URLs using the default delegation parent {@code ClassLoader}.
     * The URLs will be searched in the order specified for classes and resources after first searching in the parent
     * class loader. Any URL that ends with a '/' is assumed to refer to a directory. Otherwise, the URL is assumed to
     * refer to a JAR file which will be downloaded and opened as needed.
     * <p/>
     * <p>If there is a security manager, this method first calls the security manager's {@code checkCreateClassLoader}
     * method to ensure creation of a class loader is allowed.
     *
     * @param urls the URLs from which to load classes and resources
     * @throws SecurityException if a security manager exists and its {@code checkCreateClassLoader} method doesn't
     *                           allow creation of a class loader.
     * @see SecurityManager#checkCreateClassLoader
     */
    public IvoNetClassLoader2(final URL[] urls) {
        // this is to make the stack depth consistent with 1.1
        final SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkCreateClassLoader();
        }
        urlClassPath = new URLClassPath(urls);
        this.accessControlContext = AccessController.getContext();
    }

    IvoNetClassLoader2(final URL[] urls, final AccessControlContext context) {
        // this is to make the stack depth consistent with 1.1
        final SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkCreateClassLoader();
        }
        urlClassPath = new URLClassPath(urls);
        this.accessControlContext = context;
    }

    /* A map (used as a set) to keep track of closeable local resources
     * (either JarFiles or FileInputStreams). We don't care about
     * Http resources since they don't need to be closed.
     *
     * If the resource is coming from a jar file
     * we keep a (weak) reference to the JarFile object which can
     * be closed if URLClassLoader.close() called. Due to jar file
     * caching there will typically be only one JarFile object
     * per underlying jar file.
     *
     * For file resources, which is probably a less common situation
     * we have to keep a weak reference to each stream.
     */

    /**
     * Constructs a new URLClassLoader for the specified URLs, parent class loader, and URLStreamHandlerFactory. The
     * parent argument will be used as the parent class loader for delegation. The factory argument will be used as the
     * stream handler factory to obtain protocol handlers when creating new jar URLs.
     * <p/>
     * <p>If there is a security manager, this method first calls the security manager's {@code checkCreateClassLoader}
     * method to ensure creation of a class loader is allowed.
     *
     * @param urls    the URLs from which to load classes and resources
     * @param parent  the parent class loader for delegation
     * @param factory the URLStreamHandlerFactory to use when creating URLs
     * @throws SecurityException if a security manager exists and its {@code checkCreateClassLoader} method doesn't
     *                           allow creation of a class loader.
     * @see SecurityManager#checkCreateClassLoader
     */
    public IvoNetClassLoader2(final URL[] urls, final ClassLoader parent,
                              final URLStreamHandlerFactory factory) {
        super(parent);
        // this is to make the stack depth consistent with 1.1
        final SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkCreateClassLoader();
        }
        urlClassPath = new URLClassPath(urls, factory);
        accessControlContext = AccessController.getContext();
    }

    /**
     * Creates a new instance of URLClassLoader for the specified URLs and parent class loader. If a security manager is
     * installed, the {@code loadClass} method of the URLClassLoader returned by this method will invoke the {@code
     * SecurityManager.checkPackageAccess} method before loading the class.
     *
     * @param urls   the URLs to search for classes and resources
     * @param parent the parent class loader for delegation
     * @return the resulting class loader
     */
    public static IvoNetClassLoader2 newInstance(final URL[] urls,
                                                 final ClassLoader parent) {
        // Save the caller's context
        final AccessControlContext acc = AccessController.getContext();
        // Need a privileged block to create the class loader
        return AccessController.doPrivileged(
                new PrivilegedAction<IvoNetClassLoader2>() {
                    @Override
                    public IvoNetClassLoader2 run() {
                        return new FactoryURLClassLoader2(urls, parent, acc);
                    }
                });
    }

    /**
     * Creates a new instance of URLClassLoader for the specified URLs and default parent class loader. If a security
     * manager is installed, the {@code loadClass} method of the URLClassLoader returned by this method will invoke the
     * {@code SecurityManager.checkPackageAccess} before loading the class.
     *
     * @param urls the URLs to search for classes and resources
     * @return the resulting class loader
     */
    public static IvoNetClassLoader2 newInstance(final URL[] urls) {
        // Save the caller's context
        final AccessControlContext acc = AccessController.getContext();
        // Need a privileged block to create the class loader
        return AccessController.doPrivileged(
                new PrivilegedAction<IvoNetClassLoader2>() {
                    @Override
                    public IvoNetClassLoader2 run() {
                        return new FactoryURLClassLoader2(urls, acc);
                    }
                });
    }

    /**
     * Returns an input stream for reading the specified resource. If this loader is closed, then any resources opened
     * by this method will be closed. <p/> <p> The search order is described in the documentation for {@link
     * #getResource(String)}.  </p>
     *
     * @param name The resource name
     * @return An input stream for reading the resource, or <tt>null</tt> if the resource could not be found
     * @since 1.7
     */
    @Override
    public InputStream getResourceAsStream(final String name) {
        final URL url = getResource(name);
        try {
            if (url == null) {
                return null;
            }
            final URLConnection urlConnection = url.openConnection();
            final InputStream is = urlConnection.getInputStream();
            if (urlConnection instanceof JarURLConnection) {
                final JarURLConnection juc = (JarURLConnection) urlConnection;
                final JarFile jar = juc.getJarFile();
                synchronized (closeables) {
                    if (!closeables.containsKey(jar)) {
                        closeables.put(jar, null);
                    }
                }
            } else if (urlConnection instanceof sun.net.www.protocol.file.FileURLConnection) {
                synchronized (closeables) {
                    closeables.put(is, null);
                }
            }
            return is;
        } catch (IOException ignored) {
            return null;
        }
    }

    /**
     * Closes this URLClassLoader, so that it can no longer be used to load new classes or resources that are defined by
     * this loader. Classes and resources defined by any of this loader's parents in the delegation hierarchy are still
     * accessible. Also, any classes or resources that are already loaded, are still accessible.
     * <p/>
     * In the case of jar: and file: URLs, it also closes any files that were opened by it. If another thread is loading
     * a class when the {@code close} method is invoked, then the result of that load is undefined.
     * <p/>
     * The method makes a best effort attempt to close all opened files, by catching {@link java.io.IOException}s
     * internally.
     * Unchecked exceptions and errors are not caught. Calling close on an already closed loader has no effect.
     * <p/>
     *
     * @throws java.io.IOException       if closing any file opened by this class loader resulted in an IOException.
     * Any such
     *                           exceptions are caught internally. If only one is caught, then it is re-thrown. If more
     *                           than one exception is caught, then the second and following exceptions are added as
     *                           suppressed exceptions of the first one caught, which is then re-thrown.
     * @throws SecurityException if a security manager is set, and it denies {@link RuntimePermission}<tt>
     *                           ("closeClassLoader") </tt>
     * @since 1.7
     */
    @Override
    public void close() throws IOException {
        final SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkPermission(new RuntimePermission("closeClassLoader"));
        }
        final List<IOException> errors = urlClassPath.closeLoaders();

        // now close any remaining streams.

        synchronized (closeables) {
            final Set<Closeable> keys = closeables.keySet();
            for (final Closeable closeable : keys) {
                try {
                    closeable.close();
                } catch (IOException ioex) {
                    errors.add(ioex);
                }
            }
            closeables.clear();
        }

        if (errors.isEmpty()) {
            return;
        }

        final IOException firstex = errors.remove(0);

        // Suppress any remaining exceptions

        for (final IOException error : errors) {
            firstex.addSuppressed(error);
        }
        throw firstex;
    }

    /**
     * Appends the specified URL to the list of URLs to search for classes and resources.
     * <p/>
     * If the URL specified is {@code null} or is already in the list of URLs, or if this loader is closed, then
     * invoking this method has no effect.
     *
     * @param url the URL to be added to the search path of URLs
     */
    protected void addURL(final URL url) {
        urlClassPath.addURL(url);
    }

    /**
     * Returns the search path of URLs for loading classes and resources. This includes the original list of URLs
     * specified to the constructor, along with any URLs subsequently appended by the addURL() method.
     *
     * @return the search path of URLs for loading classes and resources.
     */
    public URL[] getURLs() {
        return urlClassPath.getURLs();
    }

    /**
     * Finds and loads the class with the specified name from the URL search path. Any URLs referring to JAR files are
     * loaded and opened as needed until the class is found.
     *
     * @param name the name of the class
     * @return the resulting class
     * @throws ClassNotFoundException if the class could not be found, or if the loader is closed.
     */
    @Override
    protected Class<?> findClass(final String name)
            throws ClassNotFoundException {
        try {
            return AccessController.doPrivileged(
                    new PrivilegedExceptionAction<Class>() {
                        @Override
                        public Class run() throws ClassNotFoundException {
                            final String path = name.replace('.', '/') + ".class";
                            final Resource res = urlClassPath.getResource(path, false);
                            if (res != null) {
                                try {
                                    return defineClass(name, res);
                                } catch (IOException e) {
                                    throw new ClassNotFoundException(name, e);
                                }
                            } else {
                                throw new ClassNotFoundException(name);
                            }
                        }
                    }, accessControlContext);
        } catch (java.security.PrivilegedActionException pae) {
            throw (ClassNotFoundException) pae.getException();
        }
    }

    /*
     * Retrieve the package using the specified package name.
     * If non-null, verify the package using the specified code
     * source and manifest.
     */
    private Package getAndVerifyPackage(final String pkgname,
                                        final Manifest man, final URL url) {
        final Package pkg = getPackage(pkgname);
        if (pkg != null) {
            // Package found, so check package sealing.
            if (pkg.isSealed()) {
                // Verify that code source URL is the same.
                if (!pkg.isSealed(url)) {
                    throw new SecurityException(
                            "sealing violation: package " + pkgname + " is sealed");
                }
            } else {
                // Make sure we are not attempting to seal the package
                // at this code source URL.
                if ((man != null) && isSealed(pkgname, man)) {
                    throw new SecurityException(
                            "sealing violation: can't seal package " + pkgname +
                            ": already loaded");
                }
            }
        }
        return pkg;
    }

    /*
     * Defines a Class using the class bytes obtained from the specified
     * Resource. The resulting Class must be resolved before it can be
     * used.
     */
    private Class defineClass(final String name, final Resource res) throws IOException {
        final long t0 = System.nanoTime();
        final int i = name.lastIndexOf('.');
        final URL url = res.getCodeSourceURL();
        if (i != -1) {
            final String pkgname = name.substring(0, i);
            // Check if package already loaded.
            final Manifest man = res.getManifest();
            if (getAndVerifyPackage(pkgname, man, url) == null) {
                try {
                    if (man != null) {
                        definePackage(pkgname, man, url);
                    } else {
                        definePackage(pkgname, null, null, null, null, null, null, null);
                    }
                } catch (IllegalArgumentException iae) {
                    // parallel-capable class loaders: re-verify in case of a
                    // race condition
                    if (getAndVerifyPackage(pkgname, man, url) == null) {
                        // Should never happen
                        throw new AssertionError("Cannot find package " +
                                                 pkgname);
                    }
                }
            }
        }
        // Now read the class bytes and define the class
        final java.nio.ByteBuffer byteBuffer = res.getByteBuffer();
        if (byteBuffer != null) {
            // Use (direct) ByteBuffer:
            final CodeSigner[] signers = res.getCodeSigners();
            final CodeSource cs = new CodeSource(url, signers);
            sun.misc.PerfCounter.getReadClassBytesTime().addElapsedTimeFrom(t0);
            return defineClass(name, byteBuffer, cs);
        } else {
            final byte[] bytes = res.getBytes();
            // must read certificates AFTER reading bytes.
            final CodeSigner[] signers = res.getCodeSigners();
            final CodeSource cs = new CodeSource(url, signers);
            sun.misc.PerfCounter.getReadClassBytesTime().addElapsedTimeFrom(t0);
            return defineClass(name, bytes, 0, bytes.length, cs);
        }
    }

    /**
     * Defines a new package by name in this ClassLoader. The attributes contained in the specified Manifest will be
     * used to obtain package version and sealing information. For sealed packages, the additional URL specifies the
     * code source URL from which the package was loaded.
     *
     * @param name the package name
     * @param man  the Manifest containing package version and sealing information
     * @param url  the code source url for the package, or null if none
     * @return the newly defined Package object
     * @throws IllegalArgumentException if the package name duplicates an existing package either in this class loader
     *                                  or one of its ancestors
     */
    protected Package definePackage(final String name, final Manifest man, final URL url)
            throws IllegalArgumentException {
        final String path = name.replace('.', '/') + '/';
        String specTitle = null;
        String specVersion = null;
        String specVendor = null;
        String implTitle = null;
        String implVersion = null;
        String implVendor = null;
        String sealed = null;
        URL sealBase = null;

        final Attributes attr = man.getAttributes(path);
        if (attr != null) {
            specTitle = attr.getValue(Attributes.Name.SPECIFICATION_TITLE);
            specVersion = attr.getValue(Attributes.Name.SPECIFICATION_VERSION);
            specVendor = attr.getValue(Attributes.Name.SPECIFICATION_VENDOR);
            implTitle = attr.getValue(Attributes.Name.IMPLEMENTATION_TITLE);
            implVersion = attr.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
            implVendor = attr.getValue(Attributes.Name.IMPLEMENTATION_VENDOR);
            sealed = attr.getValue(Attributes.Name.SEALED);
        }
        final Attributes mainAttributes = man.getMainAttributes();
        if (mainAttributes != null) {
            if (specTitle == null) {
                specTitle = mainAttributes.getValue(Attributes.Name.SPECIFICATION_TITLE);
            }
            if (specVersion == null) {
                specVersion = mainAttributes.getValue(Attributes.Name.SPECIFICATION_VERSION);
            }
            if (specVendor == null) {
                specVendor = mainAttributes.getValue(Attributes.Name.SPECIFICATION_VENDOR);
            }
            if (implTitle == null) {
                implTitle = mainAttributes.getValue(Attributes.Name.IMPLEMENTATION_TITLE);
            }
            if (implVersion == null) {
                implVersion = mainAttributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
            }
            if (implVendor == null) {
                implVendor = mainAttributes.getValue(Attributes.Name.IMPLEMENTATION_VENDOR);
            }
            if (sealed == null) {
                sealed = mainAttributes.getValue(Attributes.Name.SEALED);
            }
        }
        if ("true".equalsIgnoreCase(sealed)) {
            sealBase = url;
        }
        return definePackage(name, specTitle, specVersion, specVendor,
                             implTitle, implVersion, implVendor, sealBase);
    }

    /*
     * Returns true if the specified package name is sealed according to the
     * given manifest.
     */
    private boolean isSealed(final String name, final Manifest man) {
        final String path = name.replace('.', '/').concat("/");
        Attributes attr = man.getAttributes(path);
        String sealed = null;
        if (attr != null) {
            sealed = attr.getValue(Attributes.Name.SEALED);
        }
        if (sealed == null) {
            if ((attr = man.getMainAttributes()) != null) {
                sealed = attr.getValue(Attributes.Name.SEALED);
            }
        }
        return "true".equalsIgnoreCase(sealed);
    }

    /**
     * Finds the resource with the specified name on the URL search path.
     *
     * @param name the name of the resource
     * @return a {@code URL} for the resource, or {@code null} if the resource could not be found, or if the loader is
     *         closed.
     */
    @Override
    public URL findResource(final String name) {
        /*
         * The same restriction to finding classes applies to resources
         */
        final URL url = AccessController.doPrivileged(
                new PrivilegedAction<URL>() {
                    @Override
                    public URL run() {
                        return urlClassPath.findResource(name, true);
                    }
                }, accessControlContext);

        return url != null ? urlClassPath.checkURL(url) : null;
    }

    /**
     * Returns an Enumeration of URLs representing all of the resources on the URL search path having the specified
     * name.
     *
     * @param name the resource name
     * @return an {@code Enumeration} of {@code URL}s If the loader is closed, the Enumeration will be empty.
     * @throws java.io.IOException if an I/O exception occurs
     */
    @Override
    public Enumeration<URL> findResources(final String name)
            throws IOException {
        final Enumeration<URL> e = urlClassPath.findResources(name, true);

        return new URLEnumeration(e);
    }

    /**
     * Returns the permissions for the given codesource object. The implementation of this method first calls
     * super.getPermissions and then adds permissions based on the URL of the codesource.
     * <p/>
     * If the protocol of this URL is "jar", then the permission granted is based on the permission that is required by
     * the URL of the Jar file.
     * <p/>
     * If the protocol is "file" and there is an authority component, then permission to connect to and accept
     * connections from that authority may be granted. If the protocol is "file" and the path specifies a file, then
     * permission to read that file is granted. If protocol is "file" and the path is a directory, permission is granted
     * to read all files and (recursively) all files and subdirectories contained in that directory.
     * <p/>
     * If the protocol is not "file", then permission to connect to and accept connections from the URL's host is
     * granted.
     *
     * @param codesource the codesource
     * @return the permissions granted to the codesource
     */
    @Override
    protected PermissionCollection getPermissions(final CodeSource codesource) {
        final PermissionCollection perms = super.getPermissions(codesource);

        final URL url = codesource.getLocation();

        Permission p;
        URLConnection urlConnection;

        try {
            urlConnection = url.openConnection();
            p = urlConnection.getPermission();
        } catch (IOException ignored) {
            p = null;
            urlConnection = null;
        }

        if (p instanceof FilePermission) {
            // if the permission has a separator char on the end,
            // it means the codebase is a directory, and we need
            // to add an additional permission to read recursively
            String path = p.getName();
            if (path.endsWith(File.separator)) {
                path += "-";
                p = new FilePermission(path, SecurityConstants.FILE_READ_ACTION);
            }
        } else if ((p == null) && ("file".equals(url.getProtocol()))) {
            String path = url.getFile().replace('/', File.separatorChar);
            path = ParseUtil.decode(path);
            if (path.endsWith(File.separator)) {
                path += "-";
            }
            p = new FilePermission(path, SecurityConstants.FILE_READ_ACTION);
        } else {
            /**
             * Not loading from a 'file:' URL so we want to give the class
             * permission to connect to and accept from the remote host
             * after we've made sure the host is the correct one and is valid.
             */
            URL locUrl = url;
            if (urlConnection instanceof JarURLConnection) {
                locUrl = ((JarURLConnection) urlConnection).getJarFileURL();
            }
            final String host = locUrl.getHost();
            if (host != null && (!host.isEmpty())) {
                p = new SocketPermission(host,
                                         SecurityConstants.SOCKET_CONNECT_ACCEPT_ACTION);
            }
        }

        // make sure the person that created this class loader
        // would have this permission

        if (p != null) {
            final SecurityManager sm = System.getSecurityManager();
            if (sm != null) {
                final Permission fp = p;
                AccessController.doPrivileged(new PrivilegedAction<Void>() {
                    @Override
                    public Void run() throws SecurityException {
                        sm.checkPermission(fp);
                        return null;
                    }
                }, accessControlContext);
            }
            perms.add(p);
        }
        return perms;
    }


    private class URLEnumeration implements Enumeration<URL> {
        private final Enumeration<URL> e;
        private URL url;

        private URLEnumeration(final Enumeration<URL> urlEnumeration) {
            this.e = urlEnumeration;
            url = null;
        }

        private boolean next() {
            if (url != null) {
                return true;
            }
            do {
                final URL u = AccessController.doPrivileged(
                        new PrivilegedAction<URL>() {
                            @Override
                            public URL run() {
                                if (!e.hasMoreElements()) {
                                    return null;
                                }
                                return e.nextElement();
                            }
                        }, accessControlContext);
                if (u == null) {
                    break;
                }
                url = urlClassPath.checkURL(u);
            } while (url == null);
            return url != null;
        }

        @Override
        public URL nextElement() {
            if (!next()) {
                throw new NoSuchElementException();
            }
            final URL u = url;
            url = null;
            return u;
        }

        @Override
        public boolean hasMoreElements() {
            return next();
        }
    }
}

