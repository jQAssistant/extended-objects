package com.buschmais.cdo.api.bootstrap;

/**
 * Identifiers for {@link CdoUnit} configurations.
 *
 */
public enum CdoUnitParameter {

    NAME, DESCRIPTION, URL, PROVIDER, TYPES, INSTANCE_LISTENERS, CONCURRENCY_MODE, TRANSACTION_ATTRIBUTE, VALIDATION_MODE;

    public String getKey() {
        return name().toLowerCase();
    }

}
