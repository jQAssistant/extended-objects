package com.buschmais.xo.neo4j.remote.impl.model.state;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class StateTracker<T, C extends Collection<T>> {

    private C elements;

    private Set<T> added = new HashSet<T>();

    private Set<T> removed = new HashSet<T>();

    public StateTracker(C elements) {
        this.elements = elements;
    }

    public void add(T t) {
        elements.add(t);
        added.add(t);
        removed.remove(t);
    }

    public void addAll(C added) {
        for (T t : added) {
            add(t);
        }
    }

    public void remove(T t) {
        elements.remove(t);
        removed.add(t);
        added.remove(t);
    }

    public void removeAll(C removed) {
        for (T t : removed) {
            remove(t);
        }
    }

    public Set<T> getAdded() {
        return added;
    }

    public Set<T> getRemoved() {
        return removed;
    }

    public C getElements() {
        return elements;
    }

    public void flush() {
        added.clear();
        removed.clear();
    }

}
