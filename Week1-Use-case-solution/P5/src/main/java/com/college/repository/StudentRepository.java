package com.college.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.college.model.Student;

/**
 * Replaces StudentDAO / StudentDAOImpl entirely.
 * JpaRepository already provides save(), findById(), findAll(), deleteById().
 * We only declare the two queries that don't come for free.
 */
public interface StudentRepository extends JpaRepository<Student, Integer> {

    @Query("SELECT s FROM Student s ORDER BY s.marks DESC")
    List<Student> findAllOrderByMarksDesc();

    @Query("SELECT AVG(s.marks) FROM Student s")
    Double findAverageMarks();

}
