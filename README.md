# Training Platform
## Modular Monolith Architecture Exercise

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-green)
![Maven](https://img.shields.io/badge/Maven-Multi_Module-blue)
![JPA](https://img.shields.io/badge/Spring_Data_JPA-red)
![Architecture](https://img.shields.io/badge/Architecture-Tested-success)

---

# 1. Project Overview

This project implements a **Training Platform** using a **Modular Monolith** architecture.

The objective is not simply to expose CRUD endpoints, but to demonstrate:

- Domain-driven modularization
- Strict module boundaries
- API/Core separation
- Event-driven communication
- Package-by-subdomain organization
- Extensibility through abstraction
- Compiler-enforced dependency rules
- Future migration readiness (Kafka / HTTP / Microservices)

---

# 2. High-Level Architecture

```text
                        +------------------+
                        |       APP        |
                        | Spring Boot Main |
                        +---------+--------+
                                  |
     -------------------------------------------------
     |                       |                       |
     ▼                       ▼                       ▼

+--------------+   +----------------+   +----------------+
|   Catalog    |   |  Enrollments   |   | Notifications  |
|    Domain    |   |     Domain     |   |     Domain     |
+--------------+   +----------------+   +----------------+

 catalog-api        enrollments-api      notifications-api
 catalog-core       enrollments-core     notifications-core
```

---

# 3. Maven Module Structure

```text
training-platform
│
├── shared-kernel
│
├── app
│
├── catalog-api
├── catalog-core
│
├── enrollments-api
├── enrollments-core
│
├── notifications-api
├── notifications-core
│
└── architecture-tests
```

---

# 4. Module Dependency Diagram

```text
shared-kernel
      ▲
      │
      │
      ├── catalog-api
      │      ▲
      │      │
      │      └── catalog-core
      │
      ├── enrollments-api
      │      ▲
      │      │
      │      └── enrollments-core
      │              │
      │              └────► catalog-api
      │
      ├── notifications-api
      │      ▲
      │      │
      │      └── notifications-core
      │                │
      │                └────► enrollments-api
      │
      └── app
```

---

# 5. Architectural Rules

## Allowed

```text
enrollments-core
      |
      ▼
catalog-api
```

✅ Allowed

```text
notifications-core
      |
      ▼
enrollments-api
```

✅ Allowed

---

## Forbidden

```text
enrollments-core
      |
      ▼
catalog-core
```

❌ Forbidden

```text
notifications-core
      |
      ▼
enrollments-core
```

❌ Forbidden

```text
catalog-core
      |
      ▼
enrollments-core
```

❌ Forbidden

---

# 6. Technology Stack

| Layer | Technology |
|---------|------------|
| Language | Java 21 |
| Framework | Spring Boot |
| Build Tool | Maven |
| ORM | Spring Data JPA |
| Database | H2 |
| Migration | Flyway |
| Architecture Tests | ArchUnit |
| Unit Testing | JUnit 5 |
| Mocking | Mockito |
| Eventing | Spring Application Events |

---

# 7. Package Structure

The project follows **Package by Subdomain**, not Package by Layer.

## Catalog

```text
catalog-core

└── course
     ├── CourseController
     ├── CourseService
     ├── CourseEntity
     ├── CourseRepository
     └── CourseStatus
```

## Enrollments

```text
enrollments-core

└── enrollment
     ├── EnrollmentController
     ├── EnrollmentService
     ├── EnrollmentEntity
     ├── EnrollmentRepository
     ├── CapacityValidator
     └── CoursePublishedHandler
```

## Notifications

```text
notifications-core

└── notification
     ├── NotificationEntity
     ├── NotificationRepository
     ├── NotificationService
     ├── EnrollmentCreatedHandler
     ├── EmailNotificationChannel
     └── SmsNotificationChannel
```

---

# 8. Domain Model

## Course

```text
Course
│
├── id
├── title
├── city
├── capacity
└── published
```

## Enrollment

```text
Enrollment
│
├── id
├── name
├── email
└── courseId
```

## Notification

```text
Notification
│
├── id
├── recipient
├── channel
└── message
```

---

# 9. Event-Driven Architecture

Domains communicate using domain events rather than direct service calls.

## Course Publication Flow

```text
CourseService
      |
      ▼
CoursePublishedEvent
      |
      ▼
DomainEventPublisher
      |
      ▼
InProcessDomainEventBus
      |
      ▼
CoursePublishedHandler
```

## Enrollment Creation Flow

```text
EnrollmentService
       |
       ▼
EnrollmentCreatedEvent
       |
       ▼
InProcessDomainEventBus
       |
       ▼
EnrollmentCreatedHandler
       |
       ▼
NotificationService
```

---

# 10. Sequence Diagram – Publish Course

```text
User
 |
 ▼
CourseController
 |
 ▼
CourseService
 |
 ▼
CourseEntity.publish()
 |
 ▼
Repository
 |
 ▼
CoursePublishedEvent
 |
 ▼
DomainEventPublisher
 |
 ▼
CoursePublishedHandler
```

---

# 11. Sequence Diagram – Create Enrollment

```text
User
 |
 ▼
EnrollmentController
 |
 ▼
EnrollmentService
 |
 ▼
CoursesApi
 |
 ▼
Catalog Domain
 |
 ▼
Capacity Validation
 |
 ▼
Enrollment Repository
 |
 ▼
EnrollmentCreatedEvent
 |
 ▼
Notification Domain
```

---

# 12. REST APIs

## Create Course

### Request

```http
POST /api/v1/courses
```

```json
{
  "title": "Enterprise Spring Boot",
  "capacity": 2,
  "city": "Ahmedabad"
}
```

### Response

```http
201 CREATED
```

---

## Publish Course

### Request

```http
POST /api/v1/courses/{courseId}/publish
```

### Response

```http
200 OK
```

---

## Get Course

### Request

```http
GET /api/v1/courses/{courseId}
```

### Response

```http
200 OK
```

---

## Create Enrollment

### Request

```http
POST /api/v1/enrollments
```

```json
{
  "name": "Sagar Koshti",
  "email": "sagar.koshti@example.com",
  "courseId": "course-uuid"
}
```

### Response

```http
201 CREATED
```

---

## List Enrollments

### Request

```http
GET /api/v1/enrollments?courseId={courseId}
```

### Response

```http
200 OK
```

---

# 13. Build & Run

## Execute All Tests

```bash
mvn clean test
```

## Start Application

```bash
mvn -pl app spring-boot:run
```

---

## Access H2 Console

```text
URL:
http://localhost:8080/h2-console
```

```text
JDBC URL:
jdbc:h2:mem:trainingdb
```

```text
Username:
sa
```

```text
Password:
(empty)
```

---

# 14. Testing Strategy

## Unit Tests

Covered:

- Course creation
- Course publication
- sts

```text
Create Course
     ▼
Publish Course
     ▼
Create Enrollment
     ▼
Create Notification
```

## Architecture Tests

ArchUnit verifies:

```text
-core modules
must never depend on
other -core modules
```

Example:

```java
noClasses()
    .that()
    .resideInAPackage("..enrollments..")
    .should()
    .dependOnClassesThat()
    .resideInAPackage("..catalog.internal..");
```

---

# 15. Key Architectural Decisions

## API/Core Split

### Decision

Separate contracts from implementations.

```text
catalog-api
catalog-core
```

### Benefit

Allows implementation replacement without impacting consumers.

---

## Domain Events

### Decision

Use `DomainEventPublisher`.

### Benefit

Loose coupling between domains.

---

## Package by Subdomain

### Decision

Keep controller, service, repository, and entity together.

### Benefit

Improved maintainability as domains grow.

---

## DTO Boundary

### Decision

JPA entities never cross module boundaries.

### Benefit

Preserves encapsulation.

---

# 16. Extensibility Probe

## Requirement

Add Notifications without modifying any source file inside:

```text
enrollments-core
```

## What Was Added

```text
notifications-api
notifications-core
```

## What Was NOT Modified

```text
enrollments-core
```

## Why?

Enrollment already publishes:

```java
EnrollmentCreatedEvent
```

Notification domain subscribes to the event.

```text
Enrollment
      |
      ▼
EnrollmentCreatedEvent
      |
      ▼
Notification Listener
```

## Open/Closed Principle

The system is:

```text
Open for Extension
Closed for Modification
```

New domains subscribe to events rather than modifying existing code.

---

# 17. Future Improvements

## Optimistic Locking

```java
@Version
```

to prevent overbooking.

---

## Transactional Outbox

Reliable event delivery.

---

## Kafka Integration

Replace:

```java
InProcessDomainEventBus
```

with:

```java
KafkaDomainEventBus
```

No domain code changes required.

---

## Pagination

```http
GET /enrollments?page=0&size=20
```

---

## OpenAPI / Swagger

Add API documentation.

---

# 18. Architectural Highlights

✅ Modular Monolith

✅ Maven Multi-Module

✅ API/Core Separation

✅ Package by Subdomain

✅ DTO Boundaries

✅ Domain Events

✅ Event Bus Abstraction

✅ Open/Closed Principle

✅ Extensible Notification Design

✅ ArchUnit Enforcement

✅ No Cross-Domain JPA Relationships

✅ Future Kafka Migration Ready

✅ Future Service Decomposition Ready

---

# Author Notes

This solution prioritizes architecture, maintainability, extensibility, and explicit module boundaries over framework convenience. The design follows the assignment's intent of demonstrating how a system can evolve safely as domains, teams, and deployment models grow.