package com.buschmais.xo.impl.bootstrap.osgi;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import com.buschmais.xo.api.XOManagerFactory;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.impl.XOManagerFactoryImpl;
import com.buschmais.xo.impl.bootstrap.XOUnitFactory;

import org.osgi.framework.*;
import org.osgi.service.cm.ManagedServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XOUnitBundleListener implements BundleActivator, BundleListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(XOUnitBundleListener.class);

    private final Map<Long, List<XOManagerFactory>> registeredXOManagerFactories = new HashMap<>();

    private ServiceRegistration<ManagedServiceFactory> serviceFactoryRegistration;

    private XOManagerFactoryServiceFactory serviceFactory;

    @Override
    public void start(BundleContext context) {
        context.addBundleListener(this);
        Dictionary<String, String> props = new Hashtable<>();
        props.put(Constants.SERVICE_PID, XOManagerFactory.FACTORY_PID);
        serviceFactory = new XOManagerFactoryServiceFactory(context);
        serviceFactoryRegistration = context.registerService(ManagedServiceFactory.class, serviceFactory, props);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("ConfigAdmin listener registered");
        }
    }

    @Override
    public void stop(BundleContext context) {
        context.removeBundleListener(this);
        serviceFactory.stop();
        if (serviceFactoryRegistration != null) {
            serviceFactoryRegistration.unregister();
        }
    }

    @Override
    public void bundleChanged(BundleEvent event) {
        switch (event.getType()) {
        case BundleEvent.STARTED:
            deployXOUnits(event.getBundle());
            break;
        case BundleEvent.STOPPED:
            undeployXOUnits(event.getBundle());
            break;
        default:
            // intentionally left blank
            break;
        }
    }

    /*
     * helper
     */
    private void deployXOUnits(Bundle bundle) {
        Enumeration<?> e = bundle.findEntries("META-INF", "xo.xml", false); //$NON-NLS-1$ , $NON-NLS-2$
        if (e != null) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Deploying XO units in bundle '{}'", bundle.getSymbolicName());
            }
            List<XOManagerFactory> xoManagerFactories = new LinkedList<>();
            while (e.hasMoreElements()) {
                URL xoUnitUrl = (URL) e.nextElement();
                List<XOUnit> xoUnits = Collections.emptyList();
                try {
                    xoUnits = XOUnitFactory.getInstance().getXOUnits(xoUnitUrl);
                } catch (IOException ioe) {
                    if (LOGGER.isErrorEnabled()) {
                        LOGGER.error("Error while loading XOUnit", ioe);
                    }
                }
                for (XOUnit xoUnit : xoUnits) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Found XOUnit '{}'", xoUnit.getName());
                    }
                    XOManagerFactory xoManagerFactory = new XOManagerFactoryImpl(xoUnit);
                    Dictionary<String, Object> p = new Hashtable<>();
                    p.put("name", xoUnit.getName());
                    bundle.getBundleContext().registerService(XOManagerFactory.class, xoManagerFactory, p);
                    xoManagerFactories.add(xoManagerFactory);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Registered service for XOUnit '{}'", xoUnit.getName());
                    }
                }
            }
            this.registeredXOManagerFactories.put(Long.valueOf(bundle.getBundleId()), xoManagerFactories);
        }
    }

    private void undeployXOUnits(Bundle bundle) {
        List<XOManagerFactory> xoManagerFactories = this.registeredXOManagerFactories.remove(Long.valueOf(bundle.getBundleId()));
        if (xoManagerFactories != null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Closing XOManagerFactory for bundle '{}'.", bundle.getSymbolicName());
            }
            for (XOManagerFactory xoManagerFactory : xoManagerFactories) {
                closeXOManagerFactory(xoManagerFactory);
            }
        }
    }

    private void closeXOManagerFactory(XOManagerFactory xoManagerFactory) {
        if (xoManagerFactory != null) {
            xoManagerFactory.close();
        }
    }

}
