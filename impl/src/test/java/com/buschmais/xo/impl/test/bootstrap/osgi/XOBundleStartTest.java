package com.buschmais.xo.impl.test.bootstrap.osgi;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.inject.Inject;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.framework.Bundle;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ManagedServiceFactory;

import com.buschmais.xo.api.XOManagerFactory;
import com.buschmais.xo.api.bootstrap.XOUnitParameter;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class XOBundleStartTest extends OSGiTestCase {

    @Inject
    @Filter("(service.pid=com.buschmais.xo.factory)")
    private ManagedServiceFactory serviceFactory;

    @Inject
    @Filter("(name=testUnit)")
    private XOManagerFactory xoManagerFactory;

    @Inject
    private ConfigurationAdmin configAdmin;

    @Test
    public void checkXOUnitConfiguration() throws IOException {
        assertThat(configAdmin, is(notNullValue()));
        final Configuration config = configAdmin.createFactoryConfiguration("com.buschmais.xo.factory", null);
        assertThat(config, is(notNullValue()));
        assertThat(config.getFactoryPid(), is("com.buschmais.xo.factory"));

        final Dictionary<String, Object> properties = new Hashtable<>();
        properties.put(XOUnitParameter.NAME.getKey(), "cmUnit");
        properties.put(XOUnitParameter.URL.getKey(), "file:target/osgi");
        final Collection<String> types = new ArrayList<>();
        types.add("com.buschmais.xo.impl.test.bootstrap.composite.A");
        properties.put(XOUnitParameter.TYPES.getKey(), types);
        properties.put(XOUnitParameter.PROVIDER.getKey(), "com.buschmais.xo.impl.test.bootstrap.provider.TestXOProvider");

        config.update(properties);

        delay();

        try {
            final ServiceReference[] services = ctx.getServiceReferences(XOManagerFactory.class.getName(), "(name=cmUnit)");
            assertThat(services.length, is(1));
            for (final ServiceReference ref : services) {
                final Object service = ctx.getService(ref);
                assertThat(service, is((instanceOf(XOManagerFactory.class))));
            }
        } catch (final InvalidSyntaxException e) {
            fail(e.toString());
        }
    }

    @Test
    public void bundleStarted() {
        final Bundle bundle = getBundle("com.buschmais.xo.impl");
        assertThat(bundle.getState(), is(equalTo(Bundle.ACTIVE)));
    }

    @Test
    public void checkXOManagerFactoryServiceFactory() {
        assertThat(serviceFactory, is(notNullValue()));
    }

    @Test
    public void checkXOManagerFactory() {
        assertThat(xoManagerFactory, is(notNullValue()));
    }

}
