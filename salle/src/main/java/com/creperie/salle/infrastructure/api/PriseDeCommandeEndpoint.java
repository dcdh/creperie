package com.creperie.salle.infrastructure.api;

import com.creperie.salle.domain.*;
import com.creperie.salle.domain.command.AjouterPlat;
import com.creperie.salle.domain.command.CommencerLaPriseDeCommande;
import com.creperie.salle.domain.command.FinaliserLaCommande;
import com.creperie.salle.domain.event.CommandeEnCoursDePrise;
import com.creperie.salle.domain.event.CommandeFinalisee;
import com.creperie.salle.domain.event.PlatAjoute;
import com.creperie.salle.domain.usecase.AjouterPlatUseCase;
import com.creperie.salle.domain.usecase.CommencerLaPriseDeCommandeUseCase;
import com.creperie.salle.domain.usecase.FinaliserLaCommandeUseCase;
import com.damdamdeo.pulse.extension.core.event.EventRepository;
import com.damdamdeo.pulse.extension.core.event.ExecutedByEvent;
import com.damdamdeo.pulse.extension.core.usecase.UseCaseException;
import com.damdamdeo.pulse.extension.obfuscator.runtime.annotation.DeObfuscate;
import com.damdamdeo.pulse.extension.obfuscator.runtime.annotation.Obfuscate;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.DiscriminatorMapping;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Path("priseDeCommande")
public class PriseDeCommandeEndpoint {

    private final CommencerLaPriseDeCommandeUseCase commencerLaPriseDeCommandeUseCase;
    private final AjouterPlatUseCase ajouterPlatUseCase;
    private final FinaliserLaCommandeUseCase finaliserLaCommandeUseCase;
    private final EventRepository<Commande, CommandeIdentifier> eventRepository;

    public PriseDeCommandeEndpoint(final CommencerLaPriseDeCommandeUseCase commencerLaPriseDeCommandeUseCase,
                                   final AjouterPlatUseCase ajouterPlatUseCase,
                                   final FinaliserLaCommandeUseCase finaliserLaCommandeUseCase,
                                   final EventRepository<Commande, CommandeIdentifier> eventRepository) {
        this.commencerLaPriseDeCommandeUseCase = Objects.requireNonNull(commencerLaPriseDeCommandeUseCase);
        this.ajouterPlatUseCase = Objects.requireNonNull(ajouterPlatUseCase);
        this.finaliserLaCommandeUseCase = Objects.requireNonNull(finaliserLaCommandeUseCase);
        this.eventRepository = Objects.requireNonNull(eventRepository);
    }

    @RegisterForReflection(registerFullHierarchy = true)
    @Schema(name = "Plat", required = true, requiredProperties = {"nom"})
    public record PlatDTO(
            @Schema(type = SchemaType.STRING, implementation = String.class)
            Plat nom) {

        public static PlatDTO from(final Plat plat) {
            return new PlatDTO(plat);
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
    public record CommandeEnCoursDePriseDTO(
            @Schema(type = SchemaType.NUMBER, implementation = Integer.class)
            NombreDeConvives nombreDeConvives) implements EventDTO {

        @Override
        public String getType() {
            return "CommandeEnCoursDePrise";
        }
    }

    @RegisterForReflection(registerFullHierarchy = true)
    @Schema(name = "Event", required = true, requiredProperties = {"plat", "type"})
    public record PlatAjouteDTO(PlatDTO plat) implements EventDTO {

        @Override
        public String getType() {
            return "PlatAjoute";
        }
    }

    @RegisterForReflection(registerFullHierarchy = true)
    @Schema(name = "Event", required = true, requiredProperties = {"type"})
    public record CommandeFinaliseeDTO() implements EventDTO {

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
    public record CommandeDTO(@Schema(type = SchemaType.STRING, implementation = String.class)
                              @Obfuscate CommandeIdentifier commandeIdentifier,
                              @Schema(type = SchemaType.NUMBER, implementation = Integer.class)
                              NumeroDeTable numeroDeTable,
                              @Schema(type = SchemaType.NUMBER, implementation = Integer.class)
                              NombreDeConvives nombreDeConvives,
                              Instant datePriseDeCommande,
                              List<PlatDTO> plats,
                              Status status) {

        public static CommandeDTO from(final Commande commande) {
            return new CommandeDTO(
                    commande.id(),
                    commande.id().numeroDeTable(),
                    commande.nombreDeConvives(),
                    commande.datePriseDeCommande().date(),
                    commande.plats().stream().map(PlatDTO::from).toList(),
                    commande.status());
        }
    }

    private EventDTO from(final ExecutedByEvent<?> event) {
        return switch (event.event()) {
            case CommandeEnCoursDePrise e -> new CommandeEnCoursDePriseDTO(e.nombreDeConvives());
            case PlatAjoute e -> new PlatAjouteDTO(new PlatDTO(e.plat()));
            case CommandeFinalisee e -> new CommandeFinaliseeDTO();
            default -> throw new IllegalStateException("unhandled event: " + event);
        };
    }

    @Path("commencerLaPriseDeCommande")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public ResponseDTO commencerLaPriseDeCommande(
            @Schema(type = SchemaType.INTEGER, implementation = Integer.class, required = true)
            @FormParam("nombreDeConvives") final NombreDeConvives nombreDeConvives,
            @Schema(type = SchemaType.INTEGER, implementation = Integer.class, required = true)
            @FormParam("numeroDeTable") final NumeroDeTable numeroDeTable) throws UseCaseException {
        final Commande executed = commencerLaPriseDeCommandeUseCase.execute(new CommencerLaPriseDeCommande(nombreDeConvives, numeroDeTable, null));
        return new ResponseDTO(
                CommandeDTO.from(executed),
                eventRepository.loadOrderByVersionASC(executed.id()).stream().map(this::from).toList());
    }

    @Path("/{commandIdentifier}/ajouterPlat")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public ResponseDTO ajouterPlat(
            @Schema(type = SchemaType.STRING, implementation = String.class, required = true)
            @DeObfuscate @PathParam("commandIdentifier") final CommandeIdentifier commandeIdentifier,
            @Schema(type = SchemaType.STRING, implementation = String.class, required = true)
            @FormParam("nom") final Plat name) throws UseCaseException {
        final Commande executed = ajouterPlatUseCase.execute(new AjouterPlat(commandeIdentifier, name));
        return new ResponseDTO(
                CommandeDTO.from(executed),
                eventRepository.loadOrderByVersionASC(executed.id()).stream().map(this::from).toList());
    }

    @Path("/{commandIdentifier}/finaliserLaCommande")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseDTO finaliserLaCommande(
            @Schema(type = SchemaType.STRING, implementation = String.class, required = true)
            @DeObfuscate @PathParam("commandIdentifier") final CommandeIdentifier commandeIdentifier) throws UseCaseException {
        final Commande executed = finaliserLaCommandeUseCase.execute(new FinaliserLaCommande(commandeIdentifier));
        return new ResponseDTO(
                CommandeDTO.from(executed),
                eventRepository.loadOrderByVersionASC(executed.id()).stream().map(this::from).toList());
    }
}
