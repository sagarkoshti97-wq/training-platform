package com.training.common.events;

@FunctionalInterface
public interface DomainEventPublisher 
{
	void publish(Object event);
}