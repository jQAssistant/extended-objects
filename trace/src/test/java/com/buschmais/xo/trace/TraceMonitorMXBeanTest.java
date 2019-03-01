package com.buschmais.xo.trace;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.XOManagerFactory;
import com.buschmais.xo.api.bootstrap.XO;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.embedded.api.EmbeddedNeo4jXOProvider;
import com.buschmais.xo.trace.api.TraceDatastoreProvider;
import com.buschmais.xo.trace.composite.A;
import com.buschmais.xo.trace.impl.TraceMonitorMXBean;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import java.lang.management.ManagementFactory;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Verifies the functionality of {@link TraceMonitorMXBean}.
 */
public class TraceMonitorMXBeanTest {

    private MBeanServer mbeanServer;
    private ObjectName objectName;

    @Before
    public void init() throws JMException {
        mbeanServer = ManagementFactory.getPlatformMBeanServer();
        objectName = new ObjectName("com.buschmais.xo.trace", "xo-unit", "default");
    }

    @Test
    public void methodStatistics() throws URISyntaxException, JMException {
        Properties properties = new Properties();
        properties.setProperty("com.buschmais.xo.trace.api.DelegateProvider", EmbeddedNeo4jXOProvider.class.getName());
        XOUnit xoUnit = XOUnit.builder().uri(new URI("memory:///")).provider(TraceDatastoreProvider.class).types(ImmutableList.of(A.class)).properties(properties).build();
        XOManagerFactory xoManagerFactory = XO.createXOManagerFactory(xoUnit);
        assertThat(mbeanServer.getMBeanInfo(objectName), notNullValue());
        XOManager xoManager = xoManagerFactory.createXOManager();
        xoManager.currentTransaction().begin();
        A a = xoManager.create(A.class);
        a.setName("Test");
        xoManager.currentTransaction().commit();
        xoManager.close();

        CompositeData[] statistics = (CompositeData[]) mbeanServer.getAttribute(objectName, "MethodStatistics");
        assertThat(statistics, notNullValue());
        assertThat(statistics.length, greaterThan(0));
        CompositeData methodStatistic = statistics[0];
        assertThat((Long) methodStatistic.get("totalTime"), greaterThan(0l));
        assertThat((Long) methodStatistic.get("invocations"), greaterThan(0l));
        xoManagerFactory.close();
    }
}
