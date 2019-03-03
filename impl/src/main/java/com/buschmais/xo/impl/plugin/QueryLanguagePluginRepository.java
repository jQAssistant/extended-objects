package com.buschmais.xo.impl.plugin;

import java.lang.annotation.Annotation;

import com.buschmais.xo.spi.datastore.Datastore;
import com.buschmais.xo.spi.plugin.QueryLanguagePlugin;

/**
 * Implementation of a {@link PluginRepository} for
 * {@link com.buschmais.xo.spi.plugin.QueryLanguagePlugin}s.
 */
public class QueryLanguagePluginRepository extends AbstractPluginRepository<Class<? extends Annotation>, QueryLanguagePlugin<? extends Annotation>> {

    private final Datastore<?, ?, ?, ?, ?> datastore;

    /**
     * Constructor.
     *
     * @param datastore
     *            The datastore to be used.
     */
    public QueryLanguagePluginRepository(Datastore<?, ?, ?, ?, ?> datastore) {
        this.datastore = datastore;
    }

    @Override
    protected Class<? extends Annotation> getKey(QueryLanguagePlugin<? extends Annotation> queryLanguagePlugin) {
        return queryLanguagePlugin.init(datastore);
    }

    @Override
    public Class<? super QueryLanguagePlugin<? extends Annotation>> getPluginType() {
        return QueryLanguagePlugin.class;
    }
}
