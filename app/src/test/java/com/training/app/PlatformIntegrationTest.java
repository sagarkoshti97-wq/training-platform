package com.training.app;

import com.training.catalog.api.CourseDto;
import com.training.enrollments.api.EnrollmentDto;
import com.training.enrollments.enrollment.CoursePublishedHandler;
import com.training.notifications.api.NotificationDto;
import com.training.notifications.notification.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PlatformIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CoursePublishedHandler coursePublishedHandler;

    @Autowired
    private NotificationService notificationService;

    @Test
    void shouldPublishCourseEnrollVisitorAndProduceNotification() {
        // 1. Create a course with capacity = 1
        ResponseEntity<CourseDto> createResp = restTemplate.postForEntity(
                "/api/v1/courses",
                Map.of("title", "Microservices to Modulith", "capacity", 1, "city", "Pune"),
                CourseDto.class
        );
        assertThat(createResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        CourseDto createdCourse = createResp.getBody();
        assertThat(createdCourse).isNotNull();
        assertThat(createdCourse.published()).isFalse();

        // 2. Publish the course -> triggers CoursePublishedEvent in enrollments
        ResponseEntity<CourseDto> publishResp = restTemplate.postForEntity(
                "/api/v1/courses/" + createdCourse.id() + "/publish",
                null,
                CourseDto.class
        );
        assertThat(publishResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(publishResp.getBody().published()).isTrue();
        assertThat(coursePublishedHandler.isCourseTracked(createdCourse.id())).isTrue();

        // 3. Enroll a visitor -> checks catalog-api, saves enrollment, triggers notification
        ResponseEntity<EnrollmentDto> enrollResp = restTemplate.postForEntity(
                "/api/v1/enrollments",
                Map.of("name", "Sagar Koshti", "email", "sagar@example.com", "courseId", createdCourse.id()),
                EnrollmentDto.class
        );
        assertThat(enrollResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        EnrollmentDto enrollment = enrollResp.getBody();
        assertThat(enrollment).isNotNull();

        // 4. Verify notification was created via EnrollmentCreatedEvent
        List<NotificationDto> notifications = notificationService.findByEnrollmentId(enrollment.id());
        assertThat(notifications).hasSize(1);
        assertThat(notifications.get(0).channel()).isEqualTo("EMAIL");
        assertThat(notifications.get(0).recipient()).isEqualTo("sagar@example.com");

        // 5. Verify second enrollment is rejected (409 Conflict) because capacity (1) is reached
        ResponseEntity<String> overCapacityResp = restTemplate.postForEntity(
                "/api/v1/enrollments",
                Map.of("name", "Another Visitor", "email", "visitor2@example.com", "courseId", createdCourse.id()),
                String.class
        );
        assertThat(overCapacityResp.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }
}