package com.buschmais.cdo.impl.transaction;

import com.buschmais.cdo.api.CdoTransaction;
import com.buschmais.cdo.api.ResultIterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TransactionalResultIterator<E> implements ResultIterator<E>, CdoTransaction.Synchronization {

    private ResultIterator<E> delegateIterator;
    private CdoTransaction cdoTransaction;

    public TransactionalResultIterator(ResultIterator<E> delegateIterator, CdoTransaction cdoTransaction) {
        this.cdoTransaction = cdoTransaction;
        this.delegateIterator = delegateIterator;
        this.cdoTransaction = cdoTransaction;
        if (cdoTransaction != null) {
            cdoTransaction.registerSynchronization(this);
        }
    }

    @Override
    public void close() {
        delegateIterator.close();
    }

    @Override
    public boolean hasNext() {
        boolean hasNext = delegateIterator.hasNext();
        if (!hasNext && cdoTransaction != null) {
            unregisterSynchronization();
        }
        return hasNext;
    }

    @Override
    public E next() {
        return delegateIterator.next();
    }

    @Override
    public void remove() {
        delegateIterator.remove();
    }

    @Override
    public void beforeCompletion() {
        final List<E> detachedList = new ArrayList<>();
        while (delegateIterator.hasNext()) {
            detachedList.add(delegateIterator.next());
        }
        final Iterator<E> iterator = detachedList.iterator();
        this.delegateIterator = new ResultIterator<E>() {
            @Override
            public void close() {
            }

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public E next() {
                return iterator.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Remove is not supported for this iterator.");
            }
        };
    }

    @Override
    public void afterCompletion(boolean committed) {
        unregisterSynchronization();
    }

    private void unregisterSynchronization() {
        cdoTransaction.unregisterSynchronization(this);
        this.cdoTransaction = null;
    }
}
