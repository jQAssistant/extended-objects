package com.buschmais.xo.api.bootstrap;

import com.buschmais.xo.api.ConcurrencyMode;
import com.buschmais.xo.api.ValidationMode;

import java.net.URI;
import java.util.*;

import static com.buschmais.xo.api.Transaction.TransactionAttribute;

/**
 * Represents a CDO unit, i.e. a configuration for a {@link com.buschmais.xo.api.XOManagerFactory}.
 */
public class XOUnit {

    private final String name;

    private final String description;

    private final URI uri;

    private final Class<?> provider;

    private final Set<? extends Class<?>> types;

    private final List<? extends Class<?>> instanceListeners;

    private final ValidationMode validationMode;

    private final ConcurrencyMode concurrencyMode;

    private final TransactionAttribute defaultTransactionAttribute;

    private final Properties properties;


    /**
     * Constructs a CDO unit.
     *
     * @param name                        The name which is used to uniquely identify the CDO unit.
     * @param description                 A human readable description (optional).
     * @param uri                         The datastore specific URI.
     * @param provider                    The provider class to use.
     * @param types                       The entity types to register.
     * @param instanceListeners           The instance listener types.
     * @param validationMode              The {@link com.buschmais.xo.api.ValidationMode} to use.
     * @param concurrencyMode             The {@link com.buschmais.xo.api.ConcurrencyMode} to use.
     * @param defaultTransactionAttribute The {@link com.buschmais.xo.api.Transaction.TransactionAttribute} to use.
     * @param properties                  Additional properties to be passed to the provider.
     */
    public XOUnit(String name, String description, URI uri, Class<?> provider, Set<? extends Class<?>> types, List<? extends Class<?>> instanceListeners, ValidationMode validationMode, ConcurrencyMode concurrencyMode, TransactionAttribute defaultTransactionAttribute, Properties properties) {
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
     * @param instanceListeners           The instance listener types.
     * @param validationMode              The {@link com.buschmais.xo.api.ValidationMode} to use.
     * @param concurrencyMode             The {@link com.buschmais.xo.api.ConcurrencyMode} to use.
     * @param defaultTransactionAttribute The {@link com.buschmais.xo.api.Transaction.TransactionAttribute} to use.
     * @param properties                  Additional properties to be passed to the provider.
     */
    public XOUnit(String name, String description, URI uri, Class<?> provider, Class<?>[] types, List<? extends Class<?>> instanceListeners, ValidationMode validationMode, ConcurrencyMode concurrencyMode, TransactionAttribute defaultTransactionAttribute, Properties properties) {
        this(name, description, uri, provider, new HashSet<>(Arrays.asList(types)), instanceListeners, validationMode, concurrencyMode, defaultTransactionAttribute, properties);
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

    public Set<? extends Class<?>> getTypes() {
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

    public List<? extends Class<?>> getInstanceListeners() {
        return instanceListeners;
    }
}
