package com.buschmais.cdo.neo4j.spi;

public interface Datastore<DS extends DatastoreSession> {

    DS createSession();

    void close();

}
