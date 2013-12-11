package com.buschmais.cdo.impl.bootstrap;

import static com.buschmais.cdo.api.bootstrap.CdoUnit.TransactionAttribute.MANDATORY;
import static com.buschmais.cdo.api.bootstrap.CdoUnit.TransactionAttribute.REQUIRES;
import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.CdoManagerFactory;
import com.buschmais.cdo.schema.v1.*;
import com.buschmais.cdo.spi.bootstrap.CdoDatastoreProvider;
import com.buschmais.cdo.spi.bootstrap.CdoUnit;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.schema.v1.Cdo;
import com.buschmais.cdo.schema.v1.*;

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
        SchemaFactory xsdFactory;
        Schema cdoXsd;
        try {
            cdoContext = JAXBContext.newInstance(ObjectFactory.class);
            xsdFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            cdoXsd = xsdFactory.newSchema(new StreamSource(classLoader.getResourceAsStream("META-INF/xsd/cdo-1.0.xsd")));
        } catch (JAXBException e) {
            throw new CdoException("Cannot create JAXBContext for reading cdo.xml descriptors.", e);
        } catch (SAXException e) {
            throw new CdoException("Cannot create Schema for validation of cdo.xml descriptors.", e);
        }
        try {
            Enumeration<URL> resources = classLoader.getResources("META-INF/cdo.xml");
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                com.buschmais.cdo.schema.v1.Cdo cdo = readCdoDescriptor(cdoContext, url, cdoXsd);
                getCdoUnits(cdo);
            }
        } catch (IOException e) {
            throw new CdoException("Cannot read cdo.xml descriptors.", e);
        }
    }

    private com.buschmais.cdo.schema.v1.Cdo readCdoDescriptor(JAXBContext cdoContext, URL url, Schema cdoXsd)
            throws IOException {
        try (InputStream is = url.openStream()) {
            try {
                Unmarshaller unmarshaller = cdoContext.createUnmarshaller();
                CdoUnitValidationHandler validationHandler = new CdoUnitValidationHandler();
                unmarshaller.setSchema(cdoXsd);
                unmarshaller.setEventHandler(validationHandler);
                Cdo cdoXmlContent = unmarshaller.unmarshal(new StreamSource(is), Cdo.class).getValue();
                if (validationHandler.passesValidation()) {
                    return cdoXmlContent;
                } else {
                    throw new CdoException("Invalid cdo.xml descriptor detected: "
                            + validationHandler.getValidationMessages());
                }
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
            Class<? extends CdoDatastoreProvider> provider = ClassHelper.getType(providerName);
            Set<Class<?>> types = new HashSet<>();
            for (String typeName : cdoUnitType.getTypes().getType()) {
                types.add(ClassHelper.getType(typeName));
            }
            ValidationModeType validationModeType = cdoUnitType.getValidationMode();
            CdoManagerFactory.ValidationMode validationMode;
            if (validationModeType != null) {
                switch (validationModeType) {
                    case NONE:
                        validationMode = CdoManagerFactory.ValidationMode.NONE;
                        break;
                    case AUTO:
                        validationMode = CdoManagerFactory.ValidationMode.AUTO;
                        break;
                    default:
                        throw new CdoException("Unknown validation mode type " + validationModeType);
                }
            } else {
                validationMode = CdoManagerFactory.ValidationMode.AUTO;
            }
            TransactionAttributeType transactionAttributeType = cdoUnitType.getTransactionAttribute();
            TransactionAttribute transactionAttribute;
            if (transactionAttributeType != null) {
                switch (transactionAttributeType) {
                    case MANDATORY:
                        transactionAttribute = TransactionAttribute.MANDATORY;
                        break;
                    case REQUIRES:
                        transactionAttribute = TransactionAttribute.REQUIRES;
                        break;
                    default:
                        throw new CdoException("Unknown transaction attribute type " + transactionAttributeType);
                }
            } else {
                transactionAttribute = TransactionAttribute.MANDATORY;
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
