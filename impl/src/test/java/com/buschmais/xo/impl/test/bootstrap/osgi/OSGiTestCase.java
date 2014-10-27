package com.buschmais.xo.impl.test.bootstrap.osgi;

import org.ops4j.pax.exam.*;
import org.ops4j.pax.exam.options.UrlProvisionOption;
import org.ops4j.pax.exam.util.PathUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;

import javax.inject.Inject;

import java.util.concurrent.TimeUnit;

import static org.ops4j.pax.exam.CoreOptions.*;

public class OSGiTestCase implements ConfigurationFactory {

    @Inject
    protected BundleContext ctx;

    @Override
    public Option[] createConfiguration() {
        final Option[] xoBundles = options(cleanCaches(true), //
                systemProperty("logback.configurationFile").value("file:" + PathUtils.getBaseDir() + "/src/test/resources/logback.xml"), //
                workspaceBundle("api"), //
                workspaceBundle("spi"), //
                workspaceBundle("impl"), //
                mavenBundle("javax.validation", "validation-api", "1.1.0.Final"), //
                mavenBundle("org.apache.felix", "org.apache.felix.scr", "1.8.2"), //
                mavenBundle("org.apache.felix", "org.apache.felix.configadmin", "1.8.0").start(true), //
                mavenBundle("org.slf4j", "slf4j-api", "1.7.2"), //
                mavenBundle("ch.qos.logback", "logback-core", "1.0.6"), //
                mavenBundle("ch.qos.logback", "logback-classic", "1.0.6"), //
                mavenBundle("com.google.guava", "guava", "15.0"));
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

    protected static UrlProvisionOption workspaceBundle(String pathFromRoot) {
        String url = String.format("reference:file:%s/../%s/target/classes", PathUtils.getBaseDir(), pathFromRoot);
        return bundle(url);
    }
}
