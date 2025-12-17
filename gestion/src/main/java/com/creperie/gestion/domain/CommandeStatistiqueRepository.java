package com.creperie.gestion.domain;

import java.util.List;
import java.util.Optional;

public interface CommandeStatistiqueRepository {

    void persist(CommandeStatistique commandeStatistique);

    Optional<CommandeStatistique> findByDateDeService(DateDeService dateDeService);

    List<CommandeStatistique> listAllOrderByDateDeServiceAsc();
}
