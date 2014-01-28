package com.buschmais.cdo.api.bootstrap;

import com.buschmais.cdo.api.CdoManagerFactory;
import com.buschmais.cdo.api.ConcurrencyMode;
import com.buschmais.cdo.api.ValidationMode;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import static com.buschmais.cdo.api.Transaction.TransactionAttribute;

/**
 * Represents a CDO unit, i.e. a configuration for a {@link CdoManagerFactory}.
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

    /**
     * Constructs a CDO unit.
     *
     * @param name                        The name which is used to uniquely identify the CDO unit.
     * @param description                 A human readable description (optional).
     * @param uri                         The datastore specific URI.
     * @param provider                    The provider class to use.
     * @param types                       The entity types to register.
     * @param validationMode              The {@link ValidationMode} to use.
     * @param concurrencyMode             The {@link ConcurrencyMode} to use.
     * @param defaultTransactionAttribute The {@link TransactionAttribute} to use.
     * @param properties                  Additional properties to be passed to the provider.
     */
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

    /**
     * Constructs a CDO unit.
     *
     * @param name                        The name which is used to uniquely identify the CDO unit.
     * @param description                 A human readable description (optional).
     * @param uri                         The datastore specific URI.
     * @param provider                    The provider class to use.
     * @param types                       The entity types to register.
     * @param validationMode              The {@link ValidationMode} to use.
     * @param concurrencyMode             The {@link ConcurrencyMode} to use.
     * @param defaultTransactionAttribute The {@link TransactionAttribute} to use.
     * @param properties                  Additional properties to be passed to the provider.
     */
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
