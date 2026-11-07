package com.training.enrollments.api;

import java.util.UUID;

public record EnrollmentCreatedEvent(UUID enrollmentID, UUID courseId, String VisitorName, String VisiterEmail)
{
	
}
