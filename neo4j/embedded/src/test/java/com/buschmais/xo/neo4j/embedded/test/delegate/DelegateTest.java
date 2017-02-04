package com.buschmais.xo.neo4j.embedded.test.delegate;

import static com.buschmais.xo.api.Query.Result;
import static com.buschmais.xo.api.Query.Result.CompositeRowObject;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hamcrest.collection.IsMapContaining;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.neo4j.graphdb.DynamicLabel;

import com.buschmais.xo.api.CompositeObject;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.api.model.Neo4jNode;
import com.buschmais.xo.neo4j.api.model.Neo4jRelationship;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedLabel;
import com.buschmais.xo.neo4j.embedded.test.AbstractNeo4jXOManagerTest;
import com.buschmais.xo.neo4j.embedded.test.delegate.composite.A;
import com.buschmais.xo.neo4j.embedded.test.delegate.composite.A2B;
import com.buschmais.xo.neo4j.embedded.test.delegate.composite.B;

@RunWith(Parameterized.class)
public class DelegateTest extends AbstractNeo4jXOManagerTest {

    public DelegateTest(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() throws URISyntaxException {
        return xoUnits(A.class, B.class, A2B.class);
    }

    @Test
    public void entity() {
        XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        Neo4jNode node = ((CompositeObject) xoManager.create(A.class)).getDelegate();
        assertThat(node.hasLabel(new EmbeddedLabel(DynamicLabel.label("A"))), equalTo(true));
        xoManager.currentTransaction().commit();
    }

    @Test
    public void relation() {
        XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        A a = xoManager.create(A.class);
        B b = xoManager.create(B.class);
        xoManager.create(a, A2B.class, b);
        List<A2B> r = executeQuery("MATCH (a:A)-[r]->(b:B) RETURN r").getColumn("r");
        assertThat(r.size(), equalTo(1));
        Neo4jRelationship relationship = ((CompositeObject) r.get(0)).getDelegate();
        assertThat(relationship.getType().getName(), equalTo("RELATION"));
        xoManager.currentTransaction().commit();
    }

    @Test
    public void row() {
        XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        A a = xoManager.create(A.class);
        Result<CompositeRowObject> row = xoManager.createQuery("match (a:A) return a").execute();
        Map<String, Object> delegate = row.getSingleResult().getDelegate();
        assertThat(delegate, IsMapContaining.<String, Object>hasEntry("a", a));
        xoManager.currentTransaction().commit();
    }
}
