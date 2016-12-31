package com.buschmais.xo.api;

/**
 *
 * @see XOManagerFactory
 * @see XOManager
 * 
 * @since 0.8
 */
public interface CloseSupport {

    void addCloseListener(CloseListener listener);

    void removeCloseListener(CloseListener listener);

}
