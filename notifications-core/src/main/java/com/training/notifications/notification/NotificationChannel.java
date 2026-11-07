package com.training.notifications.notification;

public interface NotificationChannel 
{
    String channelName();
    void send(String recipient, String message);
}