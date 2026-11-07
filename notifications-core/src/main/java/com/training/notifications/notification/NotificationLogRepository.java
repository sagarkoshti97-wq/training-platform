package com.training.notifications.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

interface NotificationLogRepository extends JpaRepository<NotificationLogEntity, UUID> 
{
    List<NotificationLogEntity> findByEnrollmentId(UUID enrollmentId);
}