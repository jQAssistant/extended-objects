package com.buschmais.cdo.neo4j.impl.common;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.ResultIterable;
import com.buschmais.cdo.api.ResultIterator;

public abstract class AbstractResultIterable<T> implements ResultIterable<T> {

    @Override
    public T getSingleResult() {
        ResultIterator<T> iterator = iterator();
        if (!iterator.hasNext()) {
            throw new CdoException("No result available.");
        }
        try {
            T singleResult = iterator.next();
            if (iterator.hasNext()) {
                throw new CdoException("More than one result available.");
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
