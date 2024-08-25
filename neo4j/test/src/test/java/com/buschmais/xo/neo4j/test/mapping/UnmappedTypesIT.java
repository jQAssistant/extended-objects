package com.buschmais.xo.neo4j.test.mapping;

import static com.buschmais.xo.api.Query.Result;
import static com.buschmais.xo.api.Query.Result.CompositeRowObject;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;

import com.buschmais.xo.api.CompositeObject;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.api.model.Neo4jNode;
import com.buschmais.xo.neo4j.api.model.Neo4jRelationship;
import com.buschmais.xo.neo4j.test.AbstractNeo4JXOManagerIT;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class UnmappedTypesIT extends AbstractNeo4JXOManagerIT {

    public UnmappedTypesIT(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() {
        return xoUnits();
    }

    @Test
    public void query() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        Result<CompositeRowObject> result = xoManager.createQuery("CREATE (x:X)-[y:Y]->(z:Z) RETURN x, y, z").execute();
        CompositeRowObject compositeRowObject = result.getSingleResult();
        CompositeObject x = compositeRowObject.get("x", CompositeObject.class);
        assertThat((Object)x.getDelegate()).isInstanceOf(Neo4jNode.class);
        CompositeObject y = compositeRowObject.get("y", CompositeObject.class);
        assertThat((Object)y.getDelegate()).isInstanceOf(Neo4jRelationship.class);
        CompositeObject z = compositeRowObject.get("z", CompositeObject.class);
        assertThat((Object)z.getDelegate()).isInstanceOf(Neo4jNode.class);
        xoManager.currentTransaction().commit();
    }

}
