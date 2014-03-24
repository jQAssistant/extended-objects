package com.buschmais.xo.impl.bootstrap.osgi;

import com.buschmais.xo.api.XOManagerFactory;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.impl.XOManagerFactoryImpl;
import com.buschmais.xo.impl.bootstrap.CdoUnitFactory;
import org.osgi.framework.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class CdoUnitBundleListener implements BundleActivator, BundleListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(CdoUnitBundleListener.class);

    private final Map<Long, List<XOManagerFactory>> registeredCdoManagerFactories = new HashMap<>();

    @Override
    public void start(BundleContext context) throws Exception {
        context.addBundleListener(this);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        context.removeBundleListener(this);
    }

    @Override
    public void bundleChanged(BundleEvent event) {
        switch (event.getType()) {
            case BundleEvent.STARTED:
                deployCdoUnits(event.getBundle());
                break;
            case BundleEvent.STOPPED:
                undeployCdoUnits(event.getBundle());
                break;
        }
    }

    /*
     * helper
     */

    private void deployCdoUnits(Bundle bundle) {
        Enumeration<?> e = bundle.findEntries("META-INF", "cdo.xml", false); //$NON-NLS-1$, $NON-NLS-2$
        if (e != null) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Deploying CdoUnits in bundle '{}'", bundle.getSymbolicName());
            }
            List<XOManagerFactory> cdoManagerFactories = new LinkedList<>();
            while (e.hasMoreElements()) {
                URL cdoUnitUrl = (URL) e.nextElement();
                List<XOUnit> XOUnits = Collections.emptyList();
                try {
                    XOUnits = CdoUnitFactory.getInstance().getCdoUnits(cdoUnitUrl);
                } catch (IOException ioe) {
                    if (LOGGER.isErrorEnabled()) {
                        LOGGER.error("Error while loading XOUnit", ioe);
                    }
                }
                for (XOUnit XOUnit : XOUnits) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Found XOUnit '{}'", XOUnit.getName());
                    }
                    XOManagerFactory XOManagerFactory = new XOManagerFactoryImpl(XOUnit);
                    Dictionary<String, Object> p = new Hashtable<>();
                    p.put("name", XOUnit.getName());
                    bundle.getBundleContext().registerService(XOManagerFactory.class, XOManagerFactory, p);
                    cdoManagerFactories.add(XOManagerFactory);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Registered service for XOUnit '{}'", XOUnit.getName());
                    }
                }
            }
            this.registeredCdoManagerFactories.put(Long.valueOf(bundle.getBundleId()), cdoManagerFactories);
        }
    }

    private void undeployCdoUnits(Bundle bundle) {
        List<XOManagerFactory> cdoManagerFactories = this.registeredCdoManagerFactories.remove(Long.valueOf(bundle.getBundleId()));
        if (cdoManagerFactories != null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Closing XOManagerFactory for bundle '{}'.", bundle.getSymbolicName());
            }
            for (XOManagerFactory XOManagerFactory : cdoManagerFactories) {
                closeCdoManagerFactory(XOManagerFactory);
            }
        }
    }

    private void closeCdoManagerFactory(XOManagerFactory XOManagerFactory) {
        if (XOManagerFactory != null) {
            XOManagerFactory.close();
        }
    }

}
