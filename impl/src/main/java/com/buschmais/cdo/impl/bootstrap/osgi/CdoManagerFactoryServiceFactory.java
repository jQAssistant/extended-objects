package com.buschmais.cdo.impl.bootstrap.osgi;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.ComponentConstants;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.CdoManagerFactory;
import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.api.bootstrap.CdoUnitBuilder;
import com.buschmais.cdo.api.bootstrap.CdoUnitParameter;
import com.buschmais.cdo.impl.CdoManagerFactoryImpl;
import com.buschmais.cdo.impl.reflection.ClassHelper;
import com.buschmais.cdo.spi.bootstrap.CdoDatastoreProvider;

public class CdoManagerFactoryServiceFactory implements ManagedServiceFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(CdoManagerFactoryServiceFactory.class);

    private final Map<String, CdoManagerFactory> pidsToFactories;

    private final Map<String, ServiceRegistration<CdoManagerFactory>> pidsToServiceRegistrations;

    private final Set<String> registeredCdoUnits;

    private ComponentContext componentContext;

    private String componentName;

    public CdoManagerFactoryServiceFactory() {
        pidsToFactories = new HashMap<>();
        pidsToServiceRegistrations = new HashMap<>();
        registeredCdoUnits = new HashSet<>();
    }

    @Override
    public String getName() {
        return this.componentName;
    }

    @Override
    public void updated(String pid, Dictionary<String, ?> properties) throws ConfigurationException {
        CdoUnit cdoUnit;
        try {
            cdoUnit = getCdoUnit(properties);
        } catch (CdoException e) {
            throw new ConfigurationException(CdoUnitParameter.NAME.getKey(), e.getMessage(), e);
        }
        if (registeredCdoUnits.contains(cdoUnit.getName())) {
            LOGGER.debug("Update not yet supported {}", pid);
            return;
        }
        CdoManagerFactory cdoManagerFactory = new CdoManagerFactoryImpl<>(cdoUnit);

        Dictionary<String, Object> p = new Hashtable<>();
        p.put("name", cdoUnit.getName());
        ServiceRegistration<CdoManagerFactory> serviceRegistration = componentContext.getBundleContext().registerService(CdoManagerFactory.class,
                cdoManagerFactory, p);

        registeredCdoUnits.add(cdoUnit.getName());
        pidsToFactories.put(pid, cdoManagerFactory);
        pidsToServiceRegistrations.put(pid, serviceRegistration);
    }

    @Override
    public void deleted(String pid) {
        CdoManagerFactory cdoManagerFactory = pidsToFactories.remove(pid);
        if (cdoManagerFactory != null) {
            registeredCdoUnits.remove(cdoManagerFactory.getCdoUnit().getName());
            closeCdoManagerFactory(cdoManagerFactory);

        }
        ServiceRegistration<CdoManagerFactory> registration = pidsToServiceRegistrations.remove(pid);
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

    private void closeCdoManagerFactory(CdoManagerFactory cdoManagerFactory) {
        if (cdoManagerFactory != null) {
            cdoManagerFactory.close();
        }
    }

    private CdoUnit getCdoUnit(Dictionary<String, ?> properties) throws ConfigurationException {
        // must: url
        String url = (String) properties.get(CdoUnitParameter.URL.getKey());
        if (url == null) {
            throw new ConfigurationException(CdoUnitParameter.URL.getKey(), "Property missing");
        }
        // must: types
        Collection<String> typeNames = (Collection<String>) properties.get(CdoUnitParameter.TYPES.getKey());
        if (typeNames == null) {
            throw new ConfigurationException(CdoUnitParameter.TYPES.getKey(), "Property missing");
        }
        Collection<Class<?>> types = ClassHelper.getTypes(typeNames);
        // must: provider
        String providerName = (String) properties.get(CdoUnitParameter.PROVIDER.getKey());
        if (providerName == null) {
            throw new ConfigurationException(CdoUnitParameter.PROVIDER.getKey(), "Property missing");
        }
        Class<? extends CdoDatastoreProvider> provider = ClassHelper.getType(providerName);

        CdoUnitBuilder builder;
        try {
            builder = CdoUnitBuilder.create(url, provider, types.toArray(new Class[] {}));
        } catch (URISyntaxException e) {
            throw new ConfigurationException(CdoUnitParameter.URL.getKey(), "Could not convert '" + url + "' to url", e);
        }

        // optional: name
        String name = (String) properties.get(CdoUnitParameter.NAME.getKey());
        builder.name(name);

        // optional: description
        String description = (String) properties.get(CdoUnitParameter.DESCRIPTION.getKey());
        builder.description(description);

        // optional: listeners
        Collection<String> listenerNames = (Collection<String>) properties.get(CdoUnitParameter.INSTANCE_LISTENERS.getKey());
        if (listenerNames != null) {
            Collection<Class<?>> instanceListeners = ClassHelper.getTypes(listenerNames);
            builder.instanceListenerTypes(instanceListeners.toArray(new Class[] {}));
        }

        // optional: concurrency
        String concurrencyMode = (String) properties.get(CdoUnitParameter.CONCURRENCY_MODE.getKey());
        builder.concurrencyMode(concurrencyMode);

        // optional: validation
        String validationMode = (String) properties.get(CdoUnitParameter.VALIDATION_MODE.getKey());
        builder.validationMode(validationMode);

        // optional: transaction
        String transactionMode = (String) properties.get(CdoUnitParameter.TRANSACTION_ATTRIBUTE.getKey());
        builder.transactionAttribute(transactionMode);

        return builder.create();
    }

}
