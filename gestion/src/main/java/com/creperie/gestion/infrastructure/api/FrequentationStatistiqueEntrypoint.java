package com.creperie.gestion.infrastructure.api;

import com.creperie.gestion.domain.FrequentationStatistique;
import com.creperie.gestion.domain.FrequentationStatistiqueRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Path("/statistiques/frequentation")
@ApplicationScoped
public class FrequentationStatistiqueEntrypoint {

    @Inject
    FrequentationStatistiqueRepository frequentationStatistiqueRepository;

    @Schema(name = "FrequentationStatistique", required = true, requiredProperties = {"dateDeService", "nombreDeClients"})
    public record FrequentationStatistiqueDTO(LocalDate dateDeService, Integer nombreDeClients) {

        public static FrequentationStatistiqueDTO from(final FrequentationStatistique frequentationStatistique) {
            return new FrequentationStatistiqueDTO(
                    frequentationStatistique.dateDeService().localDate(),
                    frequentationStatistique.nombreDeClients());
        }
    }

    @GET
    public List<FrequentationStatistiqueDTO> list() {
        return frequentationStatistiqueRepository.listAllOrderByDateDeServiceAsc().stream()
                .map(FrequentationStatistiqueDTO::from)
                .toList();
    }
}
