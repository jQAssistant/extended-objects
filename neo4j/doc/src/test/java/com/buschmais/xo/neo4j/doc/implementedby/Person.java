package com.buschmais.xo.neo4j.doc.implementedby;

import com.buschmais.xo.api.annotation.ImplementedBy;
import com.buschmais.xo.api.proxy.ProxyMethod;
import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedNode;

// tag::Class[]
@Label
public interface Person {

    @ImplementedBy(SetNameMethod.class)
    String setName(String firstName, String lastName);

    class SetNameMethod implements ProxyMethod<EmbeddedNode> {

        @Override
        public Object invoke(EmbeddedNode node, Object instance, Object[] args) {
            String firstName = (String) args[0];
            String lastName = (String) args[1];
            String fullName = firstName + " " + lastName;
            node.setProperty("name", fullName);
            return fullName;
        }
    }
}
// end::Class[]
