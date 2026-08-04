# Lab 1 – Spring Core Fundamentals

## Building a College Management System using Spring IoC & Dependency Injection

### Duration


---

# Objective

By the end of this lab, students will be able to:

* Create a Spring Core application
* Understand the Spring IoC Container
* Configure Beans using Annotations
* Perform Constructor Dependency Injection
* Understand Bean Scope
* Observe Bean Lifecycle
* Experience loose coupling through Dependency Injection
* Run a Spring application completely inside Docker

---

# Pre-Requisites

Students already know

* Java OOP
* Maven Basics
* Docker
* Docker Compose

---

# Lab Environment

Only Docker is required.

Students **must not install**

* Eclipse
* IntelliJ
* Spring Tool Suite
* Tomcat
* MySQL

Everything executes inside Docker.

---

# Architecture

```
                 Spring Container
                        │
                        │
              AnnotationConfigApplicationContext
                        │
                 --------------------
                 |                  |
             College Bean
                 │
           Department Bean
                 │
           Professor Bean
                 │
             Course Bean
```

---

# Folder Structure

```
spring-core-lab/

│
├── Dockerfile
├── docker-compose.yml
├── pom.xml
│
└── src
    └── main
        ├── java
        │
        └── resources
```

---

# Docker Image

Students should use

```
maven:3.9-eclipse-temurin-21
```

This image already contains

* Java
* Maven

No installation required.

---

# Step 1

## Create Project

Create the project

```
college-management
```

---

### Add Dependency

Only one dependency is allowed.

```
spring-context
```

No other Spring modules.

---

# Expected Output

Project should compile successfully.

---

# Step 2

## Create POJOs

Create the following classes.

```
Course
```

Properties

* courseId
* courseName
* duration

---

```
Professor
```

Properties

* professorId
* professorName
* specialization

---

```
Department
```

Properties

* departmentId
* departmentName

---

```
College
```

Properties

* collegeName
* location

---

Do not add relationships yet.

---

# Verification

Create objects using Java.

Print all object values.

No Spring yet.

---

# Step 3

## Establish Object Relationships

Modify the classes.

```
College
    ↓
Department
    ↓
Professor
    ↓
Course
```

Suggested Model

```
College

- name
- location
- Department
```

```
Department

- id
- name
- Professor
```

```
Professor

- id
- name
- Course
```

```
Course

- id
- name
```

---

Verify by manually creating objects.

(Last time students are allowed to use `new`.)

---

# Step 4

## Convert Classes into Spring Beans

Remove manual object creation.

Annotate classes.

```
@Component
```

or

```
@Service
```

Use meaningful stereotypes.

Example

```
College
```

↓

```
@Service
```

```
Professor
```

↓

```
@Component
```

---

# Verification

No compilation errors.

---

# Step 5

## Constructor Injection

Inject dependencies.

```
College

↓

Department

↓

Professor

↓

Course
```

Rules

✔ Constructor Injection only

❌ Field Injection

❌ Setter Injection

❌ new keyword

---

### Expected Understanding

Spring creates every object.

---

# Step 6

## Create Configuration Class

Create

```
AppConfig
```

Configure

```
@Configuration
```

```
@ComponentScan
```

No XML configuration.

---

# Verification

Project builds successfully.

---

# Step 7

## Start Spring Container

Create

```
Main.java
```

Load

```
AnnotationConfigApplicationContext
```

Retrieve

```
College
```

from the container.

No manual object creation.

---

# Verification

Only one bean retrieval.

Everything else should be injected automatically.

---

# Step 8

## Display College Information

Print

```
College Name

Department

Professor

Course
```

Example Output

```
-----------------------------------

College

ABC Engineering College

Location

Bangalore

Department

Computer Science

Professor

Dr. Ravi Kumar

Course

Spring Framework

-----------------------------------
```

Formatting is student's choice.

---

# Step 9

## Experiment with Bean Scope

Default

```
Singleton
```

Observe

```
HashCode
```

Retrieve

```
College
```

twice.

Print

```
hashCode()
```

Observe

Same object.

---

Now change

```
Professor
```

to

```
Prototype
```

Retrieve twice.

Observe

Different object instances.

---

### Questions

1. Which bean remained Singleton?

2. Which bean became Prototype?

3. Why?

---

# Step 10

## Bean Lifecycle

Add lifecycle callbacks.

```
@PostConstruct
```

Display

```
Initializing Professor Bean...
```

---

```
@PreDestroy
```

Display

```
Destroying Professor Bean...
```

Close the Spring Context.

Observe

Initialization

↓

Execution

↓

Cleanup

---

# Challenge 1

## Replace an Implementation

Current Design

```
NotificationService
```

↓

```
EmailNotificationService
```

Inject into

```
Professor
```

Display

```
Email Notification Sent
```

---

Create another implementation

```
SMSNotificationService
```

---

Requirement

Switch

Email

↓

SMS

without modifying

```
Professor.java
```

Only Spring configuration should change.

---

### Learning Outcome

Students understand

* Interface-based Programming
* Loose Coupling
* Dependency Injection

---

# Challenge 2

## Add Another Department

Current

```
College

↓

Computer Science
```

Modify

```
College
```

to support

```
Computer Science

Electronics

Mechanical
```

Display all departments.

---

# Challenge 3

## Add Multiple Professors

Each department should have

```
Professor

Professor

Professor
```

Display all professors.

---

# Final Output

Students should produce something similar to:

```
=================================================

SPRING COLLEGE MANAGEMENT SYSTEM

=================================================

College

ABC Engineering College

Location

Bangalore

Department

Computer Science

Professor

Dr. Ravi Kumar

Course

Spring Framework

Notification

Email Notification Sent

=================================================
```

---

# Docker Execution

Since the students only have Docker, provide them with a simple workflow:

```bash
# Build the image
docker build -t spring-core-lab .

# Run the application
docker run --rm spring-core-lab
```

Or, if using Docker Compose:

```bash
docker compose up --build
```

This ensures every student uses the **same Java and Maven version**, avoids IDE installation, and keeps the environment consistent across the class.

---

# Learning Outcomes

At the end of this lab, students will be able to:

| Concept                | Covered |
| ---------------------- | ------- |
| Spring IoC             | ✅       |
| Bean Creation          | ✅       |
| Dependency Injection   | ✅       |
| Constructor Injection  | ✅       |
| Component Scanning     | ✅       |
| Java Configuration     | ✅       |
| Spring Context         | ✅       |
| Bean Scope             | ✅       |
| Bean Lifecycle         | ✅       |
| Loose Coupling         | ✅       |
| Interface-based Design | ✅       |

This is an excellent independent lab before moving into the progressive Spring labs (Spring JDBC → Spring ORM → Spring MVC), because students will already have a solid mental model of how the Spring container manages objects and dependencies without the added complexity of databases or web applications.
