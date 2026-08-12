package com.college;

public class Professor {

    private int professorId;
    private String professorName;
    private String specialization;

    private Course course;

    public Professor(int professorId,
                     String professorName,
                     String specialization,
                     Course course) {

        this.professorId = professorId;
        this.professorName = professorName;
        this.specialization = specialization;
        this.course = course;
    }

    public void displayProfessor() {

        System.out.println("Professor : " + professorName);
        System.out.println("Specialization : " + specialization);

        course.displayCourse();
    }
}