package com.training.enrollments.enrollment;

import com.training.catalog.api.CoursePublishedEvent;
import com.training.common.events.DomainEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CoursePublishedHandler implements DomainEventHandler<CoursePublishedEvent> {

    private static final Logger log = LoggerFactory.getLogger(CoursePublishedHandler.class);
    private final Map<UUID, Integer> publishedCourseCapacities = new ConcurrentHashMap<>();

    @Override
    public void handle(CoursePublishedEvent event) 
    {
        publishedCourseCapacities.put(event.courseId(), event.capacity());
        log.info("Enrollments domain warmed capacity cache for published course {} (capacity={})",
                event.courseId(), event.capacity());
    }

    @Override
    public Class<CoursePublishedEvent> eventType() {
        return CoursePublishedEvent.class;
    }

    public boolean isCourseTracked(UUID courseId) {
        return publishedCourseCapacities.containsKey(courseId);
    }
}