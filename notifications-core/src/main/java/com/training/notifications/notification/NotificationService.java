package com.training.notifications.notification;

import com.training.enrollments.api.EnrollmentCreatedEvent;
import com.training.notifications.api.NotificationDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {

    private final List<NotificationChannel> channels;
    private final NotificationLogRepository repository;

    public NotificationService(List<NotificationChannel> channels, NotificationLogRepository repository) {
        this.channels = channels;
        this.repository = repository;
    }

    @Transactional
    public void notifyEnrollmentCreated(EnrollmentCreatedEvent event) {
        String message = "Hi " + event.VisitorName() + ", your enrollment for course " + event.courseId() + " is confirmed.";

        List<NotificationLogEntity> logs = channels.stream()
                .map(channel -> {
                    channel.send(event.VisiterEmail(), message);
                    return new NotificationLogEntity(
                            UUID.randomUUID(),
                            event.enrollmentID(),
                            event.VisiterEmail(),
                            channel.channelName(),
                            message
                    );
                })
                .toList();

        repository.saveAll(logs);
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> findByEnrollmentId(UUID enrollmentId) {
        return repository.findByEnrollmentId(enrollmentId)
                .stream()
                .map(entity -> new NotificationDto(
                        entity.getId(),
                        entity.getEnrollmentId(),
                        entity.getRecipient(),
                        entity.getChannel(),
                        entity.getMessage()
                ))
                .toList();
    }
}