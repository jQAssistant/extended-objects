package com.buschmais.cdo.api.bootstrap;

import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import com.buschmais.cdo.api.TransactionAttribute;

import com.buschmais.cdo.api.ValidationMode;

/**
 * Represents a CDO unit, i.e. a configuration for a {@link com.buschmais.cdo.api.CdoManagerFactory}.
 */
public class CdoUnit {

    private String name;

    private String description;

    private URL url;

    private Class<?> provider;

    private Set<Class<?>> types;

    private ValidationMode validationMode;

    private TransactionAttribute defaultTransactionAttribute;

    private Properties properties;

    public CdoUnit(String name, String description, URL url, Class<?> provider, Set<Class<?>> types, ValidationMode validationMode, TransactionAttribute defaultTransactionAttribute, Properties properties) {
        this.name = name;
        this.description = description;
        this.url = url;
        this.provider = provider;
        this.types = types;
        this.validationMode = validationMode;
        this.defaultTransactionAttribute = defaultTransactionAttribute;
        this.properties = properties;
    }

    public CdoUnit(String name, String description, URL url, Class<?> provider, Class<?>[] types, ValidationMode validationMode, TransactionAttribute defaultTransactionAttribute, Properties properties) {
        this(name, description, url, provider, new HashSet<>(Arrays.asList(types)), validationMode, defaultTransactionAttribute, properties);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public URL getUrl() {
        return url;
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
