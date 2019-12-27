package com.buschmais.xo.neo4j.test.repository;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Collection;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.api.Neo4jRepository;
import com.buschmais.xo.neo4j.api.TypedNeo4jRepository;
import com.buschmais.xo.neo4j.test.AbstractNeo4jXOManagerTest;
import com.buschmais.xo.neo4j.test.repository.composite.A;
import com.buschmais.xo.neo4j.test.repository.composite.CustomNeo4jRepository;
import com.buschmais.xo.neo4j.test.repository.composite.CustomRepository;
import com.buschmais.xo.neo4j.test.repository.composite.CustomTypedNeo4jRepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Provides tests for the Neo4j repository functionality.
 */
@RunWith(Parameterized.class)
public class RepositoryTest extends AbstractNeo4jXOManagerTest {

    public RepositoryTest(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() {
        return xoUnits(A.class, CustomRepository.class, CustomNeo4jRepository.class, CustomTypedNeo4jRepository.class);
    }

    /**
     * Test a custom repository which does not extend any Neo4j repository.
     */
    @Test
    public void customRepository() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        A a = xoManager.create(A.class);
        a.setName("A1");
        CustomRepository repository = xoManager.getRepository(CustomRepository.class);
        assertThat(repository.findByName("A1"), equalTo(a));
        assertThat(repository.find("A1"), equalTo(a));
        xoManager.currentTransaction().commit();
    }

    /**
     * Test a custom repository which extends {@link Neo4jRepository}.
     */
    @Test
    public void customNeo4jRepository() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        A a = xoManager.create(A.class);
        a.setName("A1");
        CustomNeo4jRepository repository = xoManager.getRepository(CustomNeo4jRepository.class);
        assertThat(repository.find(A.class, "A1").getSingleResult(), equalTo(a));
        assertThat(repository.findByName("A1"), equalTo(a));
        assertThat(repository.find("A1"), equalTo(a));
        xoManager.currentTransaction().commit();
    }

    /**
     * Test a custom repository which extends {@link TypedNeo4jRepository}.
     */
    @Test
    public void customTypedNeo4jRepository() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        A a = xoManager.create(A.class);
        a.setName("A1");
        CustomTypedNeo4jRepository repository = xoManager.getRepository(CustomTypedNeo4jRepository.class);
        assertThat(repository.find("A1").getSingleResult(), equalTo(a));
        assertThat(repository.findByName("A1"), equalTo(a));
        xoManager.currentTransaction().commit();
    }

    /**
     * Test the {@link Neo4jRepository}.
     */
    @Test
    public void neo4jRepository() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        A a = xoManager.create(A.class);
        a.setName("A1");
        Neo4jRepository repository = xoManager.getRepository(Neo4jRepository.class);
        assertThat(repository.find(A.class, "A1").getSingleResult(), equalTo(a));
        xoManager.currentTransaction().commit();
    }
}
