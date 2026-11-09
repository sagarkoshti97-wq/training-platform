# Training Platform — Modular Monolith Exercise

## 1. How to Build and Run

Run all unit, integration, and ArchUnit boundary tests:
```bash
mvn clean test
```

Start the application on port `8080`:
```bash
mvn -pl app spring-boot:run
```

Access the H2 In-Memory Database Console (while the app is running):
- **URL**: `http://localhost:8080/h2-console`
- **JDBC URL**: `jdbc:h2:mem:trainingdb`
- **User Name**: `sa`
- **Password**: *(leave blank)*

---

## 2. Module Dependency Structure

```text
common (Pure JDK: DomainEventPublisher, DomainEventHandler)
  ▲
  ├── catalog-api (Pure JDK: CourseDto, CoursePublishedEvent, CoursesApi)
  │     ▲
  │     ├── catalog-core (CourseController, CourseService, CourseEntity, V1 migration)
  │     └── enrollments-core (EnrollmentController, EnrollmentService, CoursePublishedHandler, V2 migration)
  │
  ├── enrollments-api (Pure JDK: EnrollmentDto, EnrollmentCreatedEvent)
  │     ▲
  │     ├── enrollments-core
  │     └── notifications-core (EnrollmentCreatedHandler, NotificationService, NotificationChannel, V3 migration)
  │
  └── notifications-api (Pure JDK: NotificationDto)
        ▲
        └── notifications-core

app (@SpringBootApplication, InProcessDomainEventBus, ArchUnit & Integration Tests)
```

### Enforced Dependency Rules
- `<domain>-api` modules depend only on `common` and the JDK (zero Spring or JPA dependencies).
- `<domain>-core` modules may depend on another domain's `-api`, never on another `-core`.
- JPA entities and repositories are package-private inside their subdomain packages (`course`, `enrollment`, `notification`) and do not cross module boundaries.

---

## 3. REST API Endpoints

| Domain | Method | Endpoint | Description |
| :--- | :--- | :--- | :--- |
| **Catalog** | `POST` | `/api/v1/courses` | Create a new draft course (`title`, `capacity`, `city`) |
| **Catalog** | `POST` | `/api/v1/courses/{id}/publish` | Publish a course and emit `CoursePublishedEvent` |
| **Catalog** | `GET` | `/api/v1/courses/{id}` | Retrieve a course by ID |
| **Enrollments** | `POST` | `/api/v1/enrollments` | Enroll a visitor (`name`, `email`, `courseId`) and emit `EnrollmentCreatedEvent` |
| **Enrollments** | `GET` | `/api/v1/enrollments?courseId={id}` | List enrollments for a course |

---

## 4. Architectural Decisions & Trade-offs

1. **Compiler-Enforced Maven `-api`/`-core` Split + ArchUnit vs. Single Module with Package Conventions**
   - *Decision*: Separated each domain into `<domain>-api` (pure JDK records/interfaces) and `<domain>-core` (Spring/JPA internals) enforced by Maven dependencies and ArchUnit (`ArchitectureTest`).
   - *Alternative Rejected*: Single Maven module relying on package naming conventions, where developers can accidentally auto-import internal entities or repositories across domains. Separate Maven modules turn illegal cross-domain imports into compile-time errors.

2. **Pure-JDK `DomainEventPublisher` / `DomainEventHandler<T>` Seam vs. Direct Spring `@EventListener`**
   - *Decision*: Defined `DomainEventPublisher` and `DomainEventHandler<T>` in `common` and wired `InProcessDomainEventBus` in `app` using the Java Stream API.
   - *Alternative Rejected*: Injecting Spring's `ApplicationEventPublisher` and using `@EventListener` directly inside domain services. Keeping the seam pure-JDK keeps domain contracts infrastructure-agnostic and allows swapping to Kafka or RabbitMQ by modifying `InProcessDomainEventBus` in `app`.

3. **Synchronous Cross-Domain Check via `CoursesApi` Interface vs. Shared Database Join**
   - *Decision*: `enrollments-core` checks course existence, publication state, and capacity through the `CoursesApi` interface in `catalog-api` without database-level foreign keys across domains.
   - *Alternative Rejected*: Querying the `courses` table directly from `enrollments-core` or adding a JPA `@ManyToOne` relationship. Using `CoursesApi` allows `catalog` to be extracted behind an HTTP client (`RestClient`) later without changing calling code in `enrollments-core`.

4. **Package-Private JPA Entities in Subdomain Packages vs. Public Entities in Layer Packages**
   - *Decision*: Grouped controller, service, entity, and repository together in subdomain packages (`com.training.catalog.course`, `com.training.enrollments.enrollment`, `com.training.notifications.notification`) and marked `*Entity` and `*Repository` package-private.
   - *Alternative Rejected*: Top-level `controllers/`, `services/`, and `repositories/` layer buckets, which require `public` visibility on entities and repositories and scatter domain rules across folders.

5. **Strategy Pattern (`List<NotificationChannel>`) for Notification Delivery vs. Conditional Dispatch**
   - *Decision*: Injected `List<NotificationChannel>` into `NotificationService` and used the Java Stream API to dispatch messages across all registered channels.
   - *Alternative Rejected*: `if/else` or `switch` statements inside `NotificationService`. With Spring collection injection, adding an `SmsNotificationChannel` later requires adding one new `@Component` class without modifying existing classes.

---

## 5. Extensibility Probe: Adding the `notifications` Domain

- **What was touched**: Created the `notifications-api` and `notifications-core` modules, and registered them in the root `pom.xml` and `app/pom.xml`. **No source files in `enrollments-core` were modified.**
- **Why the list is so short**: `enrollments-core` already published `EnrollmentCreatedEvent` (defined in `enrollments-api`) via the `DomainEventPublisher` abstraction. Because `InProcessDomainEventBus` dynamically discovers all `DomainEventHandler<?>` beans on startup via the Java Stream API, adding `notifications-core` to `app/pom.xml` automatically subscribed `EnrollmentCreatedHandler` to `EnrollmentCreatedEvent`.

---

## 6. Deliberately Skipped Due to Timebox

- Optimistic/pessimistic locking on concurrent course enrollments (`SELECT ... FOR UPDATE` or `@Version`) to prevent race conditions under high concurrency.
- Transactional Outbox pattern for resilient event delivery across process crashes.
- Bean Validation (`@Valid`) annotations and custom `@ControllerAdvice` JSON error formatting.
- Pagination on `GET /api/v1/enrollments`.