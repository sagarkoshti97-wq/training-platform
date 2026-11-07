package com.training.enrollments.api;

import java.util.UUID;

public record EnrollmentDto(UUID id, UUID courseId, String name, String email) {
}
