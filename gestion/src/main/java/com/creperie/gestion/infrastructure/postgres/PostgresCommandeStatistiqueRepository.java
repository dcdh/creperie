package com.creperie.gestion.infrastructure.postgres;

import com.creperie.gestion.domain.CommandeStatistique;
import com.creperie.gestion.domain.CommandeStatistiqueRepository;
import com.creperie.gestion.domain.DateDeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PostgresCommandeStatistiqueRepository implements CommandeStatistiqueRepository {

    @Inject
    DataSource dataSource;

    @Inject
    ObjectMapper objectMapper;

    @Override
    public void persist(final CommandeStatistique commandeStatistique) {
        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement ps = connection.prepareStatement(
                     // language=sql
                     """
                             INSERT INTO statistic_commandes (
                                     date_de_service,
                                     payload
                             ) VALUES (?, ?::jsonb)
                             ON CONFLICT (date_de_service)
                                     DO UPDATE SET
                             payload = EXCLUDED.payload
                             """
             )) {
            ps.setObject(1, commandeStatistique.dateDeService().localDate());
            ps.setString(2, objectMapper.writeValueAsString(commandeStatistique));

            ps.executeUpdate();
        } catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<CommandeStatistique> findByDateDeService(final DateDeService dateDeService) {
        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement ps = connection.prepareStatement(
                     // language=sql
                     """
                             SELECT payload
                             FROM statistic_commandes
                             WHERE date_de_service = ?
                             """
             )) {
            ps.setObject(1, dateDeService.localDate());
            try (final ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    final String payload = rs.getString("payload");
                    return Optional.of(objectMapper.readValue(payload, CommandeStatistique.class));
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CommandeStatistique> listAllOrderByDateDeServiceAsc() {
        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement ps = connection.prepareStatement(
                     // language=sql
                     """
                             SELECT payload
                             FROM statistic_commandes
                             ORDER BY date_de_service ASC
                             """
             );
             final ResultSet rs = ps.executeQuery()) {
            // TODO je devrais faire un count pour initialiser le nombre d'elements dans la la liste
            final List<CommandeStatistique> list = new ArrayList<>();
            while (rs.next()) {
                final String payload = rs.getString("payload");
                list.add(objectMapper.readValue(payload, CommandeStatistique.class));
            }
            return list;
        } catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
