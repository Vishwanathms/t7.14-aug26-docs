package com.example.courseservice.dto;

import com.example.courseservice.model.Course;

public class CourseResponse {

    private Long id;
    private String title;
    private int capacity;

    public CourseResponse() {
    }

    public CourseResponse(Long id, String title, int capacity) {
        this.id = id;
        this.title = title;
        this.capacity = capacity;
    }

    public static CourseResponse from(Course course) {
        return new CourseResponse(course.getId(), course.getTitle(), course.getCapacity());
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
