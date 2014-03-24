package com.buschmais.xo.neo4j.api;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.spi.bootstrap.XODatastoreProvider;
import com.buschmais.xo.spi.datastore.Datastore;
import org.apache.commons.lang.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URI;

public class Neo4jXOProvider implements XODatastoreProvider {

    private static final Logger LOG = LoggerFactory.getLogger(Neo4jXOProvider.class);

    @Override
    public Datastore<?, ?, ?, ?, ?> createDatastore(XOUnit XOUnit) {
        URI uri = XOUnit.getUri();
        DatastoreFactory datastoreFactory = lookupFactory(uri);
        try {
            return datastoreFactory.createGraphDatabaseService(uri);
        } catch (MalformedURLException e) {
            throw new XOException("Cannot create datastore.", e);
        }

    }

    @SuppressWarnings("unchecked")
    DatastoreFactory lookupFactory(URI uri) {
        String factoryClass = getFactoryClassName(uri);
        LOG.debug("try to lookup provider-class {}", factoryClass);

        try {
            return ((Class<? extends DatastoreFactory>) Class.forName(factoryClass)).newInstance();
        } catch (ReflectiveOperationException e) {
            throw new XOException("Cannot create datastore factory.", e);
        }
    }

    private String getFactoryClassName(URI uri) {
        String protocol = WordUtils.capitalize(uri.getScheme().toLowerCase());
        return DatastoreFactory.class.getPackage().getName() + "." + protocol + "DatastoreFactory";
    }
}
