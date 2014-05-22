package com.buschmais.xo.spi.plugin;

import com.buschmais.xo.spi.datastore.Datastore;
import com.buschmais.xo.spi.datastore.DatastoreQuery;
import com.buschmais.xo.spi.datastore.DatastoreSession;

import java.lang.annotation.Annotation;

/**
 * A plugin interface to extend query languages by annoations.
 */
public interface QueryLanguagePlugin<QL extends Annotation> {

    /**
     * Initialize the plugin.
     *
     * @param datastore The datastore.
     * @return The query language.
     */
    Class<QL> init(Datastore<?, ?, ?, ?, ?> datastore);

    /**
     * Create a query.
     *
     * @param session The session.
     * @return The query.
     */
    DatastoreQuery<QL> createQuery(DatastoreSession<?, ?, ?, ?, ?, ?, ?, ?, ?> session);

}
