package com.college;

public class Course {

    private int courseId;
    private String courseName;
    private String duration;

    public Course(int courseId, String courseName, String duration) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.duration = duration;
    }

    public void displayCourse() {
        System.out.println("Course : " + courseName);
        System.out.println("Duration : " + duration);
    }
}