package com.training.notifications.api;

import java.util.UUID;

public record NotificationDto(
        UUID id,
        UUID enrollmentId,
        String recipient,
        String channel,
        String message
) {}