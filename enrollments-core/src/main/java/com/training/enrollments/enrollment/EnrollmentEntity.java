package com.training.enrollments.enrollment;

import java.util.UUID;

import org.springframework.data.annotation.Id;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name="enrollments")
public class EnrollmentEntity 
{
	@Id
	private UUID id;
	
	@Column(name = "course_id", nullable = false)
	private UUID courseId;
	
	@Column(name = "visitor_name", nullable= false)
	private String visitorName;
	
	@Column(name = "visitor_email", nullable= false)
	private String visitoremail;
	
	private EnrollmentEntity(){}
	
	EnrollmentEntity(UUID id, UUID courseId, String VisitorName, String VisitorEmail)
	{
		this.id =id;
		this.courseId = courseId;
		this.visitorName = visitorName;
		this.visitoremail = visitoremail;
	}

	UUID getId() {
		return id;
	}

	UUID getCourseId() {
		return courseId;
	}

	String getVisitorName() {
		return visitorName;
	}

	String getVisitoremail() {
		return visitoremail;
	}
	
	
	
}
