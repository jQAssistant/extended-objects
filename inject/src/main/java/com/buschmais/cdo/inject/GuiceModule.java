package com.buschmais.cdo.inject;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

public class GuiceModule extends AbstractModule {

	@Override
	protected void configure() {
		bindListener(Matchers.any(), new CdoManagerTypeListener());
	}

}
