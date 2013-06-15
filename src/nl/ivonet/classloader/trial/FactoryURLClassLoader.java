package nl.ivonet.classloader.trial;

import java.net.URL;
import java.security.AccessControlContext;

/**
 * @author Ivo Woltring
 */
final class FactoryURLClassLoader extends IvoNetClassLoader {

    static {
        ClassLoader.registerAsParallelCapable();
    }

    FactoryURLClassLoader(final URL[] urls, final ClassLoader parent,
                          final AccessControlContext acc) {
        super(urls, parent, acc);
    }

    FactoryURLClassLoader(final URL[] urls, final AccessControlContext acc) {
        super(urls, acc);
    }

    @Override
    public final Class loadClass(final String name, final boolean resolve)
            throws ClassNotFoundException {
        // First check if we have permission to access the package. This
        // should go away once we've added support for exported packages.
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            final int i = name.lastIndexOf('.');
            if (i != -1) {
                sm.checkPackageAccess(name.substring(0, i));
            }
        }
        return super.loadClass(name, resolve);
    }
}
