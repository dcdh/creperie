package com.creperie.gestion.infrastructure.postgres;

import com.creperie.gestion.domain.AuditEvent;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class PostgresAuditEventRepositoryTest {

    @Inject
    PostgresAuditEventRepository postgresAuditEventRepository;

    @Test
    void shouldListEvents() {
        List<AuditEvent> auditEvents = postgresAuditEventRepository.listAllOrderByEnregistreA();

        assertThat(auditEvents).isEmpty();
    }
}
