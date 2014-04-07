package com.buschmais.xo.impl.bootstrap.osgi;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.XOManagerFactory;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.api.bootstrap.XOUnitBuilder;
import com.buschmais.xo.api.bootstrap.XOUnitParameter;
import com.buschmais.xo.impl.XOManagerFactoryImpl;
import com.buschmais.xo.spi.reflection.ClassHelper;
import com.buschmais.xo.spi.bootstrap.XODatastoreProvider;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.ComponentConstants;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.*;

public class XOManagerFactoryServiceFactory implements ManagedServiceFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(XOManagerFactoryServiceFactory.class);

    private final Map<String, XOManagerFactory> pidsToFactories;

    private final Map<String, ServiceRegistration<XOManagerFactory>> pidsToServiceRegistrations;

    private final Set<String> registeredXOUnits;

    private ComponentContext componentContext;

    private String componentName;

    public XOManagerFactoryServiceFactory() {
        pidsToFactories = new HashMap<>();
        pidsToServiceRegistrations = new HashMap<>();
        registeredXOUnits = new HashSet<>();
    }

    @Override
    public String getName() {
        return this.componentName;
    }

    @Override
    public void updated(String pid, Dictionary<String, ?> properties) throws ConfigurationException {
        XOUnit xoUnit;
        try {
            xoUnit = getXOUnit(properties);
        } catch (XOException e) {
            throw new ConfigurationException(XOUnitParameter.NAME.getKey(), e.getMessage(), e);
        }
        if (registeredXOUnits.contains(xoUnit.getName())) {
            LOGGER.debug("Update not yet supported {}", pid);
            return;
        }
        XOManagerFactory xoManagerFactory = new XOManagerFactoryImpl(xoUnit);

        Dictionary<String, Object> p = new Hashtable<>();
        p.put("name", xoUnit.getName());
        ServiceRegistration<XOManagerFactory> serviceRegistration = componentContext.getBundleContext().registerService(XOManagerFactory.class, xoManagerFactory, p);

        registeredXOUnits.add(xoUnit.getName());
        pidsToFactories.put(pid, xoManagerFactory);
        pidsToServiceRegistrations.put(pid, serviceRegistration);
    }

    @Override
    public void deleted(String pid) {
        XOManagerFactory XOManagerFactory = pidsToFactories.remove(pid);
        if (XOManagerFactory != null) {
            registeredXOUnits.remove(XOManagerFactory.getXOUnit().getName());
            closeXOManagerFactory(XOManagerFactory);

        }
        ServiceRegistration<XOManagerFactory> registration = pidsToServiceRegistrations.remove(pid);
        if (registration != null) {
            registration.unregister();
        }
    }

    @SuppressWarnings("rawtypes")
    public void activate(ComponentContext componentContext) {
        this.componentContext = componentContext;
        Dictionary properties = componentContext.getProperties();
        this.componentName = (String) properties.get(ComponentConstants.COMPONENT_NAME);
    }

    public void deactivate(ComponentContext componentContext) {
        List<String> pids = new ArrayList<String>();
        pids.addAll(this.pidsToServiceRegistrations.keySet());
        for (String pid : pids) {
            deleted(pid);
        }
    }

    private void closeXOManagerFactory(XOManagerFactory xoManagerFactory) {
        if (xoManagerFactory != null) {
            xoManagerFactory.close();
        }
    }

    private XOUnit getXOUnit(Dictionary<String, ?> properties) throws ConfigurationException {
        // must: url
        String url = (String) properties.get(XOUnitParameter.URL.getKey());
        if (url == null) {
            throw new ConfigurationException(XOUnitParameter.URL.getKey(), "Property missing");
        }
        // must: types
        Collection<String> typeNames = (Collection<String>) properties.get(XOUnitParameter.TYPES.getKey());
        if (typeNames == null) {
            throw new ConfigurationException(XOUnitParameter.TYPES.getKey(), "Property missing");
        }
        Collection<Class<?>> types = ClassHelper.getTypes(typeNames);
        // must: provider
        String providerName = (String) properties.get(XOUnitParameter.PROVIDER.getKey());
        if (providerName == null) {
            throw new ConfigurationException(XOUnitParameter.PROVIDER.getKey(), "Property missing");
        }
        Class<? extends XODatastoreProvider> provider = ClassHelper.getType(providerName);

        XOUnitBuilder builder;
        try {
            builder = XOUnitBuilder.create(url, provider, types.toArray(new Class[]{}));
        } catch (URISyntaxException e) {
            throw new ConfigurationException(XOUnitParameter.URL.getKey(), "Could not convert '" + url + "' to url", e);
        }

        // optional: name
        String name = (String) properties.get(XOUnitParameter.NAME.getKey());
        builder.name(name);

        // optional: description
        String description = (String) properties.get(XOUnitParameter.DESCRIPTION.getKey());
        builder.description(description);

        // optional: listeners
        Collection<String> listenerNames = (Collection<String>) properties.get(XOUnitParameter.INSTANCE_LISTENERS.getKey());
        if (listenerNames != null) {
            Collection<Class<?>> instanceListeners = ClassHelper.getTypes(listenerNames);
            builder.instanceListenerTypes(instanceListeners.toArray(new Class[]{}));
        }

        // optional: concurrency
        String concurrencyMode = (String) properties.get(XOUnitParameter.CONCURRENCY_MODE.getKey());
        builder.concurrencyMode(concurrencyMode);

        // optional: validation
        String validationMode = (String) properties.get(XOUnitParameter.VALIDATION_MODE.getKey());
        builder.validationMode(validationMode);

        // optional: transaction
        String transactionMode = (String) properties.get(XOUnitParameter.TRANSACTION_ATTRIBUTE.getKey());
        builder.transactionAttribute(transactionMode);

        return builder.create();
    }

}
