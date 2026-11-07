package com.training.catalog.api;

import java.util.UUID;

public record CourseDto(
        UUID id,
        String title,
        int capacity,
        String city,
        boolean published
)
{}