package com.creperie.cuisine.infrastructure.api;

public record NotifyEvent(String eventName, Class<?> type, Object data) {
}
