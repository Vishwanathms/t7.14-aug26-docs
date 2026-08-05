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