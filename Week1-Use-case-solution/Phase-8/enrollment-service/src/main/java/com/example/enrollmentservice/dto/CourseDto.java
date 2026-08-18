package com.example.enrollmentservice.dto;

/**
 * The shape of a course as returned by course-service's own CourseResponse.
 * See StudentDto for why this is a local copy rather than a shared type.
 */
public class CourseDto {

    private Long id;
    private String title;
    private int capacity;

    public CourseDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
