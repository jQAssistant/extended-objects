package com.buschmais.xo.impl.bootstrap.osgi;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.buschmais.xo.api.bootstrap.XOUnitParameter.NAME;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.XOManagerFactory;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.impl.XOManagerFactoryImpl;

public class XOManagerFactoryServiceFactory implements ManagedServiceFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(XOManagerFactoryServiceFactory.class);

    private final Map<String, ServiceRegistration<XOManagerFactory>> serviceInstances;

    final BundleContext bundleContext;

    public XOManagerFactoryServiceFactory(BundleContext bundleContext) {
        serviceInstances = new HashMap<>();
        this.bundleContext = bundleContext;
    }

    @Override
    public String getName() {
        return "ManagedServiceFactory for XOManagerFactory";
    }

    @Override
    public void updated(String pid, Dictionary<String, ?> properties) throws ConfigurationException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("updated: {}", pid);
        }
        if (properties == null) {
            return;
        }
        XOUnit xoUnit;
        try {
            xoUnit = XOUnitConverter.fromProperties(properties);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("config: {}", xoUnit);
            }
        } catch (XOException e) {
            throw new ConfigurationException(NAME.getKey(), e.getMessage(), e);
        }

        deleted(pid);

        XOManagerFactory xoManagerFactory = new XOManagerFactoryImpl(xoUnit);
        ServiceRegistration<XOManagerFactory> registration = bundleContext.registerService(XOManagerFactory.class, xoManagerFactory, properties);
        serviceInstances.put(pid, registration);
    }

    @Override
    public void deleted(String pid) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("deleted: {}", pid);
        }
        ServiceRegistration<XOManagerFactory> registration = serviceInstances.get(pid);
        if (registration != null) {
            XOManagerFactory factory = bundleContext.getService(registration.getReference());
            registration.unregister();
            factory.close();
        }
    }

    public void stop() {
        for (String pid : serviceInstances.keySet()) {
            deleted(pid);
        }
    }

}
