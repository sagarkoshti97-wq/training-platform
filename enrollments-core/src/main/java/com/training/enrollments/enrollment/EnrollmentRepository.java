package com.training.enrollments.enrollment;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

interface EnrollmentRepository extends JpaRepository<EnrollmentEntity, UUID> 
{

	long countByCourseId(UUID courseId);
    
	List<EnrollmentEntity> findByCourseId(UUID courseId);
}