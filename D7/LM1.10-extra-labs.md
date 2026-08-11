
---

# Lab 9 — Combine Pagination + Filtering + Sorting

## Objective

Build a realistic API.

---

# Target API

```http
GET /students
    ?course=Java
    &sortBy=name
    &direction=asc
    &page=0
    &size=10
```

---

# Step 1 — Identify Each Parameter

```text
course
    ↓
Filtering

sortBy + direction
    ↓
Sorting

page + size
    ↓
Pagination
```

---

# Step 2 — Implement the Flow

```text
HTTP Request
     ↓
Controller
     ↓
Read query parameters
     ↓
Service
     ↓
Build Pageable
     ↓
Repository
     ↓
Database
     ↓
Filtered + Sorted + Paginated result
```

---

# Step 3 — Test Multiple Requests

### Request 1

```http
GET /students
```

### Request 2

```http
GET /students?course=Java
```

### Request 3

```http
GET /students?sortBy=name&direction=asc
```

### Request 4

```http
GET /students?page=0&size=10
```

### Request 5

```http
GET /students?course=Java&sortBy=name&direction=asc&page=0&size=10
```

---

# Lab 9 Challenge

Create a table in your lab notebook:

| Request                   | Filter | Sort | Page | Size |
| ------------------------- | ------ | ---- | ---- | ---- |
| `/students`               | None   | None | 0    | 20   |
| `/students?course=Java`   | Java   | None | 0    | 20   |
| `/students?sortBy=name`   | None   | name | 0    | 20   |
| `/students?page=1&size=5` | None   | None | 1    | 5    |

---

# Lab 10 — API Design Review

## Objective

Learn to identify bad API designs.

---

# Exercise 1

You receive:

```http
GET /getAllStudents
```

Redesign it.

Expected:

```http
GET /students
```

---

# Exercise 2

You receive:

```http
GET /getStudentById/101
```

Redesign:

```http
GET /students/101
```

---

# Exercise 3

You receive:

```http
POST /createStudent
```

Redesign:

```http
POST /students
```

---

# Exercise 4

You receive:

```http
DELETE /deleteStudent/101
```

Redesign:

```http
DELETE /students/101
```

---

# Exercise 5

You receive:

```http
GET /studentList
```

Redesign:

```http
GET /students
```

---

# Lab 11 — Understand Statelessness

## Objective

Understand why REST APIs should be stateless.

---

# Exercise

Send:

```http
GET /students/101
```

Then send:

```http
GET /students/102
```

The second request should contain everything required to process it.

The server should not depend on:

```text
"What request did this client make earlier?"
```

---

# Discussion

Imagine three Spring Boot instances:

```text
                  Load Balancer
                       |
          ┌────────────┼────────────┐
          ↓            ↓            ↓
      Server 1      Server 2      Server 3
```

Requests can go to different instances.

```text
Request 1 → Server 1
Request 2 → Server 3
Request 3 → Server 2
```

The API should still work.

---

# Lab 12 — Understand Idempotency

## Objective

Understand how repeated requests affect application state.

---

# Exercise 1 — PUT

Send:

```http
PUT /students/101
```

Body:

```json
{
  "name": "Rahul",
  "email": "rahul@example.com",
  "course": "Java"
}
```

Send the same request multiple times.

Observe the final state.

---

# Exercise 2 — POST

Send:

```http
POST /students
```

Body:

```json
{
  "name": "Rahul",
  "email": "rahul@example.com",
  "course": "Java"
}
```

Send it multiple times.

Observe whether multiple records are created.

---

# Discussion

Why is:

```text
PUT
```

generally idempotent while:

```text
POST
```

is generally not?

---

# Lab 13 — API Testing Challenge

## Objective

Test the complete API like a real API consumer.

Use Postman.

---

# Test 1 — Get All

```http
GET /students
```

Expected:

```text
200 OK
```

---

# Test 2 — Get Existing Student

```http
GET /students/101
```

Expected:

```text
200 OK
```

---

# Test 3 — Get Missing Student

```http
GET /students/999999
```

Expected:

```text
404 Not Found
```

---

# Test 4 — Create Student

```http
POST /students
```

Body:

```json
{
  "name": "Amit",
  "email": "amit@example.com",
  "course": "Spring Boot"
}
```

Expected:

```text
201 Created
```

---

# Test 5 — Invalid Student

```json
{
  "name": "",
  "email": "wrong",
  "course": ""
}
```

Expected:

```text
400 Bad Request
```

---

# Test 6 — Update Student

```http
PUT /students/101
```

Expected:

```text
200 OK
```

---

# Test 7 — Partial Update

```http
PATCH /students/101
```

Expected:

```text
200 OK
```

---

# Test 8 — Delete

```http
DELETE /students/101
```

Expected:

```text
204 No Content
```

---

# Lab 14 — Build an API Test Matrix

Create the following table.

| Test           | Method | Endpoint        | Expected Status |
| -------------- | ------ | --------------- | --------------- |
| Get all        | GET    | `/students`     | 200             |
| Get one        | GET    | `/students/101` | 200             |
| Not found      | GET    | `/students/999` | 404             |
| Create         | POST   | `/students`     | 201             |
| Invalid create | POST   | `/students`     | 400             |
| Replace        | PUT    | `/students/101` | 200             |
| Partial update | PATCH  | `/students/101` | 200             |
| Delete         | DELETE | `/students/101` | 204             |

---

# Lab 15 — Final Student Management API

## Objective

Build the complete API independently.

At this point, stop following the instructor's code.

You should implement the API yourself.

---

# Final Architecture

Your project should contain:

```text
src/main/java
└── com.example.stumgmt
    │
    ├── controller
    │   └── StudentController.java
    │
    ├── service
    │   └── StudentService.java
    │
    ├── repository
    │   └── StudentRepository.java
    │
    ├── model
    │   └── Student.java
    │
    ├── dto
    │   ├── CreateStudentRequest.java
    │   ├── UpdateStudentRequest.java
    │   └── StudentResponse.java
    │
    └── exception
        ├── StudentNotFoundException.java
        ├── ErrorResponse.java
        └── GlobalExceptionHandler.java
```

---

# Final API Contract

## Create Student

```http
POST /students
```

Request:

```json
{
  "name": "Rahul",
  "email": "rahul@example.com",
  "course": "Spring Boot"
}
```

Response:

```http
201 Created
```

```json
{
  "id": 101,
  "name": "Rahul",
  "email": "rahul@example.com",
  "course": "Spring Boot"
}
```

---

# Get All Students

```http
GET /students
```

Response:

```http
200 OK
```

---

# Get Student

```http
GET /students/101
```

Response:

```http
200 OK
```

---

# Replace Student

```http
PUT /students/101
```

---

# Partially Update Student

```http
PATCH /students/101
```

---

# Delete Student

```http
DELETE /students/101
```

Expected:

```http
204 No Content
```

---

# Advanced Query API

Support:

```http
GET /students?page=0&size=20
```

```http
GET /students?course=Java
```

```http
GET /students?sortBy=name&direction=asc
```

And:

```http
GET /students
    ?course=Java
    &sortBy=name
    &direction=asc
    &page=0
    &size=20
```

---

# Final Assignment

## Student Management REST API

Build a complete production-style REST API.

### Functional Requirements

Your application must support:

* Create student
* Read all students
* Read one student
* Replace student
* Partially update student
* Delete student
* Filter students
* Sort students
* Paginate students

---

# Final Assignment — API Design Requirements

Your API must follow these rules.

## Rule 1 — Resource-oriented URLs

Use:

```text
/students
/students/{id}
```

Do not use:

```text
/getStudents
/createStudent
/deleteStudent
```

---

## Rule 2 — Correct HTTP methods

```text
GET
POST
PUT
PATCH
DELETE
```

Use them appropriately.

---

## Rule 3 — Correct Status Codes

At minimum:

```text
200 OK
201 Created
204 No Content
400 Bad Request
404 Not Found
```

---

## Rule 4 — Validation

Validate:

```text
name
email
course
```

---

## Rule 5 — DTOs

Do not expose the database entity directly as your only API contract.

Create:

```text
CreateStudentRequest
UpdateStudentRequest
StudentResponse
```

---

## Rule 6 — Layered Architecture

Use:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

---

## Rule 7 — Error Handling

Return meaningful JSON errors.

Example:

```json
{
  "status": 404,
  "message": "Student with id 101 not found",
  "path": "/students/101",
  "timestamp": "2026-08-10T20:00:00"
}
```

---

# Final Assignment — Minimum Test Cases

You must demonstrate all of the following.

### 1. Create valid student

```text
201
```

### 2. Create invalid student

```text
400
```

### 3. Get all students

```text
200
```

### 4. Get existing student

```text
200
```

### 5. Get non-existing student

```text
404
```

### 6. Update student

```text
200
```

### 7. Partially update student

```text
200
```

### 8. Delete student

```text
204
```

### 9. Pagination

```text
?page=0&size=10
```

### 10. Filtering

```text
?course=Java
```

### 11. Sorting

```text
?sortBy=name&direction=asc
```

### 12. Combined query

```text
?course=Java&sortBy=name&direction=asc&page=0&size=10
```

---

# Final Challenge — Design Before Coding

Before writing any code, create an API design document.

For every endpoint write:

```text
HTTP Method:
URI:
Purpose:
Request Headers:
Path Variables:
Query Parameters:
Request Body:
Response Body:
Success Status:
Error Status:
```

Example:

```text
HTTP Method:
POST

URI:
/students

Purpose:
Create a new student

Request Header:
Content-Type: application/json

Request Body:
{
  "name": "Rahul",
  "email": "rahul@example.com",
  "course": "Java"
}

Success:
201 Created

Possible Errors:
400 Bad Request
409 Conflict
```

---

# Final Viva / Trainer Questions

After completing the lab, students should be able to answer:

### Question 1

Why is this:

```text
GET /students
```

better than:

```text
GET /getAllStudents
```

---

### Question 2

What is the difference between:

```text
Path Variable
```

and:

```text
Query Parameter
```

---

### Question 3

When would you use:

```text
PUT
```

instead of:

```text
PATCH
```

---

### Question 4

Why should POST generally not be treated as idempotent?

---

### Question 5

Why should controllers not contain all business logic?

---

### Question 6

What is the purpose of DTOs?

---

### Question 7

What is the difference between:

```text
Content-Type
```

and:

```text
Accept
```

---

### Question 8

Why return:

```text
404
```

instead of:

```text
200
```

when a student doesn't exist?

---

### Question 9

Why is pagination important?

---

### Question 10

Why should REST APIs be stateless?

---

# Completion Checklist

Before submitting the lab, verify:

```text
[ ] Application starts successfully

[ ] GET /students works

[ ] GET /students/{id} works

[ ] POST /students works

[ ] PUT /students/{id} works

[ ] PATCH /students/{id} works

[ ] DELETE /students/{id} works

[ ] Correct HTTP status codes implemented

[ ] Request validation implemented

[ ] DTOs implemented

[ ] Error handling implemented

[ ] Controller layer implemented

[ ] Service layer implemented

[ ] Repository layer implemented

[ ] PostgreSQL persistence works

[ ] Pagination works

[ ] Filtering works

[ ] Sorting works

[ ] Combined query works

[ ] APIs tested using Postman

[ ] API test matrix completed
```

---

# Final Architecture Students Should Reach

```text
                       POST /students
                              |
                              ↓
                     ┌────────────────┐
                     │   Controller   │
                     │                │
                     │ HTTP handling  │
                     └───────┬────────┘
                             ↓
                     ┌────────────────┐
                     │    Service     │
                     │                │
                     │ Business Logic │
                     └───────┬────────┘
                             ↓
                     ┌────────────────┐
                     │   Repository   │
                     │                │
                     │ Data Access    │
                     └───────┬────────┘
                             ↓
                     ┌────────────────┐
                     │   PostgreSQL   │
                     └────────────────┘
```

And the API consumer sees only:

```text
                    REST API
                       |
        ┌──────────────┼──────────────┐
        ↓              ↓              ↓
      Request        Response       Errors
        ↓              ↓              ↓
      JSON            JSON         HTTP Status
```

## Final Learning Outcome

The student should move from:

> "I know how to write `@GetMapping`."

to:

> "I can design an API contract, implement it using Spring Boot, validate input, handle errors, structure the application correctly, and expose an API that another developer can consume reliably."

### Recommended classroom execution

I would run this as a **progressive coding lab**, not as 15 isolated exercises:

```text
Lab 1
Basic GET API
       ↓
Lab 2
Complete CRUD
       ↓
Lab 3
DTO + Validation
       ↓
Lab 4
Controller → Service → Repository
       ↓
Lab 5
Exception Handling
       ↓
Lab 6
Pagination
       ↓
Lab 7
Filtering
       ↓
Lab 8
Sorting
       ↓
Lab 9
Combine Everything
       ↓
Lab 10–14
Design + Testing + API principles
       ↓
Lab 15
Independent Final API
```

This progression is especially suitable for your freshers because they **don't just learn REST terminology—they repeatedly modify the same Student Management application until it starts looking like a real Spring Boot application**.
