package com.buschmais.xo.api.bootstrap;

import com.buschmais.xo.api.ConcurrencyMode;
import com.buschmais.xo.api.Transaction;
import com.buschmais.xo.api.Transaction.TransactionAttribute;
import com.buschmais.xo.api.ValidationMode;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Provides functionality to build {@link XOUnit}s.
 */
public class XOUnitBuilder {

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

    XOUnitBuilder(URI uri, Class<?> provider, Class<?>[] types) {
        this.uri = uri;
        this.provider = provider;
        this.types = types;
    }

    public static XOUnitBuilder create(URI uri, Class<?> provider, Class<?>[] types) {
        return new XOUnitBuilder(uri, provider, types);
    }

    public static XOUnitBuilder create(String uri, Class<?> provider, Class<?>[] types) throws URISyntaxException {
        return create(new URI(uri), provider, types);
    }

    public XOUnitBuilder name(String name) {
        if (name != null) {
            this.name = name;
        }
        return this;
    }

    public XOUnitBuilder description(String description) {
        if (description != null) {
            this.description = description;
        }
        return this;
    }

    public XOUnitBuilder validationMode(ValidationMode validationMode) {
        this.validationMode = validationMode;
        return this;
    }

    public XOUnitBuilder validationMode(String validation) {
        if (validation != null) {
            ValidationMode mode = ValidationMode.valueOf(validation);
            return validationMode(mode);
        }
        return this;
    }

    public XOUnitBuilder concurrencyMode(ConcurrencyMode concurrencyMode) {
        this.concurrencyMode = concurrencyMode;
        return this;
    }

    public XOUnitBuilder concurrencyMode(String concurrency) {
        if (concurrency != null) {
            ConcurrencyMode mode = ConcurrencyMode.valueOf(concurrency);
            return concurrencyMode(mode);
        }
        return this;
    }

    public XOUnitBuilder transactionAttribute(Transaction.TransactionAttribute transactionAttribute) {
        this.transactionAttribute = transactionAttribute;
        return this;
    }

    public XOUnitBuilder transactionAttribute(String transaction) {
        if (transaction != null) {
            TransactionAttribute mode = TransactionAttribute.valueOf(transaction);
            return transactionAttribute(mode);
        }
        return this;
    }

    public XOUnitBuilder instanceListenerTypes(Class<?>... instanceListenerTypes) {
        this.instanceListenerTypes = Arrays.asList(instanceListenerTypes);
        return this;
    }

    public XOUnit create() {
        return new XOUnit(name, description, uri, provider, types, instanceListenerTypes, validationMode, concurrencyMode, transactionAttribute, properties);
    }

}
