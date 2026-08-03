# Lab Manual 2

# Modern Java inside Docker

**Duration:** 4 Hours

Now students enhance the previous application.

---

# Architecture

```
Docker Container

|

Employee Manager

|

Java 8

|

Threads

|

JUnit
```

---

## Lab 1 – Background Thread

Whenever employee is added

Another thread writes

```
Application Log
```

Output

```
Employee Added

Writing Log...

Completed
```

Explain

Runnable

Thread

---

## Lab 2 – Stream API

Menu

```
Show Employees

↓

Salary > 50000

↓

Sort by Name

↓

Sort by Salary
```

Students use

```
filter()

map()

sorted()

collect()
```

---

## Lab 3 – Lambda Expressions

Replace Comparator

Old style

↓

Lambda

Sort Employees

---

## Lab 4 – Optional

Search Employee

Return

```
Optional<Employee>
```

Display

```
Employee Found
```

or

```
Employee Missing
```

---

## Lab 5 – Unit Testing in Docker

Instead of installing Maven/JUnit locally, run everything in Docker.

### Folder

```
employee-app/

src/

test/

Dockerfile

pom.xml
```

Build

```bash
docker build -t employee-test .
```

Run tests

```bash
docker run employee-test
```

Students observe

```
Tests run : 4

Failures : 0

BUILD SUCCESS
```

---

## Lab 6 – Multi-stage Docker Build

Introduce

```
Builder Image

↓

Compiled Classes

↓

Small Runtime Image
```

Dockerfile

```
FROM maven:...

↓

RUN mvn test

↓

RUN mvn package

↓

FROM eclipse-temurin

↓

COPY jar

↓

ENTRYPOINT
```

Students learn

* Build container
* Runtime container
* Smaller image
* Production practice

---

## Lab 7 – Final Challenge

Build a production-ready Employee Manager.

Requirements

✔ Collections

✔ Exception Handling

✔ File Persistence

✔ Java 8 Streams

✔ Lambda

✔ Background Thread

✔ Docker Volume

✔ JUnit Tests

✔ Multi-stage Docker Build

---

# Final Project Structure

```
employee-app/

├── src/

├── test/

├── pom.xml

├── Dockerfile

├── .dockerignore

├── README.md

└── data/
```

---

# Why this is better for your course

This sequence aligns naturally with the rest of your curriculum:

* **Current Module:** Advanced Java concepts inside Docker containers.
* **Database Module:** Replace the text file with a MySQL container using Docker Compose.
* **Spring Framework:** Reuse the same project and convert it into a Spring Boot application.
* **JUnit:** Continue running tests as part of the Docker build.
* **Future CI/CD:** The same Dockerized project can later be integrated with Jenkins, SonarQube, Trivy, and deployment pipelines without restructuring.

This gives students one continuous project that evolves throughout the course, which is much closer to how enterprise Java applications are built and maintained.
