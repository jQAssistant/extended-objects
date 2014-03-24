package com.buschmais.xo.impl.transaction;

import com.buschmais.xo.api.ResultIterable;
import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.api.XOTransaction;
import com.buschmais.xo.impl.AbstractResultIterable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TransactionalResultIterable<E> extends AbstractResultIterable<E> implements XOTransaction.Synchronization {

    private final XOTransaction XOTransaction;

    private ResultIterable<E> delegate;

    public TransactionalResultIterable(ResultIterable<E> delegate, XOTransaction xoTransaction) {
        this.delegate = delegate;
        this.XOTransaction = xoTransaction;
        xoTransaction.registerSynchronization(this);
    }

    @Override
    public ResultIterator<E> iterator() {
        final ResultIterator<E> delegateIterator = delegate.iterator();
        return new TransactionalResultIterator<>(delegateIterator, XOTransaction);
    }

    @Override
    public void beforeCompletion() {
        final List<E> detachedList = new ArrayList<>();
        ResultIterator<E> iterator = delegate.iterator();
        while (iterator.hasNext()) {
            detachedList.add(iterator.next());
        }
        this.delegate = new AbstractResultIterable<E>() {
            @Override
            public ResultIterator<E> iterator() {
                final Iterator<E> detachedIterator = detachedList.iterator();
                return new ResultIterator<E>() {
                    @Override
                    public void close() {
                    }

                    @Override
                    public boolean hasNext() {
                        return detachedIterator.hasNext();
                    }

                    @Override
                    public E next() {
                        return detachedIterator.next();
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("Remove is not supported for this iterator.");
                    }
                };
            }
        };
    }

    @Override
    public void afterCompletion(boolean committed) {
        unregisterSynchronization();
    }

    protected ResultIterable<E> getDelegate() {
        return delegate;
    }

    private void unregisterSynchronization() {
        XOTransaction.unregisterSynchronization(this);
    }

}
