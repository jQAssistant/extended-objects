package com.buschmais.xo.neo4j.test.delegate;

import com.buschmais.xo.api.CompositeObject;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.xo.neo4j.test.delegate.composite.A;
import com.buschmais.xo.neo4j.test.delegate.composite.A2B;
import com.buschmais.xo.neo4j.test.delegate.composite.B;
import org.hamcrest.collection.IsMapContaining;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.buschmais.xo.api.Query.Result;
import static com.buschmais.xo.api.Query.Result.CompositeRowObject;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class DelegateTest extends AbstractCdoManagerTest {

    public DelegateTest(XOUnit XOUnit) {
        super(XOUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCdoUnits() throws URISyntaxException {
        return cdoUnits(A.class, B.class, A2B.class);
    }

    @Test
    public void entity() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        Node node = ((CompositeObject) XOManager.create(A.class)).getDelegate();
        assertThat(node.hasLabel(DynamicLabel.label("A")), equalTo(true));
        XOManager.currentTransaction().commit();
    }

    @Test
    public void relation() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        A a = XOManager.create(A.class);
        B b = XOManager.create(B.class);
        XOManager.create(a, A2B.class, b);
        List<A2B> r = executeQuery("MATCH (a:A)-[r]->(b:B) RETURN r").getColumn("r");
        assertThat(r.size(), equalTo(1));
        Relationship relationship = ((CompositeObject) r.get(0)).getDelegate();
        assertThat(relationship.getType().name(), equalTo("RELATION"));
        XOManager.currentTransaction().commit();
    }

    @Test
    public void row() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        A a = XOManager.create(A.class);
        Result<CompositeRowObject> row = XOManager.createQuery("match (a:A) return a").execute();
        Map<String, Object> delegate = row.getSingleResult().getDelegate();
        assertThat(delegate, IsMapContaining.<String, Object>hasEntry("a", a));
        XOManager.currentTransaction().commit();
    }
}
