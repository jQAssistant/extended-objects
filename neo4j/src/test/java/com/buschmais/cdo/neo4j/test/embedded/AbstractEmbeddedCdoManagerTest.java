package com.buschmais.cdo.neo4j.test.embedded;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.CdoManagerFactory;
import com.buschmais.cdo.neo4j.impl.AbstractNeo4jCdoManagerFactoryImpl;
import com.buschmais.cdo.neo4j.impl.EmbeddedNeo4jCdoManagerFactoryImpl;
import com.buschmais.cdo.neo4j.impl.node.metadata.NodeMetadata;
import com.buschmais.cdo.neo4j.impl.node.metadata.PrimitivePropertyMethodMetadata;
import com.buschmais.cdo.neo4j.test.AbstractCdoManagerTest;
import org.junit.After;
import org.junit.Before;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.util.*;

import static com.buschmais.cdo.api.Query.Result;
import static com.buschmais.cdo.api.Query.Result.CompositeRowObject;

public abstract class AbstractEmbeddedCdoManagerTest extends AbstractCdoManagerTest {

    @Override
    protected AbstractNeo4jCdoManagerFactoryImpl getNeo4jCdoManagerFactory(Class<?>[] types) throws MalformedURLException {
        return new EmbeddedNeo4jCdoManagerFactoryImpl(new File("target/neo4j/embedded").toURI().toURL(), getTypes());
    }

}
