package com.training.catalog.api;

import java.util.Optional;
import java.util.UUID;

public interface CoursesApi 
{
    Optional<CourseDto> findById(UUID courseId);
}