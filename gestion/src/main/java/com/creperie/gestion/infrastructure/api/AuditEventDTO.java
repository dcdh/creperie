package com.creperie.gestion.infrastructure.api;

import com.creperie.gestion.domain.AuditEvent;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Schema(name = "AuditEvent", required = true, requiredProperties = {"applicationName", "creationDate", "eventType", "message"})
public record AuditEventDTO(String applicationName,
                            ZonedDateTime creationDate,
                            String eventType,
                            String message) {

    public static AuditEventDTO from(final AuditEvent auditEvent, final String message) {
        return new AuditEventDTO(
                auditEvent.fromApplication().name(),
                auditEvent.storedAt().atZone(ZoneId.systemDefault()),
                auditEvent.eventType().type(),
                message);
    }
}
