# Lab 3 & Lab 4 – Employee Management Database

## Scenario Based Hands-on Lab (Progressive Learning)

---

# Lab Scenario

You have joined **ABC Telecom Pvt Ltd** as a Junior Database Engineer.

The HR department wants to build a small Employee Management System.

Initially, they only need to store employee information.

Later, they realize departments should be stored separately and employees should belong to a department.

You will gradually improve the database from a simple table to a relational database while understanding DML commands and Constraints.

---

# Learning Objectives

By the end of this lab students will be able to

* Insert records
* Retrieve records
* Update records
* Delete records
* Create Primary Keys
* Create Foreign Keys
* Apply NOT NULL
* Apply UNIQUE
* Apply DEFAULT
* Apply CHECK
* Use AUTO_INCREMENT
* Understand referential integrity
* Observe constraint violations
* Understand One-to-Many relationships

---

# Prerequisites

Running MySQL Docker Container

```bash
docker ps
```

Connect to MySQL

```bash
docker exec -it mysql mysql -u root -p
```

Login

```text
Password:
```

Create Database

```sql
CREATE DATABASE telecom;
USE telecom;
```

---

# Part 1

## Create Employee Table

```sql
CREATE TABLE employee
(
    id INT,
    name VARCHAR(50),
    salary DECIMAL(10,2),
    department VARCHAR(30)
);
```

Verify

```sql
DESC employee;
```

Expected

```
id
name
salary
department
```

---

# Task 1

## Insert Employee Records

```sql
INSERT INTO employee VALUES
(1,'John',50000,'IT'),
(2,'Mary',70000,'HR'),
(3,'Steve',90000,'IT');
```

Verify

```sql
SELECT * FROM employee;
```

Expected Output

| id | name  | salary | department |
| -- | ----- | ------ | ---------- |
| 1  | John  | 50000  | IT         |
| 2  | Mary  | 70000  | HR         |
| 3  | Steve | 90000  | IT         |

---

# Task 2

## Retrieve All Employees

```sql
SELECT * FROM employee;
```

---

# Task 3

## Employees earning more than 60,000

```sql
SELECT *
FROM employee
WHERE salary > 60000;
```

Expected

Mary

Steve

---

# Task 4

## Employees from IT Department

```sql
SELECT *
FROM employee
WHERE department='IT';
```

---

# Task 5

## Sort Employees by Salary

```sql
SELECT *
FROM employee
ORDER BY salary DESC;
```

---

# Task 6

## Update Employee Salary

Mary receives a salary revision.

```sql
UPDATE employee
SET salary=85000
WHERE id=2;
```

Verify

```sql
SELECT *
FROM employee;
```

---

# Task 7

## Delete Employee

Steve resigns from the company.

```sql
DELETE
FROM employee
WHERE id=3;
```

Verify

```sql
SELECT * FROM employee;
```

---

# Discussion

What happens after DELETE?

* Row permanently removed
* Remaining records stay unchanged
* ID sequence is unaffected

---

# End of Module 3

Now management wants a proper database design.

Instead of storing department names repeatedly, departments should have their own table.

This introduces **Relational Database Design**.

---

# Part 2

## Drop Old Employee Table

Since we are redesigning the schema.

```sql
DROP TABLE employee;
```

---

# Part 3

## Create Department Table

```sql
CREATE TABLE department
(
    dept_id INT AUTO_INCREMENT,
    dept_name VARCHAR(50) UNIQUE NOT NULL,
    location VARCHAR(50) DEFAULT 'Bangalore',

    PRIMARY KEY(dept_id)
);
```

Explain

* AUTO_INCREMENT
* PRIMARY KEY
* UNIQUE
* NOT NULL
* DEFAULT

---

# Verify

```sql
DESC department;
```

---

# Task 8

## Insert Departments

```sql
INSERT INTO department(dept_name)
VALUES
('IT'),
('HR'),
('Finance');
```

Verify

```sql
SELECT *
FROM department;
```

Expected

| dept_id | dept_name | location  |
| ------- | --------- | --------- |
| 1       | IT        | Bangalore |
| 2       | HR        | Bangalore |
| 3       | Finance   | Bangalore |

Notice

Location automatically becomes Bangalore.

---

# Task 9

## Create Employee Table with Constraints

```sql
CREATE TABLE employee
(
    emp_id INT AUTO_INCREMENT,

    emp_name VARCHAR(50) NOT NULL,

    email VARCHAR(100) UNIQUE,

    salary DECIMAL(10,2)
        CHECK(salary>=25000),

    dept_id INT,

    PRIMARY KEY(emp_id),

    FOREIGN KEY(dept_id)
    REFERENCES department(dept_id)
);
```

Explain

* Primary Key
* Foreign Key
* Unique
* Check
* Auto Increment
* Not Null

---

# Verify

```sql
DESC employee;
```

---

# Task 10

## Insert Valid Employees

```sql
INSERT INTO employee(emp_name,email,salary,dept_id)
VALUES
('John','john@abc.com',50000,1),
('Mary','mary@abc.com',70000,2),
('Steve','steve@abc.com',90000,1);
```

Verify

```sql
SELECT * FROM employee;
```

---

# Task 11

## View Both Tables

Departments

```sql
SELECT * FROM department;
```

Employees

```sql
SELECT * FROM employee;
```

Observe

Employees belong to departments using dept_id.

---

# Task 12

## Try Duplicate Email

```sql
INSERT INTO employee(emp_name,email,salary,dept_id)
VALUES
('David','john@abc.com',45000,1);
```

Expected

```
Duplicate entry
```

Reason

UNIQUE constraint.

---

# Task 13

## Try NULL Name

```sql
INSERT INTO employee(emp_name,email,salary,dept_id)
VALUES
(NULL,'abc@abc.com',40000,1);
```

Expected

```
Column 'emp_name' cannot be null
```

Reason

NOT NULL constraint.

---

# Task 14

## Invalid Salary

```sql
INSERT INTO employee(emp_name,email,salary,dept_id)
VALUES
('Tom','tom@abc.com',10000,1);
```

Expected

CHECK constraint violation (if enforced by your MySQL version).

Explain that older MySQL versions ignored CHECK constraints, while MySQL 8.0.16+ enforces them.

---

# Task 15

## Invalid Department

```sql
INSERT INTO employee(emp_name,email,salary,dept_id)
VALUES
('Kevin','kevin@abc.com',50000,10);
```

Expected

```
Cannot add or update child row
```

Reason

Department 10 does not exist.

---

# Task 16

## Delete Parent Record

Try deleting IT Department.

```sql
DELETE
FROM department
WHERE dept_id=1;
```

Expected

```
Cannot delete or update parent row
```

Reason

Employees still belong to IT.

This is called a **Foreign Key Constraint Violation**.

---

# Task 17

## Delete Child Records First

```sql
DELETE
FROM employee
WHERE dept_id=1;
```

Now delete department.

```sql
DELETE
FROM department
WHERE dept_id=1;
```

Verify

```sql
SELECT * FROM department;
```

---

# Relationship Demonstration

## One Department → Many Employees

Example

```
Department
------------
IT

Employees
-------------
John
Steve
Kevin
```

One department can have multiple employees.

This is called a **One-to-Many Relationship**.

---

## One-to-One Relationship (Concept)

```
Employee
     |
Passport
```

One employee owns one passport.

---

## Many-to-Many Relationship (Concept)

```
Students

    ↕
Enrollment

    ↕
Courses
```

One student can enroll in many courses.

One course can have many students.

Implemented using a junction table.

---

# Challenge Exercise

Perform the following without looking at the solution:

1. Add a new department **Network**.
2. Add three employees to Network.
3. Increase all IT employee salaries by 10%.
4. Find employees earning more than ₹60,000.
5. Find employees in HR.
6. Delete one employee.
7. Try inserting an employee with a duplicate email.
8. Try deleting a department that still has employees.
9. Explain why the operation failed.
10. Delete the employees first, then successfully delete the department.

---

# Lab Outcome

At the end of this lab, students will have:

* Performed complete CRUD operations using DML commands.
* Designed normalized tables using relational principles.
* Applied PRIMARY KEY, FOREIGN KEY, UNIQUE, NOT NULL, DEFAULT, CHECK, and AUTO_INCREMENT constraints.
* Understood how relationships enforce data integrity.
* Experienced real-world constraint violations and learned how to resolve them.
* Built a simple but realistic Employee Management Database similar to what is used in enterprise applications.
