# Lab Manual: Data Management with Spring Boot, JPA & MySQL

## Module 5 — Data Management

### Duration
**45–50 minutes hands-on**

### Prerequisites

You should already have:

- Java installed
- IntelliJ IDEA
- Maven
- Spring Boot project
- MySQL running
- Existing Student Management application
- Basic knowledge of REST APIs

---

# Lab Objective

By the end of this lab, you will be able to:

1. Create a JPA Entity
2. Connect an Entity to MySQL
3. Create a Spring Data JPA Repository
4. Perform database CRUD operations
5. Use DTOs
6. Validate API input
7. Implement pagination
8. Implement sorting
9. Implement filtering
10. Understand entity relationships
11. Understand transactions

---

# Overall Application Flow

You will gradually build this architecture:

```text
Client
   |
   | HTTP Request
   ↓
Controller
   |
   ↓
DTO + Validation
   |
   ↓
Service
   |
   ↓
Repository
   |
   ↓
Spring Data JPA
   |
   ↓
Hibernate
   |
   ↓
MySQL
```

Do not try to build everything at once.

We will build it progressively.

---

# LAB 1 — Create the Student Entity

## Objective

Create a Java class that represents a database table.

---

## Step 1 — Create the package

Inside your Spring Boot project, create:

```text
src/main/java
    └── com.example.stumgmt
        └── entity
```

> Use your existing package name if it is different.

---

## Step 2 — Create Student.java

Create:

```text
Student.java
```

Inside the `entity` package.

Add:

```java
package com.example.stumgmt.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
```

---

## Step 3 — Understand the annotations

### `@Entity`

```java
@Entity
```

Tells JPA:

> This Java class should be persisted in the database.

Conceptually:

```text
Student.java
     ↓
student table
```

---

### `@Id`

```java
@Id
private Long id;
```

Identifies the primary key.

```text
student
---------------------
id   ← Primary Key
name
email
```

---

### `@GeneratedValue`

```java
@GeneratedValue(strategy = GenerationType.IDENTITY)
```

Allows the database to generate the ID.

For example:

```text
Student 1 → id = 1
Student 2 → id = 2
Student 3 → id = 3
```

---

# Step 4 — Configure MySQL

Open:

```text
src/main/resources/application.properties
```

Add or verify:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/stumgmt
spring.datasource.username=root
spring.datasource.password=root

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

Use the username, password and database name from your existing MySQL setup.

---

# Step 5 — Start the application

Run the application from IntelliJ.

You should see:

```text
Started StumgmtApplication
```

There should be no database connection error.

---

# Step 6 — Verify the database

Open MySQL and run:

```sql
SHOW DATABASES;
```

Then:

```sql
USE stumgmt;
```

Then:

```sql
SHOW TABLES;
```

You should see:

```text
student
```

Check:

```sql
DESC student;
```

You should see columns similar to:

```text
id
name
email
```

---

## Checkpoint 1

Before continuing, make sure you can explain:

> What is the purpose of `@Entity`?

Expected answer:

> It tells JPA that the Java class represents persistent database data.

---

# LAB 2 — Create the Student Repository

## Objective

Use Spring Data JPA to access the database without writing SQL for basic CRUD operations.

---

# Step 1 — Create repository package

Create:

```text
repository
```

Inside:

```text
StudentRepository.java
```

---

# Step 2 — Create the repository

```java
package com.example.stumgmt.repository;

import com.example.stumgmt.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository
        extends JpaRepository<Student, Long> {
}
```

---

# Step 3 — Understand the code

This:

```java
JpaRepository<Student, Long>
```

means:

```text
Student
   ↓
Entity being managed

Long
   ↓
Type of the primary key
```

Spring Data provides methods such as:

```java
save()
findAll()
findById()
deleteById()
count()
existsById()
```

---

# Step 4 — Important observation

You did NOT write:

```sql
INSERT INTO student ...
```

You did NOT write:

```sql
SELECT * FROM student
```

Spring Data JPA handles the basic database operations.

---

## Checkpoint 2

Answer:

> Which interface gives us ready-made CRUD methods?

Answer:

```text
JpaRepository
```

---

# LAB 3 — Create the Student POST API

## Objective

Save a Student into MySQL using the REST API.

---

# Step 1 — Create controller package

Create:

```text
controller
```

Create:

```text
StudentController.java
```

---

# Step 2 — Add the controller

```java
package com.example.stumgmt.controller;

import com.example.stumgmt.entity.Student;
import com.example.stumgmt.repository.StudentRepository;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentRepository repository;

    public StudentController(StudentRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public Student create(@RequestBody Student student) {
        return repository.save(student);
    }

    @GetMapping
    public List<Student> getAll() {
        return repository.findAll();
    }
}
```

---

# Step 3 — Start the application

Run:

```text
StumgmtApplication
```

---

# Step 4 — Test POST

Use Postman, IntelliJ HTTP Client or curl.

Request:

```http
POST http://localhost:8080/students
Content-Type: application/json
```

Body:

```json
{
    "name": "Rahul",
    "email": "rahul@example.com"
}
```

---

# Step 5 — Expected response

You should receive something similar to:

```json
{
    "id": 1,
    "name": "Rahul",
    "email": "rahul@example.com"
}
```

---

# Step 6 — Verify MySQL

Run:

```sql
SELECT * FROM student;
```

Expected:

```text
1 | Rahul | rahul@example.com
```

---

# LAB 4 — Retrieve Students

## Objective

Retrieve database records using GET.

The controller already contains:

```java
@GetMapping
public List<Student> getAll() {
    return repository.findAll();
}
```

---

# Step 1 — Send GET request

```http
GET http://localhost:8080/students
```

---

# Step 2 — Expected response

```json
[
    {
        "id": 1,
        "name": "Rahul",
        "email": "rahul@example.com"
    }
]
```

---

# Step 3 — Add more students

Create three more students.

Example:

```json
{
    "name": "Priya",
    "email": "priya@example.com"
}
```

```json
{
    "name": "Arun",
    "email": "arun@example.com"
}
```

```json
{
    "name": "Kiran",
    "email": "kiran@example.com"
}
```

Then:

```http
GET /students
```

You should now see multiple records.

---

# Checkpoint 3

Draw this flow:

```text
POST /students
      ↓
StudentController
      ↓
StudentRepository
      ↓
JpaRepository
      ↓
Hibernate
      ↓
MySQL
```

---

# LAB 5 — Introduce DTO

## Objective

Understand why the Entity should not normally be directly exposed as the API contract.

Currently we have:

```text
Entity
   ↓
API
```

We want:

```text
Entity
   ↓
Service
   ↓
DTO
   ↓
Controller
   ↓
API
```

---

# Step 1 — Create DTO package

Create:

```text
dto
```

Create:

```text
StudentRequest.java
```

Add:

```java
package com.example.stumgmt.dto;

public class StudentRequest {

    private String name;

    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
```

---

# Step 2 — Create StudentResponse

Create:

```text
StudentResponse.java
```

Add:

```java
package com.example.stumgmt.dto;

public class StudentResponse {

    private Long id;
    private String name;
    private String email;

    public StudentResponse(
            Long id,
            String name,
            String email) {

        this.id = id;
        this.name = name;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
```

---

# Why DTO?

Imagine your entity eventually contains:

```text
Student Entity

id
name
email
password
internalStatus
createdDate
updatedDate
```

You may NOT want to expose everything through the API.

Instead:

```text
StudentResponse

id
name
email
```

Therefore:

```text
Database Model ≠ API Model
```

This is the key concept.

---

# LAB 6 — Add Validation

## Objective

Prevent invalid data from entering the application.

---

# Step 1 — Check validation dependency

In `pom.xml`, add:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

Reload Maven.

---

# Step 2 — Modify StudentRequest

```java
package com.example.stumgmt.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class StudentRequest {

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
```

---

# Step 3 — Use `@Valid`

Your POST method should eventually accept:

```java
@Valid @RequestBody StudentRequest request
```

For example:

```java
@PostMapping
public StudentResponse create(
        @Valid @RequestBody StudentRequest request) {

    // save student

}
```

---

# Step 4 — Test valid data

```json
{
    "name": "Rahul",
    "email": "rahul@example.com"
}
```

This should pass.

---

# Step 5 — Test invalid data

Try:

```json
{
    "name": "",
    "email": "hello"
}
```

Expected:

```text
HTTP 400 Bad Request
```

---

# Understand the flow

```text
HTTP Request
     ↓
@Valid
     ↓
Validation
     ↓
Valid?
  /     \
No       Yes
↓         ↓
400      Service
          ↓
       Database
```

---

# LAB 7 — Pagination

## Objective

Avoid returning thousands of records in a single response.

Currently:

```http
GET /students
```

might return:

```text
10,000 students
```

Instead we want:

```http
GET /students?page=0&size=5
```

---

# Step 1 — Modify the GET endpoint

Add:

```java
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
```

Then:

```java
@GetMapping
public Page<Student> getStudents(Pageable pageable) {
    return repository.findAll(pageable);
}
```

---

# Step 2 — Test

```http
GET http://localhost:8080/students?page=0&size=5
```

The response will contain a page of results.

---

# Step 3 — Test the next page

```http
GET http://localhost:8080/students?page=1&size=5
```

This gives the next five records.

---

# Understand

```text
1000 records
      ↓
Page size = 10
      ↓
100 pages
```

Pagination is important when working with large datasets.

---

# LAB 8 — Sorting

## Objective

Sort database results.

Use:

```http
GET /students?page=0&size=10&sort=name
```

This sorts by name.

For descending order:

```http
GET /students?page=0&size=10&sort=name,desc
```

---

# Try these

### Sort ascending

```http
GET /students?sort=name,asc
```

### Sort descending

```http
GET /students?sort=name,desc
```

---

# Understand

```text
Pageable
   |
   +── page
   |
   +── size
   |
   └── sort
```

Spring Data handles the database query generation.

---

# LAB 9 — Filtering/Search

## Objective

Allow users to search for students.

---

# Step 1 — Add repository method

Modify:

```java
public interface StudentRepository
        extends JpaRepository<Student, Long> {

    List<Student> findByNameContainingIgnoreCase(String name);
}
```

---

# Step 2 — Add controller method

```java
@GetMapping("/search")
public List<Student> search(
        @RequestParam String name) {

    return repository.findByNameContainingIgnoreCase(name);
}
```

---

# Step 3 — Test

If the database contains:

```text
Rahul
Rahul Kumar
Priya
Arun
```

Run:

```http
GET /students/search?name=rah
```

Expected:

```text
Rahul
Rahul Kumar
```

---

# Important Concept

This method:

```java
findByNameContainingIgnoreCase()
```

is a **Spring Data derived query**.

Spring Data understands the method name and generates the required query.

---

# LAB 10 — Create Course Entity

## Objective

Understand relationships between entities.

We will implement one simple relationship:

```text
Many Students
       |
       |
       ↓
     Course
```

This is:

```text
@ManyToOne
```

---

# Step 1 — Create Course entity

Create:

```text
entity/Course.java
```

Add:

```java
package com.example.stumgmt.entity;

import jakarta.persistence.*;

@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```

---

# Step 2 — Add relationship to Student

Inside `Student`:

```java
@ManyToOne
@JoinColumn(name = "course_id")
private Course course;
```

Add:

```java
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
```

---

# Step 3 — Understand the database

The student table now conceptually becomes:

```text
student
--------------------------------
id
name
email
course_id
```

Example:

```text
Student
--------------------------------
1 | Rahul | rahul@example.com | 10
2 | Priya | priya@example.com | 10
3 | Arun  | arun@example.com  | 20
```

Course:

```text
course
----------------
10 | Java
20 | Spring Boot
```

Therefore:

```text
Rahul ─────┐
           ├──→ Java
Priya ─────┘

Arun ─────────→ Spring Boot
```

---

# Other Relationship Types

You do NOT need to implement these now.

Understand the concepts:

### One-to-One

```text
Student → Passport
```

One student has one passport.

---

### One-to-Many

```text
Course
  |
  ├── Student
  ├── Student
  └── Student
```

One course has many students.

---

### Many-to-One

```text
Student
Student
Student
   ↓
 Course
```

Many students belong to one course.

---

### Many-to-Many

```text
Student ←→ Subject
```

A student can have many subjects.

A subject can have many students.

---

# LAB 11 — Understand Transactions

## Objective

Understand how multiple database operations can be treated as one unit.

---

# Step 1 — Create Service package

Create:

```text
service
```

Create:

```text
StudentService.java
```

---

# Step 2 — Add service

Example:

```java
@Service
public class StudentService {

    private final StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Student register(Student student) {

        Student saved = repository.save(student);

        // Other database operation

        return saved;
    }
}
```

Import:

```java
import org.springframework.transaction.annotation.Transactional;
```

---

# Step 3 — Understand the transaction

Suppose we perform:

```text
Operation 1 → Save Student
Operation 2 → Assign Course
Operation 3 → Create Audit Record
```

Without proper transaction handling, one operation could succeed while another fails.

With:

```java
@Transactional
```

we conceptually get:

```text
START TRANSACTION

Save Student       ✓
Assign Course      ✓
Create Audit       ✗

ROLLBACK
```

The database returns to the previous consistent state.

---

# Key Concept

Remember:

> A transaction groups related database operations into one unit of work.

---

# LAB 12 — Final Application Architecture

At this point, your application should conceptually look like:

```text
                         CLIENT
                           |
                           | HTTP
                           ↓
                    ┌──────────────┐
                    │  Controller  │
                    └──────┬───────┘
                           |
                    DTO + Validation
                           |
                           ↓
                    ┌──────────────┐
                    │   Service    │
                    └──────┬───────┘
                           |
                     @Transactional
                           |
                           ↓
                    ┌──────────────┐
                    │  Repository  │
                    └──────┬───────┘
                           |
                    Spring Data JPA
                           |
                       Hibernate
                           |
                           ↓
                        MySQL
```

---

# Final Verification

Run the application.

Perform the following tests.

## Test 1 — Create Student

```http
POST /students
```

```json
{
    "name": "Vishal",
    "email": "vishal@example.com"
}
```

Expected:

```text
Student created
```

---

## Test 2 — Get Students

```http
GET /students
```

Expected:

```text
Student list
```

---

## Test 3 — Pagination

```http
GET /students?page=0&size=5
```

Expected:

```text
Maximum 5 records
```

---

## Test 4 — Sorting

```http
GET /students?sort=name,asc
```

Expected:

```text
Students sorted by name
```

---

## Test 5 — Search

```http
GET /students/search?name=rah
```

Expected:

```text
Matching students
```

---

## Test 6 — Validation

Send:

```json
{
    "name": "",
    "email": "wrong"
}
```

Expected:

```text
HTTP 400 Bad Request
```

---

# Final Student Challenge

Try to implement:

```text
GET /students/{id}
```

The endpoint should:

1. Accept a student ID
2. Search the repository
3. Return the student if found
4. Return an appropriate response if the student does not exist

Hint:

```java
repository.findById(id)
```

---

# What You Should Be Able to Explain

At the end of this lab, you should be able to answer these questions.

### 1. What does `@Entity` do?

It marks a Java class as a persistent entity.

### 2. What does `JpaRepository` provide?

Ready-made database operations such as save, find, update and delete.

### 3. Why do we use DTOs?

To separate the API model from the database entity and control what the API exposes.

### 4. What does `@Valid` do?

It triggers validation of incoming request data.

### 5. Why use pagination?

To avoid returning huge amounts of data in one response.

### 6. What is `@ManyToOne`?

Many records of one entity can be associated with one record of another entity.

### 7. What does `@Transactional` mean?

Related database operations are treated as one transaction and can be rolled back if the transaction fails.

---

# Final Takeaway

The most important thing to remember is not the annotations.

Remember the flow:

```text
API Request
     ↓
DTO
     ↓
Validation
     ↓
Controller
     ↓
Service
     ↓
Transaction
     ↓
Repository
     ↓
JPA / Hibernate
     ↓
MySQL
```

And remember:

```text
Entity      → Database representation
Repository  → Database access
DTO         → API representation
Validation  → Protect input
Transaction → Maintain consistency
Pagination  → Handle large data
Relationship→ Connect data
```

## Advanced Topics — Learn Later

You have only been introduced to the fundamentals.

Later, you should learn:

```text
Optimistic Locking
N+1 Query Problem
Lazy vs Eager Loading
JPQL
Native Queries
Specifications
Auditing
Database Indexing
Transaction Propagation
Isolation Levels
```

Do not worry about these during this lab.