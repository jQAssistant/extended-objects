package com.buschmais.xo.neo4j.spi;

import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

@Builder
@Getter
@ToString
public class Notification {

    Severity severity;

    String code;

    String title;

    String description;

    int offset;

    int line;

    int column;

    @Slf4j
    public enum Severity {

        WARNING,
        INFORMATION;

        private static final Map<String, Severity> VALUES;

        static {
            VALUES = stream(values()).collect(toMap(value -> value.name()
                .toUpperCase(), value -> value));
        }

        public static Severity from(String name) {
            if (name == null) {
                return WARNING;
            }
            Severity severity = VALUES.get(name.toUpperCase());
            if (severity != null) {
                return severity;
            }
            log.warn("Unknown notification severity '{}', falling back to '{}'.", name, WARNING);
            return WARNING;
        }
    }
}
