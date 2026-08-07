package com.college.service;

import java.util.List;

import com.college.model.Student;

public interface StudentService {

    void addStudent(Student student);

    List<Student> viewStudents();

    Student searchStudent(int id);

    void updateStudent(Student student);

    void deleteStudent(int id);

    Student displayHighestMarks();

    double calculateAverage();

}
