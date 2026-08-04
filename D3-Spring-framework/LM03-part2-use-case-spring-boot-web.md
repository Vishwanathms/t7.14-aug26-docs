# Progressive Lab 2
# Convert Spring Console Application into a Spring MVC Web Application

---

# Lab Objective

In the previous lab, you converted your **JDBC Console Application** into a **layered Spring application** using:

* Spring IoC
* Dependency Injection
* Spring JDBC
* Repository Pattern

In this lab, you will **replace the Console User Interface** with a **Spring MVC Web Application**.

> **Important**
>
> * Do **not** rewrite your Service or Repository layers.
> * Reuse the existing business logic.
> * Only replace the presentation layer (Console → Web).

This lab serves as the **transition from Spring Framework to Spring Boot**.

---

# Learning Objectives

By the end of this lab, you will be able to:

* Understand Spring MVC architecture
* Configure DispatcherServlet
* Create Controllers
* Process HTTP requests
* Render JSP pages
* Handle HTML forms
* Implement MVC flow
* Reuse existing Service and Repository layers

---

# Existing Architecture

```text
Console Application

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

# Target Architecture

```text
Browser

↓

Spring MVC

↓

StudentController

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

# Prerequisites

Students should already have:

* Spring Layered Application (Lab 1)
* MySQL Database
* Student Table
* Working CRUD Operations
* Maven
* Apache Tomcat 10 (or compatible Servlet container)
* Java 17+

---

# Project Structure

Convert the existing project into the following structure.

```text
student-management-mvc

src

 main

   java

      controller

      service

      repository

      model

      config

   webapp

      WEB-INF

          views

             home.jsp

             students.jsp

             add-student.jsp

             edit-student.jsp

             search.jsp

application.properties (optional)
```

---

# Task 1 – Create a Spring MVC Project

## Objective

Convert the existing Spring project into a web application.

---

## Steps

1. Convert the project packaging from **JAR** to **WAR**.
2. Add the required Spring MVC dependency.
3. Configure the project to run on Tomcat.
4. Ensure the application builds successfully.

---

### Expected Result

The project deploys successfully to Tomcat.

---

# Task 2 – Configure DispatcherServlet

## Objective

Configure Spring MVC to receive all browser requests.

---

## Tasks

Configure the following:

* DispatcherServlet
* Component Scanning
* View Resolver
* JSP View Location

---

### Verify

Launching the application should not produce configuration errors.

---

# Task 3 – Create StudentController

## Objective

Create the Controller layer.

---

## Responsibilities

The controller should receive browser requests and delegate processing to the Service layer.

Create endpoints for:

* Home Page
* List Students
* Add Student
* Save Student
* Search Student
* Edit Student
* Update Student
* Delete Student

---

### Architecture

```text
Browser

↓

StudentController

↓

StudentService
```

---

### Verify

Controller methods are invoked when corresponding URLs are accessed.

---

# Task 4 – Create JSP Web Pages

Create the following JSP pages.

---

## Home Page

Purpose

* Landing page
* Navigation menu

Include links for:

* View Students
* Add Student
* Search Student

---

## Student List Page

Display

* Student ID
* Name
* Age
* Marks

Also provide actions:

* Edit
* Delete

---

## Add Student Page

Create a form containing:

* Student ID
* Name
* Age
* Marks

Submit the form to the appropriate controller endpoint.

---

## Search Student Page

Allow the user to search using Student ID.

Display:

* Student details if found
* Appropriate message if not found

---

## Edit Student Page

Display existing student information in a form.

Allow updates.

---

### Verify

All pages render correctly.

---

# Task 5 – Display Student Records

## Objective

Fetch student records from the Service layer.

Display them in a table.

Columns:

* Student ID
* Name
* Age
* Marks
* Actions

---

### Verify

All database records are displayed correctly.

---

# Task 6 – Process Form Submission

## Objective

Submit student data from the browser.

Flow

```text
Browser

↓

Controller

↓

Service

↓

Repository

↓

Database
```

---

### Verify

Submitting the form inserts a new student into the database.

---

# Task 7 – Edit Existing Student

## Objective

Allow users to modify student information.

---

### Steps

1. Select a student.
2. Open the edit page.
3. Update details.
4. Save changes.

---

### Verify

Updated information is reflected in the database and displayed in the student list.

---

# Task 8 – Delete Student

## Objective

Delete a student from the application.

---

### Steps

1. Select a student.
2. Click Delete.
3. Confirm deletion (optional).
4. Remove the record.

---

### Verify

The deleted student no longer appears in the list.

---

# Task 9 – Display Success Messages

## Objective

Provide user feedback after operations.

Display appropriate messages for:

* Student Added Successfully
* Student Updated Successfully
* Student Deleted Successfully
* Student Not Found
* Invalid Input

---

### Verify

Messages are displayed after each operation.

---

# Task 10 – End-to-End Testing

Verify the complete request lifecycle.

```text
Browser

↓

Spring MVC DispatcherServlet

↓

StudentController

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

# Functional Test Cases

## Test Case 1 – Home Page

Expected Result

* Home page loads successfully.
* Navigation links are visible.

---

## Test Case 2 – Add Student

Expected Result

* Student is saved successfully.
* Success message is displayed.

---

## Test Case 3 – View Students

Expected Result

* All student records are displayed in a table.

---

## Test Case 4 – Search Student

Expected Result

* Student details are displayed if found.
* Appropriate message is shown if not found.

---

## Test Case 5 – Edit Student

Expected Result

* Student information is updated successfully.

---

## Test Case 6 – Delete Student

Expected Result

* Student is removed from the database.
* Updated list is displayed.

---

# Challenge Activities (Optional)

Complete one or more of the following enhancements:

### Challenge 1

Add server-side validation for:

* Empty Name
* Invalid Age
* Invalid Marks

---

### Challenge 2

Display the total number of students on the Student List page.

---

### Challenge 3

Add a confirmation dialog before deleting a student.

---

### Challenge 4

Highlight students with marks greater than 90 using a different row style.

---

### Challenge 5

Implement search by student name in addition to student ID.

---

# Deliverables

By the end of this lab, you should submit:

* Spring MVC project with WAR packaging.
* Configured DispatcherServlet and View Resolver.
* `StudentController` with all required request mappings.
* JSP pages for Home, Student List, Add, Search, and Edit.
* Integration with the existing Service and Repository layers.
* Fully functional browser-based CRUD application.


---

# Learning Outcomes

After completing this lab, you will be able to:

* Understand the Spring MVC request lifecycle.
* Configure and use the `DispatcherServlet`.
* Develop Controllers to handle HTTP requests.
* Create JSP-based views and process HTML forms.
* Reuse existing Service and Repository layers without modification.
* Build a complete web-based CRUD application following the MVC pattern.
* Prepare the project for migration to **Spring Boot**, where much of the manual configuration (DispatcherServlet, view resolution, and deployment setup) will be simplified through auto-configuration.
