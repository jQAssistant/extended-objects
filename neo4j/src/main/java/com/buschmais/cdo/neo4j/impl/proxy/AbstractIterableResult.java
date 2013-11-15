package com.buschmais.cdo.neo4j.impl.proxy;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.IterableResult;

import java.util.Iterator;

public abstract class AbstractIterableResult<T> implements IterableResult<T> {

    @Override
    public  T getSingleResult() {
        Iterator<T> iterator = iterator();
        if (!iterator.hasNext()) {
            throw new CdoException("No result available.");
        }
        T singleResult = iterator.next();
        if (iterator.hasNext()) {
            throw new CdoException("More than one result available.");
        }
        return singleResult;
    }
}
