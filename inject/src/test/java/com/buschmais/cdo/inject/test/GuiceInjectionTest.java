package com.buschmais.cdo.inject.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.CdoManagerFactory;
import com.buschmais.cdo.inject.CdoContext;
import com.buschmais.cdo.inject.GuiceModule;
import com.buschmais.cdo.inject.test.composite.A;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class GuiceInjectionTest {

	@CdoContext(unit = "guice")
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
		cdoManager.begin();
		A a = cdoManager.create(A.class);
		a.setValue("Google Guice");
		cdoManager.commit();
	}

	private void closeCdoManager() {
		if (cdoManager != null) {
			cdoManager.close();
			cdoManager = null;
		}
	}

	private void dropDatabase() {
		cdoManager.begin();
		cdoManager.createQuery("MATCH (n)-[r]-() DELETE r").execute();
		cdoManager.createQuery("MATCH (n) DELETE n").execute();
		cdoManager.commit();
	}
}
