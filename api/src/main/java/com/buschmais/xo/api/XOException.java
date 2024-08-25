package com.buschmais.xo.api;

/**
 * The exception thrown by the XO implementation.
 */
public class XOException extends RuntimeException {

    /**
     * Constructor.
     *
     * @param message
     *     The message.
     */
    public XOException(String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param message
     *     The message.
     * @param cause
     *     The cause.
     */
    public XOException(String message, Throwable cause) {
        super(message, cause);
    }
}
