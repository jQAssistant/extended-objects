package com.buschmais.xo.impl.transaction;

import com.buschmais.xo.api.Query;
import com.buschmais.xo.api.XOTransaction;

import java.io.IOException;

public class TransactionalQueryResultIterable<E extends Query.Result<E>> extends TransactionalResultIterable<E> implements Query.Result<E> {

    public TransactionalQueryResultIterable(Query.Result<E> delegate, XOTransaction xoTransaction) {
        super(delegate, xoTransaction);
    }

    @Override
    public void close() throws IOException {
        ((Query.Result) getDelegate()).close();

    }
}
