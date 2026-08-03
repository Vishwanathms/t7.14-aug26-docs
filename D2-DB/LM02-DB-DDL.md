# Lab 2 – Creating and Modifying Tables Using DDL Commands

## Objective

In this lab, you will learn how to:

- Create a new database
- Select a database for use
- Create a table
- Add a new column
- Rename an existing column
- Drop an existing column
- Verify all changes using SQL commands

---

# Prerequisites

Before starting this lab, ensure:

- Docker Desktop is running.
- MySQL container (`mysql-db`) is running.
- You can connect to MySQL using the root user.

Connect to MySQL:

```bash
docker exec -it mysql-db mysql -uroot -p
```

Password:

```
root123
```

You should see:

```sql
mysql>
```

---

# Lab Overview

In this lab, you will build the following database.

```
training
    │
    └── employee
            │
            ├── id
            ├── name
            ├── salary
            └── department
```

Later, you will modify the table by:

- Adding a new column
- Renaming a column
- Removing a column

---

# Step 1 – View Existing Databases

Execute:

```sql
SHOW DATABASES;
```

Expected Output

```text
+--------------------+
| Database           |
+--------------------+
| company            |
| mysql              |
| information_schema |
| performance_schema |
| sys                |
+--------------------+
```

---

# Step 2 – Create a New Database

Create a database named **training**.

```sql
CREATE DATABASE training;
```

Expected Output

```text
Query OK, 1 row affected
```

---

# Step 3 – Verify Database Creation

Run:

```sql
SHOW DATABASES;
```

Expected Output

```text
+--------------------+
| Database           |
+--------------------+
| company            |
| training           |
| mysql              |
| information_schema |
| performance_schema |
| sys                |
+--------------------+
```

The **training** database should now appear in the list.

---

# Step 4 – Select the Database

Switch to the newly created database.

```sql
USE training;
```

Expected Output

```text
Database changed
```

Verify the current database.

```sql
SELECT DATABASE();
```

Expected Output

```text
+------------+
| DATABASE() |
+------------+
| training   |
+------------+
```

---

# Step 5 – Create the Employee Table

Execute the following SQL statement.

```sql
CREATE TABLE employee(
id INT PRIMARY KEY,
name VARCHAR(50),
salary DECIMAL(10,2),
department VARCHAR(30)
);
```

Expected Output

```text
Query OK
```

---

# Step 6 – Verify the Table

Display all tables.

```sql
SHOW TABLES;
```

Expected Output

```text
+-------------------+
| Tables_in_training|
+-------------------+
| employee          |
+-------------------+
```

---

# Step 7 – View Table Structure

Display the table definition.

```sql
DESCRIBE employee;
```

Expected Output

```text
+------------+---------------+------+-----+
| Field      | Type          | Null | Key |
+------------+---------------+------+-----+
| id         | int           | NO   | PRI |
| name       | varchar(50)   | YES  |     |
| salary     | decimal(10,2) | YES  |     |
| department | varchar(30)   | YES  |     |
+------------+---------------+------+-----+
```

Observe:

- Four columns
- `id` is the Primary Key

---

# Step 8 – Add a New Column

Add an email column.

```sql
ALTER TABLE employee
ADD email VARCHAR(100);
```

Expected Output

```text
Query OK
```

---

# Step 9 – Verify the Updated Structure

Execute:

```sql
DESCRIBE employee;
```

Expected Output

```text
+------------+----------------+
| Field      | Type           |
+------------+----------------+
| id         | int            |
| name       | varchar(50)    |
| salary     | decimal(10,2)  |
| department | varchar(30)    |
| email      | varchar(100)   |
+------------+----------------+
```

Notice the newly added **email** column.

---

# Step 10 – Rename a Column

Rename **department** to **dept**.

```sql
ALTER TABLE employee
RENAME COLUMN department TO dept;
```

Expected Output

```text
Query OK
```

---

# Step 11 – Verify the Renamed Column

Execute:

```sql
DESCRIBE employee;
```

Expected Output

```text
+---------+----------------+
| Field   | Type           |
+---------+----------------+
| id      | int            |
| name    | varchar(50)    |
| salary  | decimal(10,2)  |
| dept    | varchar(30)    |
| email   | varchar(100)   |
+---------+----------------+
```

Observe that **department** has been renamed to **dept**.

---

# Step 12 – Drop a Column

Remove the email column.

```sql
ALTER TABLE employee
DROP COLUMN email;
```

Expected Output

```text
Query OK
```

---

# Step 13 – Verify the Final Table Structure

Execute:

```sql
DESCRIBE employee;
```

Expected Output

```text
+---------+---------------+------+-----+
| Field   | Type          | Null | Key |
+---------+---------------+------+-----+
| id      | int           | NO   | PRI |
| name    | varchar(50)   | YES  |     |
| salary  | decimal(10,2) | YES  |     |
| dept    | varchar(30)   | YES  |     |
+---------+---------------+------+-----+
```

The **email** column should no longer be present.

---

# Complete SQL Script

```sql
CREATE DATABASE training;

USE training;

CREATE TABLE employee(
id INT PRIMARY KEY,
name VARCHAR(50),
salary DECIMAL(10,2),
department VARCHAR(30)
);

ALTER TABLE employee
ADD email VARCHAR(100);

ALTER TABLE employee
RENAME COLUMN department TO dept;

ALTER TABLE employee
DROP COLUMN email;
```

---

# Challenge Exercise

Perform the following tasks on your own.

1. Create a new database called **college**.

2. Create a table named **student** with the following columns:

| Column | Data Type |
|---------|-----------|
| id | INT |
| name | VARCHAR(50) |
| branch | VARCHAR(30) |
| marks | DECIMAL(5,2) |

3. Add a column named **email**.

4. Rename **branch** to **department**.

5. Drop the **email** column.

6. Verify the final table structure using:

```sql
DESCRIBE student;
```

---

# Learning Outcome

After completing this lab, you should be able to:

- Create databases
- Select databases
- Create tables using DDL
- Define data types
- Configure Primary Keys
- Add new columns using `ALTER`
- Rename existing columns
- Remove unwanted columns
- Verify table structures using `DESCRIBE`

---

# Common Commands Used

| Command | Purpose |
|----------|---------|
| SHOW DATABASES | List all databases |
| USE | Select a database |
| CREATE DATABASE | Create a new database |
| CREATE TABLE | Create a new table |
| SHOW TABLES | Display tables |
| DESCRIBE | View table structure |
| ALTER TABLE ADD | Add a column |
| ALTER TABLE RENAME COLUMN | Rename a column |
| ALTER TABLE DROP COLUMN | Remove a column |

---

# Troubleshooting

### Database already exists

```sql
ERROR 1007
```

Solution:

```sql
DROP DATABASE training;
```

Then recreate it.

---

### Table already exists

```sql
ERROR 1050
```

Solution:

```sql
DROP TABLE employee;
```

Create the table again.

---

### Unknown column

If you receive:

```sql
Unknown column
```

Run:

```sql
DESCRIBE employee;
```

Verify the current column names before executing the next command.

---

# Lab Summary

In this lab, you successfully:

- Created a new database
- Created an employee table
- Defined appropriate data types
- Added a new column
- Renamed an existing column
- Removed an unwanted column
- Verified each change using MySQL commands

You are now ready to move on to **DML (Data Manipulation Language)**, where you will insert, update, delete, and retrieve data from tables.