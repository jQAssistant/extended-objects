package com.buschmais.xo.neo4j.test.repository;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4jXOManagerTest;
import com.buschmais.xo.neo4j.test.repository.composite.A;
import com.buschmais.xo.neo4j.test.repository.composite.CustomRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URISyntaxException;
import java.util.Collection;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class RepositoryTest extends AbstractNeo4jXOManagerTest {

    public RepositoryTest(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() throws URISyntaxException {
        return xoUnits(A.class, CustomRepository.class);
    }

    @Test
    public void typedQuery() {
        XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        A a = xoManager.create(A.class);
        a.setName("A1");
        CustomRepository customRepository = xoManager.getRepository(CustomRepository.class);
        assertThat(customRepository.findByName("A1"), equalTo(a));
        assertThat(customRepository.find("A1"), equalTo(a));
        xoManager.currentTransaction().commit();
    }


}
