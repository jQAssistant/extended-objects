package com.buschmais.xo.impl.bootstrap.osgi;

import static com.buschmais.xo.api.bootstrap.XOUnitParameter.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Iterator;
import java.util.Properties;

import com.buschmais.xo.api.ConcurrencyMode;
import com.buschmais.xo.api.Transaction;
import com.buschmais.xo.api.ValidationMode;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.api.bootstrap.XOUnitParameter;
import com.buschmais.xo.spi.bootstrap.XODatastoreProvider;
import com.buschmais.xo.spi.reflection.ClassHelper;

import org.osgi.service.cm.ConfigurationException;

public final class XOUnitConverter {

    private XOUnitConverter() {
    }

    public static XOUnit fromProperties(final Dictionary<String, ?> properties) throws ConfigurationException {
        // must: url
        String url = getProperty(properties, URL);
        // must: types
        Collection<String> typeNames = getProperty(properties, TYPES);
        Collection<Class<?>> types = ClassHelper.getTypes(typeNames);
        // must: provider
        String providerName = getProperty(properties, PROVIDER);
        Class<? extends XODatastoreProvider> provider = ClassHelper.getType(providerName);

        XOUnit.XOUnitBuilder builder;
        try {
            builder = XOUnit.builder().uri(new URI(url)).provider(provider).types(types);
        } catch (URISyntaxException e) {
            throw new ConfigurationException(URL.getKey(), "Could not convert '" + url + "' to url", e);
        }

        // optional: name
        String name = (String) properties.get(NAME.getKey());
        builder.name(name);

        // optional: description
        String description = (String) properties.get(DESCRIPTION.getKey());
        builder.description(description);

        // optional: listeners
        Collection<String> listenerNames = (Collection<String>) properties.get(INSTANCE_LISTENERS.getKey());
        if (listenerNames != null) {
            Collection<Class<?>> instanceListeners = ClassHelper.getTypes(listenerNames);
            builder.instanceListeners(instanceListeners);
        }

        // optional: concurrency
        String concurrencyMode = (String) properties.get(CONCURRENCY_MODE.getKey());
        builder.concurrencyMode(ConcurrencyMode.valueOf(concurrencyMode));

        // optional: validation
        String validationMode = (String) properties.get(VALIDATION_MODE.getKey());
        builder.validationMode(ValidationMode.valueOf(validationMode));

        // optional: transaction
        String transactionMode = (String) properties.get(TRANSACTION_ATTRIBUTE.getKey());
        builder.defaultTransactionAttribute(Transaction.TransactionAttribute.valueOf(transactionMode));

        // optional: properties
        Collection<Object> entries = (Collection<Object>) properties.get(PROPERTIES.getKey());
        if (entries != null) {
            // only simple types and collections/arrays of simple types allowed
            Properties providerProps = toMap(entries);
            builder.properties(providerProps);
        }

        return builder.build();
    }

    private static <T> T getProperty(Dictionary<String, ?> properties, XOUnitParameter parameter) throws ConfigurationException {
        T value = (T) properties.get(parameter.getKey());
        if (value == null) {
            throw new ConfigurationException(parameter.getKey(), "Property " + parameter.getKey() + " missing");
        }
        return value;
    }

    private static Properties toMap(Collection<Object> entries) throws ConfigurationException {
        Properties properties = new Properties();
        if ((entries.size() % 2) == 0) {
            Iterator<Object> it = entries.iterator();
            while (it.hasNext()) {
                Object key = it.next();
                Object value = it.next();
                properties.put(key, value);
            }
        } else {
            throw new ConfigurationException(PROPERTIES.getKey(), "");
        }
        return properties;
    }

}
