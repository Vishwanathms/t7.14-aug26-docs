package com.college.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.college.model.Student;

@Repository
public class StudentDAOImpl implements StudentDAO {

    private final JdbcTemplate jdbcTemplate;

    public StudentDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // RowMapper
    private RowMapper<Student> studentRowMapper = new RowMapper<Student>() {

        @Override
        public Student mapRow(ResultSet rs, int rowNum) throws SQLException {

            return new Student(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("age"),
                    rs.getDouble("marks"));

        }

    };

    // Add Student
    @Override
    public void addStudent(Student student) {

        String sql =
                "INSERT INTO students(id,name,age,marks) VALUES(?,?,?,?)";

        jdbcTemplate.update(
                sql,
                student.getId(),
                student.getName(),
                student.getAge(),
                student.getMarks());

        System.out.println("Student Added Successfully.");

    }

    // View Students
    @Override
    public List<Student> viewStudents() {

        String sql = "SELECT * FROM students";

        return jdbcTemplate.query(sql, studentRowMapper);

    }

    // Search Student
    @Override
    public Student searchStudent(int id) {

        String sql =
                "SELECT * FROM students WHERE id=?";

        List<Student> students =
                jdbcTemplate.query(sql, studentRowMapper, id);

        if (students.isEmpty()) {
            return null;
        }

        return students.get(0);

    }

    // Update Student
    @Override
    public void updateStudent(Student student) {

        String sql =
                "UPDATE students SET name=?, age=?, marks=? WHERE id=?";

        int rows = jdbcTemplate.update(
                sql,
                student.getName(),
                student.getAge(),
                student.getMarks(),
                student.getId());

        if (rows > 0)
            System.out.println("Student Updated Successfully.");
        else
            System.out.println("Student Not Found.");

    }

    // Delete Student
    @Override
    public void deleteStudent(int id) {

        String sql =
                "DELETE FROM students WHERE id=?";

        int rows = jdbcTemplate.update(sql, id);

        if (rows > 0)
            System.out.println("Student Deleted Successfully.");
        else
            System.out.println("Student Not Found.");

    }

    // Highest Marks
    @Override
    public Student displayHighestMarks() {

        String sql =
                "SELECT * FROM students ORDER BY marks DESC LIMIT 1";

        List<Student> students =
                jdbcTemplate.query(sql, studentRowMapper);

        if (students.isEmpty()) {
            return null;
        }

        return students.get(0);

    }

    // Average Marks
    @Override
    public double calculateAverage() {

        String sql =
                "SELECT AVG(marks) FROM students";

        Double average =
                jdbcTemplate.queryForObject(sql, Double.class);

        if (average == null)
            return 0;

        return average;

    }

}