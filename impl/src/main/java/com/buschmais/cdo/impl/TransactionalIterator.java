package com.buschmais.cdo.impl;

import com.buschmais.cdo.api.CdoTransaction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TransactionalIterator<E> implements Iterator<E>, CdoTransaction.Synchronization {

    private Iterator<E> delegate;

    private CdoTransaction cdoTransaction;

    public TransactionalIterator(Iterator<E> delegate, CdoTransaction cdoTransaction) {
        this.delegate = delegate;
        this.cdoTransaction = cdoTransaction;
        cdoTransaction.registerSynchronization(this);
    }

    @Override
    public boolean hasNext() {
        boolean hasNext = delegate.hasNext();
        if (!hasNext) {
            cdoTransaction.unregisterSynchronization(this);
        }
        return hasNext;
    }

    @Override
    public E next() {
        return delegate.next();
    }

    @Override
    public void remove() {
        delegate.remove();
    }

    @Override
    public void beforeCompletion() {
        List<E> detachedList = new ArrayList<>();
        while (delegate.hasNext()) {
            detachedList.add(delegate.next());
        }
        this.delegate = detachedList.iterator();
    }

    @Override
    public void afterCompletion(boolean committed) {
    }
}
