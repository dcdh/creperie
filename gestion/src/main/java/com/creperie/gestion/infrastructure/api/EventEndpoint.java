package com.creperie.gestion.infrastructure.api;

import com.creperie.gestion.domain.AuditEventRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.Objects;

@Path("/events")
@ApplicationScoped
public class EventEndpoint {

    @Inject
    AuditEventRepository auditEventRepository;

    @Inject
    AuditEventDTOMapper auditEventDTOMapper;

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public List<AuditEventDTO> events() {
        return auditEventRepository.listAllOrderByEnregistreA()
                .stream().map(
                        auditEvent -> auditEventDTOMapper.mapFrom(auditEvent))
                .filter(Objects::nonNull)
                .toList();
    }
}
