package com.buschmais.cdo.neo4j.api.annotation;

import com.buschmais.cdo.neo4j.impl.proxy.method.ProxyMethod;

public @interface InvokeUsing {

    Class<? extends ProxyMethod> value();

}
