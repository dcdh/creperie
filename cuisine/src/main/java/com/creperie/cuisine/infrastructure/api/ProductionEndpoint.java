package com.creperie.cuisine.infrastructure.api;

import com.creperie.cuisine.domain.Plat;
import com.creperie.cuisine.domain.PreparationIdentifier;
import com.creperie.cuisine.domain.Production;
import com.creperie.cuisine.domain.Status;
import com.creperie.cuisine.domain.command.MarkProductionTerminee;
import com.creperie.cuisine.domain.event.CommandeAProduire;
import com.creperie.cuisine.domain.event.ProductionTerminee;
import com.damdamdeo.pulse.extension.core.command.CommandHandler;
import com.damdamdeo.pulse.extension.core.event.Event;
import com.damdamdeo.pulse.extension.core.event.EventRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.media.DiscriminatorMapping;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

@Path("production")
public class ProductionEndpoint {

    @Inject
    CommandHandler<Production, PreparationIdentifier> productionCommandeCommandHandler;

    @Inject
    EventRepository<Production, PreparationIdentifier> eventRepository;

    @Schema(name = "Plat", required = true, requiredProperties = {"nom"})
    public record PlatDTO(String nom) {

        public static PlatDTO from(final Plat plat) {
            return new PlatDTO(plat.nom());
        }
    }

    @Schema(
            name = "Event",
            description = "Generic events",
            oneOf = {
                    CommandeAProduireDTO.class,
                    ProductionTermineeDTO.class
            },
            discriminatorProperty = "type",
            discriminatorMapping = {
                    @DiscriminatorMapping(value = "CommandeAProduire", schema = CommandeAProduireDTO.class),
                    @DiscriminatorMapping(value = "ProductionTerminee", schema = ProductionTermineeDTO.class)
            },
            requiredProperties = {"type"}
    )
    public interface EventDTO {
        String getType();
    }

    @Schema(name = "Event", required = true, requiredProperties = {"type", "plats"})
    public record CommandeAProduireDTO(List<PlatDTO> plats) implements EventDTO {
        @Override
        public String getType() {
            return "CommandeAProduire";
        }
    }

    @Schema(name = "Event", required = true, requiredProperties = {"nom", "type"})
    public record ProductionTermineeDTO() implements EventDTO {
        @Override
        public String getType() {
            return "ProductionTerminee";
        }
    }

    @Schema(name = "Response", required = true, requiredProperties = {"production", "events"})
    public record ResponseDTO(ProductionDTO production, List<EventDTO> events) {

    }

    @Schema(name = "Production", required = true, requiredProperties = {"id", "plats", "status"})
    public record ProductionDTO(String id, List<PlatDTO> plats, Status status) {

        public static ProductionDTO from(final Production production) {
            return new ProductionDTO(
                    production.id().id(),
                    production.plats().stream().map(PlatDTO::from).toList(),
                    production.status());
        }
    }

    private EventDTO from(final Event event) {
        return switch (event) {
            case CommandeAProduire e -> new CommandeAProduireDTO(e.plats().stream().map(PlatDTO::from).toList());
            case ProductionTerminee e -> new ProductionTermineeDTO();
            default -> throw new IllegalStateException("unhandled event: " + event);
        };
    }

    @Path("/markProductionTerminee")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public ResponseDTO markProductionTerminee(@FormParam("id") final String id) {
        final Production handled = productionCommandeCommandHandler.handle(
                new MarkProductionTerminee(new PreparationIdentifier(id)));
        return new ResponseDTO(
                ProductionDTO.from(handled),
                eventRepository.loadOrderByVersionASC(handled.id()).stream().map(this::from).toList());
    }
}
