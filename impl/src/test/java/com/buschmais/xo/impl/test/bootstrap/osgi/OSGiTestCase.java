package com.buschmais.xo.impl.test.bootstrap.osgi;

import static org.ops4j.pax.exam.CoreOptions.bundle;
import static org.ops4j.pax.exam.CoreOptions.cleanCaches;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.ops4j.pax.exam.ConfigurationFactory;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.OptionUtils;
import org.ops4j.pax.exam.ProbeBuilder;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.util.PathUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;

public class OSGiTestCase implements ConfigurationFactory {

    @Inject
    protected BundleContext ctx;

    @Override
    public Option[] createConfiguration() {
        final Option[] xoBundles = options(cleanCaches(true), //
                systemProperty("logback.configurationFile").value("file:" + PathUtils.getBaseDir() + "/src/test/resources/logback.xml"), //
                mavenBundle("com.buschmais.xo", "xo.api").versionAsInProject(), //
                mavenBundle("com.buschmais.xo", "xo.spi").versionAsInProject(), //
                mavenBundle("javax.validation", "validation-api", "1.1.0.Final"), //
                mavenBundle("commons-lang", "commons-lang", "2.6"), //
                mavenBundle("org.osgi", "org.osgi.compendium", "4.3.1"), //
                mavenBundle("org.apache.felix", "org.apache.felix.configadmin", "1.4.0").start(true), //
                mavenBundle("org.apache.felix", "org.apache.felix.scr", "1.6.2"), //
                mavenBundle("org.slf4j", "slf4j-api", "1.7.2"), //
                mavenBundle("ch.qos.logback", "logback-core", "1.0.6"), //
                mavenBundle("ch.qos.logback", "logback-classic", "1.0.6"), //
                bundle("reference:file:target/classes"));
        return OptionUtils.combine(xoBundles, CoreOptions.junitBundles());
    }

    @ProbeBuilder
    public TestProbeBuilder probeConfiguration(final TestProbeBuilder probe) {
        probe.setHeader(Constants.EXPORT_PACKAGE, "com.buschmais.xo.impl.test.bootstrap.provider, com.buschmais.xo.impl.test.bootstrap.composite");
        probe.setHeader("Bundle-ManifestVersion", "2");
        return probe;
    }

    protected Bundle getBundle(final String bundleSymbolicName) {
        for (final Bundle bundle : ctx.getBundles()) {
            if (bundle.getSymbolicName() != null && bundle.getSymbolicName().equals(bundleSymbolicName)) {
                return bundle;
            }
        }
        return null;
    }

    protected Bundle startBundle(final String bundleSymbolicName) throws BundleException {
        final Bundle bundle = getBundle(bundleSymbolicName);
        bundle.start();
        return bundle;
    }

    protected static void delay() {
        try {
            TimeUnit.MILLISECONDS.sleep(300);
        } catch (final InterruptedException ie) {
            // dont care
        }
    }
}
