package com.buschmais.cdo.api;

public class CdoException extends RuntimeException {

    public CdoException(String message) {
        super(message);
    }

    public CdoException(String message, Throwable cause) {
        super(message, cause);
    }
}
