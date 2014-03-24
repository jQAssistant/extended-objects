package com.buschmais.xo.impl.test.bootstrap.composite;

import com.buschmais.xo.api.annotation.*;

/**
 * Test implementation of an instance listener.
 */
public class InstanceListener {

    @PostCreate
    public void postCreate(Object instance) {
    }

    @PreUpdate
    public void preUpdate(Object instance) {
    }

    @PostUpdate
    public void postUpdate(Object instance) {
    }

    @PreDelete
    public void preDelete(Object instance) {
    }

    @PostDelete
    public void postDelete(Object instance) {
    }

    @PostLoad
    public void postLoad(Object instance) {
    }
}
