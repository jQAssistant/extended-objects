package com.buschmais.xo.api.bootstrap.osgi;

import java.util.Collection;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.XOManagerFactory;
import com.buschmais.xo.api.bootstrap.XOBootstrapService;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.api.bootstrap.osgi.internal.Activator;

public final class XOSGI {

    /**
     * Create a {@link com.buschmais.xo.api.XOManagerFactory} for the XO unit
     * identified by name.
     * <p>
     * XO units are defined in XML descriptors located as classpath resources
     * with the name "/META-INF/xo.xml".
     * </p>
     *
     * @param name
     *            The name of the XO unit.
     * @return The {@link com.buschmais.xo.api.XOManagerFactory}.
     */
    public static XOManagerFactory createXOManagerFactory(String name) {
        if (Activator.getDefault() != null) {
            try {
                BundleContext bundleContext = FrameworkUtil.getBundle(XOSGI.class).getBundleContext();
                String filterString = "(name=" + name + ")";
                Collection<ServiceReference<XOManagerFactory>> xoBootstrapServiceReferences = bundleContext
                        .getServiceReferences(XOManagerFactory.class, filterString);
                for (ServiceReference<XOManagerFactory> xoBootstrapServiceReference : xoBootstrapServiceReferences) {
                    XOManagerFactory xoManagerFactory = bundleContext.getService(xoBootstrapServiceReference);
                    if (xoManagerFactory != null) {
                        return xoManagerFactory;
                    }
                }
            } catch (InvalidSyntaxException e) {
                throw new XOException("Cannot bootstrap XO implementation.", e);
            }
        }
        throw new XOException("Cannot bootstrap XO implementation.");
    }

    /**
     * Create a {@link com.buschmais.xo.api.XOManagerFactory} for the given XO
     * unit.
     *
     * @param xoUnit
     *            The XO unit.
     * @return The {@link com.buschmais.xo.api.XOManagerFactory}.
     */
    public static XOManagerFactory createXOManagerFactory(XOUnit xoUnit) {
        if (Activator.getDefault() != null) {
            BundleContext bundleContext = FrameworkUtil.getBundle(Activator.class).getBundleContext();
            ServiceReference<XOBootstrapService> xoBootstrapServiceReference = bundleContext
                    .getServiceReference(XOBootstrapService.class);
            if (xoBootstrapServiceReference == null) {
                throw new XOException("Cannot bootstrap XO implementation.");
            }
            XOBootstrapService xoBootstrapService = bundleContext.getService(xoBootstrapServiceReference);
            if (xoBootstrapService == null) {
                throw new XOException("Cannot bootstrap XO implementation.");
            }
            XOManagerFactory xoManagerFactory = xoBootstrapService.createXOManagerFactory(xoUnit);
            if (xoManagerFactory != null) {
                return xoManagerFactory;
            }
        }
        throw new XOException("Cannot bootstrap XO implementation.");
    }
}
