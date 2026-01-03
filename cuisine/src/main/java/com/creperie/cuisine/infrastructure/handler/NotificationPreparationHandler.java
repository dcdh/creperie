package com.creperie.cuisine.infrastructure.handler;

import com.creperie.cuisine.domain.Plat;
import com.creperie.cuisine.domain.PreparationIdentifier;
import com.creperie.cuisine.domain.Production;
import com.creperie.cuisine.domain.command.ProduireCommande;
import com.creperie.cuisine.infrastructure.api.ProductionEndpoint;
import com.damdamdeo.pulse.extension.consumer.runtime.Source;
import com.damdamdeo.pulse.extension.consumer.runtime.event.AsyncEventConsumerChannel;
import com.damdamdeo.pulse.extension.core.AggregateId;
import com.damdamdeo.pulse.extension.core.AggregateRootType;
import com.damdamdeo.pulse.extension.core.BelongsTo;
import com.damdamdeo.pulse.extension.core.command.CommandHandler;
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
import org.apache.commons.lang3.Validate;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@ApplicationScoped
@AsyncEventConsumerChannel(
        purpose = "notificationPreparation",
        sources = {
                @Source(functionalDomain = "Salle", componentName = "priseDeCommande")})
public class NotificationPreparationHandler implements AsyncEventChannelMessageHandler<JsonNode> {

    private LocalDateTime derniereCommandeRecu;

    private static final String PREMIERE_COMMANDE_DU_SERVICE = "PremiereCommandeDuService";

    private static final String COMMANDE_A_PRODUIRE = "CommandeAProduire";

    @Inject
    CommandHandler<Production, PreparationIdentifier> productionCommandeCommandHandler;

    @Inject
    LiveNotifierPublisher<MessageDTO> liveNotifierMessageDTOPublisher;

    @Inject
    LiveNotifierPublisher<CommandeAProduireDTO> liveNotifierCommandeAProduireDTOPublisher;

    @Schema(name = "CommandeAProduire", required = true, requiredProperties = {"id", "plats"})
    public record CommandeAProduireDTO(String id, List<ProductionEndpoint.PlatDTO> plats) {
    }

    @Schema(name = "Message", required = true, requiredProperties = {"message"})
    public record MessageDTO(String message) {
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
        if ("CommandeEnCoursDePrise".equals(eventType.type())
                && (derniereCommandeRecu == null || derniereCommandeRecu.toLocalDate().isBefore(LocalDate.now()))) {
            Log.infov("Should notify event ''{0}''", eventType.type());
            liveNotifierMessageDTOPublisher.publish(PREMIERE_COMMANDE_DU_SERVICE, new MessageDTO("Démarre la crêpière la premiére commande va arriver !"),
                    ownedBy, Audience.AllConnected.INSTANCE);
            derniereCommandeRecu = LocalDateTime.now();
        } else if ("CommandeFinalisee".equals(eventType.type())) {
            AggregateRootLoaded<JsonNode> jsonNodeAggregateRootLoaded = aggregateRootLoadedSupplier.get();
            DecryptablePayload<JsonNode> jsonNodeDecryptablePayload = jsonNodeAggregateRootLoaded.decryptableAggregateRootPayload();
            if (jsonNodeDecryptablePayload.isDecrypted()) {
                Log.debug(jsonNodeDecryptablePayload.payload());
                // jsonNodeDecryptablePayload.payload().get("nombreDeConvives").get("nombre").asInt();
                // jsonNodeDecryptablePayload.payload().get("status").asText();
                JsonNode jsonNode = jsonNodeDecryptablePayload.payload().get("plats");
                Validate.validState(jsonNode.isArray());
                final PreparationIdentifier preparationIdentifier = new PreparationIdentifier(aggregateId.id());
                final List<Plat> plats = new ArrayList<>();
                jsonNode.elements().forEachRemaining(node -> {
                    plats.add(new Plat(node.get("nom").asText()));
                });

                productionCommandeCommandHandler.handle(new ProduireCommande(preparationIdentifier, plats));
                Log.infov("Should notify event ''{0}''", eventType.type());
                liveNotifierCommandeAProduireDTOPublisher.publish(
                        COMMANDE_A_PRODUIRE, new CommandeAProduireDTO(
                                preparationIdentifier.id(),
                                plats.stream().map(ProductionEndpoint.PlatDTO::from).toList()), ownedBy,
                        Audience.AllConnected.INSTANCE);
            }
        }
    }
}
