package com.buschmais.xo.api.metadata.method;

import com.buschmais.xo.api.metadata.reflection.PropertyMethod;

public class TransientPropertyMethodMetadata extends AbstractPropertyMethodMetadata<Void> {

    public TransientPropertyMethodMetadata(PropertyMethod propertyMethod) {
        super(propertyMethod, null);
    }
}
