package com.training.common.events;

public interface DomainEventHandler<T> 
{
	void handle(T event);

	Class<T> eventType();
}