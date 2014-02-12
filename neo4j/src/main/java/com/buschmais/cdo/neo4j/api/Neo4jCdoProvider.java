package com.buschmais.cdo.neo4j.api;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.spi.bootstrap.CdoDatastoreProvider;
import com.buschmais.cdo.spi.datastore.Datastore;
import org.apache.commons.lang.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URI;

public class Neo4jCdoProvider implements CdoDatastoreProvider {

    private static final Logger LOG = LoggerFactory.getLogger(Neo4jCdoProvider.class);

    @Override
    public Datastore<?, ?, ?, ?, ?> createDatastore(CdoUnit cdoUnit) {
        URI uri = cdoUnit.getUri();
        DatastoreFactory datastoreFactory = lookupFactory(uri);
        try {
            return datastoreFactory.createGraphDatabaseService(uri);
        } catch (MalformedURLException e) {
            throw new CdoException("Cannot create datastore.", e);
        }

    }

    @SuppressWarnings("unchecked")
    DatastoreFactory lookupFactory(URI uri) {
        String factoryClass = getFactoryClassName(uri);
        LOG.debug("try to lookup provider-class {}", factoryClass);

        try {
            return ((Class<? extends DatastoreFactory>) Class.forName(factoryClass)).newInstance();
        } catch (ReflectiveOperationException e) {
            throw new CdoException("Cannot create datastore factory.", e);
        }
    }

    private String getFactoryClassName(URI uri) {
        String protocol = WordUtils.capitalize(uri.getScheme().toLowerCase());
        return DatastoreFactory.class.getPackage().getName() + "." + protocol + "DatastoreFactory";
    }
}
