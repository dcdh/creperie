package com.creperie.gestion.infrastructure.postgres;

import com.creperie.gestion.domain.DateDeService;
import com.creperie.gestion.domain.FrequentationStatistique;
import com.creperie.gestion.domain.FrequentationStatistiqueRepository;
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
public class PostgresFrequentationStatistiqueRepository implements FrequentationStatistiqueRepository {

    @Inject
    DataSource dataSource;

    @Inject
    ObjectMapper objectMapper;

    @Override
    public void persist(final FrequentationStatistique frequentationStatistique) {
        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement ps = connection.prepareStatement(
                     // language=sql
                     """
                             INSERT INTO statistic_frequentation (
                                     date_de_service,
                                     payload
                             ) VALUES (?, ?::jsonb)
                             ON CONFLICT (date_de_service)
                                     DO UPDATE SET
                             payload = EXCLUDED.payload
                             """
             )) {
            ps.setObject(1, frequentationStatistique.dateDeService().localDate());
            ps.setString(2, objectMapper.writeValueAsString(frequentationStatistique));

            ps.executeUpdate();
        } catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<FrequentationStatistique> findByDateDeService(final DateDeService dateDeService) {
        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement ps = connection.prepareStatement(
                     // language=sql
                     """
                             SELECT payload
                             FROM statistic_frequentation
                             WHERE date_de_service = ?
                             """
             )) {
            ps.setObject(1, dateDeService.localDate());
            try (final ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    final String payload = rs.getString("payload");
                    return Optional.of(objectMapper.readValue(payload, FrequentationStatistique.class));
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<FrequentationStatistique> listAllOrderByDateDeServiceAsc() {
        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement ps = connection.prepareStatement(
                     // language=sql
                     """
                             SELECT payload
                             FROM statistic_frequentation
                             ORDER BY date_de_service ASC
                             """
             );
             final ResultSet rs = ps.executeQuery()) {
            // TODO je devrais faire un count pour initialiser le nombre d'elements dans la la liste
            final List<FrequentationStatistique> list = new ArrayList<>();
            while (rs.next()) {
                final String payload = rs.getString("payload");
                list.add(objectMapper.readValue(payload, FrequentationStatistique.class));
            }
            return list;
        } catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
