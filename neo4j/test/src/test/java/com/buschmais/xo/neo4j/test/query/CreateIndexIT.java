package com.buschmais.xo.neo4j.test.query;

import java.util.Collection;
import java.util.Set;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.spi.Neo4jDatastoreSession;
import com.buschmais.xo.neo4j.spi.Neo4jDatastoreSession.Index;
import com.buschmais.xo.neo4j.test.AbstractNeo4JXOManagerIT;
import com.buschmais.xo.neo4j.test.query.composite.A;
import com.buschmais.xo.neo4j.test.query.composite.C;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class CreateIndexIT extends AbstractNeo4JXOManagerIT {

    public CreateIndexIT(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() {
        return xoUnits(A.class, C.class);
    }

    @Test
    public void createIndex() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        Set<Index> indexes = ((Neo4jDatastoreSession<?, ?, ?, ?>) xoManager.getDatastoreSession(Neo4jDatastoreSession.class)).getIndexes();
        assertThat(indexes).contains(Index.builder()
                .label("A")
                .property("value")
                .build(), Index.builder()
                .label("C")
                .property("value")
                .build());
        xoManager.currentTransaction()
            .commit();
    }
}
