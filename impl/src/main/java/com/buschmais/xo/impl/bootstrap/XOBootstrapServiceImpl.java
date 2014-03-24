package com.buschmais.xo.impl.bootstrap;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.XOManagerFactory;
import com.buschmais.xo.api.bootstrap.XOBootstrapService;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.impl.XOManagerFactoryImpl;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class XOBootstrapServiceImpl implements XOBootstrapService {

    private final CdoUnitFactory cdoUnitFactory = CdoUnitFactory.getInstance();
    private final Map<String, XOUnit> cdoUnits;

    public XOBootstrapServiceImpl() {
        this.cdoUnits = readCdoDescriptors();
    }

    @Override
    public XOManagerFactory createXOManagerFactory(String name) {
        XOUnit XOUnit = cdoUnits.get(name);
        if (XOUnit == null) {
            throw new XOException("CDO unit with name '" + name + "' does not exist.");
        }
        return createXOManagerFactory(XOUnit);
    }

    @Override
    public XOManagerFactory createXOManagerFactory(XOUnit XOUnit) {
        return new XOManagerFactoryImpl(XOUnit);
    }

    private Map<String, XOUnit> readCdoDescriptors() {
        Map<String, XOUnit> result = new HashMap<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = CdoUnitFactory.class.getClassLoader();
        }
        try {
            Enumeration<URL> resources = classLoader.getResources(XO_DESCRIPTOR_RESOURCE);
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                for (XOUnit XOUnit : cdoUnitFactory.getCdoUnits(url)) {
                    XOUnit existingXOUnit = result.put(XOUnit.getName(), XOUnit);
                    if (existingXOUnit != null) {
                        throw new XOException("Found more than one CDO unit with name '" + XOUnit.getName() + "'.");
                    }
                }
            }
        } catch (IOException e) {
            throw new XOException("Cannot read cdo.xml descriptors.", e);
        }
        return result;
    }
}
