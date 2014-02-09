package com.buschmais.cdo.neo4j.test.embedded.delegate;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.CompositeObject;
import com.buschmais.cdo.neo4j.test.embedded.AbstractEmbeddedCdoManagerTest;
import com.buschmais.cdo.neo4j.test.embedded.delegate.composite.A;
import com.buschmais.cdo.neo4j.test.embedded.delegate.composite.A2B;
import com.buschmais.cdo.neo4j.test.embedded.delegate.composite.B;
import org.hamcrest.collection.IsMapContaining;
import org.junit.Test;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import java.util.List;
import java.util.Map;

import static com.buschmais.cdo.api.Query.Result;
import static com.buschmais.cdo.api.Query.Result.CompositeRowObject;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class DelegateTest extends AbstractEmbeddedCdoManagerTest {

    @Override
    protected Class<?>[] getTypes() {
        return new Class<?>[]{A.class, B.class, A2B.class};
    }

    @Test
    public void entity() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        Node node = ((CompositeObject) cdoManager.create(A.class)).getDelegate();
        assertThat(node.hasLabel(DynamicLabel.label("A")), equalTo(true));
        cdoManager.currentTransaction().commit();
    }

    @Test
    public void relation() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        A a = cdoManager.create(A.class);
        B b = cdoManager.create(B.class);
        cdoManager.create(a, A2B.class, b);
        List<A2B> r = executeQuery("MATCH (a:A)-[r]->(b:B) RETURN r").getColumn("r");
        assertThat(r.size(), equalTo(1));
        Relationship relationship = ((CompositeObject) r.get(0)).getDelegate();
        assertThat(relationship.getType().name(), equalTo("RELATION"));
        cdoManager.currentTransaction().commit();
    }

    @Test
    public void row() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        A a = cdoManager.create(A.class);
        Result<CompositeRowObject> row = cdoManager.createQuery("match (a:A) return a").execute();
        Map<String, Object> delegate = row.getSingleResult().getDelegate();
        assertThat(delegate, IsMapContaining.<String, Object>hasEntry("a", a));
        cdoManager.currentTransaction().commit();
    }
}
