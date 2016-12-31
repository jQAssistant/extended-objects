package com.buschmais.xo.api;

/**
 * This adapter class provides default implementations for the methods described
 * by the {@link CloseListener} interface.
 * <p>
 * Classes that wish to deal with close events can extend this class and
 * override only the methods which they are interested in.
 *
 * @since 0.8
 */
public class CloseAdapter implements CloseListener {

    /**
     * The default behavior is to do nothing.
     */
    @Override
    public void onBeforeClose() {
    }

    /**
     * The default behavior is to do nothing.
     */
    @Override
    public void onAfterClose() {
    }

}
