package com.buschmais.xo.api.bootstrap.osgi;

import java.util.Collection;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import com.buschmais.xo.api.CloseAdapter;
import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.XOManagerFactory;
import com.buschmais.xo.api.bootstrap.XOBootstrapService;
import com.buschmais.xo.api.bootstrap.XOUnit;

/**
 * Provides methods for bootstrapping XO when running in OSGi environment.
 *
 * @since 0.8
 */
public final class XOSGi {

    /**
     * Create a {@link com.buschmais.xo.api.XOManagerFactory} for the XO unit
     * identified by name.
     * <p>
     * Internally it performs a lookup in the OSGi service registry to retrieve the
     * XOManagerFactory service that is bound to the given XO unit name. The bundle
     * providing this XO unit must be processed by the OSGi bundle listener.
     * </p>
     *
     * @param name
     *            The name of the XO unit.
     * @return The {@link com.buschmais.xo.api.XOManagerFactory}.
     * @see com.buschmais.xo.impl.bootstrap.osgi.XOUnitBundleListener
     */
    public static XOManagerFactory createXOManagerFactory(String name) {
        if (OSGiUtil.isXOLoadedAsOSGiBundle()) {
            try {
                BundleContext bundleContext = FrameworkUtil.getBundle(XOSGi.class).getBundleContext();
                String filterString = "(name=" + name + ")";
                Collection<ServiceReference<XOManagerFactory>> xoManagerFactoryServices = bundleContext
                        .getServiceReferences(XOManagerFactory.class, filterString);
                for (ServiceReference<XOManagerFactory> xoManagerFactoryService : xoManagerFactoryServices) {
                    XOManagerFactory xoManagerFactory = bundleContext.getService(xoManagerFactoryService);
                    if (xoManagerFactory != null) {
                        xoManagerFactory.addCloseListener(new CloseAdapter() {

                            @Override
                            public void onAfterClose() {
                                bundleContext.ungetService(xoManagerFactoryService);
                            }

                        });
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
     * <p>
     * Internally it performs a lookup in the OSGi service registry to retrieve the
     * XOBootstrapService service.
     * </p>
     *
     * @param xoUnit
     *            The XO unit.
     * @return The {@link com.buschmais.xo.api.XOManagerFactory}.
     */
    public static XOManagerFactory createXOManagerFactory(XOUnit xoUnit) {
        if (OSGiUtil.isXOLoadedAsOSGiBundle()) {
            BundleContext bundleContext = FrameworkUtil.getBundle(XOSGi.class).getBundleContext();
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
            bundleContext.ungetService(xoBootstrapServiceReference);
            if (xoManagerFactory != null) {
                return xoManagerFactory;
            }
        }
        throw new XOException("Cannot bootstrap XO implementation.");
    }

}
