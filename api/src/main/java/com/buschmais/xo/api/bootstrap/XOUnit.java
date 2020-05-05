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
         * Indicates if mappings for entities and relations are performed strict. If
         * <code>true</code> any detected problems will be reported by an
         * {@link com.buschmais.xo.api.XOException} at startup, otherwise a warning.
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
     * If <code>true</code> cached values will be cleared after transaction
     * completion.
     */
    @Default
    private boolean clearAfterCompletion = true;

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

}
