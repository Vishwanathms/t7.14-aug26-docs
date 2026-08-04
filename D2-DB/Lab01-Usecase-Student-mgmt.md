I actually recommend making the **entire database course one continuous capstone** rather than unrelated labs. Since students already have a **Java Console Student Management System** connected to MySQL, every database module should enhance the same application. This closely resembles how enterprise projects evolve.

---

# Progressive Database Lab Scenario

## Student Management System (Enterprise Evolution)

### Project Overview

You have joined a software company that is developing a **Student Management System** for a training institute.

Initially, the application stores data in a simple table. As new business requirements arrive, you will enhance the database design and move more business logic into MySQL.

Each module builds on the previous one.

---

# Module 1 – Database Fundamentals

## Scenario

The company wants to replace Excel-based student records with a database-driven application.

### Your Tasks

* Install and run MySQL using Docker.
* Connect to MySQL from the terminal.
* Explore the MySQL environment.
* Create a new database for the Student Management System.
* Understand how databases, tables, rows, and columns are organized.
* Identify suitable data types for student information.

### Deliverables

* Running MySQL container
* Student database created
* Database successfully selected

---

# Module 2 – Database Structure (DDL)

## Scenario

The development team needs the database structure before the Java developers can start coding.

### Your Tasks

Design the Student table with appropriate columns.

Later, the business requests structural changes.

Modify the table by

* Adding a new column
* Renaming an existing column
* Removing an obsolete column

### Deliverables

* Student table created
* Table modified successfully
* Updated schema verified

---

# Module 3 – Managing Student Data (DML)

## Scenario

The Java application is now ready to store student records.

Populate the database with sample student information.

Perform operations such as

* Registering students
* Updating marks
* Correcting student details
* Removing students who leave the course
* Viewing all students
* Searching for specific students
* Sorting students by marks
* Displaying top-performing students

### Deliverables

* Student records inserted
* Queries return expected results
* Reports generated successfully

---

# Module 4 – Data Integrity

## Scenario

The institute notices duplicate student IDs and invalid records.

The database must enforce business rules.

### Your Tasks

Apply suitable constraints.

Ensure

* Every student has a unique ID.
* Mandatory information cannot be empty.
* Default values are assigned where appropriate.
* Invalid values cannot be inserted.
* Automatically generate identifiers where required.

Design relationships for future expansion.

Examples

* Student → Course
* Student → Department

### Deliverables

* Constraints implemented
* Relationships established
* Invalid data prevented

---

# Module 5 – Advanced Database Design

## Scenario

The institute expands into multiple departments.

Instead of storing everything in one table, normalize the database.

### Your Tasks

Split the database into related tables.

Examples

* Student
* Course
* Department

Use foreign keys to connect them.

Update the Java application queries accordingly.

### Deliverables

* Normalized database
* Reduced redundancy
* Related tables functioning correctly

---

# Module 6 – Stored Procedures

## Scenario

The Java developers complain that SQL queries are duplicated throughout the application.

Management decides that database operations should be centralized.

### Your Tasks

Replace SQL queries with Stored Procedures.

Create procedures for

* Register Student
* View All Students
* Search Student by ID
* Update Student Information
* Delete Student

Verify that each menu option can be implemented using procedures.

### Deliverables

* CRUD procedures created
* Procedures tested
* Java application ready to call procedures

---

# Module 7 – Database Functions

## Scenario

Management wants reusable calculations inside the database.

Instead of calculating values in Java, calculations should happen in MySQL.

### Your Tasks

Create functions for

* Annual fee calculation
* Percentage calculation
* Grade calculation
* Pass/Fail determination

Use these functions inside SQL queries.

### Deliverables

* Functions created
* Functions return expected values
* Reports include calculated values

---

# Module 8 – Enterprise Stored Procedures

## Scenario

The institute introduces management reports.

Instead of allowing direct table access, every business operation must execute through Stored Procedures.

### Your Tasks

Develop procedures for

* View Student by ID
* View Students by Course
* Display Highest Marks
* Display Class Average
* Update Student Marks
* Promote Student to Next Semester (optional)
* Generate Summary Report

Verify that all reports execute through procedures.

### Deliverables

* Reporting procedures completed
* Business logic centralized
* Reusable database APIs available

---

# Module 9 – Database Automation Using Triggers

## Scenario

The management team wants to track every important database change.

Whenever student information changes, the system should automatically record the activity.

### Your Tasks

Implement triggers to

* Record every student insertion.
* Record every update.
* Record every deletion.
* Prevent invalid marks from being stored.
* Automatically update timestamps whenever records change.

Create an audit table to maintain change history.

Verify that database events execute automatically without modifying the Java application.

### Deliverables

* Audit logging implemented
* Automatic validation working
* Trigger execution verified

---

# Final Capstone

## Enterprise Student Management Database

At the end of Module 9, your Java Console Application should support:

✓ Add Student

✓ View All Students

✓ Search Student

✓ Update Student

✓ Delete Student

✓ Highest Marks Report

✓ Class Average Report

✓ Grade Calculation

✓ Pass/Fail Evaluation

✓ Data Validation

✓ Automatic Audit Logging

✓ Business Logic through Stored Procedures

✓ Automatic Database Events using Triggers

---

# Learning Journey

```text
Module 1
Database Basics
        │
        ▼
Module 2
Database Structure
        │
        ▼
Module 3
Manage Student Records
        │
        ▼
Module 4
Protect Data
        │
        ▼
Module 5
Normalize Database
        │
        ▼
Module 6
Stored Procedures
        │
        ▼
Module 7
Functions
        │
        ▼
Module 8
Enterprise Database APIs
        │
        ▼
Module 9
Automation with Triggers
        │
        ▼
Production-Ready Student Management Database
```

This progression keeps students working on **one consistent project** throughout the course. Every new database concept extends the existing Student Management System, helping them understand not just SQL syntax but how database features are applied incrementally in real software development.
