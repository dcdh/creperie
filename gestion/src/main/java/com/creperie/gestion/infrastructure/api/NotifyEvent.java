package com.creperie.gestion.infrastructure.api;

public record NotifyEvent(String eventName, Class<?> type, Object data) {
}
