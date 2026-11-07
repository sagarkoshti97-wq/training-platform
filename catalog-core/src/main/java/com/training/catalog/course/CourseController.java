package com.training.catalog.course;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;
import com.training.catalog.api.CourseDto;

@RestController
@RequestMapping("api/v1/courses")
public class CourseController {
	private CourseService courseService;

	public CourseController(CourseService courseService) 
	{
		this.courseService = courseService;
	}

	public record CreateCourseRequest(String title, int capacity, String city) 
	{}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CourseDto Create(@RequestBody CreateCourseRequest request) {
		return courseService.createCourse(request.title, request.capacity, request.city);
	}
	
	@PostMapping("/{id}/publish")
	public CourseDto publish(@PathVariable("id") UUID id)
	{
		return courseService.publishCourse(id);
	}

	@GetMapping("/{id}")
	public CourseDto getById(@PathVariable("id") UUID id) {
		return courseService.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not Found"));
	}

}
