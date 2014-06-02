package com.buschmais.xo.spi.metadata.method;

import com.buschmais.xo.spi.reflection.PropertyMethod;

public class TransientPropertyMethodMetadata extends AbstractPropertyMethodMetadata<Void> {

    public TransientPropertyMethodMetadata(PropertyMethod propertyMethod) {
        super(propertyMethod, null);
    }
}
