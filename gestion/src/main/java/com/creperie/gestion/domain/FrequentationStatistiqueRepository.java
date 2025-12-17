package com.creperie.gestion.domain;

import java.util.List;
import java.util.Optional;

public interface FrequentationStatistiqueRepository {

    void persist(FrequentationStatistique frequentationStatistique);

    Optional<FrequentationStatistique> findByDateDeService(DateDeService dateDeService);

    List<FrequentationStatistique> listAllOrderByDateDeServiceAsc();
}
