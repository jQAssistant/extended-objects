package com.buschmais.cdo.neo4j.test.composite.generics;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.CdoManagerFactory;
import com.buschmais.cdo.neo4j.impl.EmbeddedNeo4jCdoManagerFactoryImpl;
import com.buschmais.cdo.neo4j.test.composite.AbstractCdoManagerTest;
import com.buschmais.cdo.neo4j.test.composite.basic.A;
import com.buschmais.cdo.neo4j.test.composite.basic.B;
import com.buschmais.cdo.neo4j.test.composite.basic.C;
import com.buschmais.cdo.neo4j.test.composite.generics.BoundType;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Set;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class GenericTypeTest extends AbstractCdoManagerTest {


    private CdoManager cdoManager;

    @Override
    protected Class<?>[] getTypes() {
        return new Class<?>[]{GenericSuperType.class, BoundType.class};
    }

    @Before
    public void before() {
        cdoManager = getCdoManagerFactory().createCdoManager();
        cdoManager.begin();
        cdoManager.executeQuery("MATCH (n)-[r]-(d) DELETE r");
        cdoManager.executeQuery("MATCH (n) DELETE n");
        cdoManager.commit();
    }

    @Test
    public void composite() {
        cdoManager.begin();
        BoundType b = cdoManager.create(BoundType.class);
        b.setValue("value");
        cdoManager.commit();
    }

}
