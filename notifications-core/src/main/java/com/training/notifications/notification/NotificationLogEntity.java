package com.training.notifications.notification;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "notification_logs")
class NotificationLogEntity {

	@Id
	private UUID id;

	@Column(name = "enrollment_id", nullable = false)
	private UUID enrollmentId;

	@Column(nullable = false)
	private String recipient;

	@Column(nullable = false)
	private String channel;

	@Column(nullable = false)
	private String message;

	protected NotificationLogEntity() {
	}

	NotificationLogEntity(UUID id, UUID enrollmentId, String recipient, String channel, String message) {
		this.id = id;
		this.enrollmentId = enrollmentId;
		this.recipient = recipient;
		this.channel = channel;
		this.message = message;
	}

	UUID getId() {
		return id;
	}

	UUID getEnrollmentId() {
		return enrollmentId;
	}

	String getRecipient() {
		return recipient;
	}

	String getChannel() {
		return channel;
	}

	String getMessage() {
		return message;
	}
}