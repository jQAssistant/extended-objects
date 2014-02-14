package com.buschmais.cdo.api.bootstrap;

import com.buschmais.cdo.api.ConcurrencyMode;
import com.buschmais.cdo.api.Transaction;
import com.buschmais.cdo.api.ValidationMode;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Provides functionality to build {@link com.buschmais.cdo.api.bootstrap.CdoUnit}s.
 */
public class CdoUnitBuilder {

    private String name = "default";

    private String description = "The default CDO unit.";

    private URI uri;

    private Class<?> provider;

    private Class<?>[] types;

    private ValidationMode validationMode = ValidationMode.AUTO;

    private ConcurrencyMode concurrencyMode = ConcurrencyMode.SINGLETHREADED;

    private Transaction.TransactionAttribute transactionAttribute = Transaction.TransactionAttribute.MANDATORY;

    private List<Class<?>> instanceListenerTypes = Collections.emptyList();

    private Properties properties = new Properties();

    CdoUnitBuilder(URI uri, Class<?> provider, Class<?>[] types) {
        this.uri = uri;
        this.provider = provider;
        this.types = types;
    }

    public static CdoUnitBuilder create(URI uri, Class<?> provider, Class<?>[] types) {
        return new CdoUnitBuilder(uri, provider, types);
    }

    public CdoUnitBuilder name(String name) {
        this.name = name;
        return this;
    }

    public CdoUnitBuilder description(String description) {
        this.description = description;
        return this;
    }

    public CdoUnitBuilder validationMode(ValidationMode validationMode) {
        this.validationMode = validationMode;
        return this;
    }

    public CdoUnitBuilder concurrencyMode(ConcurrencyMode concurrencyMode) {
        this.concurrencyMode = concurrencyMode;
        return this;
    }

    public CdoUnitBuilder transactionAttribute(Transaction.TransactionAttribute transactionAttribute) {
        this.transactionAttribute = transactionAttribute;
        return this;
    }

    public CdoUnitBuilder instanceListenerTypes(Class<?>... instanceListenerTypes) {
        this.instanceListenerTypes = Arrays.asList(instanceListenerTypes);
        return this;
    }

    public CdoUnit create() {
        return new CdoUnit(name, description, uri, provider, types, instanceListenerTypes, validationMode, concurrencyMode, transactionAttribute, properties);
    }
}
