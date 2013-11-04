package com.buschmais.cdo.api;

public class CdoManagerException extends RuntimeException {

    public CdoManagerException(String message) {
        super(message);
    }

    public CdoManagerException(String message, Throwable cause) {
        super(message, cause);
    }
}
