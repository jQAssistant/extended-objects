package com.buschmais.cdo.api.bootstrap;

import com.buschmais.cdo.api.CdoManagerFactory;
import com.buschmais.cdo.api.ConcurrencyMode;
import com.buschmais.cdo.api.ValidationMode;

import java.net.URI;
import java.util.*;

import static com.buschmais.cdo.api.Transaction.TransactionAttribute;

/**
 * Represents a CDO unit, i.e. a configuration for a {@link CdoManagerFactory}.
 */
public class CdoUnit {

    private final String name;

    private final String description;

    private final URI uri;

    private final Class<?> provider;

    private final Set<Class<?>> types;

    private final ValidationMode validationMode;

    private final ConcurrencyMode concurrencyMode;

    private final TransactionAttribute defaultTransactionAttribute;

    private final Properties properties;

    private final List<Class<?>> instanceListeners;

    /**
     * Constructs a CDO unit.
     *
     * @param name                        The name which is used to uniquely identify the CDO unit.
     * @param description                 A human readable description (optional).
     * @param uri                         The datastore specific URI.
     * @param provider                    The provider class to use.
     * @param types                       The entity types to register.
     * @param validationMode              The {@link com.buschmais.cdo.api.ValidationMode} to use.
     * @param concurrencyMode             The {@link com.buschmais.cdo.api.ConcurrencyMode} to use.
     * @param defaultTransactionAttribute The {@link com.buschmais.cdo.api.Transaction.TransactionAttribute} to use.
     * @param properties                  Additional properties to be passed to the provider.
     * @param instanceListeners             The Entity listener types.
     */
    public CdoUnit(String name, String description, URI uri, Class<?> provider, Set<Class<?>> types, ValidationMode validationMode, ConcurrencyMode concurrencyMode, TransactionAttribute defaultTransactionAttribute, Properties properties, List<Class<?>> instanceListeners) {
        this.name = name;
        this.description = description;
        this.uri = uri;
        this.provider = provider;
        this.types = types;
        this.validationMode = validationMode;
        this.concurrencyMode = concurrencyMode;
        this.defaultTransactionAttribute = defaultTransactionAttribute;
        this.properties = properties;
        this.instanceListeners = instanceListeners;
    }

    /**
     * Constructs a CDO unit.
     *
     * @param name                        The name which is used to uniquely identify the CDO unit.
     * @param description                 A human readable description (optional).
     * @param uri                         The datastore specific URI.
     * @param provider                    The provider class to use.
     * @param types                       The entity types to register.
     * @param validationMode              The {@link com.buschmais.cdo.api.ValidationMode} to use.
     * @param concurrencyMode             The {@link com.buschmais.cdo.api.ConcurrencyMode} to use.
     * @param defaultTransactionAttribute The {@link com.buschmais.cdo.api.Transaction.TransactionAttribute} to use.
     * @param properties                  Additional properties to be passed to the provider.
     * @param instanceListeners             The Entity listener types.
     */
    public CdoUnit(String name, String description, URI uri, Class<?> provider, Class<?>[] types, ValidationMode validationMode, ConcurrencyMode concurrencyMode, TransactionAttribute defaultTransactionAttribute, Properties properties, List<Class<?>> instanceListeners) {
        this(name, description, uri, provider, new HashSet<>(Arrays.asList(types)), validationMode, concurrencyMode, defaultTransactionAttribute, properties, instanceListeners);
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

    public List<Class<?>> getInstanceListeners() {
        return instanceListeners;
    }
}
