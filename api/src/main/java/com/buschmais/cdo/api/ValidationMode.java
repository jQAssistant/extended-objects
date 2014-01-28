package com.buschmais.cdo.api;

/**
 * Defines the supported instance validation modes.
 */
public enum ValidationMode {
    /**
     * No validation will be performed. The application must explicitly call {@link CdoManager#validate()}.
     */
    NONE,
    /**
     * Instance will be automatically validated when instance are flushed to the datastore.
     */
    AUTO;
}
