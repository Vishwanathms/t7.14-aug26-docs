package com.college;

import org.springframework.stereotype.Component;

@Component
public class Professor {

    private int professorId = 1;
    private String professorName = "Dr Ravi Kumar";

    private final Course course;

    public Professor(Course course) {
        this.course = course;
    }

    public void displayProfessor() {

        System.out.println("Professor : " + professorName);
        course.displayCourse();
    }
}