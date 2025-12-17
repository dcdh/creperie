package com.creperie.salle.infrastructure.api;

public record NotifyEvent(String eventName, Class<?> type, Object data) {
}
