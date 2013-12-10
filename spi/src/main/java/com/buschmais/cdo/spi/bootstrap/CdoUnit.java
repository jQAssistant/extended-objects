package com.buschmais.cdo.spi.bootstrap;

import com.buschmais.cdo.api.CdoManagerFactory;

import java.net.URL;
import java.util.Properties;
import java.util.Set;

import static com.buschmais.cdo.api.CdoManagerFactory.TransactionAttribute;
import static com.buschmais.cdo.api.CdoManagerFactory.ValidationMode;

public class CdoUnit {

    private String name;

    private String description;

    private URL url;

    private Class<? extends CdoDatastoreProvider> provider;

    private Set<Class<?>> types;

    private ValidationMode validationMode;

    private TransactionAttribute transactionAttribute;

    private Properties properties;

    public CdoUnit(String name, String description, URL url, Class<? extends CdoDatastoreProvider> provider, Set<Class<?>> types, ValidationMode validationMode, TransactionAttribute transactionAttribute, Properties properties) {
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

    public Class<? extends CdoDatastoreProvider> getProvider() {
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
