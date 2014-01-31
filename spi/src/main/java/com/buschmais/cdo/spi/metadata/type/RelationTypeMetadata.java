package com.buschmais.cdo.spi.metadata.type;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.spi.metadata.method.MethodMetadata;
import com.buschmais.cdo.spi.reflection.AnnotatedType;

import java.util.Collection;
import java.util.Collections;

public class RelationTypeMetadata<DatastoreMetadata> extends AbstractDatastoreTypeMetadata<DatastoreMetadata> {

    public enum Direction {
        INCOMING,
        OUTGOING;

        public CdoException createNotSupportedException() {
            return new CdoException("Relation direction '" + name() + "' is not supported.");
        }
    }

    private Class<?> outgoingType;
    private Class<?> incomingType;

    public RelationTypeMetadata(AnnotatedType annotatedType, Collection<TypeMetadata> superTypes, Collection<MethodMetadata<?, ?>> properties, Class<?> outgoingType, Class<?> incomingType, DatastoreMetadata datastoreMetadata) {
        super(annotatedType, superTypes, properties, datastoreMetadata);
        this.outgoingType = outgoingType;
        this.incomingType = incomingType;
    }

    public RelationTypeMetadata(DatastoreMetadata datastoreMetadata) {
        this(null, Collections.<TypeMetadata>emptyList(), Collections.<MethodMetadata<?, ?>>emptyList(), null, null, datastoreMetadata);
    }

    public Class<?> getOutgoingType() {
        return outgoingType;
    }

    public Class<?> getIncomingType() {
        return incomingType;
    }

}
