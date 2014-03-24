package com.buschmais.xo.api;

/**
 * Defines an interface which is transparently implemented by all composite instances created by the {@link XOManager}.
 */
public interface CompositeObject {

    /**
     * Cast the instance to a specific type.
     *
     * @param type The type.
     * @param <T>  The type.
     * @return The instance.
     */
    <T> T as(Class<T> type);

    /**
     * Return the underlying datastore delegate.
     *
     * @param <D> The expected type.
     * @return The datastore delegate.
     */
    <D> D getDelegate();

}
