package com.buschmais.xo.impl.bootstrap;

import com.buschmais.xo.api.ConcurrencyMode;
import com.buschmais.xo.api.Transaction;
import com.buschmais.xo.api.ValidationMode;
import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.impl.reflection.ClassHelper;
import com.buschmais.xo.impl.schema.v1.*;
import com.buschmais.xo.spi.bootstrap.XODatastoreProvider;
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

    private final JAXBContext cdoContext;
    private final Schema cdoXsd;

    private CdoUnitFactory() {
        try {
            cdoContext = JAXBContext.newInstance(ObjectFactory.class);
            SchemaFactory xsdFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            cdoXsd = xsdFactory.newSchema(new StreamSource(CdoUnitFactory.class.getResourceAsStream("/META-INF/xsd/xo-1.0.xsd")));
        } catch (JAXBException e) {
            throw new XOException("Cannot create JAXBContext for reading cdo.xml descriptors.", e);
        } catch (SAXException e) {
            throw new XOException("Cannot create Schema for validation of cdo.xml descriptors.", e);
        }
    }

    public static CdoUnitFactory getInstance() {
        return instance;
    }

    public List<XOUnit> getCdoUnits(URL url) throws IOException {
        Xo cdo = readCdoDescriptor(cdoContext, url, cdoXsd);
        return getCdoUnits(cdo);
    }

    private Xo readCdoDescriptor(JAXBContext cdoContext, URL url, Schema cdoXsd)
            throws IOException {
        try (InputStream is = url.openStream()) {
            try {
                Unmarshaller unmarshaller = cdoContext.createUnmarshaller();
                CdoUnitValidationHandler validationHandler = new CdoUnitValidationHandler();
                unmarshaller.setSchema(cdoXsd);
                unmarshaller.setEventHandler(validationHandler);
                Xo cdoXmlContent = unmarshaller.unmarshal(new StreamSource(is), Xo.class).getValue();
                if (validationHandler.isValid()) {
                    return cdoXmlContent;
                } else {
                    throw new XOException("Invalid cdo.xml descriptor detected: "
                            + validationHandler.getValidationMessages());
                }
            } catch (JAXBException e) {
                throw new XOException("Cannot create JAXB unmarshaller for reading cdo.xml descriptors.");
            }
        }
    }

    private List<XOUnit> getCdoUnits(Xo cdo) {
        List<XOUnit> XOUnits = new LinkedList<>();
        for (XoUnitType cdoUnitType : cdo.getXoUnit()) {
            String name = cdoUnitType.getName();
            String description = cdoUnitType.getDescription();
            String urlName = cdoUnitType.getUrl();
            URI uri;
            try {
                uri = new URI(urlName);
            } catch (URISyntaxException e) {
                throw new XOException("Cannot convert '" + urlName + "' to url.");
            }
            String providerName = cdoUnitType.getProvider();
            Class<? extends XODatastoreProvider> provider = ClassHelper.getType(providerName);
            Set<Class<?>> types = new HashSet<>();
            for (String typeName : cdoUnitType.getTypes().getType()) {
                types.add(ClassHelper.getType(typeName));
            }
            List<Class<?>> instanceListeners = new ArrayList<>();
            InstanceListenersType instanceListenersType = cdoUnitType.getInstanceListeners();
            if (instanceListenersType != null) {
                for (String instanceListenerName : instanceListenersType.getInstanceListener()) {
                    instanceListeners.add(ClassHelper.getType(instanceListenerName));
                }
            }
            ValidationMode validationMode = getValidationMode(cdoUnitType.getValidationMode());
            ConcurrencyMode concurrencyMode = getConcurrencyMode(cdoUnitType.getConcurrencyMode());
            Transaction.TransactionAttribute defaultTransactionAttribute = getTransactionAttribute(cdoUnitType.getDefaultTransactionAttribute());
            Properties properties = new Properties();
            PropertiesType propertiesType = cdoUnitType.getProperties();
            if (propertiesType != null) {
                for (PropertyType propertyType : propertiesType.getProperty()) {
                    properties.setProperty(propertyType.getName(), propertyType.getValue());
                }
            }
            XOUnit XOUnit = new XOUnit(name, description, uri, provider, types, instanceListeners, validationMode, concurrencyMode, defaultTransactionAttribute, properties);
            XOUnits.add(XOUnit);
        }
        return XOUnits;
    }

    private ConcurrencyMode getConcurrencyMode(ConcurrencyModeType concurrencyModeType) {
        if (concurrencyModeType == null) return ConcurrencyMode.SINGLETHREADED;
        switch (concurrencyModeType) {
            case SINGLETHREADED:
                return ConcurrencyMode.SINGLETHREADED;
            case MULTITHREADED:
                return ConcurrencyMode.MULTITHREADED;
            default:
                throw new XOException("Unknown conucrrency mode type " + concurrencyModeType);
        }
    }

    private ValidationMode getValidationMode(ValidationModeType validationModeType) {
        if (validationModeType == null) return ValidationMode.AUTO;
        switch (validationModeType) {
            case NONE:
                return ValidationMode.NONE;
            case AUTO:
                return ValidationMode.AUTO;
            default:
                throw new XOException("Unknown validation mode type " + validationModeType);
        }
    }

    private Transaction.TransactionAttribute getTransactionAttribute(TransactionAttributeType defaultTransactionAttributeType) {
        if (defaultTransactionAttributeType == null) return Transaction.TransactionAttribute.MANDATORY;
        switch (defaultTransactionAttributeType) {
            case MANDATORY:
                return Transaction.TransactionAttribute.MANDATORY;
            case REQUIRES:
                return Transaction.TransactionAttribute.REQUIRES;
            default:
                throw new XOException("Unknown transaction attribute type " + defaultTransactionAttributeType);
        }
    }
}
