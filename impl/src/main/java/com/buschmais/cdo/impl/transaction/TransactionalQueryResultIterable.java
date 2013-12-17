package com.buschmais.cdo.impl.transaction;

import com.buschmais.cdo.api.CdoTransaction;
import com.buschmais.cdo.api.Query;

import java.io.IOException;

public class TransactionalQueryResultIterable<E extends Query.Result<E>> extends TransactionalResultIterable<E> implements Query.Result<E> {

    public TransactionalQueryResultIterable(Query.Result<E> delegate, CdoTransaction cdoTransaction) {
        super(delegate, cdoTransaction);
    }

    @Override
    public void close() throws IOException {
        ((Query.Result) getDelegate()).close();

    }
}
