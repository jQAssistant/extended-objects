package com.buschmais.xo.neo4j.test.concurrency.composite;

import java.util.concurrent.TimeUnit;

import com.buschmais.xo.api.annotation.ImplementedBy;
import com.buschmais.xo.api.proxy.ProxyMethod;
import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.impl.model.EmbeddedNode;

@Label("A")
public interface A {

    @ImplementedBy(IncrementAndGet.class)
    int incrementAndGet();

    class IncrementAndGet implements ProxyMethod<EmbeddedNode> {

        @Override
        public Object invoke(EmbeddedNode node, Object instance, Object[] args) throws Exception {
            int value;
            if (!node.hasProperty("value")) {
                value = 0;
            } else {
                value = (int) node.getProperty("value");
            }
            TimeUnit.SECONDS.sleep(5);
            value++;
            node.setProperty("value", value);
            return value;
        }
    }

}
