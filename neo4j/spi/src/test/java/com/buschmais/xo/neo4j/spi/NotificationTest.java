package com.buschmais.xo.neo4j.spi;

import com.buschmais.xo.neo4j.spi.Notification.Severity;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NotificationTest {

    @Test
    public void severity() {
        assertThat(Severity.from("information")).isEqualTo(Severity.INFORMATION);
        assertThat(Severity.from("INFORMATION")).isEqualTo(Severity.INFORMATION);
        assertThat(Severity.from("warning")).isEqualTo(Severity.WARNING);
        assertThat(Severity.from("WARNING")).isEqualTo(Severity.WARNING);
        assertThat(Severity.from("UNKNOWN")).isEqualTo(Severity.WARNING);
    }

}
