package com.buschmais.xo.test.trace.test.bootstrap;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.XOManagerFactory;
import com.buschmais.xo.api.bootstrap.XO;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.json.api.JsonFileStoreProvider;
import com.buschmais.xo.test.trace.api.TraceDatastoreProvider;
import com.buschmais.xo.test.trace.test.bootstrap.composite.A;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Verifies the functionality of {@link com.buschmais.xo.test.trace.impl.TraceMonitorMXBean}.
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
    public void methodStatistics() throws URISyntaxException, IOException, JMException {
        Properties properties = new Properties();
        properties.setProperty("com.buschmais.xo.test.trace.api.DelegateProvider", JsonFileStoreProvider.class.getName());
        XOUnit xoUnit= XOUnit.builder().uri(new URI("file:target/json/store")).provider(TraceDatastoreProvider.class).types(ImmutableList.of(A.class)).properties(properties).build();
        XOManagerFactory xoManagerFactory = XO.createXOManagerFactory(xoUnit);
        assertThat(mbeanServer.getMBeanInfo(objectName), notNullValue());
        XOManager xoManager = xoManagerFactory.createXOManager();
        A a = xoManager.create(A.class);
        a.setName("Test");
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
