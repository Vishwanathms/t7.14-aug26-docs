package com.example.studentservice.service;

import com.example.studentservice.exception.EmailAlreadyExistsException;
import com.example.studentservice.exception.InvalidStatusTransitionException;
import com.example.studentservice.exception.StudentNotFoundException;
import com.example.studentservice.model.Student;
import com.example.studentservice.model.StudentStatus;
import com.example.studentservice.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

    private static final Logger log = LoggerFactory.getLogger(StudentService.class);

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Page<Student> getAllStudents(Pageable pageable) {
        return studentRepository.findAll(pageable);
    }

    public Page<Student> searchStudentsByName(String name, Pageable pageable) {
        return studentRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Student {} not found", id);
                    return new StudentNotFoundException(id);
                });
    }

    public Student createStudent(Student student) {
        if (studentRepository.existsByEmail(student.getEmail())) {
            log.warn("Rejected create: email {} already exists", student.getEmail());
            throw new EmailAlreadyExistsException(student.getEmail());
        }
        student.setId(null);
        Student saved = studentRepository.save(student);
        log.debug("Persisted student id={}", saved.getId());
        return saved;
    }

    public Student updateStudent(Long id, Student student) {
        Student existing = getStudentById(id);
        existing.setName(student.getName());
        existing.setEmail(student.getEmail());
        return studentRepository.save(existing);
    }

    public Student changeStatus(Long id, StudentStatus newStatus) {
        Student existing = getStudentById(id);
        if (existing.getStatus() == StudentStatus.GRADUATED && newStatus != StudentStatus.GRADUATED) {
            log.warn("Rejected status change: student {} is GRADUATED, cannot move to {}", id, newStatus);
            throw new InvalidStatusTransitionException(existing.getStatus(), newStatus);
        }
        existing.setStatus(newStatus);
        Student saved = studentRepository.save(existing);
        log.debug("Student {} status changed to {}", id, newStatus);
        return saved;
    }

    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            log.warn("Rejected delete: student {} not found", id);
            throw new StudentNotFoundException(id);
        }
        studentRepository.deleteById(id);
        log.debug("Deleted student id={}", id);
    }
}
