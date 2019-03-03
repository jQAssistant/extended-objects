package com.buschmais.xo.impl.bootstrap;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.XOManagerFactory;
import com.buschmais.xo.api.bootstrap.XOBootstrapService;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.impl.XOManagerFactoryImpl;

import org.osgi.service.component.annotations.Component;

@Component
public class XOBootstrapServiceImpl implements XOBootstrapService {

    private final XOUnitFactory xoUnitFactory;
    private final Map<String, XOUnit> xoUnits;

    public XOBootstrapServiceImpl() {
        xoUnitFactory = XOUnitFactory.getInstance();
        this.xoUnits = readXODescriptors();
    }

    @Override
    public XOManagerFactory createXOManagerFactory(String name) {
        XOUnit xoUnit = xoUnits.get(name);
        if (xoUnit == null) {
            throw new XOException("XO unit with name '" + name + "' does not exist.");
        }
        return createXOManagerFactory(xoUnit);
    }

    @Override
    public XOManagerFactory createXOManagerFactory(XOUnit xoUnit) {
        return new XOManagerFactoryImpl(xoUnit);
    }

    private Map<String, XOUnit> readXODescriptors() {
        Map<String, XOUnit> result = new HashMap<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = XOUnitFactory.class.getClassLoader();
        }
        try {
            Enumeration<URL> resources = classLoader.getResources(XO_DESCRIPTOR_RESOURCE);
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                for (XOUnit xoUnit : xoUnitFactory.getXOUnits(url)) {
                    XOUnit existingXOUnit = result.put(xoUnit.getName(), xoUnit);
                    if (existingXOUnit != null) {
                        throw new XOException("Found more than one XO unit with name '" + xoUnit.getName() + "'.");
                    }
                }
            }
        } catch (IOException e) {
            throw new XOException("Cannot read xo.xml descriptors.", e);
        }
        return result;
    }
}
