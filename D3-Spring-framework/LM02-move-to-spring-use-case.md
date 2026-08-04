# Progressive Lab 1

# Convert JDBC Console Application into a Spring Framework Application

---

## Lab Objective

In the previous modules, you developed a **Student Management System** using:

* Java Console
* JDBC
* MySQL

In this lab, you will **not build a new application**.

Instead, you will **refactor your existing application** into a **Spring-based layered architecture** using:

* Spring IoC
* Dependency Injection
* Spring JDBC
* Repository Pattern

The functionality remains exactly the same.

Only the architecture changes.

---

# Expected Architecture

```
                StudentApplication
                        │
                        ▼
                 StudentService
                        │
                        ▼
               StudentRepository
                        │
                        ▼
                  JdbcTemplate
                        │
                        ▼
                    MySQL Database
```

---

# Prerequisites

You should already have:

* Existing JDBC Console Project
* MySQL Database
* Student Table
* CRUD Operations Working
* Java 17+
* Maven
* IntelliJ / Eclipse

---


# Existing Application Structure

Your current application may look similar to this:

```
StudentManagement

src

 Student.java

 StudentDAO.java

 DatabaseConnection.java

 StudentMain.java
```

Typical code flow:

```
Main

↓

new StudentDAO()

↓

JDBC Connection

↓

SQL

↓

Database
```

---

# Target Project Structure

Convert it into the following structure.

```
student-management

src/main/java

config

AppConfig

model

Student

repository

StudentRepository

service

StudentService

application

StudentApplication

src/main/resources

application.properties
```

---

# Task 1 – Create a Spring Maven Project

### Objective

Create a Maven project with Spring support.

---

### Steps

1. Create a Maven Project

2. Name the project

```
student-management-spring
```

3. Add the required Spring dependencies.

Required libraries include:

* Spring Context
* Spring JDBC
* MySQL Connector

---

### Expected Output

Project builds successfully.

---

# Task 2 – Create the Package Structure

Create the following packages.

```
config

model

repository

service

application
```

Move your existing **Student** class into the **model** package.

---

### Verify

Your project should now be organized into multiple layers.

---

# Task 3 – Configure Spring

### Objective

Enable Spring to manage your objects.

---

### Steps

Create a configuration class.

Responsibilities:

* Enable component scanning
* Configure Spring beans

---

### Verify

Spring starts successfully without errors.

---

# Task 4 – Create Repository Layer

Move all database operations into the repository.

Responsibilities:

* Insert Student
* Update Student
* Delete Student
* Search Student
* Display Students

---

### Important

The repository is the **only** class allowed to communicate with the database.

---

### Verify

No SQL statements should exist outside the repository.

---

# Task 5 – Create Service Layer

Create a service class.

Responsibilities

* Validate data (if required)
* Call repository methods
* Provide business operations

---

Example flow

```
Application

↓

Service

↓

Repository

↓

Database
```

---

### Verify

The application should communicate only with the Service.

---

# Task 6 – Enable Dependency Injection

### Objective

Remove manual object creation.

Current approach:

```
StudentRepository repository =
        new StudentRepository();
```

Replace it with Spring Dependency Injection.

Use either:

* Constructor Injection (Preferred)
* Autowired Injection

---

### Verify

No `new StudentRepository()` inside the Service.

---

# Task 7 – Configure Database Connectivity

Create the database configuration.

Configure:

* Database URL
* Username
* Password

Create beans for:

* DataSource
* JdbcTemplate

---

### Verify

Application connects successfully to MySQL.

---

# Task 8 – Replace JDBC Code with JdbcTemplate

Refactor the existing repository.

Reuse your previous SQL queries.

Only replace the JDBC implementation with Spring JDBC.

---

### Expected Operations

Implement:

* Add Student
* Update Student
* Delete Student
* Search Student
* Display All Students

---

### Note

Do not change the SQL queries unless required.

---

# Task 9 – Refactor the Console Application

Modify the main application.

Current Flow

```
Main

↓

DAO

↓

Database
```

Target Flow

```
Main

↓

Service

↓

Repository

↓

JdbcTemplate

↓

Database
```

---

### Verify

The console application no longer interacts with the repository directly.

---

# Task 10 – Test All Features

Execute the following operations.

### Test Case 1

Add a Student

Expected Result

Student record is inserted.

---

### Test Case 2

Display All Students

Expected Result

All records appear.

---

### Test Case 3

Search by ID

Expected Result

Correct student is displayed.

---

### Test Case 4

Update Student

Expected Result

Student information is updated.

---

### Test Case 5

Delete Student

Expected Result

Student is removed.

---

# Final Project Architecture

Your application should follow this flow.

```
Console Menu

↓

StudentApplication

↓

StudentService

↓

StudentRepository

↓

JdbcTemplate

↓

MySQL
```

---

# Challenge Activity (Optional)

If time permits, enhance your application by implementing one or more of the following:

* Validate that marks are within an acceptable range before saving.
* Prevent duplicate Student IDs from being added.
* Display meaningful messages when a student is not found.
* Handle database exceptions gracefully using Spring's exception hierarchy.

---

# Deliverables

By the end of the lab, you should submit:

* Maven-based Spring project.
* Layered package structure.
* Spring configuration class.
* Repository using `JdbcTemplate`.
* Service layer using Dependency Injection.
* Working console application.
* Successful execution of all CRUD operations.

---



# Learning Outcomes

After completing this lab, you will be able to:

* Convert a traditional JDBC application into a Spring application.
* Configure and use the Spring IoC container.
* Apply Dependency Injection to reduce coupling.
* Implement the Repository and Service design patterns.
* Use `JdbcTemplate` to simplify database access.
* Organize applications into a maintainable layered architecture.
* Prepare the application for the next phase, where a Spring MVC or Spring Boot web layer can be added without changing the business or data access code.
