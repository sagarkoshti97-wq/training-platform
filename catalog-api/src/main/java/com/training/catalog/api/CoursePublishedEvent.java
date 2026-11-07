package com.training.catalog.api;

import java.util.UUID;

public record CoursePublishedEvent(
        UUID courseId,
        String title,
        int capacity,
        String city
)
{}