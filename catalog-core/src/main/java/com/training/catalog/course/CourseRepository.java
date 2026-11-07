package com.training.catalog.course;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

interface CourseRepository extends JpaRepository<CourseEntity, UUID> 
{
	
}