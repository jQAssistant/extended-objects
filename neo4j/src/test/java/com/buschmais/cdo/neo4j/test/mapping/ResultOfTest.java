package com.buschmais.cdo.neo4j.test.mapping;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.IterableQueryResult;
import com.buschmais.cdo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.cdo.neo4j.test.mapping.composite.*;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;

public class ResultOfTest extends AbstractCdoManagerTest {

    @Override
    protected Class<?>[] getTypes() {
        return new Class<?>[]{E.class, F.class};
    }

    @Test
    public void resultOf() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.begin();
        E e = cdoManager.create(E.class);
        F f1 = cdoManager.create(F.class);
        f1.setValue("F1");
        e.getRelatedTo().add(f1);
        F f2 = cdoManager.create(F.class);
        f2.setValue("F2");
        e.getRelatedTo().add(f2);
        cdoManager.commit();
        cdoManager.begin();
        IterableQueryResult<ByName> byName = e.getByName("F1");
        assertThat(byName.getSingleResult().getF(), equalTo(f1));
        cdoManager.commit();
    }

}
