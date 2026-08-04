
A good progression would look like this:

| Module     | Enhancement                                               |
| ---------- | --------------------------------------------------------- |
| Module 1–5 | Build Student Management database (DDL, DML, Constraints) |
| Module 6–7 | Functions and basic Stored Procedures                     |
| Module 8   | Replace application SQL with Stored Procedures            |
| Module 9   | Add Triggers for auditing and validation                  |

For **Module 8**, the Java application should no longer execute SQL like:

```sql
SELECT * FROM student WHERE id=?
```

Instead, it calls:

```sql
CALL GetStudentById(?)
```

This demonstrates how enterprise applications encapsulate database access.

---

# Progressive Lab – Student Management System

## Exercise 1 – Verify Existing Data

Students should already have the `student` table.

```sql
SELECT * FROM student;
```

Expected data:

| id | name   | course | marks |
| -- | ------ | ------ | ----- |
| 1  | Rahul  | Java   | 85    |
| 2  | Anjali | Python | 92    |
| 3  | David  | DevOps | 78    |

---

# Exercise 2 – Create Procedure

## GetStudentById

```sql
DELIMITER $$

CREATE PROCEDURE GetStudentById
(
    IN studentId INT
)

BEGIN

SELECT *

FROM student

WHERE id = studentId;

END $$

DELIMITER ;
```

Execute

```sql
CALL GetStudentById(2);
```

---

# Exercise 3 – Create Procedure

## GetAllStudents

```sql
DELIMITER $$

CREATE PROCEDURE GetAllStudents()

BEGIN

SELECT *

FROM student;

END $$

DELIMITER ;
```

Execute

```sql
CALL GetAllStudents();
```

This replaces

```sql
SELECT * FROM student;
```

inside the Java application.

---

# Exercise 4 – Create Procedure

## AddStudent

```sql
DELIMITER $$

CREATE PROCEDURE AddStudent
(
IN sid INT,
IN sname VARCHAR(50),
IN scourse VARCHAR(50),
IN smarks INT
)

BEGIN

INSERT INTO student

VALUES

(
sid,
sname,
scourse,
smarks
);

END $$

DELIMITER ;
```

Execute

```sql
CALL AddStudent
(
4,
'Priya',
'AWS',
88
);
```

Verify

```sql
CALL GetAllStudents();
```

---

# Exercise 5 – Create Procedure

## UpdateStudentMarks

```sql
DELIMITER $$

CREATE PROCEDURE UpdateStudentMarks
(
IN sid INT,
IN newMarks INT
)

BEGIN

UPDATE student

SET marks = newMarks

WHERE id = sid;

END $$

DELIMITER ;
```

Execute

```sql
CALL UpdateStudentMarks
(
4,
95
);
```

Verify

```sql
CALL GetStudentById(4);
```

---

# Exercise 6 – Create Procedure

## DeleteStudent

```sql
DELIMITER $$

CREATE PROCEDURE DeleteStudent
(
IN sid INT
)

BEGIN

DELETE

FROM student

WHERE id = sid;

END $$

DELIMITER ;
```

Execute

```sql
CALL DeleteStudent(4);
```

Verify

```sql
CALL GetAllStudents();
```

---

# Exercise 7 – Highest Marks

Instead of SQL in Java:

```sql
SELECT *

FROM student

ORDER BY marks DESC

LIMIT 1;
```

Create

```sql
DELIMITER $$

CREATE PROCEDURE GetTopStudent()

BEGIN

SELECT *

FROM student

ORDER BY marks DESC

LIMIT 1;

END $$

DELIMITER ;
```

Execute

```sql
CALL GetTopStudent();
```

---

# Exercise 8 – Class Average

Instead of

```sql
SELECT AVG(marks)
FROM student;
```

Create

```sql
DELIMITER $$

CREATE PROCEDURE GetClassAverage()

BEGIN

SELECT AVG(marks)
AS AverageMarks

FROM student;

END $$

DELIMITER ;
```

Execute

```sql
CALL GetClassAverage();
```

---

# Final Challenge

Convert the complete Student Management System so that **every menu option uses a Stored Procedure** instead of writing SQL directly.

| Java Menu         | Stored Procedure       |
| ----------------- | ---------------------- |
| Add Student       | `AddStudent()`         |
| View All Students | `GetAllStudents()`     |
| Search Student    | `GetStudentById()`     |
| Update Student    | `UpdateStudentMarks()` |
| Delete Student    | `DeleteStudent()`      |
| Highest Marks     | `GetTopStudent()`      |
| Class Average     | `GetClassAverage()`    |

---

I would also slightly extend the project in **Module 9 (Triggers)** by adding one more table:

```text
student
---------
id
name
course
marks

student_audit
--------------
audit_id
student_id
operation
old_marks
new_marks
modified_time
```

Then, every **UPDATE** and **DELETE** on `student` automatically writes to `student_audit` using triggers. This gives students a realistic enterprise evolution of the same project instead of switching to unrelated examples. I think that flow will feel much more cohesive across your database course.
