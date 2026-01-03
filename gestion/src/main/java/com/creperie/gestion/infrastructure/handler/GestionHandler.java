package com.creperie.gestion.infrastructure.handler;

import com.creperie.gestion.domain.*;
import com.creperie.gestion.infrastructure.api.AuditEventDTO;
import com.creperie.gestion.infrastructure.api.AuditEventDTOMapper;
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
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.lang3.Validate;

import java.time.Instant;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

@ApplicationScoped
@AsyncEventConsumerChannel(
        purpose = "aggregator",
        sources = {
                @Source(functionalDomain = "Salle", componentName = "priseDeCommande"),
                @Source(functionalDomain = "Cuisine", componentName = "Production")})
public class GestionHandler implements AsyncEventChannelMessageHandler<JsonNode> {

    @Inject
    LiveNotifierPublisher<AuditEventDTO> liveNotifierAuditEventDTOPublisherProducer;

    @Inject
    AuditEventRepository auditEventRepository;

    @Inject
    CommandeStatistiqueRepository commandeStatistiqueRepository;

    @Inject
    FrequentationStatistiqueRepository frequentationStatistiqueRepository;

    @Inject
    AuditEventDTOMapper auditEventDTOMapper;

    private void applyForCommandeStatistique(final DateDeService dateDeService, Consumer<CommandeStatistique> consumer) {
        final CommandeStatistique byDateDeService = commandeStatistiqueRepository.findByDateDeService(dateDeService)
                .orElse(new CommandeStatistique(dateDeService, 0, 0, 0, 0, new HashMap<>()));
        consumer.accept(byDateDeService);
        commandeStatistiqueRepository.persist(byDateDeService);
    }

    private void applyForFrequentationStatistique(final DateDeService dateDeService, Consumer<FrequentationStatistique> consumer) {
        final FrequentationStatistique byDateDeService = frequentationStatistiqueRepository.findByDateDeService(dateDeService)
                .orElse(new FrequentationStatistique(dateDeService, 0));
        consumer.accept(byDateDeService);
        frequentationStatistiqueRepository.persist(byDateDeService);
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
        if (decryptableEventPayload.isDecrypted()) {
            final AuditEvent auditEvent = new AuditEvent(
                    fromApplication, aggregateRootType, aggregateId, currentVersionInConsumption, creationDate,
                    eventType, encryptedPayload, ownedBy);
            auditEventRepository.store(auditEvent);
            final DateDeService dateDeService = DateDeService.from(creationDate);
            switch (fromApplication.functionalDomain()) {
                case "Salle":
                    switch (eventType.type()) {
                        case "CommandeEnCoursDePrise":
                            final int nombreDeConvives = decryptableEventPayload.payload().get("nombreDeConvives").get("nombre").asInt();
                            applyForCommandeStatistique(dateDeService, CommandeStatistique::ajouterNouvelleCommandeEnCoursDePrise);
                            applyForFrequentationStatistique(dateDeService, frequentationStatistique -> frequentationStatistique.ajouterConvives(nombreDeConvives));
                            break;
                        case "PlatAjoute":
                            final String plat = decryptableEventPayload.payload().get("plat").get("nom").asText();
                            applyForCommandeStatistique(dateDeService, commandeStatistique -> commandeStatistique.ajouterPlat(plat));
                            break;
                        case "CommandeFinalisee":
                            applyForCommandeStatistique(dateDeService, CommandeStatistique::nouvelleCommandeFinalisee);
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown eventType %s for functionalDomain %s"
                                    .formatted(eventType.type(), fromApplication.functionalDomain()));
                    }
                    break;
                case "Cuisine":
                    switch (eventType.type()) {
                        case "CommandeAProduire":
                            final JsonNode platsJsonNode = decryptableEventPayload.payload().get("plats");
                            Validate.validState(platsJsonNode.isArray());
                            platsJsonNode.elements().forEachRemaining(platJsonNode -> {
                                final String nom = platJsonNode.get("nom").asText();
                                applyForCommandeStatistique(dateDeService, commandeStatistique -> commandeStatistique.ajouterPlat(nom));
                            });
                            applyForCommandeStatistique(dateDeService, CommandeStatistique::nouvelleCommandeEnProduction);
                            break;
                        case "ProductionTerminee":
                            applyForCommandeStatistique(dateDeService, CommandeStatistique::nouvelleCommandeProduite);
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown eventType %s for functionalDomain %s"
                                    .formatted(eventType.type(), fromApplication.functionalDomain()));
                    }
                    break;
                default:
                    throw new IllegalStateException("Unknown functionalDomain %s".formatted(fromApplication.functionalDomain()));
            }
            liveNotifierAuditEventDTOPublisherProducer.publish(
                    "LiveEvents", auditEventDTOMapper.mapFrom(auditEvent), ownedBy, Audience.AllConnected.INSTANCE);
        }
    }
}
