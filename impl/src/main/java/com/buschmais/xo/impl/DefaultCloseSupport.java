package com.buschmais.xo.impl;

import java.util.HashSet;
import java.util.Set;

import com.buschmais.xo.api.CloseListener;
import com.buschmais.xo.api.CloseSupport;

class DefaultCloseSupport implements CloseSupport {

    private Set<CloseListener> listeners = new HashSet<>();

    @Override
    public void addCloseListener(CloseListener listener) {
        if (listener == null) {
            return;
        }
        listeners.add(listener);
    }

    @Override
    public void removeCloseListener(CloseListener listener) {
        if (listener == null) {
            return;
        }
        listeners.remove(listener);
    }

    void fireOnBeforeClose() {
        if (listeners != null) {
            for (CloseListener listener : listeners) {
                listener.onBeforeClose();
            }
        }
    }

    void fireOnAfterClose() {
        if (listeners != null) {
            for (CloseListener listener : listeners) {
                listener.onAfterClose();
            }
        }

    }
}
