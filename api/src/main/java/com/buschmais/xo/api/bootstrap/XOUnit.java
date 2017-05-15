package com.buschmais.xo.api.bootstrap;

import static com.buschmais.xo.api.Transaction.TransactionAttribute;
import static lombok.AccessLevel.PRIVATE;

import java.net.URI;
import java.util.*;

import com.buschmais.xo.api.ConcurrencyMode;
import com.buschmais.xo.api.ValidationMode;

import lombok.*;
import lombok.Builder.Default;

/**
 * Represents a XO unit, i.e. a configuration for a
 * {@link com.buschmais.xo.api.XOManagerFactory}.
 */
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
@ToString
public class XOUnit {

    @Getter
    @Builder
    @NoArgsConstructor(access = PRIVATE)
    @AllArgsConstructor(access = PRIVATE)
    @ToString
    public static class MappingConfiguration {

        /**
         * Indicates if mappings for entities and relations are performed
         * strict. If <code>true</code> any detected problems will be reported
         * by an {@link com.buschmais.xo.api.XOException} at startup, otherwise
         * a warning.
         */
        @Default
        private boolean strictValidation = false;

    }

    /**
     * The name which is used to uniquely identify the XO unit.
     */
    @Default
    private String name = "default";

    /*
     * A human readable description (optional).
     */
    @Default
    private String description = "The default XO unit.";

    /*
     * The datastore specific URI.
     */
    private URI uri;

    /**
     * The provider class to use.
     */
    private Class<?> provider;

    /**
     * The entity types to register.
     */
    @Singular
    private Set<? extends Class<?>> types = new HashSet<>();

    /**
     * The instance listener types.
     */
    @Singular
    private List<? extends Class<?>> instanceListeners = new ArrayList<>();

    /**
     * The {@link com.buschmais.xo.api.ValidationMode} to use.
     */
    @Default
    private ValidationMode validationMode = ValidationMode.AUTO;

    /**
     * The {@link com.buschmais.xo.api.ConcurrencyMode} to use.
     */
    @Default
    private ConcurrencyMode concurrencyMode = ConcurrencyMode.SINGLETHREADED;

    /**
     * The {@link TransactionAttribute} to use.
     */
    @Default
    private TransactionAttribute defaultTransactionAttribute = TransactionAttribute.NONE;

    /**
     * Additional properties to be passed to the provider.
     */
    @Default
    private Properties properties = new Properties();

    /**
     * The mapping configuration.
     */
    @Default
    private MappingConfiguration mappingConfiguration = MappingConfiguration.builder().build();

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
