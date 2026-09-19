package com.creperie.cuisine.infrastructure.api;

import com.creperie.cuisine.domain.PreparationIdentifier;
import com.creperie.cuisine.domain.Production;
import com.creperie.cuisine.domain.Status;
import com.creperie.cuisine.domain.command.MarkProductionTerminee;
import com.creperie.cuisine.domain.event.CommandeAProduire;
import com.creperie.cuisine.domain.event.ProductionTerminee;
import com.creperie.cuisine.domain.usecase.MarkProductionTermineeUseCase;
import com.damdamdeo.pulse.extension.core.event.EventRepository;
import com.damdamdeo.pulse.extension.core.event.ExecutedByEvent;
import com.damdamdeo.pulse.extension.core.usecase.UseCaseException;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.DiscriminatorMapping;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

@Path("production")
public class ProductionEndpoint {

    private final MarkProductionTermineeUseCase markProductionTermineeUseCase;
    private final EventRepository<Production, PreparationIdentifier> eventRepository;

    public ProductionEndpoint(final MarkProductionTermineeUseCase markProductionTermineeUseCase,
                              final EventRepository<Production, PreparationIdentifier> eventRepository) {
        this.markProductionTermineeUseCase = markProductionTermineeUseCase;
        this.eventRepository = eventRepository;
    }

    @RegisterForReflection(registerFullHierarchy = true)
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

    @RegisterForReflection(registerFullHierarchy = true)
    @Schema(name = "Event", required = true, requiredProperties = {"type", "plats"})
    public record CommandeAProduireDTO(List<PlatDTO> plats) implements EventDTO {
        @Override
        public String getType() {
            return "CommandeAProduire";
        }
    }

    @RegisterForReflection(registerFullHierarchy = true)
    @Schema(name = "Event", required = true, requiredProperties = {"nom", "type"})
    public record ProductionTermineeDTO() implements EventDTO {
        @Override
        public String getType() {
            return "ProductionTerminee";
        }
    }

    @RegisterForReflection(registerFullHierarchy = true)
    @Schema(name = "Response", required = true, requiredProperties = {"production", "events"})
    public record ResponseDTO(ProductionDTO production, List<EventDTO> events) {

    }

    @RegisterForReflection(registerFullHierarchy = true)
    @Schema(name = "Production", required = true, requiredProperties = {"id", "plats", "status"})
    public record ProductionDTO(PreparationIdentifier id, List<PlatDTO> plats, Status status) {
        public static ProductionDTO from(final Production production) {
            return new ProductionDTO(
                    production.id(),
                    production.plats().stream().map(PlatDTO::from).toList(),
                    production.status());
        }
    }

    private EventDTO from(final ExecutedByEvent<?> event) {
        return switch (event.event()) {
            case CommandeAProduire e -> new CommandeAProduireDTO(e.plats().stream().map(PlatDTO::from).toList());
            case ProductionTerminee e -> new ProductionTermineeDTO();
            default -> throw new IllegalStateException("unhandled event: " + event);
        };
    }

    @Path("/markProductionTerminee")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public ResponseDTO markProductionTerminee(
            @Schema(type = SchemaType.STRING, implementation = String.class)
            @FormParam("id") final PreparationIdentifier id) throws UseCaseException {
        final Production handled = markProductionTermineeUseCase.execute(new MarkProductionTerminee(id));
        return new ResponseDTO(
                ProductionDTO.from(handled),
                eventRepository.loadOrderByVersionASC(handled.id()).stream().map(this::from).toList());
    }
}
