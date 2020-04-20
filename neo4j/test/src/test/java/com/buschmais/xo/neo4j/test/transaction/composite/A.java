package com.buschmais.xo.neo4j.test.transaction.composite;

import static com.buschmais.xo.api.annotation.ResultOf.Parameter;

import java.util.List;

import com.buschmais.xo.api.annotation.ImplementedBy;
import com.buschmais.xo.api.annotation.ResultOf;
import com.buschmais.xo.api.proxy.ProxyMethod;
import com.buschmais.xo.neo4j.api.annotation.Cypher;
import com.buschmais.xo.neo4j.api.annotation.Indexed;
import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.model.Neo4jNode;

@Label("A")
public interface A {

    @Indexed
    String getValue();

    void setValue(String value);

    List<B> getListOfB();

    @ImplementedBy(ThrowException.class)
    void throwException(String value) throws Exception;

    @ImplementedBy(ThrowRuntimeException.class)
    void throwRuntimeException(String value);

    @ResultOf
    ByValue getByValue(@Parameter("value") String value);

    @Cypher("match (a:A) where a.value=$value return a")
    interface ByValue {
        A getA();
    }

    class ThrowException implements ProxyMethod<Neo4jNode> {
        @Override
        public Object invoke(Neo4jNode node, Object instance, Object[] args) throws Exception {
            ((A) instance).setValue((String) args[0]);
            throw new Exception();
        }
    }

    class ThrowRuntimeException implements ProxyMethod<Neo4jNode> {
        @Override
        public Object invoke(Neo4jNode node, Object instance, Object[] args) {
            ((A) instance).setValue((String) args[0]);
            throw new RuntimeException();
        }
    }
}
