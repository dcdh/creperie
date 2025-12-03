package com.creperie.salle.infrastructure.api;

import com.creperie.salle.domain.*;
import com.creperie.salle.domain.command.AjouterPlat;
import com.creperie.salle.domain.command.CommencerLaPriseDeCommande;
import com.creperie.salle.domain.command.FinaliserLaCommande;
import com.damdamdeo.pulse.extension.core.command.CommandHandler;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Path("prise_de_commande")
public class PriseDeCommandeEndpoint {

    private final CommandHandler<Commande, CommandeIdentifier> commandeCommandHandler;
    private final DatePriseDeCommandeProvider datePriseDeCommandeProvider;

    public PriseDeCommandeEndpoint(final CommandHandler<Commande, CommandeIdentifier> commandeCommandHandler,
                                   final DatePriseDeCommandeProvider datePriseDeCommandeProvider) {
        this.commandeCommandHandler = Objects.requireNonNull(commandeCommandHandler);
        this.datePriseDeCommandeProvider = Objects.requireNonNull(datePriseDeCommandeProvider);
    }

    private Map<NumeroDeTable, DatePriseDeCommande> datePriseDeCommandesParNumeroDeTable = new HashMap<>();

    @Schema(name = "Plat", required = true, requiredProperties = {"nom"})
    public record PlatDTO(String nom) {

        public static PlatDTO from(final Plat plat) {
            return new PlatDTO(plat.nom());
        }
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

    @Path("commencerLaPriseDeCommande")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public CommandeDTO commencerLaPriseDeCommande(
            @FormParam("nombreDeConvives") final Integer nombreDeConvives,
            @FormParam("numeroDeTable") final Integer numeroDeTable) {
        final NumeroDeTable numero = new NumeroDeTable(numeroDeTable);
        final DatePriseDeCommande datePriseDeCommande = datePriseDeCommandeProvider.provide();
        datePriseDeCommandesParNumeroDeTable.put(numero, datePriseDeCommande);
        return CommandeDTO.from(commandeCommandHandler.handle(
                new CommencerLaPriseDeCommande(new NombreDeConvives(nombreDeConvives),
                        numero, datePriseDeCommande)));
    }

    @Path("/{numeroDeTable}/ajouterPlat")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public CommandeDTO ajouterPlat(@PathParam("numeroDeTable") final Integer numeroDeTable,
                                   @FormParam("nom") final String name) {
        final NumeroDeTable numero = new NumeroDeTable(numeroDeTable);
        return CommandeDTO.from(commandeCommandHandler.handle(
                new AjouterPlat(
                        new CommandeIdentifier(numero, datePriseDeCommandesParNumeroDeTable.get(numero)),
                        new Plat(name))));
    }
// FCK retourner une ReponseDTO avec la CommandeDTO et une liste d'eventDTO
    @Path("/{numeroDeTable}/finaliserLaCommande")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public CommandeDTO finaliserLaCommande(@PathParam("numeroDeTable") final Integer numeroDeTable) {
        final NumeroDeTable numero = new NumeroDeTable(numeroDeTable);

        return CommandeDTO.from(commandeCommandHandler.handle(new FinaliserLaCommande(
                new CommandeIdentifier(numero, datePriseDeCommandesParNumeroDeTable.get(numero)))));
    }
}
