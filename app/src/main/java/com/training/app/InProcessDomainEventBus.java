package com.training.app;

import com.training.common.events.DomainEventHandler;
import com.training.common.events.DomainEventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InProcessDomainEventBus implements DomainEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final List<DomainEventHandler<?>> handlers;

    public InProcessDomainEventBus(
            ApplicationEventPublisher applicationEventPublisher,
            List<DomainEventHandler<?>> handlers
    ) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.handlers = handlers;
    }

    @Override
    public void publish(Object event) 
    {
        applicationEventPublisher.publishEvent(event);
        
    }

    @EventListener
    public void onApplicationEvent(Object event) 
    {
        handlers.stream()
                .filter(handler -> handler.eventType().isInstance(event))
                .forEach(handler -> invokeHandler(handler, event));
    }

    @SuppressWarnings("unchecked")
    private <T> void invokeHandler(DomainEventHandler<T> handler, Object event) {
        handler.handle((T) event);
    }
}