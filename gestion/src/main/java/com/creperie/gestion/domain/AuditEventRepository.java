package com.creperie.gestion.domain;

import java.util.List;

public interface AuditEventRepository {

    void store(AuditEvent auditEvent);

    List<AuditEvent> listAllOrderByEnregistreA();
}
