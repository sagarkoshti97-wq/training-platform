package com.training.catalog.course;

import java.util.Optional;
import java.util.UUID;

import com.training.catalog.api.CourseDto;
import com.training.catalog.api.CoursesApi;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CourseService implements CoursesApi
{
	private CourseRepository courseRepository;
	
	public CourseService(CourseRepository courseRepository)
	{
		this.courseRepository = courseRepository;
	}

	@Override
	@Transactional(readOnly=true)
	public Optional<CourseDto> findById(UUID courseId) {
		
		return courseRepository.findById(courseId).map(this::toDto);
	}
	
	private CourseDto toDto(CourseEntity entity)
	{
		return new CourseDto(entity.getId(),
								entity.getTitle(),
								entity.getCapacity(),
								entity.getCity(),
								entity.isPublished());
	}

}
