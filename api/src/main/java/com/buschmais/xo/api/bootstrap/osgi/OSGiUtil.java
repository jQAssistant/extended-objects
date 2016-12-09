package com.buschmais.xo.api.bootstrap.osgi;

import org.osgi.framework.BundleReference;

/**
 *
 * @since 0.8
 */
final class OSGiUtil {

    private static volatile Boolean loadedAsBundle;

    public static boolean isXOLoadedAsOSGiBundle() {
        if (loadedAsBundle == null) {
            ClassLoader classLoader = OSGiUtil.class.getClassLoader();
            try {
                classLoader.loadClass("org.osgi.framework.BundleReference");
            } catch (ClassNotFoundException e) {
                return false;
            }

            if (classLoader instanceof BundleReference) {
                loadedAsBundle = true;
            } else {
                loadedAsBundle = false;
            }
        }
        return loadedAsBundle;
    }

    private OSGiUtil() {
    }
}
