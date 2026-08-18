package com.example.studentservice.dto;

import com.example.studentservice.model.Student;
import com.example.studentservice.model.StudentStatus;

public class StudentResponse {

    private Long id;
    private String name;
    private String email;
    private StudentStatus status;

    public StudentResponse() {
    }

    public StudentResponse(Long id, String name, String email, StudentStatus status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.status = status;
    }

    public static StudentResponse from(Student student) {
        return new StudentResponse(student.getId(), student.getName(), student.getEmail(), student.getStatus());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public StudentStatus getStatus() {
        return status;
    }

    public void setStatus(StudentStatus status) {
        this.status = status;
    }
}
