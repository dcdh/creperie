package com.creperie.salle.infrastructure.handler;

import com.creperie.salle.infrastructure.api.SseBroadcaster;
import com.damdamdeo.pulse.extension.consumer.runtime.EventChannel;
import com.damdamdeo.pulse.extension.core.AggregateId;
import com.damdamdeo.pulse.extension.core.AggregateRootType;
import com.damdamdeo.pulse.extension.core.consumer.*;
import com.damdamdeo.pulse.extension.core.encryption.EncryptedPayload;
import com.damdamdeo.pulse.extension.core.event.EventType;
import com.damdamdeo.pulse.extension.core.event.OwnedBy;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.util.Objects;
import java.util.function.Supplier;

@ApplicationScoped
@EventChannel(
        target = "notification",
        sources = {
                @EventChannel.Source(functionalDomain = "Cuisine", componentName = "Production")})
public class CuisineHandler implements AsyncEventChannelMessageHandler<JsonNode> {

    public static final String EVENT_NAME = "cuisine";

    private final SseBroadcaster sseBroadcaster;

    public CuisineHandler(final SseBroadcaster sseBroadcaster) {
        this.sseBroadcaster = sseBroadcaster;
    }

    public record CommandePretePourEtreServieDTO(Integer numeroDeTable) {

        public CommandePretePourEtreServieDTO {
            Objects.requireNonNull(numeroDeTable);
        }
    }

    @Override
    public void handleMessage(final Target target,
                              final AggregateRootType aggregateRootType,
                              final AggregateId aggregateId,
                              final CurrentVersionInConsumption currentVersionInConsumption,
                              final Instant creationDate,
                              final EventType eventType,
                              final EncryptedPayload encryptedPayload,
                              final OwnedBy ownedBy,
                              final DecryptablePayload<JsonNode> decryptableEventPayload,
                              final Supplier<AggregateRootLoaded<JsonNode>> aggregateRootLoadedSupplier) {
        final Integer numeroDeTable = Integer.valueOf(aggregateId.id().split("/")[0]);
        sseBroadcaster.broadcast(EVENT_NAME, CommandePretePourEtreServieDTO.class,
                new CommandePretePourEtreServieDTO(numeroDeTable));
    }
}
