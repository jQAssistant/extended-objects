package com.buschmais.cdo.api;

public interface CompositeObject {

    <T> T as(Class<T> type);

}
