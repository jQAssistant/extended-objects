package com.buschmais.cdo.impl.bootstrap;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.CdoManagerFactory;
import com.buschmais.cdo.api.bootstrap.CdoBootstrapService;
import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.impl.CdoManagerFactoryImpl;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class CdoBootstrapServiceImpl implements CdoBootstrapService {

    private final CdoUnitFactory cdoUnitFactory = CdoUnitFactory.getInstance();
    private final Map<String, CdoUnit> cdoUnits;

    public CdoBootstrapServiceImpl() {
        this.cdoUnits = readCdoDescriptors();
    }

    @Override
    public CdoManagerFactory createCdoManagerFactory(String name) {
        CdoUnit cdoUnit = cdoUnits.get(name);
        if (cdoUnit == null) {
            throw new CdoException("CDO unit with name '" + name + "' does not exist.");
        }
        return createCdoManagerFactory(cdoUnit);
    }

    @Override
    public CdoManagerFactory createCdoManagerFactory(CdoUnit cdoUnit) {
        return new CdoManagerFactoryImpl(cdoUnit);
    }

    private Map<String, CdoUnit> readCdoDescriptors() {
        Map<String, CdoUnit> result = new HashMap<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = CdoUnitFactory.class.getClassLoader();
        }
        try {
            Enumeration<URL> resources = classLoader.getResources("META-INF/cdo.xml");
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                for (CdoUnit cdoUnit : cdoUnitFactory.getCdoUnits(url)) {
                    CdoUnit existingCdoUnit = result.put(cdoUnit.getName(), cdoUnit);
                    if (existingCdoUnit != null) {
                        throw new CdoException("Found more than one CDO unit with name '" + cdoUnit.getName() + "'.");
                    }
                }
            }
        } catch (IOException e) {
            throw new CdoException("Cannot read cdo.xml descriptors.", e);
        }
        return result;
    }
}
