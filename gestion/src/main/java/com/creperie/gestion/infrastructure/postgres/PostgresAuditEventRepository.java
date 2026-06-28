package com.creperie.gestion.infrastructure.postgres;

import com.creperie.gestion.domain.AuditEvent;
import com.creperie.gestion.domain.AuditEventRepository;
import com.damdamdeo.pulse.extension.core.AggregateRootType;
import com.damdamdeo.pulse.extension.core.consumer.AnyAggregateId;
import com.damdamdeo.pulse.extension.core.consumer.CurrentVersionInConsumption;
import com.damdamdeo.pulse.extension.core.consumer.FromApplication;
import com.damdamdeo.pulse.extension.core.encryption.EncryptedPayload;
import com.damdamdeo.pulse.extension.core.event.EventType;
import com.damdamdeo.pulse.extension.core.event.OwnedBy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class PostgresAuditEventRepository implements AuditEventRepository {

    @Inject
    DataSource dataSource;

    @Override
    @Transactional
    public void store(final AuditEvent auditEvent) {
        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement ps = connection.prepareStatement(
                     // language=sql
                     """
                             INSERT INTO audit_event (
                                         application_name,
                                         aggregate_type,
                                         aggregate_id,
                                         version,
                                         creation_date,
                                         event_type,
                                         encrypted_payload,
                                         owned_by
                                     ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                             """
             )) {
            ps.setString(1, auditEvent.fromApplication().name());
            ps.setString(2, auditEvent.aggregateRootType().type());
            ps.setString(3, auditEvent.aggregateId().id());
            ps.setInt(4, auditEvent.currentVersionInConsumption().version());
            // Instant -> TIMESTAMPTZ
            ps.setTimestamp(5, Timestamp.from(auditEvent.storedAt()));
            ps.setString(6, auditEvent.eventType().type());
            // byte[] -> BYTEA
            ps.setBytes(7, auditEvent.encryptedPayload().payload());
            ps.setString(8, auditEvent.ownedBy().id());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AuditEvent> listAllOrderByEnregistreA() {
        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement ps = connection.prepareStatement(
                     // language=sql
                     """
                             SELECT application_name,
                                    aggregate_type,
                                    aggregate_id,
                                    version,
                                    creation_date,
                                    event_type,
                                    encrypted_payload,
                                    owned_by
                             FROM audit_event ORDER BY creation_date ASC
                             """
             );
             final ResultSet rs = ps.executeQuery()) {
            // TODO je devrais faire un count pour initialiser le nombre d'elements dans la la liste
            final List<AuditEvent> list = new ArrayList<>();
            while (rs.next()) {
                list.add(
                        new AuditEvent(
                                new FromApplication(
                                        rs.getString("application_name")),
                                new AggregateRootType(
                                        rs.getString("aggregate_type")),
                                new AnyAggregateId(
                                        rs.getString("aggregate_id")),
                                new CurrentVersionInConsumption(
                                        rs.getInt("version")),
                                rs.getTimestamp("creation_date").toInstant(),
                                new EventType(
                                        rs.getString("event_type")),
                                new EncryptedPayload(
                                        rs.getBytes("encrypted_payload")),
                                new OwnedBy(
                                        rs.getString("owned_by"))
                        )
                );
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
