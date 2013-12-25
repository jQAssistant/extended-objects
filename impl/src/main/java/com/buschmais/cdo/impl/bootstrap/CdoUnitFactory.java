package com.buschmais.cdo.impl.bootstrap;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.TransactionAttribute;
import com.buschmais.cdo.api.ValidationMode;
import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.impl.reflection.ClassHelper;
import com.buschmais.cdo.schema.v1.*;
import com.buschmais.cdo.spi.bootstrap.CdoDatastoreProvider;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

public class CdoUnitFactory {

    private static final CdoUnitFactory instance = new CdoUnitFactory();

    private JAXBContext cdoContext;
    private Schema cdoXsd;

    private CdoUnitFactory() {
        try {
            cdoContext = JAXBContext.newInstance(ObjectFactory.class);
            SchemaFactory xsdFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            cdoXsd = xsdFactory.newSchema(new StreamSource(CdoUnitFactory.class.getResourceAsStream("/META-INF/xsd/cdo-1.0.xsd")));
        } catch (JAXBException e) {
            throw new CdoException("Cannot create JAXBContext for reading cdo.xml descriptors.", e);
        } catch (SAXException e) {
            throw new CdoException("Cannot create Schema for validation of cdo.xml descriptors.", e);
        }
    }

    public static CdoUnitFactory getInstance() {
        return instance;
    }

    public List<CdoUnit> getCdoUnits(URL url) throws IOException {
        Cdo cdo = readCdoDescriptor(cdoContext, url, cdoXsd);
        return getCdoUnits(cdo);
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
                if (validationHandler.isValid()) {
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

    private List<CdoUnit> getCdoUnits(Cdo cdo) {
        List<CdoUnit> cdoUnits = new LinkedList<>();
        for (CdoUnitType cdoUnitType : cdo.getCdoUnit()) {
            String name = cdoUnitType.getName();
            String description = cdoUnitType.getDescription();
            String urlName = cdoUnitType.getUrl();
            URI uri;
            try {
                uri = new URI(urlName);
            } catch (URISyntaxException e) {
                throw new CdoException("Cannot convert '" + urlName + "' to url.");
            }
            String providerName = cdoUnitType.getProvider();
            Class<? extends CdoDatastoreProvider> provider = ClassHelper.getType(providerName);
            Set<Class<?>> types = new HashSet<>();
            for (String typeName : cdoUnitType.getTypes().getType()) {
                types.add(ClassHelper.getType(typeName));
            }
            ValidationMode validationMode = getValidationMode(cdoUnitType.getValidationMode());
            TransactionAttribute defaultTransactionAttribute = getTransactionAttribute(cdoUnitType.getDefaultTransactionAttribute());
            Properties properties = new Properties();
            PropertiesType propertiesType = cdoUnitType.getProperties();
            if (propertiesType != null) {
                for (PropertyType propertyType : propertiesType.getProperty()) {
                    properties.setProperty(propertyType.getName(), propertyType.getValue());
                }
            }
            CdoUnit cdoUnit = new CdoUnit(name, description, uri, provider, types, validationMode, defaultTransactionAttribute, properties);
            cdoUnits.add(cdoUnit);
        }
        return cdoUnits;
    }

    private ValidationMode getValidationMode(ValidationModeType validationModeType) {
        if (validationModeType == null) return ValidationMode.AUTO;
        switch (validationModeType) {
            case NONE:
                return ValidationMode.NONE;
            case AUTO:
                return ValidationMode.AUTO;
            default:
                throw new CdoException("Unknown validation mode type " + validationModeType);
        }
    }

    private TransactionAttribute getTransactionAttribute(TransactionAttributeType defaultTransactionAttributeType) {
        if (defaultTransactionAttributeType == null) return TransactionAttribute.MANDATORY;
        switch (defaultTransactionAttributeType) {
            case MANDATORY:
                return TransactionAttribute.MANDATORY;
            case REQUIRES:
                return TransactionAttribute.REQUIRES;
            default:
                throw new CdoException("Unknown transaction attribute type " + defaultTransactionAttributeType);
        }
    }
}
