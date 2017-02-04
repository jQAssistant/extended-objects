package com.buschmais.xo.api.bootstrap;

import static com.buschmais.xo.api.Transaction.TransactionAttribute;

import java.net.URI;
import java.util.*;

import com.buschmais.xo.api.ConcurrencyMode;
import com.buschmais.xo.api.ValidationMode;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Represents a XO unit, i.e. a configuration for a
 * {@link com.buschmais.xo.api.XOManagerFactory}.
 */
@Getter
@Builder
@ToString
public class XOUnit {

    /**
     * The name which is used to uniquely identify the XO unit.
     */
    private String name = "default";

    /*
     * A human readable description (optional).
     */
    private String description = "The default XO unit.";

    /*
     * The datastore specific URI.
     */
    private final URI uri;

    /**
     * The provider class to use.
     */
    private final Class<?> provider;

    /**
     * The entity types to register.
     */
    private final Set<? extends Class<?>> types;

    /**
     * The instance listener types.
     */
    private final List<? extends Class<?>> instanceListeners;

    /**
     * The {@link com.buschmais.xo.api.ValidationMode} to use.
     */
    private ValidationMode validationMode = ValidationMode.AUTO;

    /**
     * The {@link com.buschmais.xo.api.ConcurrencyMode} to use.
     */
    private ConcurrencyMode concurrencyMode = ConcurrencyMode.SINGLETHREADED;

    /**
     * The {@link TransactionAttribute} to use.
     */
    private TransactionAttribute defaultTransactionAttribute = TransactionAttribute.NONE;

    /**
     * Additional properties to be passed to the provider.
     */
    private Properties properties = new Properties();

    /**
     * Constructs a XO unit.
     *
     * This constructor is deprecated, use {@link #builder()} instead.
     *
     * @param name
     *            The name which is used to uniquely identify the XO unit.
     * @param description
     *            A human readable description (optional).
     * @param uri
     *            The datastore specific URI.
     * @param provider
     *            The provider class to use.
     * @param types
     *            The entity types to register.
     * @param instanceListeners
     *            The instance listener types.
     * @param validationMode
     *            The {@link com.buschmais.xo.api.ValidationMode} to use.
     * @param concurrencyMode
     *            The {@link com.buschmais.xo.api.ConcurrencyMode} to use.
     * @param defaultTransactionAttribute
     *            The {@link TransactionAttribute} to use.
     * @param properties
     *            Additional properties to be passed to the provider.
     */
    @Deprecated
    public XOUnit(String name, String description, URI uri, Class<?> provider, Set<? extends Class<?>> types, List<? extends Class<?>> instanceListeners,
            ValidationMode validationMode, ConcurrencyMode concurrencyMode, TransactionAttribute defaultTransactionAttribute, Properties properties) {
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
     * Constructs a XO unit.
     *
     * This constructor is deprecated, use {@link #builder()} instead.
     *
     * @param name
     *            The name which is used to uniquely identify the XO unit.
     * @param description
     *            A human readable description (optional).
     * @param uri
     *            The datastore specific URI.
     * @param provider
     *            The provider class to use.
     * @param types
     *            The entity types to register.
     * @param instanceListeners
     *            The instance listener types.
     * @param validationMode
     *            The {@link com.buschmais.xo.api.ValidationMode} to use.
     * @param concurrencyMode
     *            The {@link com.buschmais.xo.api.ConcurrencyMode} to use.
     * @param defaultTransactionAttribute
     *            The {@link TransactionAttribute} to use.
     * @param properties
     *            Additional properties to be passed to the provider.
     */
    @Deprecated
    public XOUnit(String name, String description, URI uri, Class<?> provider, Class<?>[] types, List<? extends Class<?>> instanceListeners,
            ValidationMode validationMode, ConcurrencyMode concurrencyMode, TransactionAttribute defaultTransactionAttribute, Properties properties) {
        this(name, description, uri, provider, new HashSet<>(Arrays.asList(types)), instanceListeners, validationMode, concurrencyMode,
                defaultTransactionAttribute, properties);
    }
}
