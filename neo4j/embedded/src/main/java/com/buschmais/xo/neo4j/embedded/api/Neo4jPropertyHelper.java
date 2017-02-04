package com.buschmais.xo.neo4j.embedded.api;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Neo4jPropertyHelper {

    private static final Pattern NEO4J_PROPERTY_PATTERN = Pattern.compile("neo4j\\.(.*)");

    private Neo4jPropertyHelper() {
    }

    public static Properties getNeo4jProperties(Properties properties) {
        Properties neo4jProperties = new Properties();
        for (String propertyName : properties.stringPropertyNames()) {
            Matcher matcher = NEO4J_PROPERTY_PATTERN.matcher(propertyName);
            if (matcher.matches()) {
                String neo4jProperty = matcher.group(1);
                neo4jProperties.setProperty(neo4jProperty, properties.getProperty(propertyName));
            }
        }
        return neo4jProperties;
    }

}
