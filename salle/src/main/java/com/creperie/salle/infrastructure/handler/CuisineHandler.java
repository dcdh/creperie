package com.creperie.salle.infrastructure.handler;

import com.damdamdeo.pulse.extension.consumer.runtime.Source;
import com.damdamdeo.pulse.extension.consumer.runtime.event.AsyncEventConsumerChannel;
import com.damdamdeo.pulse.extension.core.AggregateId;
import com.damdamdeo.pulse.extension.core.AggregateRootType;
import com.damdamdeo.pulse.extension.core.BelongsTo;
import com.damdamdeo.pulse.extension.core.consumer.CurrentVersionInConsumption;
import com.damdamdeo.pulse.extension.core.consumer.DecryptablePayload;
import com.damdamdeo.pulse.extension.core.consumer.FromApplication;
import com.damdamdeo.pulse.extension.core.consumer.Purpose;
import com.damdamdeo.pulse.extension.core.consumer.event.AggregateRootLoaded;
import com.damdamdeo.pulse.extension.core.consumer.event.AsyncEventChannelMessageHandler;
import com.damdamdeo.pulse.extension.core.encryption.EncryptedPayload;
import com.damdamdeo.pulse.extension.core.event.EventType;
import com.damdamdeo.pulse.extension.core.event.OwnedBy;
import com.damdamdeo.pulse.extension.core.executedby.ExecutedBy;
import com.damdamdeo.pulse.extension.livenotifier.runtime.Audience;
import com.damdamdeo.pulse.extension.livenotifier.runtime.LiveNotifierPublisher;
import com.fasterxml.jackson.databind.JsonNode;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.Instant;
import java.util.Objects;
import java.util.function.Supplier;

@ApplicationScoped
@AsyncEventConsumerChannel(
        purpose = "notification",
        sources = {
                @Source(functionalDomain = "Cuisine", componentName = "Production")})
public class CuisineHandler implements AsyncEventChannelMessageHandler<JsonNode> {

    @Inject
    LiveNotifierPublisher<CommandePretePourEtreServieDTO> liveNotifierPublisherProducer;

    @Schema(name = "CommandePretePourEtreServie", required = true, requiredProperties = {"numeroDeTable"})
    public record CommandePretePourEtreServieDTO(Integer numeroDeTable) {

        public CommandePretePourEtreServieDTO {
            Objects.requireNonNull(numeroDeTable);
        }
    }

    @Override
    public void handleMessage(final FromApplication fromApplication,
                              final Purpose purpose,
                              final AggregateRootType aggregateRootType,
                              final AggregateId aggregateId,
                              final CurrentVersionInConsumption currentVersionInConsumption,
                              final Instant creationDate,
                              final EventType eventType,
                              final EncryptedPayload encryptedPayload,
                              final OwnedBy ownedBy,
                              final BelongsTo belongsTo,
                              final ExecutedBy executedBy,
                              final DecryptablePayload<JsonNode> decryptableEventPayload,
                              final Supplier<AggregateRootLoaded<JsonNode>> aggregateRootLoadedSupplier) {
        Log.infov("Handling message for event type ''{0}''", eventType.type());
        if ("ProductionTerminee".equals(eventType.type())) {
            final Integer numeroDeTable = Integer.valueOf(aggregateId.id().split("/")[0]);
            liveNotifierPublisherProducer.publish(
                    "CommandePretePourEtreServie",
                    new CommandePretePourEtreServieDTO(numeroDeTable),
                    ownedBy, Audience.AllConnected.INSTANCE);
        }
    }
}
