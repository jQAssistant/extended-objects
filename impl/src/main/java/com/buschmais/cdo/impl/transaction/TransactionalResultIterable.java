package com.buschmais.cdo.impl.transaction;

import com.buschmais.cdo.api.CdoTransaction;
import com.buschmais.cdo.api.ResultIterable;
import com.buschmais.cdo.api.ResultIterator;
import com.buschmais.cdo.impl.AbstractResultIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TransactionalResultIterable<E> extends AbstractResultIterable<E> implements CdoTransaction.Synchronization {

    private ResultIterable<E> delegate;

    private CdoTransaction cdoTransaction;

    public TransactionalResultIterable(ResultIterable<E> delegate, CdoTransaction cdoTransaction) {
        this.delegate = delegate;
        this.cdoTransaction = cdoTransaction;
        cdoTransaction.registerSynchronization(this);
    }

    @Override
    public ResultIterator<E> iterator() {
        final ResultIterator<E> delegateIterator = delegate.iterator();
        return new TransactionalResultIterator<>(delegateIterator, cdoTransaction);
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
                final Iterator<E> detachedIterator=detachedList.iterator();
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

    protected  ResultIterable<E> getDelegate() {
        return delegate;
    }

    private void unregisterSynchronization() {
        cdoTransaction.unregisterSynchronization(this);
        this.cdoTransaction = null;
    }

}
