package com.buschmais.xo.api;

/**
 * Classes which implement this interface provide methods that deal with the
 * close events that are generated when the framework close a resource.
 *
 * @see XOManagerFactory
 * @see XOManager
 *
 * @since 0.8
 */
public interface CloseListener {

    /**
     * Occurs before the Closeable is closed.
     */
    void onBeforeClose();

    /**
     * Occurs after the Closable is closed.
     */
    void onAfterClose();

}
