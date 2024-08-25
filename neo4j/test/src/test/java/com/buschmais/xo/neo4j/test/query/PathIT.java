package com.buschmais.xo.neo4j.test.query;

import java.util.Collection;
import java.util.List;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4JXOManagerIT;
import com.buschmais.xo.neo4j.test.query.composite.A;
import com.buschmais.xo.neo4j.test.query.composite.A2B;
import com.buschmais.xo.neo4j.test.query.composite.B;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static com.buschmais.xo.api.Query.Result;
import static com.buschmais.xo.api.Query.Result.CompositeRowObject;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class PathIT extends AbstractNeo4JXOManagerIT {

    public PathIT(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() {
        return xoUnits(A.class, B.class, A2B.class);
    }

    @Test
    public void queryReturningPath() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        A a = xoManager.create(A.class);
        B b = xoManager.create(B.class);
        A2B a2b = xoManager.create(a, A2B.class, b);
        xoManager.currentTransaction()
            .commit();
        xoManager.currentTransaction()
            .begin();
        Result<CompositeRowObject> result = xoManager.createQuery("match path=(a:A)-->(b:B) return path")
            .execute();
        List<?> path = result.getSingleResult()
            .get("path", List.class);
        assertThat(path).hasSize(3);
        assertThat(path.get(0)).isEqualTo(a);
        assertThat(path.get(1)).isEqualTo(a2b);
        assertThat(path.get(2)).isEqualTo(b);
        xoManager.currentTransaction()
            .commit();
    }

}
