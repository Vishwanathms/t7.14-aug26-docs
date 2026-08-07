# Phase 5 - ORM with Spring Data JPA / Hibernate

This project is intentionally the progression from:

Phase 1: Core Java + Arrays
Phase 2: Core Java + JDBC + MySQL
Phase 3: Spring Core + Spring JDBC + MySQL
Phase 4: Spring MVC (plain, no Spring Boot) - browser UI
Phase 5: Spring MVC + ORM (Spring Data JPA / Hibernate) - no hand-written SQL

Changes from Phase 4 (files marked **[UPDATED]** were modified, everything
else was copied unchanged from D4):

- **[UPDATED]** `pom.xml` - removed nothing, added `spring-orm`,
  `spring-data-jpa`, and `hibernate-core` dependencies
- **[UPDATED]** `model/Student.java` - annotated `@Entity` / `@Table` / `@Id`;
  the class itself now declares the table mapping, no `RowMapper` needed
- **[DELETED]** `dao/StudentDAO.java`, `dao/StudentDAOImpl.java` - no longer
  needed; there is no DAO implementation to write at all
- **[NEW]** `repository/StudentRepository.java` - an interface extending
  `JpaRepository<Student, Integer>`. `save()`, `findById()`, `findAll()`,
  `deleteById()` come for free. Only two custom queries are declared
  (`findAllOrderByMarksDesc`, `findAverageMarks`) using JPQL, not SQL.
- **[UPDATED]** `service/StudentServiceImpl.java` - now depends on
  `StudentRepository` instead of `StudentDAO`, calls repository methods
  instead of hand-written SQL, and adds `@Transactional` on writes
- **[UPDATED]** `config/AppConfig.java` - replaced the `JdbcTemplate` bean
  with `LocalContainerEntityManagerFactoryBean` (Hibernate as JPA provider),
  a `JpaTransactionManager`, `@EnableJpaRepositories`, and
  `@EnableTransactionManagement`. The `DataSource` bean is unchanged.
- Unchanged: `StudentService` interface, `StudentController`, `WebConfig`,
  `WebAppInitializer`, all JSP views, `Main.java` (console entry point still
  works against the same `StudentService` interface, now backed by JPA)

The controller, views, and service *interface* never had to change - only the
persistence layer underneath did. That's the point of this lesson: swapping
JDBC for an ORM should not ripple up through the rest of the app.


## Before (D4 - JdbcTemplate) vs After (D5 - JPA/Hibernate)

### Model

```java
// D4: plain POJO, no persistence metadata
public class Student {
    private int id;
    private String name;
    ...
}
```
```java
// D5: the class itself declares the table mapping
@Entity
@Table(name = "students")
public class Student {
    @Id
    private int id;
    private String name;
    ...
}
```

### Data access layer

```java
// D4: dao/StudentDAOImpl.java - hand-written SQL + manual row mapping
@Repository
public class StudentDAOImpl implements StudentDAO {
    private final JdbcTemplate jdbcTemplate;

    public void addStudent(Student student) {
        String sql = "INSERT INTO students(id,name,age,marks) VALUES(?,?,?,?)";
        jdbcTemplate.update(sql, student.getId(), student.getName(),
                student.getAge(), student.getMarks());
    }

    public Student searchStudent(int id) {
        String sql = "SELECT * FROM students WHERE id=?";
        List<Student> students = jdbcTemplate.query(sql, studentRowMapper, id);
        return students.isEmpty() ? null : students.get(0);
    }
    // ...update, delete, highest marks, average - all raw SQL
}
```
```java
// D5: repository/StudentRepository.java - an interface, zero implementation
public interface StudentRepository extends JpaRepository<Student, Integer> {

    // save(), findById(), findAll(), deleteById() are inherited - no code needed

    @Query("SELECT s FROM Student s ORDER BY s.marks DESC")
    List<Student> findAllOrderByMarksDesc();

    @Query("SELECT AVG(s.marks) FROM Student s")
    Double findAverageMarks();
}
```

### Service layer

```java
// D4: service/StudentServiceImpl.java
public void addStudent(Student student) {
    studentDAO.addStudent(student);
}
public Student searchStudent(int id) {
    return studentDAO.searchStudent(id);
}
```
```java
// D5: service/StudentServiceImpl.java
@Transactional
public void addStudent(Student student) {
    studentRepository.save(student);
}
public Student searchStudent(int id) {
    return studentRepository.findById(id).orElse(null);
}
```

### Spring configuration

```java
// D4: config/AppConfig.java - DataSource + JdbcTemplate
@Bean
public DataSource dataSource() { ... }

@Bean
public JdbcTemplate jdbcTemplate(DataSource dataSource) {
    return new JdbcTemplate(dataSource);
}
```
```java
// D5: config/AppConfig.java - DataSource is unchanged, JdbcTemplate is
// replaced by an EntityManagerFactory + JpaTransactionManager
@EnableJpaRepositories(basePackages = "com.college.repository")
@EnableTransactionManagement
public class AppConfig {

    @Bean
    public DataSource dataSource() { ... }   // identical to D4

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setPersistenceProvider(new HibernatePersistenceProvider());
        emf.setPackagesToScan("com.college.model");
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        return emf;
    }

    @Bean
    public PlatformTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean emf) {
        return new JpaTransactionManager(emf.getObject());
    }
}
```

### The one-line takeaway

**JdbcTemplate**: you tell Spring *how* to talk to the table (write the SQL,
map the row yourself).
**JPA/Hibernate**: you tell Spring *what* the table looks like (`@Entity`)
and *what* you want (`save`, `findById`) - Hibernate generates the SQL.

`hibernate.hbm2ddl.auto=update` is set in `AppConfig` so Hibernate will
create/update the `students` table from the `@Entity` automatically; you no
longer need to run `studentdb.sql` by hand for this phase (though it's kept
here for reference / comparison with Phase 2-4).

Routes (same as Phase 4):
- `GET /students` - list all students
- `GET /students/add` , `POST /students/add` - add student form + submit
- `GET /students/edit/{id}` , `POST /students/edit/{id}` - edit form + submit
- `GET /students/delete/{id}` - delete student
- `GET /students/stats` - topper + class average


# Execution

```bash
docker build -t student-management:jpa .

docker run -it --name student-app -p 8080:8080 --network student-network student-management:jpa
```

Then open http://localhost:8080/students in a browser.

* Make sure the DB is running with container name mysql-db and on port 3306
if not run the below command

```bash

docker run -d --name mysql-db --network student-network -e MYSQL_ROOT_PASSWORD=root123 -e MYSQL_DATABASE=studentdb -p 4306:3306 mysql:8.4
```


StudentManagementSpring
│
├── pom.xml                     [UPDATED]
│
└── src
    └── main
        ├── java
        │
        │   └── com
        │       └── college
        │
        │           ├── config
        │           │      AppConfig.java        [UPDATED]
        │           │      WebConfig.java
        │           │
        │           ├── web
        │           │      WebAppInitializer.java
        │           │
        │           ├── controller
        │           │      StudentController.java
        │           │
        │           ├── repository
        │           │      StudentRepository.java  [NEW]
        │           │
        │           ├── model
        │           │      Student.java           [UPDATED]
        │           │
        │           ├── service
        │           │      StudentService.java
        │           │      StudentServiceImpl.java [UPDATED]
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
