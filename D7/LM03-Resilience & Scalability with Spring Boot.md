# Lab Manual: Resilience & Scalability with Spring Boot

**Duration:** 1 hour
**Level:** Beginner / Fresher
**Technology:** Java, Spring Boot, Maven, REST, Resilience4j
**Applications:** Student Service + Course Service

---

## 1. Lab Objectives

By the end of this lab, you will be able to:

* Create two Spring Boot microservices.
* Make one service call another service using REST.
* Understand what happens when a dependent service fails.
* Introduce an intentionally slow service.
* Configure a timeout.
* Implement retry.
* Implement a circuit breaker using Resilience4j.
* Implement a fallback response.
* Run multiple instances of a Spring Boot application.
* Understand the relationship between horizontal scaling, Docker, and Kubernetes.

---

# 2. Final Architecture

During the lab, you will gradually build this architecture:

```text
                 HTTP Request
                      |
                      v
             +------------------+
             | Student Service  |
             |      :8080       |
             +--------+---------+
                      |
                      | REST
                      v
             +------------------+
             |  Course Service  |
             |      :8081       |
             +------------------+
```

Later, you will introduce:

```text
Student Service
      |
      v
Circuit Breaker
      |
      v
Course Service
      |
      X
    FAIL
```

And finally:

```text
                 Load Balancer
                      |
          +-----------+-----------+
          |           |           |
          v           v           v
       Student     Student     Student
       Instance    Instance    Instance
        :8080       :8082       :8083
```

---

# 3. Prerequisites

Before starting, make sure you have:

```text
Java 17 or later
Maven
IntelliJ IDEA
curl
Internet connectivity
```

Verify Java:

```bash
java -version
```

Verify Maven:

```bash
mvn -version
```

Verify curl:

```bash
curl --version
```

---

# LAB 1 — Create the Course Service

## Objective

Create a simple Course Service that provides course information.

---

## Step 1 — Create Spring Boot Project

Open IntelliJ IDEA.

Create a new Spring Boot project.

Use:

```text
Name: course-service
Group: com.example
Artifact: course-service
Language: Java
Build Tool: Maven
Java: 17+
```

Add dependency:

```text
Spring Web
```

Create the project.

---

## Step 2 — Configure the Port

Open:

```text
src/main/resources/application.properties
```

Add:

```properties
spring.application.name=course-service
server.port=8081
```

Save the file.

---

## Step 3 — Create Course Controller

Create:

```text
src/main/java/com/example/course/controller/CourseController.java
```

Add:

```java
package com.example.course.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CourseController {

    @GetMapping("/courses/101")
    public String getCourse() {
        return "Spring Boot";
    }
}
```

---

## Step 4 — Run Course Service

Run the Spring Boot application from IntelliJ.

You should see something similar to:

```text
Tomcat started on port 8081
Started CourseServiceApplication
```

---

## Step 5 — Test the API

Open a terminal:

```bash
curl http://localhost:8081/courses/101
```

Expected:

```text
Spring Boot
```

### Checkpoint

You have successfully created:

```text
Course Service
      |
      +---- GET /courses/101
```

---

# LAB 2 — Create Student Service

## Objective

Create another Spring Boot application that calls Course Service.

---

## Step 1 — Create Project

Create another Spring Boot project.

Use:

```text
Name: student-service
Group: com.example
Artifact: student-service
Language: Java
Build Tool: Maven
Java: 17+
```

Add:

```text
Spring Web
```

---

## Step 2 — Configure Port

Open:

```text
src/main/resources/application.properties
```

Add:

```properties
spring.application.name=student-service
server.port=8080
```

---

# Step 3 — Create Student Controller

Create:

```text
src/main/java/com/example/student/controller/StudentController.java
```

Add:

```java
package com.example.student.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
public class StudentController {

    private final RestClient restClient;

    public StudentController(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("http://localhost:8081")
                .build();
    }

    @GetMapping("/students/101")
    public String getStudent() {

        String course = restClient
                .get()
                .uri("/courses/101")
                .retrieve()
                .body(String.class);

        return "Student 101 -> Course: " + course;
    }
}
```

---

## Step 4 — Start Both Applications

You should now have:

```text
Course Service
localhost:8081

Student Service
localhost:8080
```

---

## Step 5 — Test Course Service

```bash
curl http://localhost:8081/courses/101
```

Expected:

```text
Spring Boot
```

---

## Step 6 — Test Student Service

```bash
curl http://localhost:8080/students/101
```

Expected:

```text
Student 101 -> Course: Spring Boot
```

---

## What Just Happened?

The request flow is:

```text
curl
 |
 | GET /students/101
 v
Student Service
 |
 | GET /courses/101
 v
Course Service
 |
 | "Spring Boot"
 v
Student Service
 |
 v
Response
```

### Checkpoint

You have now created your first service-to-service communication.

---

# LAB 3 — Simulate Service Failure

## Objective

Understand what happens when a dependent service becomes unavailable.

---

## Step 1 — Stop Course Service

Stop the Course Service application.

Keep Student Service running.

---

## Step 2 — Call Student Service

Run:

```bash
curl http://localhost:8080/students/101
```

You should receive an error.

The exact error may vary depending on the Spring Boot version.

You may see a connection-refused or `ResourceAccessException` type of error.

---

## Step 3 — Understand the Problem

The architecture is now:

```text
Student Service
      |
      | HTTP
      X
Course Service
      X
     DOWN
```

Student Service depends on Course Service.

Therefore:

> A failure in one service can affect another service.

---

## Student Question

**What happens if Course Service is down?**

Write your answer:

```text
____________________________________________________

____________________________________________________
```

---

# LAB 4 — Simulate a Slow Service

## Objective

Understand why a timeout is required.

---

## Step 1 — Start Course Service Again

Start Course Service.

Verify:

```bash
curl http://localhost:8081/courses/101
```

Expected:

```text
Spring Boot
```

---

## Step 2 — Make Course Service Slow

Modify:

```text
CourseController.java
```

Change the method to:

```java
@GetMapping("/courses/101")
public String getCourse() throws InterruptedException {

    Thread.sleep(10000);

    return "Spring Boot";
}
```

This makes Course Service wait for:

```text
10 seconds
```

before responding.

---

## Step 3 — Test Course Service

Run:

```bash
time curl http://localhost:8081/courses/101
```

You should notice that the request takes approximately 10 seconds.

---

## Step 4 — Test Student Service

Run:

```bash
time curl http://localhost:8080/students/101
```

Student Service also waits for Course Service.

---

## Observe

```text
Student Service
       |
       | Request
       v
Course Service
       |
       | WAIT 10 seconds
       |
       v
Response
```

---

# LAB 5 — Configure Timeout

## Objective

Prevent Student Service from waiting too long.

We will configure:

```text
Connect Timeout = 2 seconds
Read Timeout    = 3 seconds
```

---

## Step 1 — Create Configuration Class

In Student Service create:

```text
src/main/java/com/example/student/config/RestClientConfig.java
```

Add:

```java
package com.example.student.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient restClient(RestClient.Builder builder) {

        SimpleClientHttpRequestFactory factory =
                new SimpleClientHttpRequestFactory();

        factory.setConnectTimeout(2000);
        factory.setReadTimeout(3000);

        return builder
                .requestFactory(factory)
                .baseUrl("http://localhost:8081")
                .build();
    }
}
```

---

## Step 2 — Update Student Controller

Replace the constructor:

```java
public StudentController(RestClient.Builder builder) {
    this.restClient = builder
            .baseUrl("http://localhost:8081")
            .build();
}
```

with:

```java
public StudentController(RestClient restClient) {
    this.restClient = restClient;
}
```

---

## Step 3 — Run Student Service

Restart Student Service.

---

## Step 4 — Test

Run:

```bash
time curl http://localhost:8080/students/101
```

Course Service still takes:

```text
10 seconds
```

But Student Service should stop waiting after approximately:

```text
3 seconds
```

---

## What Did We Achieve?

Previously:

```text
Student
   |
   |---------------- 10 seconds ----------------|
   |
```

Now:

```text
Student
   |
   |---- 3 seconds ----|
                       X
                    TIMEOUT
```

### Key Concept

> **A timeout prevents a service from waiting indefinitely for another service.**

---

# LAB 6 — Add Retry

## Objective

Handle temporary failures by trying the request again.

---

## Step 1 — Restore Course Service

For this lab, remove the artificial delay temporarily.

Change:

```java
@GetMapping("/courses/101")
public String getCourse() throws InterruptedException {

    Thread.sleep(10000);

    return "Spring Boot";
}
```

back to:

```java
@GetMapping("/courses/101")
public String getCourse() {
    return "Spring Boot";
}
```

Restart Course Service.

---

# Step 2 — Add Resilience4j

Open Student Service:

```text
pom.xml
```

Add:

```xml
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-spring-boot3</artifactId>
    <version>2.3.0</version>
</dependency>
```

Save the file.

Reload Maven.

---

# Step 3 — Configure Retry

Open:

```text
application.properties
```

Add:

```properties
resilience4j.retry.instances.courseService.max-attempts=3
resilience4j.retry.instances.courseService.wait-duration=1s
```

Meaning:

```text
Maximum attempts = 3
Wait between attempts = 1 second
```

---

# Step 4 — Add `@Retry`

Import:

```java
import io.github.resilience4j.retry.annotation.Retry;
```

Add the annotation:

```java
@Retry(name = "courseService")
@GetMapping("/students/101")
public String getStudent() {

    String course = restClient
            .get()
            .uri("/courses/101")
            .retrieve()
            .body(String.class);

    return "Student 101 -> Course: " + course;
}
```

---

# Step 5 — Simulate Failure

Stop Course Service.

Call:

```bash
curl http://localhost:8080/students/101
```

Conceptually, the flow is:

```text
Request
   |
   v
Course Service
   |
   X Failure
   |
 Retry #1
   |
   X Failure
   |
 Retry #2
   |
   X Failure
```

---

## Important

Retry does **not** mean:

> "Retry everything."

Retry may make sense for:

```text
Temporary network failure
Temporary service unavailable
Transient infrastructure failure
```

Retry may be dangerous for:

```text
Invalid request
Authentication failure
Authorization failure
Validation failure
```

Especially:

```text
POST /payments
```

A blind retry could potentially create a duplicate payment.

---

# LAB 7 — Circuit Breaker

## Objective

Prevent repeated calls to a service that is continuously failing.

This is the most important resilience exercise.

---

# Step 1 — Remove Retry Annotation

For this lab, remove:

```java
@Retry(name = "courseService")
```

We are focusing on the circuit breaker independently so that the behavior is easier to understand.

---

# Step 2 — Add Circuit Breaker

Import:

```java
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
```

Change the method to:

```java
@CircuitBreaker(
        name = "courseService",
        fallbackMethod = "courseFallback"
)
@GetMapping("/students/101")
public String getStudent() {

    String course = restClient
            .get()
            .uri("/courses/101")
            .retrieve()
            .body(String.class);

    return "Student 101 -> Course: " + course;
}
```

---

# Step 3 — Add Fallback

Add this method to the same controller:

```java
public String courseFallback(Exception ex) {

    return "Course Service is currently unavailable";
}
```

---

# Step 4 — Configure Circuit Breaker

Add to:

```text
application.properties
```

```properties
resilience4j.circuitbreaker.instances.courseService.sliding-window-size=5

resilience4j.circuitbreaker.instances.courseService.failure-rate-threshold=50

resilience4j.circuitbreaker.instances.courseService.wait-duration-in-open-state=10s
```

---

# Step 5 — Stop Course Service

Stop Course Service.

Student Service remains running.

---

# Step 6 — Call Student Service

Run:

```bash
curl http://localhost:8080/students/101
```

Expected:

```text
Course Service is currently unavailable
```

---

# Step 7 — Repeat the Request

Run several times:

```bash
curl http://localhost:8080/students/101
```

The circuit breaker observes failures.

Conceptually:

```text
             Course Service
                   |
                 FAIL
                   |
             Circuit Breaker
                   |
              failures
                   |
                   v
                OPEN
                   |
                   v
             Fast Failure
                   |
                   v
               Fallback
```

---

# LAB 8 — Understand Circuit Breaker States

The circuit breaker has three important states.

## CLOSED

Normal operation:

```text
Student
   |
   v
Circuit Breaker
   |
   v
Course
   |
   v
Success
```

---

## OPEN

Too many failures:

```text
Student
   |
   v
Circuit Breaker
   |
   v
OPEN
   |
   X
Do not call Course Service
```

The request fails quickly and the fallback can be returned.

---

## HALF-OPEN

After the configured waiting period:

```text
OPEN
  |
  | wait
  v
HALF-OPEN
  |
  | test request
  v
Course Service
```

If the service has recovered:

```text
HALF-OPEN
     |
   SUCCESS
     |
     v
  CLOSED
```

If it is still failing:

```text
HALF-OPEN
     |
   FAILURE
     |
     v
   OPEN
```

---

# LAB 9 — Understand the Difference

Complete the following table.

| Technique       | Problem it solves  |
| --------------- | ------------------ |
| Timeout         | __________________ |
| Retry           | __________________ |
| Circuit Breaker | __________________ |
| Fallback        | __________________ |

Expected answers:

| Technique       | Purpose                                   |
| --------------- | ----------------------------------------- |
| Timeout         | Prevent waiting too long                  |
| Retry           | Handle temporary failures                 |
| Circuit Breaker | Stop repeatedly calling a failing service |
| Fallback        | Provide controlled alternative response   |

---

# LAB 10 — Horizontal Scaling

## Objective

Run multiple instances of the same Spring Boot application.

This introduces the concept of horizontal scalability.

---

## Step 1 — Stop Student Service

Stop the Student Service currently running on port `8080`.

---

## Step 2 — Build the Application

From the Student Service project:

```bash
mvn clean package
```

You should get:

```text
target/student-service-....jar
```

---

## Step 3 — Run Instance 1

```bash
java -jar target/student-service-*.jar --server.port=8080
```

---

## Step 4 — Run Instance 2

Open another terminal:

```bash
java -jar target/student-service-*.jar --server.port=8082
```

---

## Step 5 — Run Instance 3

Open another terminal:

```bash
java -jar target/student-service-*.jar --server.port=8083
```

You now have:

```text
Student Service Instance 1
        :8080

Student Service Instance 2
        :8082

Student Service Instance 3
        :8083
```

---

# LAB 11 — Understand Horizontal Scaling

Draw:

```text
             Student Service
              Load Balancer
                   |
        +----------+----------+
        |          |          |
        v          v          v
      :8080      :8082      :8083
    Instance 1 Instance 2 Instance 3
```

Instead of:

```text
1 Server
4 CPU
8 GB RAM
```

we now have:

```text
Instance 1
Instance 2
Instance 3
```

This is:

> **Horizontal Scaling**

---

# LAB 12 — Compare Vertical and Horizontal Scaling

Complete the table.

|                     | Vertical | Horizontal |
| ------------------- | -------- | ---------- |
| Basic idea          |          |            |
| Example             |          |            |
| Number of instances |          |            |
| Main benefit        |          |            |

Expected:

|                     | Vertical                   | Horizontal                     |
| ------------------- | -------------------------- | ------------------------------ |
| Basic idea          | Make machine bigger        | Add more instances             |
| Example             | 4 CPU → 16 CPU             | 1 instance → 3 instances       |
| Number of instances | Usually same               | Increased                      |
| Main benefit        | More resources per machine | More capacity and availability |

---

# LAB 13 — Connect This to Docker

You already know Docker.

Think about:

```text
student-service
```

as one Docker image.

We can run:

```text
student-service image
       |
       +---- Container 1
       |
       +---- Container 2
       |
       +---- Container 3
```

Therefore:

```text
One application image
          ↓
Multiple containers
          ↓
Horizontal scaling
```

---

# LAB 14 — Connect This to Kubernetes

Kubernetes takes the same idea further.

Instead of manually running:

```text
Instance 1
Instance 2
Instance 3
```

we can define replicas.

Conceptually:

```yaml
spec:
  replicas: 3
```

Kubernetes can then maintain:

```text
Pod 1
Pod 2
Pod 3
```

And expose them through a Kubernetes Service:

```text
              Kubernetes Service
                      |
          +-----------+-----------+
          |           |           |
          v           v           v
        Pod 1       Pod 2       Pod 3
```

This is the connection:

```text
Spring Boot
     ↓
Docker
     ↓
Multiple Containers
     ↓
Kubernetes
     ↓
Multiple Pods
     ↓
Load Balancing
     ↓
Horizontal Scaling
```

---

# Final Challenge

## Scenario

You are building a Student Management System.

Architecture:

```text
Student Service
      ↓
Course Service
      ↓
Payment Service
```

Suddenly:

* Course Service becomes slow.
* Some requests fail.
* Traffic increases from 100 users to 10,000 users.

### Question 1

What prevents Student Service from waiting forever?

**Answer:**

```text
Timeout
```

### Question 2

What can handle a temporary failure?

**Answer:**

```text
Retry
```

### Question 3

What prevents continuous calls to a failing service?

**Answer:**

```text
Circuit Breaker
```

### Question 4

What can provide a controlled response when Course Service is unavailable?

**Answer:**

```text
Fallback
```

### Question 5

How can we handle increased traffic?

**Answer:**

```text
Horizontal Scaling
```

### Question 6

How would Kubernetes help?

**Answer:**

```text
Multiple Pods
+
Service Load Balancing
+
Replica Management
+
Scaling
```

---

# Final Architecture

At the end of the lab, students should understand this conceptual architecture:

```text
                         Users
                           |
                           v
                    Load Balancer
                           |
              +------------+------------+
              |            |            |
              v            v            v
           Student      Student      Student
           Instance     Instance     Instance
              |
              v
        +----------------+
        | Circuit Breaker|
        +-------+--------+
                |
             Timeout
                |
             Retry
                |
                v
        +----------------+
        | Course Service |
        +----------------+
                |
          ┌─────┴─────┐
          |           |
       Healthy      Failure
          |           |
          v           v
       Response    Fallback
```

---

# Student Checklist

Before leaving the lab, make sure you have completed:

* [ ] Created Course Service
* [ ] Created Student Service
* [ ] Called Course Service from Student Service
* [ ] Tested both services
* [ ] Stopped Course Service and observed failure
* [ ] Made Course Service slow
* [ ] Configured timeout
* [ ] Added Resilience4j
* [ ] Tested retry
* [ ] Added circuit breaker
* [ ] Added fallback
* [ ] Observed circuit breaker behavior
* [ ] Started multiple Student Service instances
* [ ] Understood vertical scaling
* [ ] Understood horizontal scaling
* [ ] Connected the concepts to Docker
* [ ] Connected the concepts to Kubernetes

## One-line takeaway

> **Timeout controls waiting, Retry handles temporary failures, Circuit Breaker protects against repeated failures, and Horizontal Scaling handles increasing traffic.**
