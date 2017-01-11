package com.buschmais.xo.neo4j.test.mapping;

import com.buschmais.xo.api.CompositeObject;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.api.model.Neo4jNode;
import com.buschmais.xo.neo4j.api.model.Neo4jRelationship;
import com.buschmais.xo.neo4j.test.AbstractNeo4jXOManagerTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import java.net.URISyntaxException;
import java.util.Collection;

import static com.buschmais.xo.api.Query.Result;
import static com.buschmais.xo.api.Query.Result.CompositeRowObject;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class UnmappedTypesTest extends AbstractNeo4jXOManagerTest {

    public UnmappedTypesTest(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() throws URISyntaxException {
        return xoUnits();
    }

    @Test
    public void query() {
        XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        Result<CompositeRowObject> result = xoManager.createQuery("CREATE (x:X)-[y:Y]->(z:Z) RETURN x, y, z").execute();
        CompositeRowObject compositeRowObject = result.getSingleResult();
        CompositeObject x = compositeRowObject.get("x", CompositeObject.class);
        assertThat(x.getDelegate(), instanceOf(Neo4jNode.class));
        CompositeObject y = compositeRowObject.get("y", CompositeObject.class);
        assertThat(y.getDelegate(), instanceOf(Neo4jRelationship.class));
        CompositeObject z = compositeRowObject.get("z", CompositeObject.class);
        assertThat(z.getDelegate(), instanceOf(Neo4jNode.class));
        xoManager.currentTransaction().commit();
    }

}
