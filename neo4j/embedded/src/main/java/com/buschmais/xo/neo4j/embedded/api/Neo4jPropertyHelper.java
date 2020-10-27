package com.buschmais.xo.neo4j.embedded.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class Neo4jPropertyHelper {

    private static final Pattern NEO4J_PROPERTY_PATTERN = Pattern.compile("neo4j\\.(.*)");

    private Neo4jPropertyHelper() {
    }

    static Map<String, String> getNeo4jProperties(Properties properties) {
        Map<String, String> neo4jProperties = new HashMap<>();
        for (String propertyName : properties.stringPropertyNames()) {
            Matcher matcher = NEO4J_PROPERTY_PATTERN.matcher(propertyName);
            if (matcher.matches()) {
                String neo4jProperty = matcher.group(1);
                neo4jProperties.put(neo4jProperty, properties.getProperty(propertyName));
            }
        }
        return neo4jProperties;
    }

}
