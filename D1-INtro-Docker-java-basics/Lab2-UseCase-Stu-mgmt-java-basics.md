
## Sample Assessment

### Assignment: Student Management System

**Objective**
Develop a Java console application that manages student records and package it as a Docker container.

### Functional Requirements

The application should allow users to:

1. Add a student
2. View all students
3. Search for a student by ID
4. Update student information
5. Delete a student
6. Display the student with the highest marks
7. Calculate the class average
8. Exit the application

---

## Technical Requirements

Students **must** use:

### Variables

* Store student details such as ID, name, age, and marks.

### Loops

* Display the application menu repeatedly until the user exits.

### Methods

Create separate methods such as:

* `addStudent()`
* `viewStudents()`
* `searchStudent()`
* `updateStudent()`
* `deleteStudent()`
* `calculateAverage()`

### Classes

Create at least two classes:

```text
Student
Main
```

Or

```text
Student
StudentManager
Main
```

### Arrays (or ArrayList, if you've covered it)

Store student objects.

### Basic OOP

The `Student` class should contain:

* Private fields
* Constructor(s)
* Getter and setter methods
* At least one custom method (e.g., `displayStudent()`)

---

## Docker Requirements

Students should:

1. Create a `Dockerfile`.
2. Build the Docker image.
3. Run the application inside a container.

Example:

```bash
docker build -t student-management .
docker run -it student-management
```
