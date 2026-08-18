# Phase 8 — How Swagger Was Integrated (Technical Detail)

Swagger/OpenAPI was added to all three microservices independently — `student-service`, `course-service`, `enrollment-service` each generate and serve their own spec and UI. There is no shared Swagger configuration between them; each service's integration is three small, self-contained additions.

---

## 1. The dependency

**Files:** `student-service/pom.xml`, `course-service/pom.xml`, `enrollment-service/pom.xml` (identical addition in all three)

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.6.0</version>
</dependency>
```

**What this one dependency does, automatically, with zero further code:**

- Scans every `@RestController` bean in the Spring context at startup
- Introspects each `@RequestMapping`/`@GetMapping`/`@PostMapping`/etc. method: HTTP method, path, path variables, query parameters, request body type, response type
- Introspects every DTO class reachable from those methods (via Jackson's own field/getter conventions) to build JSON Schema definitions
- Reads `jakarta.validation` annotations (`@NotBlank`, `@Email`, `@Min`, `@NotNull`) off request DTOs and reflects them into the schema as `required` fields and constraints
- Exposes the resulting document at `GET /v3/api-docs` (JSON, OpenAPI 3.0 format)
- Serves a bundled Swagger UI single-page app at `GET /swagger-ui/index.html`, pre-configured to load the spec from `/v3/api-docs`

None of this required a controller, a configuration class, or a route to be written by hand — it is auto-configuration in the same sense Spring Boot's DataSource or embedded Tomcat setup is: present because the dependency is on the classpath, wired together by conventions the framework already recognizes (the existing `@RestController` beans).

## 2. The one piece of manual configuration: `OpenApiConfig`

**Files:** `*/src/main/java/com/example/*service/config/OpenApiConfig.java` (one per service)

```java
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI studentServiceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Student Service API")
                        .description("Owns student records: registration, lookup, search, and status " +
                                "(ACTIVE / INACTIVE / GRADUATED). Part of the Student Management System " +
                                "microservices - see also course-service and enrollment-service.")
                        .version("v1"));
    }
}
```

**Why this bean, specifically:** everything else springdoc generates is derived automatically from the controllers and DTOs. The one thing it *can't* infer is the human-facing metadata — what to call this API, what it's for, what version it is. Supplying an `OpenAPI` bean with an `Info` object is springdoc's documented hook for that: at startup, springdoc looks for a bean of type `OpenAPI` in the context and merges it with what it generates from scanning, rather than requiring a giant hand-written document to override generation entirely.

Each service's `description` explicitly names the other two services and each service's scope boundary — an intentional choice, since anyone landing on `course-service`'s Swagger UI without other context should immediately understand this is one third of a larger system, not the whole API.

## 3. Grouping and documenting individual endpoints

**Files:** each `*Controller.java`

Two annotations, both from `io.swagger.v3.oas.annotations` (transitively provided by the springdoc starter — no separate dependency needed):

```java
@RestController
@RequestMapping("/api/students")
@Tag(name = "Students", description = "Registration, lookup, search, and status transitions")
public class StudentController {
    ...
    @Operation(summary = "Change a student's status",
            description = "Valid transitions: ACTIVE <-> INACTIVE, either <-> GRADUATED. " +
                    "GRADUATED is terminal - once set, no further transitions are allowed " +
                    "and this endpoint returns 409 Conflict.")
    @PatchMapping("/{id}/status")
    public StudentResponse changeStatus(...) { ... }
}
```

- **`@Tag`** on the class groups every endpoint in that controller under one named, expandable section in the UI (visible as the "Students" / "Courses" / "Enrollments" headers). Without it, springdoc still generates a group — it just names it after the controller's Java class name, which is a worse reading experience than a deliberately chosen name and description.
- **`@Operation`** on individual methods overrides the auto-generated summary/description for that one endpoint. Applied selectively here — only to `PATCH /api/students/{id}/status` and `POST /api/enrollments`, the two endpoints with a business rule (status transition validity; capacity + duplicate + existence checks) that isn't obvious from the method signature alone. Endpoints whose behavior is self-evident from their name and path (`GET /api/students/{id}`, `DELETE /api/students/{id}`) were left with springdoc's defaults rather than adding `@Operation` annotations that would just restate the obvious.

## 4. What required no changes at all

Worth being explicit about, since it's easy to assume more had to be built:

- **No custom `/v3/api-docs` route** — springdoc registers this itself.
- **No custom Swagger UI assets** — the `-ui` artifact in `springdoc-openapi-starter-webmvc-ui` bundles the entire Swagger UI static app inside the JAR; it's served, not built.
- **No annotations needed on DTOs for basic field documentation** — field names, types, and whether a field is required (derived from `@NotBlank`/`@NotNull`/etc.) all come from reflection over the existing `StudentRequest`, `CourseRequest`, `StudentStatusRequest` classes untouched.
- **No changes to the `GlobalExceptionHandler` classes** — springdoc documents the *shape* of successful responses; it does not enumerate every possible error status a caller might see. `ErrorResponse` still appears in the schema list only because it's directly referenced as a return type; the exception-handling behavior itself doesn't change.

## 5. Why per-service Swagger, not one combined UI

Each service publishing its own `/v3/api-docs` and `/swagger-ui` was a deliberate continuation of Phase 6/7's decision to keep the three services independently deployable with zero shared code:

- A single combined Swagger UI would require either (a) one service acting as an aggregator, fetching and merging the other two specs at runtime — new code, a new failure mode, and a new reason for `enrollment-service` to depend on knowing the other two exist beyond its already-explicit `StudentClient`/`CourseClient`; or (b) a separate, fourth "docs" service — infrastructure this project doesn't otherwise need.
- Each service already needs to be independently runnable and independently correct (Phase 7's whole premise). Independently-documented is the same property applied one layer up.
- The frontend's nginx (`Phase-8/frontend/nginx.conf`) deliberately does **not** proxy `/swagger-ui/*` or `/v3/api-docs` — Swagger is a development/testing surface, reached directly on each service's own port (`8081`/`8082`/`8083`), not something end users of the product would ever see through the `:8080` origin.

## 6. Verified

```bash
$ curl -o /dev/null -w "%{http_code}\n" http://localhost:8081/swagger-ui/index.html
200
$ curl -o /dev/null -w "%{http_code}\n" http://localhost:8082/swagger-ui/index.html
200
$ curl -o /dev/null -w "%{http_code}\n" http://localhost:8083/swagger-ui/index.html
200

$ curl -s http://localhost:8081/v3/api-docs | python3 -c "import json,sys; print(json.load(sys.stdin)['info']['title'])"
Student Service API
$ curl -s http://localhost:8082/v3/api-docs | python3 -c "import json,sys; print(json.load(sys.stdin)['info']['title'])"
Course Service API
$ curl -s http://localhost:8083/v3/api-docs | python3 -c "import json,sys; print(json.load(sys.stdin)['info']['title'])"
Enrollment Service API
```

All three services' full test suites (`mvn test`) were also re-run after adding springdoc, since `@WebMvcTest` and `@SpringBootTest` both load the full Spring context, including any new auto-configuration — confirming Swagger's presence doesn't interfere with the `@RestControllerAdvice`-based error handling, validation, or any existing endpoint behavior. All 47 tests across the three services still pass.

See [`Swagger-Usage-Guide.md`](./Swagger-Usage-Guide.md) for how to actually use the running UI.
