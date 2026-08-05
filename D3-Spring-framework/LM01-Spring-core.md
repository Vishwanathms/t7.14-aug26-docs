---

# Lab 1 – Spring Core Fundamentals

# Building a College Management System using Spring IoC & Dependency Injection

---

# Objective

In this lab, you will build your **first Spring application**.

Unlike traditional Java programs where you create objects using the `new` keyword, Spring will create and manage all objects for you.

By the end of this lab, you will understand:

* What Spring Framework is
* What an IoC Container does
* How Dependency Injection works
* How Spring creates objects
* How Spring wires objects together
* How Bean Scope works
* Bean Lifecycle
* Loose Coupling using Interfaces
* Running Spring applications completely inside Docker

---

# Business Scenario

A college wants to build a simple College Management System.

The application should display information about

* College
* Department
* Professor
* Course

Initially, everything is hardcoded.

Later, Spring will automatically create every object and connect them together.

---

# Lab Architecture

```
                 Spring IoC Container
                          │
                          │
      AnnotationConfigApplicationContext
                          │
            ------------------------------
            │                            │
        College Service Bean
                  │
          Department Bean
                  │
          Professor Bean
                  │
            Course Bean
```

---

# Before You Begin

Since this course is completely Docker-based,

Students **DO NOT install**

* Java
* Maven
* IntelliJ
* Eclipse
* Spring Tool Suite
* Tomcat

Everything runs inside Docker.

---

# Step 1 – Create the Project Structure

## Why?

Every Maven project follows a standard directory structure.

Create the following folders.

```
college-management/

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

## Folder Creation

Inside the project folder execute

```bash
mkdir -p src/main/java
mkdir -p src/main/resources
```

Verify

```
tree .
```

Expected

```
college-management
│
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── src
```

---

# Step 2 – Create pom.xml

## Why?

Maven downloads all required libraries automatically.

For this lab only one Spring dependency is required.

Create

```
pom.xml
```

Add

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0">

    <modelVersion>4.0.0</modelVersion>

    <groupId>com.college</groupId>

    <artifactId>college-management</artifactId>

    <version>1.0</version>

    <properties>

        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>

    </properties>

    <dependencies>

        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>6.2.0</version>
        </dependency>

    </dependencies>

</project>
```

---

## Verify Maven

Run

```bash
mvn compile
```

Expected

```
BUILD SUCCESS
```

If successful,

Spring libraries have been downloaded.

---

# Step 3 – Create Dockerfile

## Why?

Docker provides the same environment for every student.

Create

```
Dockerfile
```

```dockerfile
FROM maven:3.9-eclipse-temurin-21

WORKDIR /app

COPY . .

CMD ["mvn","compile"]
```

or 
* If there is issues with the docker build , showing up some kind of SSL issues.
* use the below DOckerfile

```bash
FROM maven:3.9-eclipse-temurin-21

WORKDIR /app

RUN apt-get update && apt-get install -y --no-install-recommends \ 
    ca-certificates && \
    update-ca-certificates && \
    rm -rf /var/lib/apt/lists/*

ENV MAVEN_OPTS="-Dmaven.resolver.transport=wagon -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true"

COPY . .

CMD ["mvn", "-Dmaven.resolver.transport=wagon", "-Dmaven.wagon.http.ssl.insecure=true", "-Dmaven.wagon.http.ssl.allowall=true", "-Dmaven.wagon.http.ssl.ignore.validity.dates=true", 
"compile"]

CMD ["mvn", "compile"] 
```

---

## Build Docker Image

```bash
docker build -t spring-core-lab .
```

Expected

```
Successfully built
```

---

## Run Container

```bash
docker run --rm spring-core-lab
```

Expected

```
BUILD SUCCESS
```

Congratulations.

Your Java environment is ready.

---

# Step 4 – Create Your First Java Classes

## Why?

Before Spring manages objects,

you should understand how objects are created manually.

Create package

```
com.college
```

Create

```
Course.java
Professor.java
Department.java
College.java
```

Each class should contain

* Private variables
* Constructors
* Getters
* Setters
* toString()

---

Example

```
Course

courseId

courseName

duration
```

---

Do the same for

Professor

Department

College

---

## Verify

Create

```
Main.java
```

Write

```java
Course course = new Course();

System.out.println(course);
```

Run

```bash
mvn exec:java
```

Expected

Object details should print.

---

# Step 5 – Create Objects Manually

Now create

```
Course

↓

Professor

↓

Department

↓

College
```

Example

```java
Course course = new Course(...);

Professor professor = new Professor(..., course);

Department department = new Department(..., professor);

College college = new College(..., department);
```

Print

```
college
```

Observe

You created every object yourself.

---

## Discussion

Ask yourself

Who created the objects?

Answer

**You did using `new`.**

---

# Step 6 – Introduce Spring

Until now,

you controlled object creation.

Spring changes this.

Instead of

```java
new College();
```

Spring will do

```
Create College

↓

Create Department

↓

Create Professor

↓

Create Course
```

This is called

**Inversion of Control (IoC)**

---

# Step 7 – Convert Classes into Spring Beans

Add annotation

```java
@Component
```

or

```java
@Service
```

Example

```java
@Service
public class College
```

```java
@Component
public class Department
```

```java
@Component
public class Professor
```

```java
@Component
public class Course
```

---

## Discussion

Question

Did Spring create any object yet?

Answer

**No.**

Spring only knows these classes are eligible.

---

# Step 8 – Constructor Injection

Remove every

```java
new
```

inside your classes.

Inject dependencies.

Example

```java
public Professor(Course course)
{
    this.course = course;
}
```

Department

```java
public Department(Professor professor)
```

College

```java
public College(Department department)
```

---

## Verify

Search entire project

```
new
```

Only

```
AnnotationConfigApplicationContext
```

should remain.

No other object creation.

---

# Step 9 – Create Spring Configuration

Create

```
AppConfig.java
```

```java
@Configuration
@ComponentScan("com.college")
public class AppConfig
{

}
```

---

## Discussion

ComponentScan tells Spring

```
Scan this package

↓

Find Components

↓

Create Objects

↓

Store them inside Container
```

---

# Step 10 – Start Spring Container

Replace old Main.java

```java
AnnotationConfigApplicationContext context =
        new AnnotationConfigApplicationContext(AppConfig.class);

College college =
        context.getBean(College.class);

System.out.println(college);

context.close();
```

---

## Observe Carefully

You never created

```
Course

Professor

Department
```

Yet

they exist.

Who created them?

**Spring Container**

---

# Step 11 – Print College Information

Add method

```java
displayCollegeInformation()
```

Output

```
====================================

College

ABC Engineering College

Location

Bangalore

Department

Computer Science

Professor

Dr Ravi Kumar

Course

Spring Framework

====================================
```

---

# Step 12 – Bean Scope Experiment

Inside Main

```java
College c1 = context.getBean(College.class);

College c2 = context.getBean(College.class);
```

Print

```java
System.out.println(c1.hashCode());

System.out.println(c2.hashCode());
```

Expected

```
1254876

1254876
```

Same object.

Singleton.

---

Now

Professor

```java
@Scope("prototype")
```

Retrieve

```java
Professor p1

Professor p2
```

Print hashcode.

Expected

Different values.

---

## Discussion

Why?

Singleton

One object.

Prototype

New object every request.

---

# Step 13 – Bean Lifecycle

Inside Professor

```java
@PostConstruct
public void init()
{
    System.out.println("Professor Bean Initialized");
}
```

Add

```java
@PreDestroy
public void destroy()
{
    System.out.println("Professor Bean Destroyed");
}
```

Run again.

Observe

```
Container Starts

↓

Bean Created

↓

@PostConstruct

↓

Application Runs

↓

Context Closed

↓

@PreDestroy
```

---

# Step 14 – Loose Coupling Challenge

Create Interface

```
NotificationService
```

Method

```java
sendNotification()
```

---

Implementation 1

```
EmailNotificationService
```

Output

```
Email Notification Sent
```

Implementation 2

```
SMSNotificationService
```

Output

```
SMS Notification Sent
```

Inject

```
NotificationService
```

inside Professor.

Without changing

```
Professor.java
```

Switch implementation.

Observe

Only Spring configuration changes.

---

# Step 15 – Multiple Departments

Modify College

```
List<Department>
```

Display

```
Computer Science

Mechanical

Electronics
```

---

# Step 16 – Multiple Professors

Each department

```
List<Professor>
```

Display

```
Professor 1

Professor 2

Professor 3
```

---

# Step 17 – Run Using Docker

Update the Dockerfile to build and run the application:

```dockerfile
FROM maven:3.9-eclipse-temurin-21

WORKDIR /app

COPY . .

RUN mvn clean package

CMD ["java","-cp","target/classes:target/dependency/*","com.college.Main"]
```

Or, if using the Maven Exec Plugin:

```dockerfile
FROM maven:3.9-eclipse-temurin-21

WORKDIR /app

COPY . .

CMD ["mvn","compile","exec:java"]
```

Build

```bash
docker build -t spring-core-lab .
```

Run

```bash
docker run --rm spring-core-lab
```

---

# Expected Final Output

```
===================================================

SPRING COLLEGE MANAGEMENT SYSTEM

===================================================

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

===================================================

Professor Bean Initialized

...

Professor Bean Destroyed
```

---

# What Students Learned

| Step  | Concept               | What Students Understand                                                              |
| ----- | --------------------- | ------------------------------------------------------------------------------------- |
| 1–3   | Maven & Docker        | How to create and build a Spring project without installing Java locally              |
| 4–5   | Traditional Java      | Manual object creation using `new`                                                    |
| 6     | IoC                   | Spring takes control of object creation                                               |
| 7     | Components            | How classes become Spring-managed beans                                               |
| 8     | Constructor Injection | Dependencies are injected automatically                                               |
| 9     | Configuration         | How `@Configuration` and `@ComponentScan` bootstrap the application                   |
| 10    | Spring Container      | Creating and retrieving beans using `AnnotationConfigApplicationContext`              |
| 11    | Bean Usage            | Accessing and using injected dependencies                                             |
| 12    | Bean Scope            | Difference between Singleton and Prototype                                            |
| 13    | Bean Lifecycle        | Initialization and destruction callbacks                                              |
| 14    | Loose Coupling        | Programming to interfaces and swapping implementations without changing business code |
| 15–16 | Collections & DI      | Managing multiple beans and object relationships                                      |
| 17    | Docker                | Running the entire Spring application in a consistent containerized environment       |

This structure slows the pace intentionally, explains the reasoning behind each concept, and includes a verification checkpoint after every major step—making it well suited for students encountering Spring Framework for the first time.
