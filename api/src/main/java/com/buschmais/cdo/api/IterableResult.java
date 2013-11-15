package com.buschmais.cdo.api;

public interface IterableResult<T> extends Iterable<T> {

    T getSingleResult();

}
