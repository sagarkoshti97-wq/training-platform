package com.training.enrollments.enrollment;

import com.training.catalog.api.CourseDto;
import com.training.catalog.api.CoursesApi;
import com.training.common.events.DomainEventPublisher;
import com.training.enrollments.api.EnrollmentCreatedEvent;
import com.training.enrollments.api.EnrollmentDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class EnrollmentService {

	private final EnrollmentRepository enrollmentRepository;
	private final CoursesApi coursesApi;
	private final DomainEventPublisher eventPublisher;

	public EnrollmentService(EnrollmentRepository enrollmentRepository, CoursesApi coursesApi,
			DomainEventPublisher eventPublisher) 
	{
		this.enrollmentRepository = enrollmentRepository;
		this.coursesApi = coursesApi;
		this.eventPublisher = eventPublisher;
	}

	@Transactional
	public EnrollmentDto enroll(String name, String email, UUID courseId) 
	{
		CourseDto course = coursesApi.findById(courseId).filter(CourseDto::published).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course does not exist or is not published"));

		long currentEnrollments = enrollmentRepository.countByCourseId(courseId);
		if (currentEnrollments >= course.capacity()) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Course is at capacity");
		}

		EnrollmentEntity saved = enrollmentRepository
				.save(new EnrollmentEntity(UUID.randomUUID(), courseId, name, email));

		eventPublisher.publish(new EnrollmentCreatedEvent(saved.getId(), saved.getCourseId(), saved.getVisitorName(),
				saved.getVisitoremail()));

		return toDto(saved);
	}

	@Transactional(readOnly = true)
	public List<EnrollmentDto> listByCourse(UUID courseId) 
	{
		return enrollmentRepository.findByCourseId(courseId).stream().map(this::toDto).toList();
	}

	private EnrollmentDto toDto(EnrollmentEntity entity) 
	{
		return new EnrollmentDto(entity.getId(), entity.getCourseId(), entity.getVisitorName(),
				entity.getVisitoremail());
	}
}