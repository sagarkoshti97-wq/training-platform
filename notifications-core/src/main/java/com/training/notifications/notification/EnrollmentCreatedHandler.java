package com.training.notifications.notification;

import com.training.common.events.DomainEventHandler;
import com.training.enrollments.api.EnrollmentCreatedEvent;
import org.springframework.stereotype.Component;

@Component
public class EnrollmentCreatedHandler implements DomainEventHandler<EnrollmentCreatedEvent> {

    private final NotificationService notificationService;

    public EnrollmentCreatedHandler(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public void handle(EnrollmentCreatedEvent event) {
        notificationService.notifyEnrollmentCreated(event);
    }

    @Override
    public Class<EnrollmentCreatedEvent> eventType() {
        return EnrollmentCreatedEvent.class;
    }
}