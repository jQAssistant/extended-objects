package com.buschmais.cdo.neo4j.test.embedded.mapping;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.neo4j.test.embedded.AbstractEmbeddedCdoManagerTest;
import com.buschmais.cdo.neo4j.test.embedded.mapping.composite.E;
import com.buschmais.cdo.neo4j.test.embedded.mapping.composite.E2F;
import com.buschmais.cdo.neo4j.test.embedded.mapping.composite.F;
import org.junit.Before;
import org.junit.Test;

import static com.buschmais.cdo.api.Query.Result;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;

public class RelationResultOfTest extends AbstractEmbeddedCdoManagerTest {

    private E e;
    private F f1;
    private F f2;

    private E2F e2f1;
    private E2F e2f2;

    @Override
    protected Class<?>[] getTypes() {
        return new Class<?>[]{E.class, F.class, E2F.class};
    }

    @Before
    public void createData() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        e = cdoManager.create(E.class);
        f1 = cdoManager.create(F.class);
        e2f1 = cdoManager.create(e, E2F.class, f1);
        e2f1.setValue("E2F1");
        f2 = cdoManager.create(F.class);
        e2f2 = cdoManager.create(e, E2F.class, f2);
        e2f2.setValue("E2F2");
        cdoManager.currentTransaction().commit();
    }

    @Test
    public void resultUsingExplicitQuery() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        Result<E2F.ByValue> byValue = e2f1.getResultByValueUsingExplicitQuery("E2F1");
        assertThat(byValue.getSingleResult().getF(), equalTo(f1));
        cdoManager.currentTransaction().commit();
    }

    @Test
    public void resultUsingReturnType() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        Result<E2F.ByValue> byValue = e2f1.getResultByValueUsingReturnType("E2F1");
        assertThat(byValue.getSingleResult().getF(), equalTo(f1));
        cdoManager.currentTransaction().commit();
    }

    @Test
    public void byValueUsingExplicitQuery() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        E2F.ByValue byValue = e2f1.getByValueUsingExplicitQuery("E2F1");
        assertThat(byValue.getF(), equalTo(f1));
        cdoManager.currentTransaction().commit();
    }

    @Test
    public void byValueUsingReturnType() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        E2F.ByValue byValue = e2f1.getByValueUsingReturnType("E2F1");
        assertThat(byValue.getF(), equalTo(f1));
        byValue = e2f1.getByValueUsingReturnType("unknownE2F");
        assertThat(byValue, equalTo(null));
        cdoManager.currentTransaction().commit();
    }

    @Test
    public void byValueUsingImplicitThis() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        E2F.ByValueUsingImplicitThis byValue = e2f1.getByValueUsingImplicitThis("E2F1");
        assertThat(byValue.getF(), equalTo(f1));
        cdoManager.currentTransaction().commit();
    }

    @Test
    public void resultUsingCypher() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        Result<F> result = e2f1.getResultUsingCypher("E2F1");
        assertThat(result, hasItems(equalTo(f1)));
        result = e2f1.getResultUsingCypher("unknownF");
        assertThat(result.iterator().hasNext(), equalTo(false));
        cdoManager.currentTransaction().commit();
    }

    @Test
    public void singleResultUsingCypher() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        F result = e2f1.getSingleResultUsingCypher("E2F1");
        assertThat(result, equalTo(f1));
        result = e2f1.getSingleResultUsingCypher("unknownF");
        assertThat(result, equalTo(null));
        cdoManager.currentTransaction().commit();
    }

}
