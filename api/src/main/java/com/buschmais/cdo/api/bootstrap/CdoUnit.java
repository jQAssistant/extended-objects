package com.buschmais.cdo.api.bootstrap;

import com.buschmais.cdo.api.TransactionAttribute;
import com.buschmais.cdo.api.ValidationMode;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * Represents a CDO unit, i.e. a configuration for a {@link com.buschmais.cdo.api.CdoManagerFactory}.
 */
public class CdoUnit {

    private final String name;

    private final String description;

    private final URI uri;

    private final Class<?> provider;

    private final Set<Class<?>> types;

    private final ValidationMode validationMode;

    private final TransactionAttribute defaultTransactionAttribute;

    private final Properties properties;

    public CdoUnit(String name, String description, URI uri, Class<?> provider, Set<Class<?>> types, ValidationMode validationMode, TransactionAttribute defaultTransactionAttribute, Properties properties) {
        this.name = name;
        this.description = description;
        this.uri = uri;
        this.provider = provider;
        this.types = types;
        this.validationMode = validationMode;
        this.defaultTransactionAttribute = defaultTransactionAttribute;
        this.properties = properties;
    }

    public CdoUnit(String name, String description, URI uri, Class<?> provider, Class<?>[] types, ValidationMode validationMode, TransactionAttribute defaultTransactionAttribute, Properties properties) {
        this(name, description, uri, provider, new HashSet<>(Arrays.asList(types)), validationMode, defaultTransactionAttribute, properties);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public URI getUri() {
        return uri;
    }

    public URL getUrl() {
        try {
            return getUri().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public Class<?> getProvider() {
        return provider;
    }

    public Set<Class<?>> getTypes() {
        return types;
    }

    public ValidationMode getValidationMode() {
        return validationMode;
    }

    public TransactionAttribute getDefaultTransactionAttribute() {
        return defaultTransactionAttribute;
    }

    public Properties getProperties() {
        return properties;
    }
}
