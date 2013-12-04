package com.buschmais.cdo.api.bootstrap;

import java.net.URL;
import java.util.Properties;
import java.util.Set;

public class CdoUnit {

    public enum ValidationMode {
        NONE, AUTO;
    }

    public enum TransactionAttribute {
        MANDATORY,
        REQUIRES;
    }

    private String name;

    private String description;

    private URL url;

    private Class<? extends CdoProvider> provider;

    private Set<Class<?>> types;

    private ValidationMode validationMode;

    private TransactionAttribute transactionAttribute;

    private Properties properties;

    public CdoUnit(String name, String description, URL url, Class<? extends CdoProvider> provider, Set<Class<?>> types, ValidationMode validationMode, TransactionAttribute transactionAttribute, Properties properties) {
        this.name = name;
        this.description = description;
        this.url = url;
        this.provider = provider;
        this.types = types;
        this.validationMode = validationMode;
        this.transactionAttribute = transactionAttribute;
        this.properties = properties;
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

    public Class<? extends CdoProvider> getProvider() {
        return provider;
    }

    public Set<Class<?>> getTypes() {
        return types;
    }

    public ValidationMode getValidationMode() {
        return validationMode;
    }

    public TransactionAttribute getTransactionAttribute() {
        return transactionAttribute;
    }

    public Properties getProperties() {
        return properties;
    }
}
