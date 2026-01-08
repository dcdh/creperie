package com.creperie.gestion.domain;

import com.damdamdeo.pulse.extension.core.AggregateId;
import com.damdamdeo.pulse.extension.core.AggregateRootType;
import com.damdamdeo.pulse.extension.core.consumer.CurrentVersionInConsumption;
import com.damdamdeo.pulse.extension.core.consumer.FromApplication;
import com.damdamdeo.pulse.extension.core.encryption.EncryptedPayload;
import com.damdamdeo.pulse.extension.core.event.EventType;
import com.damdamdeo.pulse.extension.core.event.OwnedBy;

import java.time.Instant;
import java.util.Objects;

public record AuditEvent(FromApplication fromApplication,
                         AggregateRootType aggregateRootType,
                         AggregateId aggregateId,
                         CurrentVersionInConsumption currentVersionInConsumption,
                         Instant creationDate,
                         EventType eventType,
                         EncryptedPayload encryptedPayload,
                         OwnedBy ownedBy) {

    public AuditEvent {
        Objects.requireNonNull(fromApplication);
        Objects.requireNonNull(aggregateRootType);
        Objects.requireNonNull(aggregateId);
        Objects.requireNonNull(currentVersionInConsumption);
        Objects.requireNonNull(creationDate);
        Objects.requireNonNull(eventType);
        Objects.requireNonNull(encryptedPayload);
        Objects.requireNonNull(ownedBy);
    }
}
