package com.training.enrollments.enrollment;

import com.training.enrollments.api.EnrollmentDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/enrollments")
public class EnrollmentController {

	private final EnrollmentService enrollmentService;

	public EnrollmentController(EnrollmentService enrollmentService) 
	{
		this.enrollmentService = enrollmentService;
	}

	public record CreateEnrollmentRequest(String name, String email, UUID courseId) {
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public EnrollmentDto enroll(@RequestBody CreateEnrollmentRequest request) 
	{
		return enrollmentService.enroll(request.name(), request.email(), request.courseId());
	}

	@GetMapping
	public List<EnrollmentDto> listByCourse(@RequestParam UUID courseId) 
	{
		return enrollmentService.listByCourse(courseId);
	}
}