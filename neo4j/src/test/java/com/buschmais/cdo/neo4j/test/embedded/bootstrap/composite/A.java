package com.buschmais.cdo.neo4j.test.embedded.bootstrap.composite;

import com.buschmais.cdo.api.annotation.ImplementedBy;
import com.buschmais.cdo.api.proxy.ProxyMethod;
import com.buschmais.cdo.neo4j.api.annotation.Label;
import org.neo4j.graphdb.Node;

@Label("A")
public interface A {

    String getName();

    void setName(String name);

    @ImplementedBy(ToString.class)
    String toString();

    public class ToString implements ProxyMethod<Node> {

        @Override
        public Object invoke(Node node, Object instance, Object[] args) throws Exception {
            return "Test";
        }
    }
}
