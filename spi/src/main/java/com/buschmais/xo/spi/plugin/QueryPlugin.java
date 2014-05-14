package com.buschmais.xo.spi.plugin;

import com.buschmais.xo.spi.datastore.Datastore;
import com.buschmais.xo.spi.datastore.DatastoreQuery;
import com.buschmais.xo.spi.datastore.DatastoreSession;

import java.lang.annotation.Annotation;

/**
 * Created by Dirk Mahler on 14.05.2014.
 */
public interface QueryPlugin<D extends Datastore, QL extends Annotation> {

    Class<QL> init(Datastore datastore);

    DatastoreQuery<QL> createQuery(DatastoreSession session);

}
