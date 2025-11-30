package com.creperie.salle.infrastructure.api;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.SseEventSink;

import java.util.Objects;

@Path("/sse")
public class SseBroadcasterEndpoint {

    private final SseBroadcaster sseBroadcaster;

    public SseBroadcasterEndpoint(final SseBroadcaster sseBroadcaster) {
        this.sseBroadcaster = Objects.requireNonNull(sseBroadcaster);
    }

    @GET
    @Path("/stream")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void register(@Context SseEventSink eventSink) {
        sseBroadcaster.register(eventSink);
    }
}
