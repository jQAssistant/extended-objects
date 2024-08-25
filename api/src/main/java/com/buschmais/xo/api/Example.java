package com.buschmais.xo.api;

/**
 * An example to be used for
 * {@link com.buschmais.xo.api.XOManager#find(Example, Class)}.
 *
 * @param <T>
 *     The type to be prepared.
 */
@FunctionalInterface
public interface Example<T> {
    void prepare(T example);
}
