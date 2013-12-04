package com.buschmais.cdo.inject;

import java.lang.reflect.Field;
import java.net.MalformedURLException;

import com.buschmais.cdo.api.bootstrap.Cdo;
import com.google.inject.MembersInjector;

public class CdoManagerFactoryInjector<T> implements MembersInjector<T> {

	private String cdoUnitName;

	private Field field;

	public CdoManagerFactoryInjector(Field field, String cdoUnitName)
			throws MalformedURLException {
		this.field = field;
		this.cdoUnitName = cdoUnitName;
		field.setAccessible(true);
	}

	public void injectMembers(T instance) {
		try {
			field.set(instance, Cdo.createCdoManagerFactory(cdoUnitName));
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

}
