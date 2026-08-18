# Phase 7 — Database Schema and Reference-Value Changes

This document is a focused deep-dive on exactly two things Phase 7 changed: the **database schema** (what tables exist, where, and what constraints they have), and the **application-side handling of "reference values"** — the ids one service holds that point at data another service owns. [`CHANGES.md`](./CHANGES.md) covers the phase broadly; this document is the detail underneath the "database" half of that story.

---

## Part 1 — Database schema

### 1.1 Before: one database, three tables, real foreign keys

Through Phase 6, all three services pointed at the same PostgreSQL instance and the same database, `student_db`:

```
student_db  (one PostgreSQL container)
├── student
│     id          bigint   PK
│     name        varchar
│     email       varchar
│
├── course
│     id          bigint   PK
│     title       varchar
│
└── enrollment
      id          bigint   PK
      student_id  bigint   FK → student.id
      course_id   bigint   FK → course.id
```

Because `enrollment.student_id` and `enrollment.course_id` were real foreign keys *inside the same database*, PostgreSQL itself guaranteed you could never insert an enrollment row pointing at a student or course id that didn't exist — that was free, structural, database-enforced integrity. Deleting a referenced student or course would fail (or cascade, depending on the constraint) automatically, no application code involved. This is the constraint discovered in Phase 5 as `DataIntegrityViolationException` on `DELETE /students/{id}` for an enrolled student.

`enrollment-service`'s Phase 6 `StudentRef`/`CourseRef` entities worked *only because* this was still true — they were read-only JPA mappings onto `student` and `course`, tables that physically lived in the same database `enrollment-service` was already connected to.

### 1.2 After: three databases, three schemas, no cross-database constraints

```
student-db container          course-db container         enrollment-db container
  (PostgreSQL, its own volume)  (PostgreSQL, its own volume) (PostgreSQL, its own volume)

student_db                     course_db                    enrollment_db
└── student                    └── course                   └── enrollment
      id                             id                            id
      name                           title                         student_id   bigint  (plain column)
      email                                                        course_id    bigint  (plain column)
```

Confirmed directly against the running containers:

```bash
$ docker compose exec student-db psql -U stumgmt -d student_db -c "\dt"
 public | student | table | stumgmt

$ docker compose exec course-db psql -U stumgmt -d course_db -c "\dt"
 public | course | table | stumgmt

$ docker compose exec enrollment-db psql -U stumgmt -d enrollment_db -c "\dt"
 public | enrollment | table | stumgmt
```

Each database has exactly one table. `enrollment.student_id` and `enrollment.course_id` are **plain `bigint` columns with no foreign key constraint at all** — PostgreSQL in `enrollment-db` has never heard of a `student` or `course` table; it *cannot* enforce a relationship to something that, from its point of view, doesn't exist.

### 1.3 What this means concretely

| Guarantee | Before (Phase 6) | After (Phase 7) |
|---|---|---|
| Can you insert an enrollment for a nonexistent student id? | No — PostgreSQL rejects it (FK violation) | **Yes, at the database level** — nothing stops it. It's rejected only because `EnrollmentService` checks first, in application code (see Part 2). |
| What enforces "a course can't be deleted while students are enrolled in it"? | The FK constraint itself | Nothing, currently — there's no course-deletion endpoint yet, but if one existed, this integrity check would need to be rebuilt in application code (or accepted as a known gap) |
| Can `student-db` and `enrollment-db` ever be restored independently, at different points in time, and stay internally consistent? | N/A — one database, one backup | Yes for each database *individually*, but a restore of `enrollment-db` to an older snapshot than `student-db` could leave `enrollment` rows pointing at student ids that have since changed or been deleted |
| Schema migration blast radius | A migration touching `student` could, in principle, affect anything else in the same database | A migration to `student`'s schema can only ever affect `student-service` — `course-db` and `enrollment-db` are physically incapable of seeing it |

The last row is the actual payoff of this phase: schema changes are now genuinely isolated per service. The first three rows are the cost — referential integrity that used to be free is now something the application has to earn, and only partially can.

### 1.4 Docker Compose — the schema split, expressed as infrastructure

```yaml
student-db:
  image: postgres:16-alpine
  environment: { POSTGRES_DB: student_db }
  volumes: [student-db-data:/var/lib/postgresql/data]

course-db:
  image: postgres:16-alpine
  environment: { POSTGRES_DB: course_db }
  volumes: [course-db-data:/var/lib/postgresql/data]

enrollment-db:
  image: postgres:16-alpine
  environment: { POSTGRES_DB: enrollment_db }
  volumes: [enrollment-db-data:/var/lib/postgresql/data]
```

Three separate Postgres **containers** (not three databases inside one Postgres server) — each with its own process, its own port (internally 5432, never exposed to the host), and critically, its own **volume**. `docker compose down -v` on this stack removes three independent volumes; there's no single "the database" to back up or restore anymore, there are three.

Each app service's datasource now points only at its own container:

```yaml
student-service:     { DB_HOST: student-db,     DB_NAME: student_db }
course-service:       { DB_HOST: course-db,       DB_NAME: course_db }
enrollment-service:  { DB_HOST: enrollment-db,  DB_NAME: enrollment_db }
```

No service's `application.yml` references a database host it doesn't own.

---

## Part 2 — Reference values on the application side

"Reference value" here means: an id one service stores that identifies a row owned by a *different* service — `enrollment.student_id` pointing at a row in `student-service`'s world, `enrollment.course_id` pointing at a row in `course-service`'s world. This is the part of the design that had to change the most, because the database could no longer help with it at all.

### 2.1 Before (Phase 6): reference values resolved by reading the other service's table directly

```java
@Entity
@Table(name = "student")   // same physical table student-service writes to
@Immutable
public class StudentRef {
    @Id private Long id;
    private String name;
}
```

`enrollment-service` had its own `StudentRefRepository extends JpaRepository<StudentRef, Long>`, and resolved a reference value with a plain JPA call:

```java
StudentRef student = studentRefRepository.findById(studentId)
        .orElseThrow(() -> new StudentNotFoundException(studentId));
```

This was fast (one SQL query, same database, no network hop) but only worked because of the shared database. It's also why it was explicitly flagged as **temporary** in the Phase 6 write-up.

### 2.2 After (Phase 7): reference values resolved by calling the owning service's API

`StudentRef`/`CourseRef`/`StudentRefRepository`/`CourseRefRepository` are **deleted** — not deprecated, not kept as a fallback, deleted. In their place:

**A local, minimal DTO describing what enrollment-service needs to know:**
```java
public class StudentDto {
    private Long id;
    private String name;
    // no email - enrollment-service has no use for it
}
```

**An HTTP client that resolves the reference value over the network:**
```java
@Component
public class StudentClient {

    private final RestClient restClient;

    public StudentClient(@Qualifier("studentServiceClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public Optional<StudentDto> findById(Long id) {
        try {
            StudentDto student = restClient.get()
                    .uri("/api/students/{id}", id)
                    .retrieve()
                    .body(StudentDto.class);
            return Optional.ofNullable(student);
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode().value() == 404) return Optional.empty();
            throw ex;
        }
    }
}
```

**Resolved the same way in `EnrollmentService`, just through the client instead of the ref-repository:**
```java
StudentDto student = studentClient.findById(studentId)
        .orElseThrow(() -> new StudentNotFoundException(studentId));
```

### 2.3 Side-by-side: what actually changed at the call site

| | Phase 6 (`StudentRef`) | Phase 7 (`StudentClient`) |
|---|---|---|
| Mechanism | JPA `findById` against a mapped entity | HTTP `GET /api/students/{id}` via `RestClient` |
| What backs it | A read-only view of `student-service`'s own table | `student-service`'s actual REST API, running as its own process |
| Network involved | No — same JVM's connection pool, same database | Yes — TCP/HTTP round trip, resolved via Docker's internal DNS (`http://student-service:8080`) |
| Failure modes | SQL error, connection pool exhaustion | All of the above **plus**: `student-service` down, slow to respond, DNS resolution failure, timeout |
| "Not found" representation | Empty `Optional` from `findById` | Empty `Optional`, translated from a `404` HTTP status by `StudentClient` |
| Data available | Whatever columns `StudentRef` mapped (id, name) | Whatever `StudentResponse` (student-service's own DTO) puts in its JSON — currently id, name, email; `StudentDto` only reads id and name out of it |
| Coupling | Table-name coupling (`@Table(name = "student")` — silently breaks if student-service renames its table) | Contract coupling (`StudentDto` fields — silently breaks if student-service renames a JSON field, but is fine with column/table renames on student-service's side) |

The last row is worth sitting with: Phase 7 didn't remove coupling between `enrollment-service` and the other two — it **moved** it, from the database schema to the HTTP/JSON contract. That's a real improvement (an HTTP API is a much more deliberately-versioned, intentionally-stable surface than a database table was ever meant to be), but it is not the elimination of coupling, and shouldn't be described that way.

### 2.4 Where the base URLs for reference-value resolution live

```yaml
# enrollment-service/src/main/resources/application.yml
services:
  student-url: ${STUDENT_SERVICE_URL:http://localhost:8081}
  course-url: ${COURSE_SERVICE_URL:http://localhost:8082}
```

```yaml
# docker-compose.yml, enrollment-service environment:
STUDENT_SERVICE_URL: http://student-service:8080
COURSE_SERVICE_URL: http://course-service:8080
```

Locally (each service run individually via `mvn spring-boot:run`), the defaults point at `localhost:8081`/`8082`, matching how the three services are conventionally run side-by-side on one machine. Inside Docker Compose, the environment variables override those defaults to the internal service names (`http://student-service:8080` — note port `8080`, the *container's internal* port, not the `8081` host-mapped port used from outside Docker). Getting this pair of values wrong in one direction (using the host-mapped port inside Compose, or the internal port outside it) is the most common way this specific setup breaks — worth calling out explicitly if debugging a "connection refused" from `enrollment-service`.

---

## Verified after reseeding

```bash
$ curl -X POST localhost:8081/api/students -d '{"name":"Aarav Sharma","email":"aarav.sharma@example.com"}'
{"id":2,"name":"Aarav Sharma","email":"aarav.sharma@example.com"}

$ curl -X POST localhost:8083/api/enrollments?studentId=2&courseId=2
{"id":...,"studentId":2,"studentName":"Aarav Sharma","courseId":2,"courseTitle":"Java Fundamentals"}
```

`studentName` above came from a live HTTP call `enrollment-service` made to `student-service` at the moment of enrollment — `enrollment-db` itself has no column, table, or any other way of knowing a student's name.

Current seeded state: 10 students, 7 courses, 22 enrollments, spread so that most students have 1–2 enrollments and one (Amara Okafor, id 7) is enrolled in all 7 courses, useful for exercising both `GET /api/enrollments/student/{id}` and `GET /api/enrollments/course/{id}` with non-trivial result sets.
