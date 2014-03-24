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

public class XOUnitFactory {

    private static final XOUnitFactory instance = new XOUnitFactory();

    private final JAXBContext xoContext;
    private final Schema xoXsd;

    private XOUnitFactory() {
        try {
            xoContext = JAXBContext.newInstance(ObjectFactory.class);
            SchemaFactory xsdFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            xoXsd = xsdFactory.newSchema(new StreamSource(XOUnitFactory.class.getResourceAsStream("/META-INF/xsd/xo-1.0.xsd")));
        } catch (JAXBException e) {
            throw new XOException("Cannot create JAXBContext for reading xo.xml descriptors.", e);
        } catch (SAXException e) {
            throw new XOException("Cannot create Schema for validation of xo.xml descriptors.", e);
        }
    }

    public static XOUnitFactory getInstance() {
        return instance;
    }

    public List<XOUnit> getXOUnits(URL url) throws IOException {
        Xo xo = readXODescriptor(url, xoXsd);
        return getXOUnits(xo);
    }

    private Xo readXODescriptor(URL url, Schema xoXsd)
            throws IOException {
        try (InputStream is = url.openStream()) {
            try {
                Unmarshaller unmarshaller = xoContext.createUnmarshaller();
                XOUnitValidationHandler validationHandler = new XOUnitValidationHandler();
                unmarshaller.setSchema(xoXsd);
                unmarshaller.setEventHandler(validationHandler);
                Xo xoXmlContent = unmarshaller.unmarshal(new StreamSource(is), Xo.class).getValue();
                if (validationHandler.isValid()) {
                    return xoXmlContent;
                } else {
                    throw new XOException("Invalid xo.xml descriptor detected: " + validationHandler.getValidationMessages());
                }
            } catch (JAXBException e) {
                throw new XOException("Cannot create JAXB unmarshaller for reading xo.xml descriptors.");
            }
        }
    }

    private List<XOUnit> getXOUnits(Xo xo) {
        List<XOUnit> xoUnits = new LinkedList<>();
        for (XOUnitType xoUnitType : xo.getXoUnit()) {
            String name = xoUnitType.getName();
            String description = xoUnitType.getDescription();
            String urlName = xoUnitType.getUrl();
            URI uri;
            try {
                uri = new URI(urlName);
            } catch (URISyntaxException e) {
                throw new XOException("Cannot convert '" + urlName + "' to url.");
            }
            String providerName = xoUnitType.getProvider();
            Class<? extends XODatastoreProvider> provider = ClassHelper.getType(providerName);
            Set<Class<?>> types = new HashSet<>();
            for (String typeName : xoUnitType.getTypes().getType()) {
                types.add(ClassHelper.getType(typeName));
            }
            List<Class<?>> instanceListeners = new ArrayList<>();
            InstanceListenersType instanceListenersType = xoUnitType.getInstanceListeners();
            if (instanceListenersType != null) {
                for (String instanceListenerName : instanceListenersType.getInstanceListener()) {
                    instanceListeners.add(ClassHelper.getType(instanceListenerName));
                }
            }
            ValidationMode validationMode = getValidationMode(xoUnitType.getValidationMode());
            ConcurrencyMode concurrencyMode = getConcurrencyMode(xoUnitType.getConcurrencyMode());
            Transaction.TransactionAttribute defaultTransactionAttribute = getTransactionAttribute(xoUnitType.getDefaultTransactionAttribute());
            Properties properties = new Properties();
            PropertiesType propertiesType = xoUnitType.getProperties();
            if (propertiesType != null) {
                for (PropertyType propertyType : propertiesType.getProperty()) {
                    properties.setProperty(propertyType.getName(), propertyType.getValue());
                }
            }
            XOUnit xoUnit = new XOUnit(name, description, uri, provider, types, instanceListeners, validationMode, concurrencyMode, defaultTransactionAttribute, properties);
            xoUnits.add(xoUnit);
        }
        return xoUnits;
    }

    private ConcurrencyMode getConcurrencyMode(ConcurrencyModeType concurrencyModeType) {
        if (concurrencyModeType == null) return ConcurrencyMode.SINGLETHREADED;
        switch (concurrencyModeType) {
            case SINGLETHREADED:
                return ConcurrencyMode.SINGLETHREADED;
            case MULTITHREADED:
                return ConcurrencyMode.MULTITHREADED;
            default:
                throw new XOException("Unknown concurrency mode type " + concurrencyModeType);
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
