package com.buschmais.xo.neo4j.test.delegate;

import java.util.*;

import com.buschmais.xo.api.CompositeObject;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.api.model.Neo4jLabel;
import com.buschmais.xo.neo4j.api.model.Neo4jNode;
import com.buschmais.xo.neo4j.api.model.Neo4jRelationship;
import com.buschmais.xo.neo4j.test.AbstractNeo4JXOManagerIT;
import com.buschmais.xo.neo4j.test.delegate.composite.A;
import com.buschmais.xo.neo4j.test.delegate.composite.A2B;
import com.buschmais.xo.neo4j.test.delegate.composite.B;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static com.buschmais.xo.api.Query.Result;
import static com.buschmais.xo.api.Query.Result.CompositeRowObject;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class DelegateIT extends AbstractNeo4JXOManagerIT {

    public DelegateIT(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() {
        return xoUnits(A.class, B.class, A2B.class);
    }

    @Test
    public void entity() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        Neo4jNode<Neo4jLabel, ?, ?, ?> node = ((CompositeObject) xoManager.create(A.class)).getDelegate();
        Iterable<Neo4jLabel> labels = node.getLabels();
        Set<String> labelsAsStrings = new HashSet<>();
        for (Neo4jLabel label : labels) {
            labelsAsStrings.add(label.getName());
        }
        assertThat(labelsAsStrings).hasSize(1)
            .contains("A");
        xoManager.currentTransaction()
            .commit();
    }

    @Test
    public void relation() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        A a = xoManager.create(A.class);
        B b = xoManager.create(B.class);
        xoManager.create(a, A2B.class, b);
        List<A2B> r = executeQuery("MATCH (a:A)-[r]->(b:B) RETURN r").getColumn("r");
        assertThat(r).hasSize(1);
        Neo4jRelationship relationship = ((CompositeObject) r.get(0)).getDelegate();
        assertThat(relationship.getType()
            .getName()).isEqualTo("RELATION");
        xoManager.currentTransaction()
            .commit();
    }

    @Test
    public void row() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        A a = xoManager.create(A.class);
        Result<CompositeRowObject> row = xoManager.createQuery("match (a:A) return a")
            .execute();
        Map<String, Object> delegate = row.getSingleResult()
            .getDelegate();
        assertThat(delegate).containsEntry("a", a);
        xoManager.currentTransaction()
            .commit();
    }
}
