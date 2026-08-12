package com.college;

import org.springframework.stereotype.Component;

@Component
public class Course {

    private int courseId = 1001;
    private String courseName = "Spring Framework";
    private String duration = "30 Hours";

    public void displayCourse() {

        System.out.println("Course : " + courseName);
        System.out.println("Duration : " + duration);
    }
}