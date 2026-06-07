package com.creperie.salle.infrastructure.api;

import com.creperie.salle.domain.*;
import com.creperie.salle.domain.command.AjouterPlat;
import com.creperie.salle.domain.command.CommencerLaPriseDeCommande;
import com.creperie.salle.domain.command.FinaliserLaCommande;
import com.creperie.salle.domain.event.CommandeEnCoursDePrise;
import com.creperie.salle.domain.event.CommandeFinalisee;
import com.creperie.salle.domain.event.PlatAjoute;
import com.damdamdeo.pulse.extension.core.BusinessException;
import com.damdamdeo.pulse.extension.core.command.CommandHandler;
import com.damdamdeo.pulse.extension.core.event.EventRepository;
import com.damdamdeo.pulse.extension.core.event.ExecutedByEvent;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.DiscriminatorMapping;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Path("priseDeCommande")
public class PriseDeCommandeEndpoint {

    private final CommandHandler<Commande, CommandeIdentifier> commandeCommandHandler;
    private final DatePriseDeCommandeProvider datePriseDeCommandeProvider;
    private final EventRepository<Commande, CommandeIdentifier> eventRepository;

    public PriseDeCommandeEndpoint(final CommandHandler<Commande, CommandeIdentifier> commandeCommandHandler,
                                   final DatePriseDeCommandeProvider datePriseDeCommandeProvider,
                                   final EventRepository<Commande, CommandeIdentifier> eventRepository) {
        this.commandeCommandHandler = Objects.requireNonNull(commandeCommandHandler);
        this.datePriseDeCommandeProvider = Objects.requireNonNull(datePriseDeCommandeProvider);
        this.eventRepository = Objects.requireNonNull(eventRepository);
    }

    private Map<NumeroDeTable, DatePriseDeCommande> datePriseDeCommandesParNumeroDeTable = new HashMap<>();

    @RegisterForReflection(registerFullHierarchy = true)
    @Schema(name = "Plat", required = true, requiredProperties = {"nom"})
    public record PlatDTO(String nom) {

        public static PlatDTO from(final Plat plat) {
            return new PlatDTO(plat.nom());
        }
    }

    @RegisterForReflection(registerFullHierarchy = true)
    @Schema(
            name = "Event",
            description = "Generic events",
            oneOf = {
                    CommandeEnCoursDePriseDTO.class,
                    PlatAjouteDTO.class,
                    CommandeFinaliseeDTO.class
            },
            discriminatorProperty = "type",
            discriminatorMapping = {
                    @DiscriminatorMapping(value = "CommandeEnCoursDePrise", schema = CommandeEnCoursDePriseDTO.class),
                    @DiscriminatorMapping(value = "PlatAjoute", schema = PlatAjouteDTO.class),
                    @DiscriminatorMapping(value = "CommandeFinalisee", schema = CommandeFinaliseeDTO.class)
            },
            requiredProperties = {"type"}
    )
    public interface EventDTO {
        String getType();
    }

    @RegisterForReflection(registerFullHierarchy = true)
    @Schema(name = "Event", required = true, requiredProperties = {"nombreDeConvives", "type"})
    public record CommandeEnCoursDePriseDTO(Integer nombreDeConvives) implements EventDTO {

        @JsonProperty("type")
        @Override
        public String getType() {
            return "CommandeEnCoursDePrise";
        }
    }

    @RegisterForReflection(registerFullHierarchy = true)
    @Schema(name = "Event", required = true, requiredProperties = {"plat", "type"})
    public record PlatAjouteDTO(PlatDTO plat) implements EventDTO {

        @JsonProperty("type")
        @Override
        public String getType() {
            return "PlatAjoute";
        }
    }

    @RegisterForReflection(registerFullHierarchy = true)
    @Schema(name = "Event", required = true, requiredProperties = {"type"})
    public record CommandeFinaliseeDTO() implements EventDTO {

        @JsonProperty("type")
        @Override
        public String getType() {
            return "CommandeFinalisee";
        }
    }

    @RegisterForReflection(registerFullHierarchy = true)
    @Schema(name = "Response", required = true, requiredProperties = {"commande", "events"})
    public record ResponseDTO(CommandeDTO commande, List<EventDTO> events) {

    }

    @RegisterForReflection(registerFullHierarchy = true)
    @Schema(name = "Commande", required = true, requiredProperties = {"commandeIdentifier", "numeroDeTable",
            "nombreDeConvives", "datePriseDeCommande", "plats", "status"})
    public record CommandeDTO(String commandeIdentifier,
                              Integer numeroDeTable,
                              Integer nombreDeConvives,
                              Instant datePriseDeCommande,
                              List<PlatDTO> plats,
                              Status status) {

        public static CommandeDTO from(final Commande commande) {
            return new CommandeDTO(
                    commande.id().id(),
                    commande.id().numeroDeTable().numero(),
                    commande.nombreDeConvives().nombre(),
                    commande.datePriseDeCommande().date(),
                    commande.plats().stream().map(PlatDTO::from).toList(),
                    commande.status());
        }
    }

    private EventDTO from(final ExecutedByEvent<?> event) {
        return switch (event.event()) {
            case CommandeEnCoursDePrise e -> new CommandeEnCoursDePriseDTO(e.nombreDeConvives().nombre());
            case PlatAjoute e -> new PlatAjouteDTO(new PlatDTO(e.plat().nom()));
            case CommandeFinalisee e -> new CommandeFinaliseeDTO();
            default -> throw new IllegalStateException("unhandled event: " + event);
        };
    }

    @Path("commencerLaPriseDeCommande")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public ResponseDTO commencerLaPriseDeCommande(
            @FormParam("nombreDeConvives") final Integer nombreDeConvives,
            @FormParam("numeroDeTable") final Integer numeroDeTable) throws BusinessException {
        final NumeroDeTable numero = new NumeroDeTable(numeroDeTable);
        final DatePriseDeCommande datePriseDeCommande = datePriseDeCommandeProvider.provide();
        datePriseDeCommandesParNumeroDeTable.put(numero, datePriseDeCommande);
        final Commande handled = commandeCommandHandler.handle(sequenceNumber -> new CommandeIdentifier(numero, sequenceNumber),
                new CommencerLaPriseDeCommande(new NombreDeConvives(nombreDeConvives),
                        numero, datePriseDeCommande), DuplicateCommandException::new);
        return new ResponseDTO(
                CommandeDTO.from(handled),
                eventRepository.loadOrderByVersionASC(handled.id()).stream().map(this::from).toList());
    }

    @Path("/{commandIdentifier}/ajouterPlat")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public ResponseDTO ajouterPlat(
            @Parameter(schema = @Schema(type = SchemaType.STRING, implementation = String.class, required = true))
            @PathParam("commandIdentifier") final CommandeIdentifier commandeIdentifier,
            @FormParam("nom") final String name) throws BusinessException {
        final Commande handled = commandeCommandHandler.handle(
                new AjouterPlat(commandeIdentifier, new Plat(name)));
        return new ResponseDTO(
                CommandeDTO.from(handled),
                eventRepository.loadOrderByVersionASC(handled.id()).stream().map(this::from).toList());
    }


    @Path("/{commandIdentifier}/finaliserLaCommande")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseDTO finaliserLaCommande(
            @Parameter(schema = @Schema(type = SchemaType.STRING, implementation = String.class, required = true))
            @PathParam("commandIdentifier") final CommandeIdentifier commandeIdentifier) throws BusinessException {
        final Commande handled = commandeCommandHandler.handle(new FinaliserLaCommande(commandeIdentifier));
        return new ResponseDTO(
                CommandeDTO.from(handled),
                eventRepository.loadOrderByVersionASC(handled.id()).stream().map(this::from).toList());
    }
}
