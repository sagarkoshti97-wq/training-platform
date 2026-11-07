package com.training.catalog.course;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="courses")
public class CourseEntity 
{
	@Id
	private UUID id;
	
	@Column(nullable=false)
	private String title;
	
	@Column(nullable=false)
	private int capacity;
	
	@Column(nullable=false)
	private String city;
	
	@Column(nullable=false)
	private boolean published;
	
	
	protected CourseEntity(){}

	CourseEntity(UUID id, String title, int capacity, String city, boolean publish) {
		this.id = id;
		this.title = title;
		this.capacity = capacity;
		this.city = city;
		this.published = published;
	}
	
	void publish()
	{
		this.published = true;
	}

	UUID getId() {
		return id;
	}

	String getTitle() {
		return title;
	}

	int getCapacity() {
		return capacity;
	}

	String getCity() {
		return city;
	}

	boolean isPublished() {
		return published;
	}
	
	
	
	

}
