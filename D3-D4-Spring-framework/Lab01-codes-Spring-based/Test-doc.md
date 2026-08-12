I think for your course, the testing should **not just be "run the application and see the output."** Since this is the students' **first Spring application**, the testing itself should teach them **how Spring works**.

Below is the lab manual I would give students.

---

# Lab Verification – Spring Core Fundamentals

## Objective

After completing the Spring Core application, you will verify that:

* Spring Container starts successfully.
* Spring discovers your components.
* Spring creates the required beans.
* Spring injects dependencies automatically.
* Bean scope works correctly.
* Bean lifecycle callbacks execute correctly.

All verification is performed **inside the Docker container**.

---

# Lab Environment

Log in to the Docker container.

```bash
docker run --rm -it \
-v $(pwd):/app \
-w /app \
maven:3.9-eclipse-temurin-21 \
bash
```

You should see

```text
root@xxxxxxxx:/app#
```

All remaining commands are executed inside this container.

---

# Task 1 – Verify the Project Structure

## Step 1

Check your current directory.

```bash
pwd
```

Expected Output

```text
/app
```

---

## Step 2

Verify the project files.

```bash
ls
```

Expected Output

```text
Dockerfile
pom.xml
src
docker-compose.yml
```

---

## Step 3

Verify Java source files.

```bash
find src/main/java
```

Expected Output

```text
src/main/java/com/college/Main.java
src/main/java/com/college/College.java
src/main/java/com/college/Department.java
src/main/java/com/college/Professor.java
src/main/java/com/college/Course.java
src/main/java/com/college/config/AppConfig.java
```

---

# Task 2 – Verify the Maven Project

Verify that Spring dependency exists.

```bash
grep spring-context pom.xml
```

Expected

```text
spring-context
```

This confirms the Spring Core dependency has been added.

---

# Task 3 – Compile the Application

Compile the project.

```bash
mvn clean compile
```

Expected Output

```text
BUILD SUCCESS
```

---

## Verification

If compilation succeeds:

✔ Java syntax is correct.

✔ Spring libraries were downloaded.

✔ All classes compiled successfully.

---

# Task 4 – Verify Compiled Classes

Check that Maven generated class files.

```bash
find target/classes
```

Expected

```text
target/classes/com/college/Main.class

target/classes/com/college/College.class

target/classes/com/college/Department.class

target/classes/com/college/Professor.class

target/classes/com/college/Course.class
```

---

# Task 5 – Start the Spring Application

Execute

```bash
mvn exec:java
```

Expected Output

```text
------------------------------------

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

------------------------------------
```

---

## Verification Questions

Did the application execute successfully?

□ Yes

□ No

---

# Task 6 – Verify Spring Created the Beans

Open

```bash
cat src/main/java/com/college/Main.java
```

Locate

```java
College college =
context.getBean(College.class);
```

---

### Question 1

Can you find

```java
new College()
```

Answer

□ Yes

□ No

---

### Question 2

Can you find

```java
new Department()
```

Answer

□ Yes

□ No

---

### Question 3

Can you find

```java
new Professor()
```

Answer

□ Yes

□ No

---

### Question 4

Can you find

```java
new Course()
```

Answer

□ Yes

□ No

---

Expected Answer

All should be

```text
No
```

---

## Learning Outcome

Spring created every object.

You did not.

---

# Task 7 – Verify Constructor Dependency Injection

Display the constructor of each class.

```bash
grep "public College" -A5 src/main/java/com/college/College.java
```

Repeat for

```text
Department

Professor
```

---

Verify

Every constructor receives its dependency.

Example

```java
public College(Department department)
```

```java
public Department(Professor professor)
```

```java
public Professor(Course course)
```

---

## Verification

Can you find

```java
new Course()
```

inside Professor?

□ Yes

□ No

Expected

```text
No
```

---

# Task 8 – Observe Spring Creating Beans

Modify the constructors.

Example

```java
public Course()
{
    System.out.println("Creating Course Bean...");
}
```

Repeat

```java
Professor

↓

Creating Professor Bean...
```

```java
Department

↓

Creating Department Bean...
```

```java
College

↓

Creating College Bean...
```

Compile again.

```bash
mvn clean compile
```

Run again.

```bash
mvn exec:java
```

Expected

```text
Creating Course Bean...

Creating Professor Bean...

Creating Department Bean...

Creating College Bean...
```

---

## Learning Outcome

Observe the order in which Spring creates beans.

Question

Which bean was created first?

Answer

```text
Course
```

---

# Task 9 – Display Beans Inside the Spring Container

Modify

Main.java

Add

```java
System.out.println("Beans in Spring Container");

for(String bean : context.getBeanDefinitionNames())
{
    System.out.println(bean);
}
```

Compile

```bash
mvn clean compile
```

Run

```bash
mvn exec:java
```

Expected

```text
Beans in Spring Container

appConfig

course

professor

department

college
```

---

## Verification

Did Spring create

College?

□ Yes

Department?

□ Yes

Professor?

□ Yes

Course?

□ Yes

---

# Task 10 – Verify Singleton Scope

Modify Main.java

```java
College c1 =
context.getBean(College.class);

College c2 =
context.getBean(College.class);

System.out.println(c1.hashCode());

System.out.println(c2.hashCode());
```

Compile

```bash
mvn clean compile
```

Run

```bash
mvn exec:java
```

Expected

```text
14523654

14523654
```

---

Question

Same hash code?

□ Yes

Meaning

Singleton Bean

---

# Task 11 – Verify Prototype Scope

Modify

Professor.java

```java
@Component

@Scope("prototype")
```

Modify Main.java

```java
Professor p1 =
context.getBean(Professor.class);

Professor p2 =
context.getBean(Professor.class);

System.out.println(p1.hashCode());

System.out.println(p2.hashCode());
```

Compile

```bash
mvn clean compile
```

Run

```bash
mvn exec:java
```

Expected

```text
15423687

98456321
```

Different hash codes.

---

Question

Different object?

□ Yes

Meaning

Prototype Bean

---

# Task 12 – Verify Bean Lifecycle

Add

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

Ensure Main.java contains

```java
context.close();
```

Compile

```bash
mvn clean compile
```

Run

```bash
mvn exec:java
```

Expected

```text
Professor Bean Initialized

College

...

Professor Bean Destroyed
```

---

# Final Verification Checklist

| Verification                               | Status |
| ------------------------------------------ | ------ |
| Project structure is correct               | ☐      |
| Maven project compiles successfully        | ☐      |
| Spring dependency is present               | ☐      |
| Spring application runs successfully       | ☐      |
| No `new` keyword used for business objects | ☐      |
| Beans are created by Spring                | ☐      |
| Constructor injection works                | ☐      |
| Spring container contains all beans        | ☐      |
| Singleton scope verified                   | ☐      |
| Prototype scope verified                   | ☐      |
| Bean lifecycle verified                    | ☐      |
| Application exits without errors           | ☐      |

