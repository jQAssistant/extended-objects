package com.buschmais.xo.neo4j.remote.impl.model.state;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class StateTracker<T, C extends Collection<T>> {

    private C elements;

    private Set<T> added = new HashSet<>();

    private Set<T> removed = new HashSet<>();

    public StateTracker(C elements) {
        this.elements = elements;
    }

    public void load(C elements) {
        this.elements = elements;
        this.added.clear();
        this.removed.clear();
    }

    public void add(T t) {
        elements.add(t);
        if (!removed.remove(t)) {
            added.add(t);
        }
    }

    public void addAll(C added) {
        for (T t : added) {
            add(t);
        }
    }

    public void remove(T t) {
        elements.remove(t);
        if (!added.remove(t)) {
            removed.add(t);
        }
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

    @Override
    public String toString() {
        return "StateTracker{" + "elements=" + elements + ", added=" + added + ", removed=" + removed + '}';
    }
}
