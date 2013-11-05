package com.buschmais.cdo.neo4j.test;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.CdoManagerFactory;
import com.buschmais.cdo.neo4j.impl.EmbeddedNeo4jCdoManagerFactoryImpl;
import com.buschmais.cdo.neo4j.test.composite.basic.A;
import com.buschmais.cdo.neo4j.test.composite.basic.B;
import com.buschmais.cdo.neo4j.test.composite.basic.C;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Set;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class EmbeddedNeo4jCdoManagerTest {

    private static CdoManagerFactory cdoManagerFactory;
    private CdoManager cdoManager;

    @BeforeClass
    public static void createNodeManagerFactory() throws MalformedURLException {
        cdoManagerFactory = new EmbeddedNeo4jCdoManagerFactoryImpl(new File("target/neo4j").toURI().toURL(), A.class, B.class, C.class);
    }

    @AfterClass
    public static void closeNodeManagerFactory() {
        cdoManagerFactory.close();
    }

    @Before
    public void before() {
        cdoManager = cdoManagerFactory.createCdoManager();
        cdoManager.begin();
        cdoManager.executeQuery("MATCH (n)-[r]-(d) DELETE r");
        cdoManager.executeQuery("MATCH (n) DELETE n");
        cdoManager.commit();
    }

    @Test
    public void composite() {
        cdoManager.begin();
        A a = cdoManager.create(A.class);
        B b = cdoManager.create(B.class);
        C c = cdoManager.create(C.class);
        cdoManager.commit();
    }

    @Test
    public void primitiveProperty() {
        cdoManager.begin();
        A a = cdoManager.create(A.class);
        a.setString("value");
        cdoManager.commit();
        cdoManager.begin();
        assertThat(a.getString(), equalTo("value"));
        a.setString("updatedValue");
        cdoManager.commit();
        cdoManager.begin();
        assertThat(a.getString(), equalTo("updatedValue"));
        a.setString(null);
        cdoManager.commit();
        cdoManager.begin();
        assertThat(a.getString(), equalTo(null));
        cdoManager.commit();
    }

    @Test
    public void referenceProperty() {
        cdoManager.begin();
        A a = cdoManager.create(A.class);
        B b1 = cdoManager.create(B.class);
        B b2 = cdoManager.create(B.class);
        a.setB(b1);
        cdoManager.commit();
        cdoManager.begin();
        assertThat(a.getB(), equalTo(b1));
        a.setB(b2);
        cdoManager.commit();
        cdoManager.begin();
        assertThat(a.getB(), equalTo(b2));
        a.setB(null);
        cdoManager.commit();
        cdoManager.begin();
        assertThat(a.getB(), equalTo(null));
        cdoManager.commit();
    }

    @Test
    public void collectionProperty() {
        cdoManager.begin();
        A a = cdoManager.create(A.class);
        B b = cdoManager.create(B.class);
        Set<B> setOfB = a.getSetOfB();
        assertThat(setOfB.add(b), equalTo(true));
        assertThat(setOfB.add(b), equalTo(false));
        assertThat(setOfB.size(), equalTo(1));
        cdoManager.commit();
        cdoManager.begin();
        assertThat(setOfB.remove(b), equalTo(true));
        assertThat(setOfB.remove(b), equalTo(false));
        cdoManager.commit();
    }

    @Test
    public void indexedProperty() {
        cdoManager.begin();
        A a1 = cdoManager.create(A.class);
        a1.setIndex("1");
        A a2 = cdoManager.create(A.class);
        a2.setIndex("2");
        cdoManager.commit();
        cdoManager.begin();
        assertThat(cdoManager.find(A.class, "1").iterator().next(), equalTo(a1));
        assertThat(cdoManager.find(A.class, "2").iterator().next(), equalTo(a2));
        assertThat(cdoManager.find(A.class, "3").iterator().hasNext(), equalTo(false));
        cdoManager.commit();
    }

    @Test
    public void anonymousSuperclass() {
        cdoManager.begin();
        C c = cdoManager.create(C.class);
        c.setIndex("1");
        c.setVersion(1);
        cdoManager.commit();
        cdoManager.begin();
        A a = cdoManager.find(A.class, "1").iterator().next();
        assertThat(c, equalTo(a));
        assertThat(a.getVersion(), equalTo(1L));
        cdoManager.commit();
    }
}
