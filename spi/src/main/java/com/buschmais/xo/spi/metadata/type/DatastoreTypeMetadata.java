package com.buschmais.xo.spi.metadata.type;

public interface DatastoreTypeMetadata<DatastoreMetadata> extends TypeMetadata {

    DatastoreMetadata getDatastoreMetadata();

    boolean isAbstract();

    boolean isFinal();
}
