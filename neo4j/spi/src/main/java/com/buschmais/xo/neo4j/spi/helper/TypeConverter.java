package com.buschmais.xo.neo4j.spi.helper;

public interface TypeConverter {

    Class<?> getType();

    Object convert(Object value);

}
