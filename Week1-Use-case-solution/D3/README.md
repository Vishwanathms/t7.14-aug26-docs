# Phase 3 - Spring Framework Conversion

This project is intentionally the progression from:

Phase 1: Core Java + Arrays
Phase 2: Core Java + JDBC + MySQL
Phase 3: Spring Core + Spring JDBC + MySQL

Changes from Phase 2:
- DBConnection removed -> Spring DataSource bean
- DriverManager removed -> JdbcTemplate
- StudentDAO becomes interface
- StudentDAOImpl annotated with @Repository
- StudentService layer added
- Dependency Injection replaces new StudentDAO()
- AppConfig replaces manual object creation

The console menu remains the same so students focus on Spring concepts.


# Execution 

```bash
docker build -t student-management:sf-db .

docker run -it --name student-app --network student-network student-management:sf-db
```

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
        │           └── Main.java
        │
        └── resources