package com.college.dao;

import java.util.List;

import com.college.model.Student;

public interface StudentDAO {

    void addStudent(Student student);

    List<Student> viewStudents();

    Student searchStudent(int id);

    void updateStudent(Student student);

    void deleteStudent(int id);

    Student displayHighestMarks();

    double calculateAverage();

}