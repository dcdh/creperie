package com.creperie.gestion.infrastructure.api;

import com.creperie.gestion.domain.CommandeStatistique;
import com.creperie.gestion.domain.CommandeStatistiqueRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Path("/statistiques/commandes")
@ApplicationScoped
public class CommandesStatistiqueEntrypoint {

    @Inject
    CommandeStatistiqueRepository commandeStatistiqueRepository;

    @Schema(name = "CommandeStatistique", required = true, requiredProperties = {"dateDeService", "nombreDeCommandes",
            "nombreDeCommandesEnCoursDePrise", "nombreDeCommandesFinalisees",
            "nombreDeCommandesEnProduction", "nombreDeCommandeProduite",
            "plats"})
    public record CommandeStatistiqueDTO(LocalDate dateDeService,
                                         Integer nombreDeCommandes,
                                         Integer nombreDeCommandesEnCoursDePrise,
                                         Integer nombreDeCommandesFinalisees,
                                         Integer nombreDeCommandesEnProduction,
                                         Integer nombreDeCommandeProduite,
                                         List<PlatDTO> plats) {

        public static CommandeStatistiqueDTO from(final CommandeStatistique commandeStatistique) {
            return new CommandeStatistiqueDTO(
                    commandeStatistique.dateDeService().localDate(),
                    commandeStatistique.nombreDeCommandes(),
                    commandeStatistique.nombreDeCommandesEnCoursDePrise(),
                    commandeStatistique.nombreDeCommandesFinalisees(),
                    commandeStatistique.nombreDeCommandesEnProduction(),
                    commandeStatistique.nombreDeCommandeProduite(),
                    commandeStatistique.plats().entrySet().stream()
                            .map(entry -> new PlatDTO(entry.getKey(), entry.getValue()))
                            .toList()
            );
        }
    }

    @Schema(name = "Plat", required = true, requiredProperties = {"nom", "nombreDePlatsCommandes"})
    public record PlatDTO(String nom, Integer nombreDePlatsCommandes) {

    }

    @GET
    public List<CommandeStatistiqueDTO> list() {
        return commandeStatistiqueRepository.listAllOrderByDateDeServiceAsc().stream()
                .map(CommandeStatistiqueDTO::from)
                .toList();
    }
}
