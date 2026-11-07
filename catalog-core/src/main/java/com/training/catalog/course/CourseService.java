package com.training.catalog.course;

import java.util.Optional;
import java.util.UUID;

import com.training.catalog.api.CourseDto;
import com.training.catalog.api.CoursePublishedEvent;
import com.training.catalog.api.CoursesApi;
import com.training.common.events.DomainEventPublisher;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CourseService implements CoursesApi
{
	private CourseRepository courseRepository;
	private DomainEventPublisher domainEventPublisher;
	
	public CourseService(CourseRepository courseRepository, DomainEventPublisher domainEventPublisher)
	{
		this.courseRepository = courseRepository;
		this.domainEventPublisher = domainEventPublisher;
	}
	
	//To Create Course in Table
	@Transactional
	public CourseDto createCourse(String title, int capacity, String city)
	{
		CourseEntity entity = new CourseEntity(UUID.randomUUID(), title, capacity, city, false);
		return toDto(courseRepository.save(entity));
	}
	
	//To Publish Course in Event
	@Transactional
	public CourseDto publishCourse(UUID courseId)
	{
		return courseRepository.findById(courseId)
								.map(entity -> {
									entity.publish();
									CourseEntity saved = courseRepository.save(entity);
									domainEventPublisher.publish(new CoursePublishedEvent(
											saved.getId(), 
											saved.getTitle(),
											saved.getCapacity(),
											saved.getCity()
											));
									return toDto(saved);
								})
							.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not Found"));
		
	}

	//To Fetch Course from Table
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
