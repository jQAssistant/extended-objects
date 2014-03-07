package com.buschmais.cdo.impl.bootstrap.osgi;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.buschmais.cdo.api.CdoManagerFactory;
import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.impl.CdoManagerFactoryImpl;
import com.buschmais.cdo.impl.bootstrap.CdoUnitFactory;

public class CdoUnitBundleListener implements BundleActivator, BundleListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(CdoUnitBundleListener.class);

    private final Map<Long, List<CdoManagerFactory>> registeredCdoManagerFactories = new HashMap<>();

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
            List<CdoManagerFactory> cdoManagerFactories = new LinkedList<>();
            while (e.hasMoreElements()) {
                URL cdoUnitUrl = (URL) e.nextElement();
                List<CdoUnit> cdoUnits = Collections.emptyList();
                try {
                    cdoUnits = CdoUnitFactory.getInstance().getCdoUnits(cdoUnitUrl);
                } catch (IOException ioe) {
                    if (LOGGER.isErrorEnabled()) {
                        LOGGER.error("Error while loading CdoUnit", ioe);
                    }
                }
                for (CdoUnit cdoUnit : cdoUnits) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Found CdoUnit '{}'", cdoUnit.getName());
                    }
                    CdoManagerFactory cdoManagerFactory = new CdoManagerFactoryImpl(cdoUnit);
                    Dictionary<String, Object> p = new Hashtable<>();
                    p.put("name", cdoUnit.getName());
                    bundle.getBundleContext().registerService(CdoManagerFactory.class, cdoManagerFactory, p);
                    cdoManagerFactories.add(cdoManagerFactory);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Registered service for CdoUnit '{}'", cdoUnit.getName());
                    }
                }
            }
            this.registeredCdoManagerFactories.put(Long.valueOf(bundle.getBundleId()), cdoManagerFactories);
        }
    }

    private void undeployCdoUnits(Bundle bundle) {
        List<CdoManagerFactory> cdoManagerFactories = this.registeredCdoManagerFactories.remove(Long.valueOf(bundle.getBundleId()));
        if (cdoManagerFactories != null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Closing CdoManagerFactory for bundle '{}'.", bundle.getSymbolicName());
            }
            for (CdoManagerFactory cdoManagerFactory : cdoManagerFactories) {
                closeCdoManagerFactory(cdoManagerFactory);
            }
        }
    }

    private void closeCdoManagerFactory(CdoManagerFactory cdoManagerFactory) {
        if (cdoManagerFactory != null) {
            cdoManagerFactory.close();
        }
    }

}
