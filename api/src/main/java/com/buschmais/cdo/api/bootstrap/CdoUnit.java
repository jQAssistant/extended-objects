package com.buschmais.cdo.api.bootstrap;

import com.buschmais.cdo.api.ConcurrencyMode;
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

    private String name;

    private String description;

    private URI uri;

    private Class<?> provider;

    private Set<Class<?>> types;

    private ValidationMode validationMode;

    private ConcurrencyMode concurrencyMode;

    private TransactionAttribute defaultTransactionAttribute;

    private Properties properties;

    public CdoUnit(String name, String description, URI uri, Class<?> provider, Set<Class<?>> types, ValidationMode validationMode, ConcurrencyMode concurrencyMode, TransactionAttribute defaultTransactionAttribute, Properties properties) {
        this.name = name;
        this.description = description;
        this.uri = uri;
        this.provider = provider;
        this.types = types;
        this.validationMode = validationMode;
        this.concurrencyMode = concurrencyMode;
        this.defaultTransactionAttribute = defaultTransactionAttribute;
        this.properties = properties;
    }

    public CdoUnit(String name, String description, URI uri, Class<?> provider, Class<?>[] types, ValidationMode validationMode, ConcurrencyMode concurrencyMode, TransactionAttribute defaultTransactionAttribute, Properties properties) {
        this(name, description, uri, provider, new HashSet<>(Arrays.asList(types)), validationMode, concurrencyMode, defaultTransactionAttribute, properties);
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

    public ConcurrencyMode getConcurrencyMode() {
        return concurrencyMode;
    }

    public TransactionAttribute getDefaultTransactionAttribute() {
        return defaultTransactionAttribute;
    }

    public Properties getProperties() {
        return properties;
    }
}
