package com.training.notifications.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationChannel implements NotificationChannel {

	private static final Logger log = LoggerFactory.getLogger(EmailNotificationChannel.class);

	@Override
	public String channelName() {
		return "EMAIL";
	}

	@Override
	public void send(String recipient, String message) 
	{
		log.info("[EMAIL] To {}: {}", recipient, message);
	}
}