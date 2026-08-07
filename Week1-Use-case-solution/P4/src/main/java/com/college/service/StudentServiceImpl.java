package com.college.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.college.dao.StudentDAO;
import com.college.model.Student;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentDAO studentDAO;

    @Autowired
    public StudentServiceImpl(StudentDAO studentDAO) {
        this.studentDAO = studentDAO;
    }

    @Override
    public void addStudent(Student student) {

        studentDAO.addStudent(student);

    }

    @Override
    public List<Student> viewStudents() {

        return studentDAO.viewStudents();

    }

    @Override
    public Student searchStudent(int id) {

        return studentDAO.searchStudent(id);

    }

    @Override
    public void updateStudent(Student student) {

        studentDAO.updateStudent(student);

    }

    @Override
    public void deleteStudent(int id) {

        studentDAO.deleteStudent(id);

    }

    @Override
    public Student displayHighestMarks() {

        return studentDAO.displayHighestMarks();

    }

    @Override
    public double calculateAverage() {

        return studentDAO.calculateAverage();

    }

}