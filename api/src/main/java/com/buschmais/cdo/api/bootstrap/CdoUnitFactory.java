package com.buschmais.cdo.api.bootstrap;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.schema.v1.Cdo;
import com.buschmais.cdo.schema.v1.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static com.buschmais.cdo.api.bootstrap.CdoUnit.TransactionAttribute.MANDATORY;
import static com.buschmais.cdo.api.bootstrap.CdoUnit.TransactionAttribute.REQUIRES;

public class CdoUnitFactory {

    private Map<String, CdoUnit> cdoUnits = new HashMap<>();

    public CdoUnitFactory() {
        readCdoDescriptors();
    }

    private void readCdoDescriptors() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = CdoUnitFactory.class.getClassLoader();
        }
        JAXBContext cdoContext;
        try {
            cdoContext = JAXBContext.newInstance(ObjectFactory.class);
        } catch (JAXBException e) {
            throw new CdoException("Cannot create JAXBContext for reading cdo.xml descriptors.", e);
        }
        try {
            Enumeration<URL> resources = classLoader.getResources("META-INF/cdo.xml");
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                com.buschmais.cdo.schema.v1.Cdo cdo = readCdoDescriptor(cdoContext, url);
                getCdoUnits(cdo);
            }
        } catch (IOException e) {
            throw new CdoException("Cannot read cdo.xml descriptors.", e);
        }
    }

    private com.buschmais.cdo.schema.v1.Cdo readCdoDescriptor(JAXBContext cdoContext, URL url) throws IOException {
        try (InputStream is = url.openStream()) {
            try {
                Unmarshaller unmarshaller = cdoContext.createUnmarshaller();
                return unmarshaller.unmarshal(new StreamSource(is), com.buschmais.cdo.schema.v1.Cdo.class).getValue();
            } catch (JAXBException e) {
                throw new CdoException("Cannot create JAXB unmarshaller for reading cdo.xml descriptors.");
            }
        }
    }

    private void getCdoUnits(Cdo cdo) {
        cdoUnits = new HashMap<>();
        for (CdoUnitType cdoUnitType : cdo.getCdoUnit()) {
            String name = cdoUnitType.getName();
            String description = cdoUnitType.getDescription();
            String urlName = cdoUnitType.getUrl();
            URL url = null;
            try {
                url = new URL(urlName);
            } catch (MalformedURLException e) {
                throw new CdoException("Cannot convert '" + urlName + "' to url.");
            }
            String providerName = cdoUnitType.getProvider();
            Class<? extends CdoProvider> provider = ClassHelper.getType(providerName);
            Set<Class<?>> types = new HashSet<>();
            for (String typeName : cdoUnitType.getTypes().getType()) {
                types.add(ClassHelper.getType(typeName));
            }
            ValidationModeType validationModeType = cdoUnitType.getValidationMode();
            CdoUnit.ValidationMode validationMode;
            if (validationModeType != null) {
                switch (validationModeType) {
                    case NONE:
                        validationMode = CdoUnit.ValidationMode.NONE;
                        break;
                    case AUTO:
                        validationMode = CdoUnit.ValidationMode.AUTO;
                        break;
                    default:
                        throw new CdoException("Unknown validation mode type " + validationModeType);
                }
            } else {
                validationMode = CdoUnit.ValidationMode.AUTO;
            }
            TransactionAttributeType transactionAttributeType = cdoUnitType.getTransactionAttribute();
            CdoUnit.TransactionAttribute transactionAttribute;
            if (transactionAttributeType != null) {
                switch (transactionAttributeType) {
                    case MANDATORY:
                        transactionAttribute = MANDATORY;
                        break;
                    case REQUIRES:
                        transactionAttribute = REQUIRES;
                        break;
                    default:
                        throw new CdoException("Unknown transaction attribute type " + transactionAttributeType);
                }
            }
            else {
                transactionAttribute = MANDATORY;
            }
            Properties properties = new Properties();
            PropertiesType propertiesType = cdoUnitType.getProperties();
            if (propertiesType != null) {
                for (PropertyType propertyType : propertiesType.getProperty()) {
                    properties.setProperty(propertyType.getName(), propertyType.getValue());
                }
            }
            CdoUnit cdoUnit = new CdoUnit(name, description, url, provider, types, validationMode, transactionAttribute, properties);
            cdoUnits.put(name, cdoUnit);
        }
    }

    public CdoUnit getCdoUnit(String name) {
        CdoUnit cdoUnit = cdoUnits.get(name);
        if (cdoUnit == null) {
            throw new CdoException("CDO unit with name '" + name + "' does not exist.");
        }
        return cdoUnit;
    }

    public Collection<CdoUnit> getCdoUnits() {
        return Collections.unmodifiableCollection(cdoUnits.values());
    }
}
