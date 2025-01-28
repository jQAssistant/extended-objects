package com.buschmais.xo.neo4j.test.repository;

import java.util.Collection;
import java.util.List;

import com.buschmais.xo.api.ResultIterable;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.api.Neo4jRepository;
import com.buschmais.xo.neo4j.api.TypedNeo4jRepository;
import com.buschmais.xo.neo4j.test.AbstractNeo4JXOManagerIT;
import com.buschmais.xo.neo4j.test.repository.composite.A;
import com.buschmais.xo.neo4j.test.repository.composite.CustomNeo4jRepository;
import com.buschmais.xo.neo4j.test.repository.composite.CustomRepository;
import com.buschmais.xo.neo4j.test.repository.composite.CustomTypedNeo4jRepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Provides tests for the Neo4j repository functionality.
 */
@RunWith(Parameterized.class)
public class RepositoryIT extends AbstractNeo4JXOManagerIT {

    public RepositoryIT(XOUnit xoUnit) {
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
        xoManager.currentTransaction()
            .begin();
        A a = xoManager.create(A.class);
        a.setName("A1");
        CustomRepository repository = xoManager.getRepository(CustomRepository.class);
        assertThat(repository.findByName("A1")).isEqualTo(a);
        assertThat(repository.find("A1")).isEqualTo(a);
        xoManager.currentTransaction()
            .commit();
    }

    /**
     * Test the {@link Neo4jRepository}.
     */
    @Test
    public void projection() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        A a = xoManager.create(A.class);
        a.setName("A1");
        CustomRepository repository = xoManager.getRepository(CustomRepository.class);
        CustomRepository.Projection projection = repository.projection("A1");
        verifyProjection(projection, a);
        ResultIterable<CustomRepository.Projection> iterableProjection = repository.iterableProjection("A1");
        verifyProjection(iterableProjection.getSingleResult(), a);
        List<CustomRepository.NestedProjection> nestedProjections = projection.getNestedProjections();
        assertThat(nestedProjections).hasSize(1);
        CustomRepository.NestedProjection nestedProjection = nestedProjections.get(0);
        verifyNestedProjection(nestedProjection, a);
        xoManager.currentTransaction()
            .commit();
    }

    private static void verifyProjection(CustomRepository.Projection projection, A a) {
        assertThat(projection).isNotNull();
        assertThat(projection.getA()).isEqualTo(a);
        CustomRepository.NestedProjection nestedProjection = projection.getNestedProjection();
        verifyNestedProjection(nestedProjection, a);
    }

    private static void verifyNestedProjection(CustomRepository.NestedProjection nestedProjection, A a) {
        assertThat(nestedProjection).isNotNull();
        assertThat(nestedProjection.getA()).isEqualTo(a);
        assertThat(nestedProjection.getName()).isEqualTo("A1");
    }

    /**
     * Test a custom repository which extends {@link Neo4jRepository}.
     */
    @Test
    public void customNeo4jRepository() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        A a = xoManager.create(A.class);
        a.setName("A1");
        CustomNeo4jRepository repository = xoManager.getRepository(CustomNeo4jRepository.class);
        assertThat(repository.find(A.class, "A1")
            .getSingleResult()).isEqualTo(a);
        assertThat(repository.findByName("A1")).isEqualTo(a);
        assertThat(repository.find("A1")).isEqualTo(a);
        xoManager.currentTransaction()
            .commit();
    }

    /**
     * Test a custom repository which extends {@link TypedNeo4jRepository}.
     */
    @Test
    public void customTypedNeo4jRepository() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        A a = xoManager.create(A.class);
        a.setName("A1");
        CustomTypedNeo4jRepository repository = xoManager.getRepository(CustomTypedNeo4jRepository.class);
        assertThat(repository.find("A1")
            .getSingleResult()).isEqualTo(a);
        assertThat(repository.findByName("A1")).isEqualTo(a);
        xoManager.currentTransaction()
            .commit();
    }

    /**
     * Test the {@link Neo4jRepository}.
     */
    @Test
    public void neo4jRepository() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        A a = xoManager.create(A.class);
        a.setName("A1");
        Neo4jRepository repository = xoManager.getRepository(Neo4jRepository.class);
        assertThat(repository.find(A.class, "A1")
            .getSingleResult()).isEqualTo(a);
        xoManager.currentTransaction()
            .commit();
    }
}
