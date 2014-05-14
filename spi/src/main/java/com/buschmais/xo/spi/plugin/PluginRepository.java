package com.buschmais.xo.spi.plugin;

import com.buschmais.xo.spi.datastore.Datastore;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by Dirk Mahler on 14.05.2014.
 */
public class PluginRepository {

    private Map<Class<? extends Annotation>, QueryPlugin<?, ?>> queryPlugins;

    void init(Datastore datastore) {
        for (QueryPlugin<?, ?> queryPlugin : getQueryPlugins(datastore)) {
            Class<? extends Annotation> queryLanguage = queryPlugin.init(datastore);
            queryPlugins.put(queryLanguage, queryPlugin);
        }
    }

    public QueryPlugin getQueryPlugin(Class<? extends Annotation> queryLanguage) {
        return queryPlugins.get(queryLanguage);
    }

    public List<QueryPlugin<?, ?>> getQueryPlugins(Datastore datastore) {
        return Collections.emptyList();
    }
}
