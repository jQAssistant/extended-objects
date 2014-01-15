package com.buschmais.cdo.inject.test;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.CdoManagerFactory;
import com.buschmais.cdo.inject.GuiceModule;
import com.buschmais.cdo.inject.test.composite.A;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;

public class GuiceInjectionTest {

    @Inject
    private CdoManagerFactory cdoManagerFactory;

	private CdoManager cdoManager;

	@Before
	public void setUp() {
		Injector injector = Guice.createInjector(new GuiceModule());
		injector.injectMembers(this);
		cdoManager = cdoManagerFactory.createCdoManager();
		dropDatabase();
	}

	@After
	public void tearDown() {
		closeCdoManager();
		cdoManagerFactory.close();
	}

	@Test
	public void testInjectedCdoManager() {
		assertThat(cdoManagerFactory, notNullValue());
		cdoManager.currentTransaction().begin();
		A a = cdoManager.create(A.class);
		a.setValue("Google Guice");
		cdoManager.currentTransaction().commit();

        cdoManager.currentTransaction().begin();
        assertEquals("Google Guice", a.getValue());
        cdoManager.currentTransaction().commit();
	}

	private void closeCdoManager() {
		if (cdoManager != null) {
			cdoManager.close();
			cdoManager = null;
		}
	}

	private void dropDatabase() {
		cdoManager.currentTransaction().begin();
		cdoManager.createQuery("MATCH (n)-[r]-() DELETE r").execute();
		cdoManager.createQuery("MATCH (n) DELETE n").execute();
		cdoManager.currentTransaction().commit();
	}
}
