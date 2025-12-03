package com.creperie.salle.infrastructure.api;

import com.creperie.salle.domain.*;
import com.creperie.salle.domain.command.AjouterPlat;
import com.creperie.salle.domain.command.CommencerLaPriseDeCommande;
import com.creperie.salle.domain.command.FinaliserLaCommande;
import com.creperie.salle.domain.event.CommandeEnCoursDePrise;
import com.creperie.salle.domain.event.CommandeFinalisee;
import com.creperie.salle.domain.event.PlatAjoute;
import com.damdamdeo.pulse.extension.core.command.CommandHandler;
import com.damdamdeo.pulse.extension.core.event.Event;
import com.damdamdeo.pulse.extension.core.event.EventRepository;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.media.DiscriminatorMapping;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.*;

@Path("prise_de_commande")
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

    @Schema(name = "Event", required = true, requiredProperties = {"nombreDeConvives", "type"})
    public record CommandeEnCoursDePriseDTO(Integer nombreDeConvives) implements EventDTO {
        @Override
        public String getType() {
            return "CommandeEnCoursDePrise";
        }
    }

    @Schema(name = "Event", required = true, requiredProperties = {"nom", "type"})
    public record PlatAjouteDTO(String nom) implements EventDTO {
        @Override
        public String getType() {
            return "PlatAjoute";
        }
    }

    @Schema(name = "Event", required = true, requiredProperties = {"type"})
    public record CommandeFinaliseeDTO() implements EventDTO {
        @Override
        public String getType() {
            return "CommandeFinalisee";
        }
    }

    @Schema(name = "Response", required = true, requiredProperties = {"commande", "events"})
    public record ResponseDTO(CommandeDTO commande, List<EventDTO> events) {

    }

    @Schema(name = "Commande", required = true, requiredProperties = {"numeroDeTable", "datePriseDeCommande", "nombreDeConvives", "plats", "status"})
    public record CommandeDTO(Integer numeroDeTable, Long datePriseDeCommande, Integer nombreDeConvives, List<PlatDTO> plats, Status status) {

        public static CommandeDTO from(final Commande commande) {
            return new CommandeDTO(
                    commande.id().numeroDeTable().numero(),
                    commande.id().datePriseDeCommande().toEpochMilli(),
                    commande.nombreDeConvives().nombre(),
                    commande.plats().stream().map(PlatDTO::from).toList(),
                    commande.status());
        }
    }

    private EventDTO from(final Event event) {
        return switch (event) {
            case CommandeEnCoursDePrise e -> new CommandeEnCoursDePriseDTO(e.nombreDeConvives());
            case PlatAjoute e -> new PlatAjouteDTO(e.nom());
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
            @FormParam("numeroDeTable") final Integer numeroDeTable) {
        final NumeroDeTable numero = new NumeroDeTable(numeroDeTable);
        final DatePriseDeCommande datePriseDeCommande = datePriseDeCommandeProvider.provide();
        datePriseDeCommandesParNumeroDeTable.put(numero, datePriseDeCommande);
        final Commande handled = commandeCommandHandler.handle(
                new CommencerLaPriseDeCommande(new NombreDeConvives(nombreDeConvives),
                        numero, datePriseDeCommande));
        return new ResponseDTO(
                CommandeDTO.from(handled),
                eventRepository.loadOrderByVersionASC(handled.id()).stream().map(this::from).toList());
    }

    @Path("/{numeroDeTable}/ajouterPlat")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public ResponseDTO ajouterPlat(@PathParam("numeroDeTable") final Integer numeroDeTable,
                                   @FormParam("nom") final String name) {
        final NumeroDeTable numero = new NumeroDeTable(numeroDeTable);
        final Commande handled = commandeCommandHandler.handle(
                new AjouterPlat(
                        new CommandeIdentifier(numero, datePriseDeCommandesParNumeroDeTable.get(numero)),
                        new Plat(name)));
        return new ResponseDTO(
                CommandeDTO.from(handled),
                eventRepository.loadOrderByVersionASC(handled.id()).stream().map(this::from).toList());
    }


    @Path("/{numeroDeTable}/finaliserLaCommande")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseDTO finaliserLaCommande(@PathParam("numeroDeTable") final Integer numeroDeTable) {
        final NumeroDeTable numero = new NumeroDeTable(numeroDeTable);
        final Commande handled = commandeCommandHandler.handle(new FinaliserLaCommande(
                new CommandeIdentifier(numero, datePriseDeCommandesParNumeroDeTable.get(numero))));
        return new ResponseDTO(
                CommandeDTO.from(handled),
                eventRepository.loadOrderByVersionASC(handled.id()).stream().map(this::from).toList());
    }
}
