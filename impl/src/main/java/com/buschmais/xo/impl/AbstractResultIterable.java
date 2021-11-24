package com.buschmais.xo.impl;

import com.buschmais.xo.api.ResultIterable;
import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.api.XOException;

public abstract class AbstractResultIterable<T> implements ResultIterable<T> {

    @Override
    public T getSingleResult() {
        try (ResultIterator<T> iterator = iterator()) {
            if (!iterator.hasNext()) {
                throw new XOException("No result available.");
            }
            T singleResult = iterator.next();
            if (iterator.hasNext()) {
                T nextResult = iterator.next();
                throw new XOException("Expected exactly one result, but got '" + singleResult + "' and '" + nextResult + "'");
            }
            return singleResult;
        }
    }

    @Override
    public boolean hasResult() {
        return iterator().hasNext();
    }
}
