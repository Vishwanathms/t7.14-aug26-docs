# Phase 4 - Spring MVC (No Spring Boot)

This project is intentionally the progression from:

Phase 1: Core Java + Arrays
Phase 2: Core Java + JDBC + MySQL
Phase 3: Spring Core + Spring JDBC + MySQL
Phase 4: Spring MVC (plain, no Spring Boot) - browser UI

Changes from Phase 3:
- Packaging changed from jar to war (deployable to any Servlet container)
- Added spring-webmvc, jakarta.servlet-api, and JSTL dependencies
- `WebAppInitializer` (implements `WebApplicationInitializer`) replaces web.xml
  and registers the `DispatcherServlet` - this is the Java-config equivalent
  of the classic `web.xml` + `<servlet>` entries students see in traditional
  Spring MVC tutorials
- `WebConfig` (`@EnableWebMvc`) configures the JSP view resolver
- `AppConfig` (Phase 3) is now the **root** Spring context, shared by the
  MVC (child) context - same Service/DAO beans, no changes needed there
- New `StudentController` exposes the existing `StudentService` over HTTP
- New JSP views under `src/main/webapp/WEB-INF/views` render the UI:
  `list.jsp`, `form.jsp` (add/edit), `stats.jsp` (highest marks + average)
- The old console menu (`Main.java`) is left in place for reference/comparison
  but is no longer the deployed entry point - the app now runs inside Tomcat

Routes:
- `GET /students` - list all students
- `GET /students/add` , `POST /students/add` - add student form + submit
- `GET /students/edit/{id}` , `POST /students/edit/{id}` - edit form + submit
- `GET /students/delete/{id}` - delete student
- `GET /students/stats` - topper + class average


# Execution

```bash
docker build -t student-management:mvc .

docker run -it --name student-app -p 8080:8080 --network student-network student-management:mvc
```

Then open http://localhost:8080/students in a browser.

* Make sure the DB is running with contianer name mysql-db and on port 3306
if not run the below command 

```bash

docker run -d --name mysql-db --network student-network -e MYSQL_ROOT_PASSWORD=root123 -e MYSQL_DATABASE=studentdb -p 4306:3306 mysql:8.4
```


StudentManagementSpring
│
├── pom.xml
│
└── src
    └── main
        ├── java
        │
        │   └── com
        │       └── college
        │
        │           ├── config
        │           │      AppConfig.java
        │           │      WebConfig.java
        │           │
        │           ├── web
        │           │      WebAppInitializer.java
        │           │
        │           ├── controller
        │           │      StudentController.java
        │           │
        │           ├── dao
        │           │      StudentDAO.java
        │           │      StudentDAOImpl.java
        │           │
        │           ├── model
        │           │      Student.java
        │           │
        │           ├── service
        │           │      StudentService.java
        │           │      StudentServiceImpl.java
        │           │
        │           └── Main.java (legacy console entry point)
        │
        ├── resources
        │      application.properties
        │
        └── webapp
               └── WEB-INF
                      └── views
                             list.jsp
                             form.jsp
                             stats.jsp