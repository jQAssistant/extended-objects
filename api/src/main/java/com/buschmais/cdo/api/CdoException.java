package com.buschmais.cdo.api;

/**
 * The exception thrown by the CDO implementation-
 */
public class CdoException extends RuntimeException {

    /**
     * Constructor.
     *
     * @param message The message.
     */
    public CdoException(String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param message The message.
     * @param cause   The cause.
     */
    public CdoException(String message, Throwable cause) {
        super(message, cause);
    }
}
