package com.example.enrollmentservice.dto;

/**
 * The shape of a student as returned by student-service's own StudentResponse.
 * Deliberately duplicated here rather than shared as a library - enrollment-service
 * only needs id and name, and a copy means the two services can evolve their DTOs
 * independently without a shared dependency between them.
 */
public class StudentDto {

    private Long id;
    private String name;

    public StudentDto() {
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
}
