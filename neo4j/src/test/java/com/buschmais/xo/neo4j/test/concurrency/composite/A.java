package com.buschmais.xo.neo4j.test.concurrency.composite;

import com.buschmais.xo.api.annotation.ImplementedBy;
import com.buschmais.xo.api.proxy.ProxyMethod;
import com.buschmais.xo.neo4j.api.annotation.Label;
import org.neo4j.graphdb.Node;

import java.util.concurrent.TimeUnit;

@Label("A")
public interface A {

    @ImplementedBy(IncrementAndGet.class)
    int incrementAndGet();

    public class IncrementAndGet implements ProxyMethod<Node> {

        @Override
        public Object invoke(Node node, Object instance, Object[] args) throws Exception {
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
