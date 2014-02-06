package com.buschmais.cdo.neo4j.test.embedded.query;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.neo4j.test.embedded.AbstractEmbeddedCdoManagerTest;
import com.buschmais.cdo.neo4j.test.embedded.query.composite.A2B;
import com.buschmais.cdo.neo4j.test.embedded.query.composite.A;
import com.buschmais.cdo.neo4j.test.embedded.query.composite.B;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.List;

import static com.buschmais.cdo.api.Query.Result;
import static com.buschmais.cdo.api.Query.Result.CompositeRowObject;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class PathTest extends AbstractEmbeddedCdoManagerTest {

    @Override
    protected Class<?>[] getTypes() {
        return new Class<?>[]{A.class, B.class, A2B.class};
    }

    @Test
    public void queryReturningPath() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        A a = cdoManager.create(A.class);
        B b = cdoManager.create(B.class);
        A2B a2b = cdoManager.create(a, A2B.class, b);
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        Result<CompositeRowObject> result = cdoManager.createQuery("match path=(a:A)-->(b:B) return path").execute();
        List<?> path = result.getSingleResult().get("path", List.class);
        assertThat(path.size(), equalTo(3));
        assertThat(path.get(0), Matchers.<Object>equalTo(a));
        assertThat(path.get(1), Matchers.<Object>equalTo(a2b));
        assertThat(path.get(2), Matchers.<Object>equalTo(b));
        cdoManager.currentTransaction().commit();
    }

}
