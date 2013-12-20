package com.buschmais.cdo.impl.bootstrap.osgi;

import java.net.URL;
import java.util.Enumeration;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CdoUnitBundleListener implements BundleActivator, BundleListener {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(CdoUnitBundleListener.class);

	public void start(BundleContext context) throws Exception {
		context.addBundleListener(this);
	}

	public void stop(BundleContext context) throws Exception {
		context.removeBundleListener(this);
	}

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
		if (e.hasMoreElements()) {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info(
						"[CdoUnitDeployer] Deploying CdoUnit in bundle '{0}'",
						bundle.getSymbolicName());
			}
			URL cdoUnitUrl = (URL) e.nextElement();
			String bundleLocation = bundle.getLocation();
			LOGGER.info("cdoUnitUrl: {0}", cdoUnitUrl);
			LOGGER.info("bundleLocation: {0}", bundleLocation);
		}
	}

	private void undeployCdoUnit(Bundle bundle) {
	}

}
