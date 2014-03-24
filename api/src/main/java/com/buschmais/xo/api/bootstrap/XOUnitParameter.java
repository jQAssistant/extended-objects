package com.buschmais.xo.api.bootstrap;

/**
 * Identifiers for {@link XOUnit} configurations.
 */
public enum XOUnitParameter {

    NAME, DESCRIPTION, URL, PROVIDER, TYPES, INSTANCE_LISTENERS, CONCURRENCY_MODE, TRANSACTION_ATTRIBUTE, VALIDATION_MODE;

    public String getKey() {
        return name().toLowerCase();
    }

}
