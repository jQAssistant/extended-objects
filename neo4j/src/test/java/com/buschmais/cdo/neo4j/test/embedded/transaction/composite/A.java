package com.buschmais.cdo.neo4j.test.embedded.transaction.composite;

import com.buschmais.cdo.api.annotation.ImplementedBy;
import com.buschmais.cdo.neo4j.api.annotation.Label;
import com.buschmais.cdo.api.proxy.ProxyMethod;
import org.neo4j.graphdb.Node;

@Label("A")
public interface A {

    String getValue();

    void setValue(String value);

    @ImplementedBy(ThrowException.class)
    void throwException(String value) throws Exception;

    @ImplementedBy(ThrowRuntimeException.class)
    void throwRuntimeException(String value);

    class ThrowException implements ProxyMethod<Node> {
        @Override
        public Object invoke(Node node, Object instance, Object[] args) throws Exception {
            ((A) instance).setValue((String) args[0]);
            throw new Exception();
        }
    }

    class ThrowRuntimeException implements ProxyMethod<Node> {
        @Override
        public Object invoke(Node node, Object instance, Object[] args) {
            ((A) instance).setValue((String) args[0]);
            throw new RuntimeException();
        }
    }
}
