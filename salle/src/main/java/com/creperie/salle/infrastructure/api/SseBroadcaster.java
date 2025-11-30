package com.creperie.salle.infrastructure.api;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;
import org.jboss.logging.Logger;

import java.util.Objects;

@ApplicationScoped
public class SseBroadcaster {

    private final Sse sse;
    private final jakarta.ws.rs.sse.SseBroadcaster sseBroadcaster;
    private final Logger logger;

    public SseBroadcaster(@Context final Sse sse,
                          final Logger logger) {
        this.sse = Objects.requireNonNull(sse);
        this.sseBroadcaster = sse.newBroadcaster();
        this.logger = Objects.requireNonNull(logger);
    }

    public void register(final SseEventSink eventSink) {
        sseBroadcaster.register(eventSink);
    }

    public void broadcast(final String eventName, final GenericType<?> type, Object data) {
        sseBroadcaster.broadcast(sse.newEventBuilder()
                        .name(eventName)
                        .data(type, data)
                        .mediaType(MediaType.APPLICATION_JSON_TYPE)
                        .build())
                .exceptionallyAsync(throwable -> {
                    logger.warnv("Something wrong happened ''{0}''", throwable.getMessage());
                    return null;
                });
    }

    public void broadcast(final String eventName, final Class<?> type, Object data) {
        sseBroadcaster.broadcast(sse.newEventBuilder()
                .name(eventName)
                .data(type, data)
                .mediaType(MediaType.APPLICATION_JSON_TYPE)
                .build());
    }
}
