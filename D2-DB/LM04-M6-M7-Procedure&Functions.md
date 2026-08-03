# Lab 6 & Lab 7 – Stored Procedures and Functions in MySQL

## Course

Database Fundamentals with MySQL using Docker

## Modules

- Module 6 – Stored Procedures
- Module 7 – Functions

## Lab Duration

**90 Minutes**

---

# Lab Objectives

After completing this lab, you will be able to:

- Create Stored Procedures
- Execute Stored Procedures
- Create Parameterized Procedures
- Create MySQL Functions
- Execute Functions
- Use Functions inside SQL queries
- Understand when to use Procedures and Functions

---

# Lab Prerequisites

Before starting this lab, ensure:

- Docker Desktop is running.
- The **mysql-db** container is running.
- MySQL 8 is installed inside Docker.
- You have completed:
  - Lab 1 – Running MySQL using Docker
  - Lab 2 – Creating Databases and Tables
  - DML Lab (INSERT, UPDATE, DELETE, SELECT)

The **employee** table should already contain sample data.

---

# Verify the Docker Container

Open Git Bash or PowerShell.

```bash
docker ps
```

Expected Output

```
CONTAINER ID    IMAGE      STATUS
xxxxxxxxxxxx    mysql:8    Up
```

---

# Connect to MySQL

```bash
docker exec -it mysql-db mysql -uroot -p
```

Password

```
root123
```

Select the training database.

```sql
USE training;
```

Verify.

```sql
SELECT DATABASE();
```

Expected Output

```
training
```

---

# Verify the Employee Table

Display all records.

```sql
SELECT * FROM employee;
```

Example

| id | name | salary | dept |
|----|------|---------|------|
|1|John|50000|IT|
|2|Mary|70000|HR|
|3|Steve|90000|IT|

If your table is empty, insert a few sample records before continuing.

---

# Module 6 – Stored Procedures

---

# Exercise 1 – Create Your First Stored Procedure

A Stored Procedure stores SQL statements inside the database.

Since MySQL uses **;** to terminate SQL statements, we temporarily change the delimiter.

Execute the following.

```sql
DELIMITER $$

CREATE PROCEDURE GetEmployees()
BEGIN
    SELECT * FROM employee;
END $$

DELIMITER ;
```

Expected Output

```
Query OK
```

---

# Exercise 2 – Verify the Procedure

Display all procedures in the current database.

```sql
SHOW PROCEDURE STATUS
WHERE Db='training';
```

Expected Output

```
GetEmployees
```

---

# Exercise 3 – Execute the Procedure

Execute the procedure.

```sql
CALL GetEmployees();
```

Expected Output

| id | name | salary | dept |
|----|------|---------|------|
|1|John|50000|IT|
|2|Mary|70000|HR|
|3|Steve|90000|IT|

Notice that no SQL query was written during execution.

The procedure performed the query internally.

---

# Exercise 4 – Create a Parameterized Procedure

A parameterized procedure accepts input values.

Execute

```sql
DELIMITER $$

CREATE PROCEDURE GetEmployeeByDept(
    IN dept_name VARCHAR(30)
)
BEGIN
    SELECT *
    FROM employee
    WHERE dept = dept_name;
END $$

DELIMITER ;
```

Expected Output

```
Query OK
```

---

# Exercise 5 – Execute the Parameterized Procedure

Retrieve IT employees.

```sql
CALL GetEmployeeByDept('IT');
```

Expected Output

| id | name | salary | dept |
|----|------|---------|------|
|1|John|50000|IT|
|3|Steve|90000|IT|

---

Retrieve HR employees.

```sql
CALL GetEmployeeByDept('HR');
```

Expected Output

| id | name | salary | dept |
|----|------|---------|------|
|2|Mary|70000|HR|

---

# Exercise 6 – View Stored Procedures

Display all stored procedures.

```sql
SHOW PROCEDURE STATUS
WHERE Db='training';
```

Observe

- GetEmployees
- GetEmployeeByDept

---

# Exercise 7 – Remove a Procedure (Optional)

Delete a procedure.

```sql
DROP PROCEDURE GetEmployees;
```

Verify.

```sql
SHOW PROCEDURE STATUS
WHERE Db='training';
```

---

# Module 7 – Functions

---

# What is a Function?

Unlike Procedures,

Functions return a single value.

They can be used inside SQL statements.

---

# Exercise 8 – Create Your First Function

Execute

```sql
DELIMITER $$

CREATE FUNCTION AnnualSalary(
    monthly DECIMAL(10,2)
)
RETURNS DECIMAL(10,2)
DETERMINISTIC
BEGIN
    RETURN monthly * 12;
END $$

DELIMITER ;
```

> **Note:** `DETERMINISTIC` indicates that the function always returns the same output for the same input.

Expected Output

```
Query OK
```

---

# Exercise 9 – Execute the Function

Calculate the annual salary for an employee earning ₹50,000 per month.

```sql
SELECT AnnualSalary(50000);
```

Expected Output

| AnnualSalary(50000) |
|---------------------|
|600000|

---

Calculate another salary.

```sql
SELECT AnnualSalary(75000);
```

Expected Output

```
900000
```

---

# Exercise 10 – Use the Function with a Table

Functions become more useful when combined with SQL queries.

Execute

```sql
SELECT
    name,
    salary,
    AnnualSalary(salary) AS annual_salary
FROM employee;
```

Expected Output

| name | salary | annual_salary |
|------|---------|---------------|
|John|50000|600000|
|Mary|70000|840000|
|Steve|90000|1080000|

Observe how the function is applied to every row.

---

# Exercise 11 – View Functions

Display all functions.

```sql
SHOW FUNCTION STATUS
WHERE Db='training';
```

Expected Output

```
AnnualSalary
```

---

# Exercise 12 – Remove a Function (Optional)

Delete the function.

```sql
DROP FUNCTION AnnualSalary;
```

Verify.

```sql
SHOW FUNCTION STATUS
WHERE Db='training';
```

---

# Challenge Exercise

### Task 1

Create a procedure named

```
GetHighSalaryEmployees
```

It should display employees earning more than **60000**.

---

### Task 2

Create a parameterized procedure

```
GetEmployeeBySalary
```

Accept a salary value and display employees earning more than that amount.

---

### Task 3

Create a function

```
MonthlyBonus()
```

Bonus = Monthly Salary × 10%

Example

```
50000

↓

5000
```

---

### Task 4

Display

- Employee Name
- Monthly Salary
- Bonus
- Annual Salary

using your functions.

---

# Procedure vs Function

| Stored Procedure | Function |
|------------------|----------|
|Executed using CALL|Used inside SELECT|
|Can return result sets|Returns one value|
|Can perform multiple SQL operations|Primarily performs calculations|
|Ideal for workflows|Ideal for reusable business logic|

---

# Learning Outcome

After completing this lab, you should be able to:

- Create Stored Procedures
- Execute Procedures using `CALL`
- Pass parameters to Procedures
- Create reusable Functions
- Return calculated values
- Use Functions inside SQL queries
- Apply business logic at the database layer

---

# Troubleshooting

## ERROR 1304

```
PROCEDURE already exists
```

Solution

```sql
DROP PROCEDURE GetEmployees;
```

or

```sql
DROP PROCEDURE GetEmployeeByDept;
```

Then recreate it.

---

## ERROR 1304

```
FUNCTION already exists
```

Solution

```sql
DROP FUNCTION AnnualSalary;
```

Create the function again.

---

## Procedure Not Found

Verify

```sql
SHOW PROCEDURE STATUS
WHERE Db='training';
```

---

## Function Not Found

Verify

```sql
SHOW FUNCTION STATUS
WHERE Db='training';
```

---

## Unknown Database

Verify the active database.

```sql
SELECT DATABASE();
```

If required

```sql
USE training;
```

---

# Lab Summary

In this lab, you successfully:

- Created and executed Stored Procedures
- Passed input parameters to Procedures
- Queried employee data using reusable Procedures
- Created reusable MySQL Functions
- Returned calculated values from Functions
- Applied Functions within SQL queries to derive annual salaries

You have now completed the core MySQL programming concepts. These skills are widely used in enterprise applications for encapsulating business logic, improving code reusability, and enhancing database performance.