package com.buschmais.cdo.api;

public interface CdoManagerFactory {

    CdoManager createCdoManager();

    void close();
}
