package com.college.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.college.model.Student;
import com.college.repository.StudentRepository;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    @Autowired
    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    @Transactional
    public void addStudent(Student student) {

        studentRepository.save(student);

    }

    @Override
    public List<Student> viewStudents() {

        return studentRepository.findAll();

    }

    @Override
    public Student searchStudent(int id) {

        return studentRepository.findById(id).orElse(null);

    }

    @Override
    @Transactional
    public void updateStudent(Student student) {

        studentRepository.save(student);

    }

    @Override
    @Transactional
    public void deleteStudent(int id) {

        studentRepository.deleteById(id);

    }

    @Override
    public Student displayHighestMarks() {

        List<Student> students = studentRepository.findAllOrderByMarksDesc();

        return students.isEmpty() ? null : students.get(0);

    }

    @Override
    public double calculateAverage() {

        Double average = studentRepository.findAverageMarks();

        return average == null ? 0 : average;

    }

}
