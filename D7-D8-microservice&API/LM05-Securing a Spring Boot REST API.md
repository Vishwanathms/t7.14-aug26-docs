# Lab Manual: Securing a Spring Boot REST API

## API Security — Authentication, Authorization, BCrypt, Roles and JWT

### Duration

**1 Hour**

### Prerequisites

Before starting this lab, you should already have:

- Java installed
- IntelliJ IDEA
- Maven
- Basic Spring Boot knowledge
- Basic REST API knowledge
- Student Management API created
- `StudentController`
- `StudentService`
- `StudentRepository`

Your existing API should have endpoints similar to:

```text
GET     /students
GET     /students/{id}
POST    /students
PUT     /students/{id}
DELETE  /students/{id}
```

---

# Lab Objective

By completing this lab, you will understand how to protect a REST API using Spring Security.

You will progressively implement:

```text
1. Spring Security
        ↓
2. Authentication
        ↓
3. Password Hashing
        ↓
4. Roles
        ↓
5. Authorization
        ↓
6. 401 vs 403
        ↓
7. JWT concept
```

The final security flow will look like:

```text
Client
   |
   | HTTP Request
   ↓
Spring Security
   |
   | Authentication
   ↓
Authorization
   |
   | Allowed?
   ↓
Controller
   ↓
Service
   ↓
Repository
```

---

# Lab 1 — Add Spring Security

## Objective

Add Spring Security to the existing Student Management application.

---

## Step 1 — Open the Existing Project

Open your existing Student Management Spring Boot project in IntelliJ IDEA.

Your project should look approximately like:

```text
student-management
│
├── src
│   └── main
│       ├── java
│       │   └── com.example.stumgmt
│       │       ├── StudentMgmtApplication.java
│       │       ├── controller
│       │       ├── service
│       │       ├── repository
│       │       └── entity
│       │
│       └── resources
│
└── pom.xml
```

---

# Step 2 — Add Spring Security Dependency

Open:

```text
pom.xml
```

Add:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

Save the file.

---

# Step 3 — Reload Maven

In IntelliJ:

```text
Maven
   ↓
Reload All Maven Projects
```

Wait until Maven finishes downloading the dependencies.

---

# Step 4 — Start the Application

Run:

```text
StudentMgmtApplication
```

Check the console.

The application should start successfully.

---

# Step 5 — Test the Existing API

Open Postman.

Send:

```http
GET http://localhost:8080/students
```

Previously, you could access the API directly.

Now Spring Security will block the request.

You may receive:

```text
401 Unauthorized
```

---

# What Just Happened?

Previously:

```text
HTTP Request
     ↓
Controller
```

After adding Spring Security:

```text
HTTP Request
     ↓
Spring Security
     ↓
Controller
```

Spring Security is now sitting in front of your API.

---

# Checkpoint 1

Answer these questions:

### Q1. What happens when Spring Security is added?

Expected answer:

```text
Requests are protected by default.
```

### Q2. Does the controller receive an unauthenticated request?

```text
No
```

### Q3. What HTTP status indicates an authentication problem?

```text
401 Unauthorized
```

---

# Lab 2 — Understand Authentication

## Objective

Understand how Spring Security identifies a user.

Spring Security provides a default user for development.

When you start the application, look at the console.

You may see something similar to:

```text
Using generated security password:
xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
```

The default username is:

```text
user
```

---

# Step 1 — Open Postman

Create:

```http
GET http://localhost:8080/students
```

Go to:

```text
Authorization
```

Select:

```text
Basic Auth
```

Enter:

```text
Username: user
Password: <password shown in console>
```

Click:

```text
Send
```

---

# Expected Result

Your request can now reach the application.

If the Student API is working correctly, you should receive your normal response.

For example:

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

# Understand the Flow

```text
Username
   +
Password
   ↓
Spring Security
   ↓
Authentication
   ↓
Request Allowed
   ↓
StudentController
```

---

# Important Concept

### Authentication means:

> **Who are you?**

Example:

```text
Username = rahul
Password = secret123
```

Spring Security verifies the credentials.

---

# Lab 3 — Configure Our Own Users

## Objective

Instead of using the automatically generated user, create our own users.

We will create:

```text
rahul
ROLE_STUDENT
```

and:

```text
admin
ROLE_ADMIN
```

---

# Step 1 — Create Security Configuration

Create a new class:

```text
SecurityConfig.java
```

Example:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
```

---

# Step 2 — Add Users

Inside the same configuration class, add:

```java
@Bean
public UserDetailsService userDetailsService(
        PasswordEncoder passwordEncoder) {

    UserDetails student = User
            .withUsername("rahul")
            .password(passwordEncoder.encode("student123"))
            .roles("STUDENT")
            .build();

    UserDetails admin = User
            .withUsername("admin")
            .password(passwordEncoder.encode("admin123"))
            .roles("ADMIN")
            .build();

    return new InMemoryUserDetailsManager(student, admin);
}
```

---

# Step 3 — Add BCrypt

Add:

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

Your configuration now contains:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(
            PasswordEncoder passwordEncoder) {

        UserDetails student = User
                .withUsername("rahul")
                .password(passwordEncoder.encode("student123"))
                .roles("STUDENT")
                .build();

        UserDetails admin = User
                .withUsername("admin")
                .password(passwordEncoder.encode("admin123"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(
                student,
                admin
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
```

---

# Step 4 — Restart the Application

Stop the application.

Start it again.

There should no longer be a need to use the generated default password.

---

# Step 5 — Test STUDENT

In Postman:

```text
Authorization
    ↓
Basic Auth
```

Enter:

```text
Username: rahul
Password: student123
```

Call:

```http
GET /students
```

Expected:

```text
200 OK
```

---

# Step 6 — Test ADMIN

Change the credentials to:

```text
Username: admin
Password: admin123
```

Call:

```http
GET /students
```

Expected:

```text
200 OK
```

Both users are authenticated.

But they have different roles.

```text
rahul
  ↓
ROLE_STUDENT

admin
  ↓
ROLE_ADMIN
```

---

# Lab 4 — Understand Password Hashing

## Objective

Understand why passwords should never be stored as plain text.

---

# Bad Approach

Never store:

```text
username: rahul
password: student123
```

in a database.

If the database is compromised, the actual password is exposed.

---

# Correct Approach

Use BCrypt.

```text
student123
     ↓
 BCrypt
     ↓
$2a$10$................
```

The database stores the BCrypt hash.

---

# Step 1 — Observe BCrypt

Add a temporary test:

```java
String hash =
        passwordEncoder.encode("student123");

System.out.println(hash);
```

Run the application.

You should see something similar to:

```text
$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
```

---

# Step 2 — Run It Again

Run the same password through BCrypt again.

You may get a different hash.

For example:

```text
student123
↓
$2a$10$ABC...

student123
↓
$2a$10$XYZ...
```

This is expected.

BCrypt uses a random salt.

---

# Important

Do not try to decrypt a BCrypt password.

Instead:

```text
Entered Password
       ↓
BCrypt comparison
       ↓
Match?
  ↓       ↓
 YES      NO
```

---

# Checkpoint 2

### What should be stored in the database?

```text
BCrypt password hash
```

### What should NOT be stored?

```text
Plain-text password
```

---

# Lab 5 — Implement Authorization

## Objective

Now we know:

```text
WHO are you?
        ↓
Authentication
```

Now we need to answer:

```text
WHAT can you do?
        ↓
Authorization
```

---

# Requirement

We will create these rules:

| Endpoint | STUDENT | ADMIN |
|---|---|---|
| GET `/students` | ✅ | ✅ |
| GET `/students/{id}` | ✅ | ✅ |
| POST `/students` | ❌ | ✅ |
| PUT `/students/{id}` | ❌ | ✅ |
| DELETE `/students/{id}` | ❌ | ✅ |

---

# Step 1 — Modify Security Configuration

Change:

```java
.anyRequest().authenticated()
```

to rules similar to:

```java
.authorizeHttpRequests(auth -> auth

    .requestMatchers(HttpMethod.GET, "/students/**")
        .hasAnyRole("STUDENT", "ADMIN")

    .requestMatchers(HttpMethod.POST, "/students/**")
        .hasRole("ADMIN")

    .requestMatchers(HttpMethod.PUT, "/students/**")
        .hasRole("ADMIN")

    .requestMatchers(HttpMethod.DELETE, "/students/**")
        .hasRole("ADMIN")

    .anyRequest().authenticated()
)
```

Make sure you have:

```java
import org.springframework.http.HttpMethod;
```

---

# Lab 6 — Test STUDENT Authorization

## Test 1 — Read Students

Use:

```text
Username: rahul
Password: student123
```

Call:

```http
GET /students
```

Expected:

```text
200 OK
```

Why?

```text
rahul
 ↓
ROLE_STUDENT
 ↓
GET allowed
```

---

# Test 2 — Delete Student

Keep the same user:

```text
Username: rahul
Password: student123
```

Call:

```http
DELETE /students/1
```

Expected:

```text
403 Forbidden
```

---

# Why 403?

Rahul is authenticated.

Spring Security knows who Rahul is.

But Rahul does not have permission to delete students.

Therefore:

```text
Authentication
      ↓
SUCCESS

Authorization
      ↓
FAILED

Result
      ↓
403 Forbidden
```

---

# Lab 7 — Test ADMIN Authorization

Use:

```text
Username: admin
Password: admin123
```

Call:

```http
GET /students
```

Expected:

```text
200 OK
```

Now call:

```http
DELETE /students/1
```

Expected:

```text
200 OK
```

assuming the student exists and your controller successfully performs the delete.

---

# Understand 401 vs 403

This is one of the most important concepts in the lab.

## 401 Unauthorized

The user is not properly authenticated.

Example:

```text
No username/password
        ↓
GET /students
        ↓
401
```

Think:

> "I don't know who you are."

---

## 403 Forbidden

The user is authenticated but does not have permission.

Example:

```text
Rahul
 ↓
ROLE_STUDENT
 ↓
DELETE /students/1
 ↓
403
```

Think:

> "I know who you are, but you are not allowed to do this."

---

# Lab 8 — Test All Security Scenarios

Students must complete this table.

| Test | Expected |
|---|---|
| No credentials → GET `/students` | 401 |
| STUDENT → GET `/students` | 200 |
| ADMIN → GET `/students` | 200 |
| STUDENT → DELETE `/students/1` | 403 |
| ADMIN → DELETE `/students/1` | 200 |

Take screenshots of the results.

---

# Lab 9 — Understand JWT

## Objective

Understand how modern REST APIs commonly authenticate clients using JWT.

For this lab, you will understand the flow rather than implement a complete JWT authentication system.

---

# Step 1 — Login

The client sends:

```http
POST /auth/login
Content-Type: application/json
```

Request:

```json
{
    "username": "rahul",
    "password": "student123"
}
```

---

# Step 2 — Server Authenticates

The server verifies:

```text
Username
    +
Password
    ↓
Authentication
```

If successful:

```text
Authentication SUCCESS
```

---

# Step 3 — Server Creates JWT

The server generates a token:

```text
eyJhbGciOiJIUzI1NiJ9...
```

Response:

```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

---

# Step 4 — Client Uses JWT

For future requests:

```http
GET /students
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

The request flow becomes:

```text
Client
  |
  | Authorization: Bearer JWT
  ↓
Spring Security
  |
  | Validate JWT
  ↓
Identify User
  |
  | Check Role
  ↓
Controller
```

---

# JWT Structure

A JWT generally looks like:

```text
HEADER
.
PAYLOAD
.
SIGNATURE
```

Example:

```text
eyJhbGciOiJIUzI1NiJ9
.
eyJzdWIiOiJyYWh1bCIsInJvbGUiOiJTVFVERU5UIn0
.
xxxxxxxxxxxxxxxx
```

---

# Important JWT Rule

Do NOT put passwords inside a JWT.

Bad:

```json
{
    "username": "rahul",
    "password": "student123"
}
```

Better:

```json
{
    "username": "rahul",
    "role": "STUDENT"
}
```

JWT claims should contain only information appropriate for the token.

Also remember:

> A signed JWT is not automatically encrypted.

---

# Lab 10 — Understand the Complete Security Architecture

Draw the following diagram in your notebook.

```text
                  CLIENT
                     |
                     |
              Login / Request
                     |
                     ↓
             Spring Security
                     |
          +----------+----------+
          |                     |
   Authentication         JWT Validation
          |                     |
          +----------+----------+
                     |
                     ↓
               Authorization
                     |
             +-------+-------+
             |               |
          Allowed          Denied
             |               |
             ↓               ↓
        Controller        403/401
             |
             ↓
          Service
             |
             ↓
        Repository
             |
             ↓
          Database
```

---

# Lab 11 — Security Checklist

Before completing the lab, verify each item.

### Authentication

- [ ] Spring Security added
- [ ] Users configured
- [ ] Login/authentication understood

### Password Security

- [ ] BCrypt configured
- [ ] Plain-text passwords avoided

### Authorization

- [ ] STUDENT role created
- [ ] ADMIN role created
- [ ] GET allowed for students
- [ ] DELETE restricted to admins

### HTTP Status Codes

- [ ] 401 understood
- [ ] 403 understood

### JWT

- [ ] JWT purpose understood
- [ ] Bearer token understood
- [ ] Authorization header understood

### General Security

- [ ] HTTPS/TLS concept understood
- [ ] CORS concept understood
- [ ] CSRF concept understood
- [ ] Secrets should not be committed to Git

---

# Final Challenge

## Secure the Student API

You are now given the following requirement:

> "Students can view student information, but only administrators can create, update and delete students."

Implement the following security matrix:

```text
                  STUDENT       ADMIN

GET /students        ✅           ✅

GET /students/{id}   ✅           ✅

POST /students       ❌           ✅

PUT /students/{id}   ❌           ✅

DELETE /students/id  ❌           ✅
```

---

# Final Verification

Run these tests.

### Test 1

```http
GET /students
```

No credentials.

Expected:

```text
401 Unauthorized
```

---

### Test 2

```http
GET /students
```

Credentials:

```text
rahul / student123
```

Expected:

```text
200 OK
```

---

### Test 3

```http
DELETE /students/1
```

Credentials:

```text
rahul / student123
```

Expected:

```text
403 Forbidden
```

---

### Test 4

```http
DELETE /students/1
```

Credentials:

```text
admin / admin123
```

Expected:

```text
200 OK
```

---

# What You Learned

At the end of this lab, you should be able to explain:

### Authentication

```text
Who are you?
```

### Authorization

```text
What are you allowed to do?
```

### BCrypt

```text
How do we protect passwords?
```

### Spring Security

```text
How do we enforce security rules?
```

### JWT

```text
How can a client carry its authenticated identity
between API requests?
```

### 401

```text
Authentication failed / missing
```

### 403

```text
Authenticated but not allowed
```

---

# Takeaway

A secure REST API follows this basic flow:

```text
             CLIENT
                |
                ↓
        Authentication
                |
                ↓
          JWT / Identity
                |
                ↓
        Spring Security
                |
                ↓
         Authorization
                |
         +------+------+
         |             |
       ALLOW          DENY
         |             |
         ↓             ↓
     Controller     401 / 403
         |
         ↓
       Service
         |
         ↓
     Repository
         |
         ↓
      Database
```

## Remember

> **Authentication answers "Who are you?"**

> **Authorization answers "What can you do?"**

> **BCrypt protects passwords.**

> **Spring Security enforces security rules.**

> **JWT can carry authenticated identity between requests.**

> **HTTPS protects data while travelling over the network.**