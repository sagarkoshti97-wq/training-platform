package com.training.catalog.course;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;
import com.training.catalog.api.CourseDto;

@RestController
@RequestMapping("api/v1/courses")
public class CourseController 
{
	private CourseService courseService;
	
	public CourseController(CourseService courseService)
	{
		this.courseService = courseService;
	}
	
	@GetMapping("/{id}")
	public CourseDto getById(@PathVariable UUID id)
	{
		return courseService.findById(id)
							.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Course not Found"));
	}

}
