package com.buschmais.xo.neo4j.embedded.api;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLDecoder;

import com.buschmais.xo.api.XOException;

import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileDatabaseManagementServiceBuilderFactory implements DatabaseManagementServiceBuilderFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileDatabaseManagementServiceBuilderFactory.class);

    @Override
    public DatabaseManagementServiceBuilder createDatabaseManagementServiceBuilder(URI uri) {
        String path;
        try {
            path = URLDecoder.decode(uri.toURL()
                .getPath(), "UTF-8");
        } catch (UnsupportedEncodingException | MalformedURLException e) {
            throw new XOException("Cannot get path fro URI" + uri, e);
        }
        File storeDir = new File(path);
        storeDir.mkdirs();
        LOGGER.debug("Creating graph database service datastore for directory '{}'.", storeDir.getAbsolutePath());
        return new DatabaseManagementServiceBuilder(storeDir.toPath());
    }
}
