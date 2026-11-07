package com.training.enrollments.enrollment;

import com.training.catalog.api.CourseDto;
import com.training.catalog.api.CoursesApi;
import com.training.common.events.DomainEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

	@Mock
	private EnrollmentRepository enrollmentRepository;

	@Mock
	private CoursesApi coursesApi;

	@Mock
	private DomainEventPublisher eventPublisher;

	@InjectMocks
	private EnrollmentService enrollmentService;

	@Test
	void shouldRejectEnrollmentWhenCourseIsAtCapacity() {
		UUID courseId = UUID.randomUUID();
		CourseDto publishedCourse = new CourseDto(courseId, "Spring Modulith", 2, "Pune", true);

		when(coursesApi.findById(courseId)).thenReturn(Optional.of(publishedCourse));
		when(enrollmentRepository.countByCourseId(courseId)).thenReturn(2L);

		assertThatThrownBy(() -> enrollmentService.enroll("Sagar", "sagar@example.com", courseId))
				.isInstanceOf(ResponseStatusException.class).hasMessageContaining("Course is at capacity");

		verify(enrollmentRepository, never()).save(any());
		verifyNoInteractions(eventPublisher);
	}

	@Test
	void shouldRejectEnrollmentWhenCourseIsNotPublished() {
		UUID courseId = UUID.randomUUID();
		CourseDto draftCourse = new CourseDto(courseId, "Java Streams", 10, "Ahmedabad", false);

		when(coursesApi.findById(courseId)).thenReturn(Optional.of(draftCourse));

		assertThatThrownBy(() -> enrollmentService.enroll("Sagar", "sagar@example.com", courseId))
				.isInstanceOf(ResponseStatusException.class)
				.hasMessageContaining("Course does not exist or is not published");

		verify(enrollmentRepository, never()).save(any());
	}
}