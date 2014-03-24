package com.buschmais.xo.impl;

import com.buschmais.xo.api.ResultIterable;
import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.api.XOException;

public abstract class AbstractResultIterable<T> implements ResultIterable<T> {

    @Override
    public T getSingleResult() {
        ResultIterator<T> iterator = iterator();
        if (!iterator.hasNext()) {
            throw new XOException("No result available.");
        }
        try {
            T singleResult = iterator.next();
            if (iterator.hasNext()) {
                throw new XOException("More than one result available.");
            }
            return singleResult;
        } finally {
            iterator.close();
        }
    }

    @Override
    public boolean hasResult() {
        return iterator().hasNext();
    }
}
