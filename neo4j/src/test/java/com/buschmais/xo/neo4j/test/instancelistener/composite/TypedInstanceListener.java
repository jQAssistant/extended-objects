package com.buschmais.xo.neo4j.test.instancelistener.composite;

import java.util.ArrayList;
import java.util.List;

import com.buschmais.xo.api.annotation.PostCreate;

public class TypedInstanceListener {

    private List<A> listOfA;

    private List<B> listOfB;

    private List<A2B> listOfA2B;

    public TypedInstanceListener() {
        listOfA = new ArrayList<>();
        listOfB = new ArrayList<>();
        listOfA2B = new ArrayList<>();
    }

    @PostCreate
    public void postCreateA(A instance) {
        this.listOfA.add(instance);
    }

    @PostCreate
    public void postCreateB(B instance) {
        this.listOfB.add(instance);
    }

    @PostCreate
    public void postCreateA2B(A2B instance) {
        this.listOfA2B.add(instance);
    }

    public List<A> getListOfA() {
        return listOfA;
    }

    public List<B> getListOfB() {
        return listOfB;
    }

    public List<A2B> getListOfA2B() {
        return listOfA2B;
    }
}
