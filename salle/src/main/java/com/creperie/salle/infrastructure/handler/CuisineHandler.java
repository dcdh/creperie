package com.creperie.salle.infrastructure.handler;

import com.creperie.salle.infrastructure.api.NotifyEvent;
import com.damdamdeo.pulse.extension.consumer.runtime.EventChannel;
import com.damdamdeo.pulse.extension.core.AggregateId;
import com.damdamdeo.pulse.extension.core.AggregateRootType;
import com.damdamdeo.pulse.extension.core.consumer.*;
import com.damdamdeo.pulse.extension.core.encryption.EncryptedPayload;
import com.damdamdeo.pulse.extension.core.event.EventType;
import com.damdamdeo.pulse.extension.core.event.OwnedBy;
import com.fasterxml.jackson.databind.JsonNode;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.Instant;
import java.util.Objects;
import java.util.function.Supplier;

@ApplicationScoped
@EventChannel(
        target = "notification",
        sources = {
                @EventChannel.Source(functionalDomain = "Cuisine", componentName = "Production")})
public class CuisineHandler implements AsyncEventChannelMessageHandler<JsonNode> {

    @Inject
    Event<NotifyEvent> notifyEventProducer;

    @Schema(name = "CommandePretePourEtreServie", required = true, requiredProperties = {"numeroDeTable"})
    public record CommandePretePourEtreServieDTO(Integer numeroDeTable) {

        public CommandePretePourEtreServieDTO {
            Objects.requireNonNull(numeroDeTable);
        }
    }

    @Override
    public void handleMessage(final FromApplication fromApplication,
                              final Target target,
                              final AggregateRootType aggregateRootType,
                              final AggregateId aggregateId,
                              final CurrentVersionInConsumption currentVersionInConsumption,
                              final Instant creationDate,
                              final EventType eventType,
                              final EncryptedPayload encryptedPayload,
                              final OwnedBy ownedBy,
                              final DecryptablePayload<JsonNode> decryptableEventPayload,
                              final Supplier<AggregateRootLoaded<JsonNode>> aggregateRootLoadedSupplier) {
        Log.infov("Handling message for event type ''{0}''", eventType.type());
        if ("ProductionTerminee".equals(eventType.type())) {
            final Integer numeroDeTable = Integer.valueOf(aggregateId.id().split("/")[0]);
            notifyEventProducer.fireAsync(new NotifyEvent("CommandePretePourEtreServie",
                            CommandePretePourEtreServieDTO.class,
                            new CommandePretePourEtreServieDTO(numeroDeTable)))
                    .whenComplete((result, throwable) -> {
                        if (throwable != null) {
                            Log.warnv("Error firing ''{0}'': {1}", eventType.type(), throwable.getMessage());
                        } else {
                            Log.infov("Broadcast firing for event ''{0}''", eventType.type());
                        }
                    });
        }
    }
}
