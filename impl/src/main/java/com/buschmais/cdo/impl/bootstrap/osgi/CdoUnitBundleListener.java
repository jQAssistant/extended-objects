package com.buschmais.cdo.impl.bootstrap.osgi;

import java.io.IOException;
import java.net.URL;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

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

    private static final Logger LOGGER = LoggerFactory
            .getLogger(CdoUnitBundleListener.class);

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
            deployCdoUnit(event.getBundle());
            break;
        case BundleEvent.STOPPED:
            undeployCdoUnit(event.getBundle());
            break;
        }
    }

    private void deployCdoUnit(Bundle bundle) {
        Enumeration<?> e = bundle.findEntries("META-INF", "cdo.xml", false);
        if (e != null && e.hasMoreElements()) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(
                        "[CdoUnitBundleListener] Deploying CdoUnits in bundle '{0}'",
                        bundle.getSymbolicName());
            }
            URL cdoUnitUrl = (URL) e.nextElement();
            try {
                List<CdoUnit> cdoUnits = CdoUnitFactory.getInstance()
                        .getCdoUnits(cdoUnitUrl);
                for (CdoUnit cdoUnit : cdoUnits) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(
                                "[CdoUnitBundleListener] Found CdoUnit '{0}'",
                                cdoUnit.getName());
                    }
                    CdoManagerFactory cdoManagerFactory = new CdoManagerFactoryImpl(
                            cdoUnit);
                    Dictionary<String, Object> p = new Hashtable<String, Object>();
                    p.put("name", cdoUnit.getName());
                    bundle.getBundleContext().registerService(
                            CdoManagerFactory.class, cdoManagerFactory, p);

                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(
                                "[CdoUnitBundleListener] Registered service for CdoUnit '{0}'",
                                cdoUnit.getName());
                    }
                }
            } catch (IOException ioe) {
                if (LOGGER.isErrorEnabled()) {
                    LOGGER.error("Error while loading CdoUnit", ioe);
                }
            }
        }
    }

    private void undeployCdoUnit(Bundle bundle) {
    }

}
