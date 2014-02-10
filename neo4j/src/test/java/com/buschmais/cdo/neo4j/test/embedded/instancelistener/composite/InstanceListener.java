package com.buschmais.cdo.neo4j.test.embedded.instancelistener.composite;

import com.buschmais.cdo.api.annotation.*;

import java.util.ArrayList;
import java.util.List;

public class InstanceListener {

    private static final List<Object> postCreate = new ArrayList<>();

    private static final List<Object> preUpdate = new ArrayList<>();

    private static final List<Object> postUpdate = new ArrayList<>();

    private static final List<Object> preDelete = new ArrayList<>();

    private static final List<Object> postDelete = new ArrayList<>();

    private static final List<Object> postLoad = new ArrayList<>();

    @PostCreate
    public void postCreate(Object instance) {
        postCreate.add(instance);
    }

    @PreUpdate
    public void preUpdate(Object instance) {
        preUpdate.add(instance);
    }

    @PostUpdate
    public void postUpdate(Object instance) {
        postUpdate.add(instance);
    }

    @PreDelete
    public void preDelete(Object instance) {
        preDelete.add(instance);
    }

    @PostDelete
    public void postDelete(Object instance) {
        postDelete.add(instance);
    }

    @PostLoad
    public void postLoad(Object instance) {
        postLoad.add(instance);
    }

    public static List<Object> getPostCreate() {
        return postCreate;
    }

    public static List<Object> getPreUpdate() {
        return preUpdate;
    }

    public static List<Object> getPostUpdate() {
        return postUpdate;
    }

    public static List<Object> getPreDelete() {
        return preDelete;
    }

    public static List<Object> getPostDelete() {
        return postDelete;
    }

    public static List<Object> getPostLoad() {
        return postLoad;
    }
}

